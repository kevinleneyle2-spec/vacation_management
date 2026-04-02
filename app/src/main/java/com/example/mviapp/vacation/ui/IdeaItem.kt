package com.example.mviapp.vacation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.local.model.Activity
import com.example.data.local.model.Day
import com.example.mviapp.R
import com.example.mviapp.ui.theme.MVIAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaItem(
    ideas: List<String>,
    onAddIdea: (String) -> Unit,
    onRemoveIdea: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var newActivityValue by remember { mutableStateOf("") }

    fun checkAddAllowed() : Boolean {
        return newActivityValue.isNotBlank()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .testTag("IdeaCard"),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white_orange)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.activitiesscreen_ideas_title),
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.activitiesscreen_ideas_list_title),
                color = colorResource(R.color.orange),
                fontWeight = FontWeight.Bold
            )

            if(ideas.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.activitiesscreen_ideas_empty),
                        modifier = Modifier.weight(1f)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            ideas.forEachIndexed { index, idea ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "- $idea",
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { onRemoveIdea(index) },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(width = 32.dp, height = 32.dp)
                            .testTag("activitiesDeleteButton"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "X",
                            color = colorResource(R.color.red),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                Modifier
                    .border(
                        width = 2.dp,
                        color = colorResource(R.color.orange),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(all = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.activitiesscreen_ideas_add_title),
                    color = colorResource(R.color.orange)
                )

                Row(modifier = Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = newActivityValue,
                        onValueChange = { newValue ->
                            newActivityValue = newValue
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        label = { Text(stringResource(R.string.activitiesscreen_ideas_description)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            if (checkAddAllowed()) {
                                onAddIdea(newActivityValue)

                                newActivityValue = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (checkAddAllowed()) {
                                colorResource(id = R.color.orange)
                            } else { Color.Gray.copy(alpha = 0.2f) },
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.activitiesscreen_add_button),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IdeasItemPreview() {
    MVIAppTheme {
        val mockIdeas = listOf("piscine", "tennis")

        IdeaItem(
            ideas = mockIdeas,
            onAddIdea = { _ -> },
            onRemoveIdea = {}
        )
    }
}