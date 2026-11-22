package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dev.wine.music.app.R
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.SansBody

/** Back arrow on the left and a mono caps label on the right, e.g. "FICHA DEL ÁLBUM". */
@Composable
fun DetailTopBar(eyebrow: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 4.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }
        Text(
            text = eyebrow.uppercase(),
            style = LabelMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Mono caps title with a solid ink rule under it ("LADO A", "RESEÑAS · 1"). */
@Composable
fun SectionTitle(text: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text.uppercase(),
            style = LabelMono,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            // TalkBack users jump between sections with the headings navigation.
            modifier = Modifier.padding(bottom = 10.dp).semantics { heading() },
        )
        HorizontalDivider(thickness = 1.5.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}

/** Message under a section title when the section has no items. */
@Composable
fun EmptySection(text: String, tag: String) {
    Text(
        text = text,
        style = SansBody,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 16.dp).testTag(tag),
    )
}
