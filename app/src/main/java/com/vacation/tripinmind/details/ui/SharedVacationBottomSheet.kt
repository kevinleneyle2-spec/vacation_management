package com.vacation.tripinmind.details.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.details.model.DetailsError
import com.vacation.tripinmind.details.model.VacationUiModel
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import com.vacation.tripinmind.util.ShareCodeVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedVacationBottomSheet(
    onDismiss: () -> Unit,
    onAddViewer: (String) -> Unit,
    onClearError: () -> Unit = {},
    vacationModel: VacationUiModel? = null,
    error: DetailsError? = null,
    onRemoveSharedUser: (Int) -> Unit,
    sheetState: SheetState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val dummyFocusRequester = remember { FocusRequester() }

    var newViewerValue by remember { mutableStateOf("") }
    val isButtonEnabled = newViewerValue.length == 12

    LaunchedEffect(error) {
        if (error == DetailsError.SUCCESS) {
            newViewerValue = ""
            onClearError()
        }
    }

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
                    .fillMaxHeight(0.9f)
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
                        text = stringResource(R.string.detailsscreen_share_bottomsheet_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.detailsscreen_share_bottomsheet_text),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.detailsscreen_share_bottomsheet_text_2),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = stringResource(R.string.detailsscreen_share_bottomsheet_add_viewer_title),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = newViewerValue,
                                onValueChange = { newValue ->
                                    if (error != null)
                                        onClearError()

                                    val filtered =
                                        newValue.filter { it.isLetterOrDigit() }.uppercase()
                                    if (filtered.length <= 12) {
                                        newViewerValue = filtered
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        dummyFocusRequester.requestFocus()
                                        keyboardController?.hide()

                                        if (isButtonEnabled) {
                                            onAddViewer(newViewerValue)
                                        }
                                    }
                                ),
                                visualTransformation = ShareCodeVisualTransformation(),
                                placeholder = { Text(stringResource(R.string.detailsscreen_share_bottomsheet_placeholder)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.5f
                                    ),
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                                ),
                                trailingIcon = {
                                    if (newViewerValue.isNotEmpty()) {
                                        IconButton(onClick = { newViewerValue = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear share code",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                },
                                label = { Text(stringResource(R.string.detailsscreen_share_bottomsheet_add_viewer_text)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .padding(top = 4.dp)
                                    .focusRequester(dummyFocusRequester)
                                    .focusable()
                            ) {
                                if (error != null && error != DetailsError.SUCCESS) {
                                    Text(
                                        text = when (error) {
                                            DetailsError.NOT_FOUND -> stringResource(R.string.detailsscreen_share_error_not_found)
                                            DetailsError.ALREADY_ADDED -> stringResource(R.string.detailsscreen_share_error_already_added)
                                            DetailsError.NOT_YOURSELF -> stringResource(R.string.detailsscreen_share_error_not_yourself)
                                            DetailsError.TOO_MANY_VIEWERS -> stringResource(R.string.detailsscreen_share_error_too_many_viewers)
                                        },
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        if (isButtonEnabled)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    )
                                    .then(
                                        if (isButtonEnabled) {
                                            Modifier.clickable {
                                                onAddViewer(newViewerValue)
                                                dummyFocusRequester.requestFocus()
                                                keyboardController?.hide()
                                            }
                                        } else
                                            Modifier
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.detailsscreen_share_bottomsheet_add_viewer_button),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.detailsscreen_share_bottomsheet_add_viewer_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.detailsscreen_share_bottomsheet_access_text),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth()
                    )

                    val shareWithList = vacationModel?.vacation?.shareWith

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        shareWithList?.forEachIndexed { index, shareWith ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = shareWith,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                TextButton(
                                    onClick = { onRemoveSharedUser(index) },
                                    modifier = Modifier
                                        .size(width = 32.dp, height = 32.dp)
                                        .testTag("sharedDeleteButton"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        0.dp
                                    )
                                ) {
                                    Text(
                                        text = "X",
                                        color = colorResource(R.color.red),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
fun SharedVacationBottomSheetPreview() {
    MVIAppTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        SharedVacationBottomSheet(
            onDismiss = {},
            onAddViewer = {},
            onClearError = {},
            vacationModel = VacationUiModel(
                VacationDto(
                    id = "",
                    shareWith = listOf("AAA-111-AAA-111")
                ), error = DetailsError.SUCCESS
            ),
            error = null,
            onRemoveSharedUser = {},
            sheetState = sheetState
        )
    }
}