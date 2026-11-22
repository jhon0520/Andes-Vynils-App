package com.dev.wine.music.app.a11y

import android.graphics.Bitmap
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult
import com.google.android.apps.common.testing.accessibility.framework.Parameters
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage
import java.io.File
import java.util.Locale

/**
 * Runs the Accessibility Test Framework checks (the engine behind Accessibility Scanner) on the
 * screen that is showing: touch target size, contrast, labels, duplicate descriptions, etc.
 *
 * Each [audit] saves a screenshot and adds the findings to a Markdown report in the app's
 * external files (`Android/data/<package>/files/a11y/<run>/`), so they can be pulled with adb.
 */
class AccessibilityAudit(private val run: String) {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val outputDir = File(context.getExternalFilesDir(null), "a11y/$run").apply {
        deleteRecursively()
        mkdirs()
    }
    private val report = StringBuilder()

    /** Findings of every audited screen, without INFO, NOT_RUN or the results ATF suppresses. */
    val findings = mutableListOf<Finding>()

    data class Finding(val screen: String, val type: AccessibilityCheckResultType, val check: String, val element: String, val message: String)

    fun audit(screen: String) {
        // Compose is idle as soon as the data arrives, but Coil keeps downloading covers and photos:
        // without this pause the contrast checks would measure the empty placeholders.
        Thread.sleep(IMAGE_SETTLE_MS)
        instrumentation.waitForIdleSync()
        val uiAutomation = instrumentation.uiAutomation
        // Right after launch the window may not be exposed to accessibility yet.
        var root = uiAutomation.rootInActiveWindow
        var attempts = 0
        while (root == null && attempts++ < ROOT_ATTEMPTS) {
            Thread.sleep(ROOT_WAIT_MS)
            root = uiAutomation.rootInActiveWindow
        }
        checkNotNull(root) { "No active window to audit on $screen" }
        val screenshot: Bitmap = checkNotNull(uiAutomation.takeScreenshot()) { "No screenshot of $screen" }
        File(outputDir, "$screen.png").outputStream().use { screenshot.compress(Bitmap.CompressFormat.PNG, 100, it) }

        val hierarchy = AccessibilityHierarchyAndroid.newBuilder(root, context).build()
        val parameters = Parameters().apply { putScreenCapture(BitmapImage(screenshot)) }
        val results: List<AccessibilityHierarchyCheckResult> = AccessibilityCheckPreset
            .getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST)
            .flatMap { it.runCheckOnHierarchy(hierarchy, null, parameters) }
            .filter { it.type == AccessibilityCheckResultType.ERROR || it.type == AccessibilityCheckResultType.WARNING }

        report.append("\n## $screen\n\n![${screen}](${screen}.png)\n\n")
        if (results.isEmpty()) report.append("Sin hallazgos.\n")
        results.forEach { result ->
            val element = result.element?.let { it.resourceName ?: it.className?.toString() ?: "?" } ?: "-"
            val finding = Finding(
                screen = screen,
                type = result.type,
                check = result.sourceCheckClass.simpleName,
                element = element,
                message = result.getMessage(Locale.getDefault()).toString(),
            )
            findings += finding
            report.append("- **${finding.type}** `${finding.check}` en `${finding.element}`: ${finding.message}\n")
            Log.w(TAG, "$screen · ${finding.type} · ${finding.check} · ${finding.element} · ${finding.message}")
        }
        File(outputDir, REPORT).writeText("# Auditoría ATF · $run\n$report")
    }

    companion object {
        const val TAG = "A11yAudit"
        const val REPORT = "report.md"
        private const val ROOT_ATTEMPTS = 10
        private const val ROOT_WAIT_MS = 500L
        private const val IMAGE_SETTLE_MS = 2_500L
    }
}
