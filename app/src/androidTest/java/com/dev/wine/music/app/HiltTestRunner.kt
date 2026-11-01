package com.dev.wine.music.app

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Runs instrumented tests on [HiltTestApplication], so a test can replace Hilt modules.
 * Every test that launches [MainActivity] must be `@HiltAndroidTest` with a `HiltAndroidRule` at order 0.
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
