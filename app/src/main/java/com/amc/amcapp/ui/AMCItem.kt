package com.amc.amcapp.ui


import StatusTracker
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.RecordUiItem
import com.amc.amcapp.model.Status
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Avatar
import com.amc.amcapp.util.image.AppImagePicker
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AMCItem(
    item: AMC, onClick: () -> Unit
) {
    val dateText = remember(item.createdDate) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        formatter.format(Instant.ofEpochMilli(item.createdDate))
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .semantics {
                contentDescription =
                    "AMC: ${item.name}, Status: ${item.status}, Assignee: ${item.assignedName}, Updated: $dateText"
            }, shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(LocalDimens.current.spacingMedium.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Avatar(
                    imageUrl = item.gymImage, name = item.assignedName, modifier = Modifier.align(
                        Alignment.CenterVertically
                    )
                )

                Spacer(modifier = Modifier.width(LocalDimens.current.spacingMedium.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.gymName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))

                    Row {
                        Icon(
                            imageVector = Icons.Filled.PersonOutline,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(LocalDimens.current.spacingSmall.dp))
                        Text(
                            text = item.assignedName,
                            fontSize = LocalDimens.current.textMedium.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                }


                Column(
                    modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.End
                ) {
                    //StatusTag(status = item.status)
                    //Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
                    Row {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(LocalDimens.current.spacingSmall.dp))
                        Text(
                            text = dateText,
                            fontSize = LocalDimens.current.textSmall.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            }
        }

        StatusTracker(Status.entries.filter { it != Status.CANCELLED }.map {
            it.label
        }, Status.entries.indexOf(item.status))
        Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
    }
}


@Composable
fun RecordUiItem(
    enabled: Boolean,
    index: Int,
    recordUiItem: RecordUiItem,
    onRecordUpdated: (Int, RecordUiItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(LocalDimens.current.spacingSmall.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            text = (index + 1).toString() + ". " + recordUiItem.recordItem.equipmentName,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = LocalDimens.current.textLarge.sp
        )

        Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Before Service", modifier = Modifier.padding(LocalDimens.current.spacingMedium.dp)
            )
            AppImagePicker(
                buttonEnabled = enabled,
                index = index,
                modifier = Modifier.fillMaxWidth(),
                imageUrl = recordUiItem.beforeImage.imageUrl,
                imageUri = recordUiItem.beforeImage.imageUri,
                onImageReturned = { uri ->
                    val recordUiItem = recordUiItem.copy(
                        beforeImage = recordUiItem.beforeImage.copy(
                            imageUri = uri.toString(), shouldUseUrl = false, shouldUseUri = true
                        )
                    )
                    onRecordUpdated(index, recordUiItem)
                },
                onPermissionDenied = { permission ->

                },
                shouldUseUri = recordUiItem.beforeImage.shouldUseUri,
                shouldUseUrl = recordUiItem.beforeImage.shouldUseUrl,
                contentScale = ContentScale.FillWidth
            )
            Text(
                "After Service", modifier = Modifier.padding(LocalDimens.current.spacingMedium.dp)
            )
            AppImagePicker(
                enabled,
                index = index,
                imageUrl = recordUiItem.afterImage.imageUrl,
                imageUri = recordUiItem.afterImage.imageUri,
                onImageReturned = { uri ->
                    val recordUiItem = recordUiItem.copy(
                        afterImage = recordUiItem.afterImage.copy(
                            imageUri = uri.toString(), shouldUseUrl = false, shouldUseUri = true
                        )
                    )
                    onRecordUpdated(index, recordUiItem)
                },
                onPermissionDenied = { permission ->

                },
                shouldUseUri = recordUiItem.afterImage.shouldUseUri,
                shouldUseUrl = recordUiItem.afterImage.shouldUseUrl,
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
        }
    }

}

@Composable
fun FlexRadioLayout() {
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6")
    val checkedStates = remember { mutableStateListOf(*Array(options.size) { false }) }

    FlowRow(
        mainAxisSpacing = 16.dp,   // horizontal spacing between items
        crossAxisSpacing = 12.dp,  // vertical spacing between lines
        mainAxisAlignment = FlowMainAxisAlignment.Start
    ) {
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)
            ) {
                Checkbox(
                    checked = checkedStates[index], onCheckedChange = { checkedStates[index] = it })
                Text(text = option)
            }
        }
    }
}
