package com.vacation.tripinmind.vacation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.model.Activity
import com.vacation.tripinmind.model.Day
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import com.vacation.tripinmind.vacation.model.VacationState

@Composable
fun ActivitiesScreenContent(
    state: VacationState,
    onUpdateAddInfo: (Int, String) -> Unit,
    onAddDayActivities: (Int, Activity) -> Unit,
    onModifyDayActivities: (Int, Int, Activity) -> Unit,
    onRemoveDayActivities: (Int, Int) -> Unit,
    onAddIdea: (String) -> Unit,
    onRemoveIdea: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        itemsIndexed(
            state.days
        ) { index, day ->
            ActivitiesItem(
                day = day,
                onAddInfo = { newValue ->
                    if (newValue.length <= 100 && !newValue.contains("\n")) {
                        onUpdateAddInfo(index, newValue)
                    }
                },
                onAddActivity = { activity ->
                    onAddDayActivities(
                        index,
                        activity
                    )
                },
                onModifyActivity = { updateIndex, activity ->
                    onModifyDayActivities(index, updateIndex, activity)
                },
                onRemoveActivity = { removeIndex ->
                    onRemoveDayActivities(index, removeIndex)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            IdeaItem(
                ideas = state.ideas,
                onAddIdea = { name ->
                    onAddIdea(name)
                },
                onRemoveIdea = { index ->
                    onRemoveIdea(index)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivitiesScreenPreview() {
    MVIAppTheme {
        Surface {
            ActivitiesScreenContent(
                state = VacationState(
                    days = listOf(
                        Day(
                            date = 1789862400000,
                            "Day 1",
                            "test",
                            listOf(Activity("Visit the Eiffel Tower", "10:00", "2h00", "01h00"))
                        ),
                        Day(
                            date = 1789862400000,
                            "Day 2",
                            "",
                            listOf(Activity("Go to the Louvre", "14:00", "2h00", "01h00"))
                        )
                    )
                ),
                onUpdateAddInfo = { _, _ -> },
                onAddDayActivities = { _, _ -> },
                onModifyDayActivities = { _, _, _ -> },
                onRemoveDayActivities = { _, _ -> },
                onAddIdea = {},
                onRemoveIdea = {}
            )
        }
    }
}
