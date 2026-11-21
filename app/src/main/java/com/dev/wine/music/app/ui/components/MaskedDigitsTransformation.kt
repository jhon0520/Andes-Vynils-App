package com.dev.wine.music.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Shows plain digits with separators, so the numeric keyboard is enough: "01081984" reads as
 * "01/08/1984" with [separator] '/' after positions 2 and 4.
 */
class MaskedDigitsTransformation(
    private val separator: Char,
    private val separatorsAfter: List<Int>,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val masked = buildString {
            text.text.forEachIndexed { index, digit ->
                if (index in separatorsAfter) append(separator)
                append(digit)
            }
        }
        val offsets = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                offset + separatorsAfter.count { it < offset }

            override fun transformedToOriginal(offset: Int): Int {
                var original = offset
                separatorsAfter.forEachIndexed { index, position ->
                    if (offset > position + index) original--
                }
                return original.coerceIn(0, text.length)
            }
        }
        return TransformedText(AnnotatedString(masked), offsets)
    }
}
