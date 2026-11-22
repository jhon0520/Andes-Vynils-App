package com.dev.wine.music.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.ui.theme.SansBody
import com.dev.wine.music.app.ui.theme.Terracotta

/** Five stars, filled up to [rating]; read aloud as "Calificación: 4 de 5". Album (HU02) and collector (HU06) reviews. */
@Composable
fun RatingStars(rating: Int, modifier: Modifier = Modifier) {
    val stars = rating.coerceIn(0, Comment.MAX_RATING)
    val description = stringResource(R.string.review_rating_description, stars, Comment.MAX_RATING)
    Text(
        text = "★".repeat(stars) + "☆".repeat(Comment.MAX_RATING - stars),
        style = SansBody.copy(fontSize = 17.sp, letterSpacing = 1.sp),
        color = Terracotta,
        modifier = modifier.semantics { contentDescription = description },
    )
}
