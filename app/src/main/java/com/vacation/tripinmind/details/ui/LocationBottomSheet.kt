package com.vacation.tripinmind.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.details.model.NavigationAppEnum
import com.vacation.tripinmind.ui.theme.MVIAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationBottomSheet(
    onDismiss: () -> Unit,
    location: String,
    isGoogleMapsInstalled: Boolean,
    isWazeInstalled: Boolean,
    onItineraryClick: (NavigationAppEnum) -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null,
        shape = RectangleShape,
        tonalElevation = 0.dp,
        contentWindowInsets = { WindowInsets(0.dp) }
    ) {
        Surface(
            color = Color.White,
            shape = BottomSheetDefaults.ExpandedShape,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = BottomSheetDefaults.ExpandedShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                BottomSheetDefaults.DragHandle(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.detailsscreen_dialog_address_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isGoogleMapsInstalled) {
                            IconButton(
                                onClick = { onItineraryClick(NavigationAppEnum.GOOGLE_MAP) },
                                modifier = Modifier
                                    .padding(top = 32.dp, end = if (isWazeInstalled) 36.dp else 0.dp)
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .testTag("detailsGoogleMapButton")
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.map_ico),
                                    contentDescription = "GoogleMap",
                                    modifier = Modifier.size(72.dp)
                                )
                            }
                        }

                        if (isWazeInstalled) {
                            IconButton(
                                onClick = { onItineraryClick(NavigationAppEnum.WAZE) },
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .testTag("detailsWazeMapButton")
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.waze_ico),
                                    contentDescription = "Waze",
                                    modifier = Modifier.size(72.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, heightDp = 800)
@Composable
fun LocationBottomSheetPreview() {
    MVIAppTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LocationBottomSheet(
            onDismiss = {},
            location = "50 rue des ponts, Paris",
            isGoogleMapsInstalled = true,
            isWazeInstalled = true,
            onItineraryClick = {},
            sheetState = sheetState
        )
    }
}
