package com.vacation.tripinmind.details.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import kotlinx.coroutines.delay

@Composable
fun IdeaCard(
    ideas: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    if (ideas.size > 4) {
        LaunchedEffect(ideas) {
            while (true) {
                try {
                    while (listState.canScrollForward) {
                        listState.animateScrollBy(
                            value = 50f,
                            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                        )
                    }

                    delay(2000)

                    while (listState.canScrollBackward) {
                        listState.animateScrollBy(
                            value = -50f,
                            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                        )
                    }

                    delay(2000)
                } catch (e: Exception) {
                    while (listState.isScrollInProgress) {
                        delay(500)
                    }
                    delay(2000)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .heightIn(max = 250.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        if (ideas.isNotEmpty()) {
            Text(
                text = stringResource(R.string.detailsscreen_idea_title),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 32.dp, top = 16.dp, end = 32.dp)
                    .align(Alignment.CenterHorizontally),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ideas) { idea ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "- $idea",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.detailsscreen_error_no_idea),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(all = 16.dp)

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IdeaCardPreview() {
    MVIAppTheme {
        Surface {
            IdeaCard(
                ideas = listOf("piscine", "tennis")
            )
        }
    }
}