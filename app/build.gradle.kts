import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Backend domain: defined once in gradle.properties (api.base.url).
// local.properties can override it on your machine, e.g. http://localhost:3000/ for a physical device.
val apiBaseUrl: String = Properties()
    .apply { rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }
    .getProperty("api.base.url")
    ?: providers.gradleProperty("api.base.url").orNull
    ?: error("Define api.base.url in gradle.properties")
val apiUri: URI = URI(apiBaseUrl)
require(apiUri.scheme in setOf("http", "https") && apiUri.host != null && apiBaseUrl.endsWith("/")) {
    "api.base.url must look like http://host:port/ (with the trailing slash): $apiBaseUrl"
}

/** Writes res/xml/network_security_config.xml from the backend domain, so the host lives in one place. */
abstract class NetworkSecurityConfigTask : DefaultTask() {
    @get:Input abstract val host: Property<String>
    @get:Input abstract val cleartext: Property<Boolean>
    /** Debug only: the error E2E tests run a fake backend on http://localhost inside the device. */
    @get:Input abstract val allowLocalhost: Property<Boolean>
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val file = outputDir.file("xml/network_security_config.xml").get().asFile
        file.parentFile.mkdirs()
        fun domainConfig(domain: String, cleartext: Boolean) =
            "    <domain-config cleartextTrafficPermitted=\"$cleartext\">\n" +
                "        <domain includeSubdomains=\"false\">$domain</domain>\n" +
                "    </domain-config>\n"
        file.writeText(
            buildString {
                append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                append("<!-- Generated from api.base.url (gradle.properties). Do not edit. -->\n")
                append("<network-security-config>\n")
                append(domainConfig(host.get(), cleartext.get()))
                if (allowLocalhost.get() && host.get() != "localhost") {
                    append(domainConfig("localhost", true))
                }
                append("</network-security-config>\n")
            },
        )
    }
}

android {
    namespace = "com.dev.wine.music.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dev.wine.music.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Hilt runner: lets error tests swap the API URL for a fake backend (see HiltTestRunner).
        testInstrumentationRunner = "com.dev.wine.music.app.HiltTestRunner"

        // The only domain the app talks to; NetworkModule builds Retrofit with it.
        // String.valueOf keeps it from being a compile-time constant: Kotlin would copy a constant
        // into NetworkModule and incremental builds would keep the old domain after a change.
        buildConfigField("String", "API_BASE_URL", "String.valueOf(\"$apiBaseUrl\")")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

androidComponents {
    onVariants { variant ->
        val task = tasks.register<NetworkSecurityConfigTask>(
            "generate${variant.name.replaceFirstChar { it.uppercase() }}NetworkSecurityConfig",
        ) {
            host.set(apiUri.host)
            cleartext.set(apiUri.scheme == "http")
            allowLocalhost.set(variant.buildType == "debug")
        }
        variant.sources.res?.addGeneratedSourceDirectory(task, NetworkSecurityConfigTask::outputDir)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.splashscreen)

    // Architecture
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    // Dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Images
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    androidTestImplementation(libs.okhttp.mockwebserver)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}