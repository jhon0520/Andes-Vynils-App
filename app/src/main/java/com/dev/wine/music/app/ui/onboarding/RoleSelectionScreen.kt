package com.dev.wine.music.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.wine.music.app.R
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.RecordBlack
import com.dev.wine.music.app.ui.theme.RecordGreen
import com.dev.wine.music.app.ui.theme.RecordGroove
import com.dev.wine.music.app.ui.theme.SleeveText
import com.dev.wine.music.app.ui.theme.Terracotta
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme

@Composable
fun RoleSelectionScreen(
    onVisitorSelected: () -> Unit,
    onCollectorClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoleSelectionViewModel = hiltViewModel(),
) {
    RoleSelectionContent(
        onVisitorClick = {
            viewModel.onVisitorSelect()
            onVisitorSelected()
        },
        onCollectorClick = onCollectorClick,
        modifier = modifier,
    )
}

@Composable
fun RoleSelectionContent(
    onVisitorClick: () -> Unit,
    onCollectorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .testTag("role_selection"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VinylHero()
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.onboarding_tagline),
            style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.onboarding_how_to_enter),
            style = LabelMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
        RoleOption(
            icon = Icons.Filled.Person,
            title = stringResource(R.string.role_visitor),
            subtitle = stringResource(R.string.role_visitor_description),
            highlighted = false,
            onClick = onVisitorClick,
            modifier = Modifier.testTag("role_visitor"),
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        RoleOption(
            icon = Icons.Filled.Favorite,
            title = stringResource(R.string.role_collector),
            subtitle = stringResource(R.string.role_collector_description),
            highlighted = true,
            onClick = onCollectorClick,
            modifier = Modifier.testTag("role_collector"),
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** Green sleeve with a black record sliding out to the right, drawn without raster images. */
@Composable
private fun VinylHero(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(width = 240.dp, height = 170.dp)) {
        Canvas(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.CenterEnd),
        ) {
            val radius = size.minDimension / 2
            drawCircle(color = RecordBlack, radius = radius)
            // Grooves: thin concentric rings.
            listOf(0.92f, 0.82f, 0.72f, 0.62f, 0.52f).forEach { fraction ->
                drawCircle(color = RecordGroove, radius = radius * fraction, style = Stroke(width = 1.dp.toPx()))
            }
            drawCircle(color = Terracotta, radius = radius * 0.3f)
            drawCircle(color = RecordBlack, radius = radius * 0.05f, center = Offset(size.width / 2, size.height / 2))
        }
        Column(
            modifier = Modifier
                .size(170.dp)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(4.dp))
                .background(RecordGreen)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.onboarding_side_a), style = LabelMono, color = SleeveText)
            Text(
                text = stringResource(R.string.onboarding_brand),
                style = MaterialTheme.typography.displaySmall.copy(fontStyle = FontStyle.Italic),
                color = SleeveText,
            )
        }
    }
}

@Composable
private fun RoleOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    highlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Outlined circle for the visitor, filled terracotta for the collector.
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .then(
                    if (highlighted) {
                        Modifier.background(Terracotta)
                    } else {
                        Modifier.border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground), CircleShape)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) SleeveText else MaterialTheme.colorScheme.onBackground,
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionPreview() {
    WineMusicAppTheme(darkTheme = false) {
        RoleSelectionContent(onVisitorClick = {}, onCollectorClick = {})
    }
}
