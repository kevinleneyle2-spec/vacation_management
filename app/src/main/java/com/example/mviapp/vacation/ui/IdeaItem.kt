package com.example.mviapp.vacation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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


    var showAddIdeaDialog by remember { mutableStateOf(false) }

    var newIdeaValue by remember { mutableStateOf("") }

    fun checkAddAllowed(): Boolean {
        return newIdeaValue.isNotBlank()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .testTag("IdeaCard"),
        border = BorderStroke(
            width = 3.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    MaterialTheme.colorScheme.primary
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.light_bulb_ico),
                        contentDescription = "idea ico",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = stringResource(R.string.activitiesscreen_ideas_list_title),
                        color = colorResource(R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (ideas.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.activitiesscreen_ideas_empty),
                            modifier = Modifier
                                .weight(1f)
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { showAddIdeaDialog = true },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.orange))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add idea",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            if (showAddIdeaDialog) {
                Dialog(onDismissRequest = { showAddIdeaDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.activitiesscreen_ideas_add_title),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.orange)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = newIdeaValue,
                                onValueChange = { newIdeaValue = it },
                                label = { Text(stringResource(R.string.activitiesscreen_ideas_description)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showAddIdeaDialog = false }) {
                                    Text(stringResource(R.string.cancel_button))
                                }
                                TextButton(
                                    onClick = {
                                        if (checkAddAllowed()) {
                                            onAddIdea(newIdeaValue)
                                            newIdeaValue = ""
                                            showAddIdeaDialog = false
                                        }
                                    },
                                    enabled = checkAddAllowed()
                                ) {
                                    Text(stringResource(R.string.activitiesscreen_add_button))
                                }
                            }
                        }
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