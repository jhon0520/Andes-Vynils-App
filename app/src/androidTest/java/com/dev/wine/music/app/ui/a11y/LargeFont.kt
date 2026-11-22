package com.dev.wine.music.app.ui.a11y

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue

/** Largest font the system offers (200 %, Android 14+). */
const val MAX_FONT_SCALE = 2f

/**
 * Renders [content] like a 360 × 640 dp phone with the font at [fontScale], whatever the test device is,
 * so the A11Y-03 checks give the same result on a phone, a tablet or an emulator.
 */
@Composable
fun PhoneWithFontScale(fontScale: Float = MAX_FONT_SCALE, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        Box(modifier = Modifier.requiredSize(width = 360.dp, height = 640.dp)) { content() }
    }
}

fun SemanticsNodeInteraction.bounds(): Rect = fetchSemanticsNode().boundsInRoot

/** The whole node is inside [container]: it is not cut by the container's edges. */
fun SemanticsNodeInteraction.assertFullyInside(container: SemanticsNodeInteraction) {
    val inner = bounds()
    val outer = container.bounds()
    assertTrue(
        "Expected $inner to be fully inside $outer",
        inner.top >= outer.top && inner.bottom <= outer.bottom && inner.left >= outer.left && inner.right <= outer.right,
    )
}
