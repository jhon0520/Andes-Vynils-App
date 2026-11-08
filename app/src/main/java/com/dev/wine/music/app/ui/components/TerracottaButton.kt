package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dev.wine.music.app.ui.theme.RecordBlack
import com.dev.wine.music.app.ui.theme.SleeveText
import com.dev.wine.music.app.ui.theme.Terracotta

private val ButtonShape = RoundedCornerShape(4.dp)

/** Terracotta "+ text" button from the mockups, with a darker bottom edge like a pressed key. */
@Composable
fun TerracottaAddButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Button(
        onClick = onClick,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = SleeveText),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        modifier = modifier
            .background(RecordBlack, ButtonShape)
            .padding(bottom = 3.dp),
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = textStyle,
            fontWeight = FontWeight.Bold,
        )
    }
}
