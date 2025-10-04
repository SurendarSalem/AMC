package com.amc.amcapp.ui

import StatusTracker
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amc.amcapp.equipments.spares.SpareUiItem
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.RecordUiItem
import com.amc.amcapp.model.SpareDetails
import com.amc.amcapp.model.Status
import com.amc.amcapp.ui.screens.amc.AddAmcViewModel
import com.amc.amcapp.ui.screens.gym.SpareItem
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Avatar
import com.amc.amcapp.util.image.AppImagePicker
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AMCItem(
    item: AMC,
    onClick: () -> Unit
) {
    val dateText = remember(item.createdDate) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        formatter.format(Instant.ofEpochMilli(item.createdDate))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .semantics {
                contentDescription =
                    "AMC: ${item.name}, Status: ${item.status}, Assignee: ${item.assignedName}, Updated: $dateText"
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
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
                    imageUrl = item.gymImage,
                    name = item.assignedName,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(LocalDimens.current.spacingMedium.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.gymName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PersonOutline,
                            contentDescription = "Assignee",
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
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Date",
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

            StatusTracker(
                steps = Status.entries.filter { it != Status.CANCELLED }.map { it.label },
                currentStep = Status.entries.indexOf(item.status)
            )
            Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
        }
    }
}

@Composable
fun RecordUiListItem(
    enabled: Boolean,
    index: Int,
    recordUiItem: RecordUiItem,
    amcViewModel: AddAmcViewModel,
    onRecordUpdated: (Int, RecordUiItem) -> Unit
) {
    var recordUiItemState by remember { mutableStateOf(recordUiItem) }

    // Fetch spares when equipment ID changes
    LaunchedEffect(recordUiItem.recordItem.equipmentId) {
        amcViewModel.getSpares(recordUiItem.recordItem.equipmentId)
    }

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
            text = "${index + 1}. ${recordUiItemState.recordItem.equipmentName}",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = LocalDimens.current.textLarge.sp
        )

        Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Before Service",
                modifier = Modifier.padding(LocalDimens.current.spacingMedium.dp)
            )
            AppImagePicker(
                buttonEnabled = enabled,
                index = index,
                modifier = Modifier.fillMaxWidth(),
                imageUrl = recordUiItemState.beforeImage.imageUrl,
                imageUri = recordUiItemState.beforeImage.imageUri,
                onImageReturned = { uri ->
                    recordUiItemState = recordUiItemState.copy(
                        beforeImage = recordUiItemState.beforeImage.copy(
                            imageUri = uri.toString(),
                            shouldUseUrl = false,
                            shouldUseUri = true
                        )
                    )
                    onRecordUpdated(index, recordUiItemState)
                },
                onPermissionDenied = { /* Handle permission denied */ },
                shouldUseUri = recordUiItemState.beforeImage.shouldUseUri,
                shouldUseUrl = recordUiItemState.beforeImage.shouldUseUrl,
                contentScale = ContentScale.FillWidth
            )

            Text("After Service", modifier = Modifier.padding(LocalDimens.current.spacingMedium.dp))
            AppImagePicker(
                buttonEnabled = enabled,
                index = index,
                imageUrl = recordUiItemState.afterImage.imageUrl,
                imageUri = recordUiItemState.afterImage.imageUri,
                onImageReturned = { uri ->
                    recordUiItemState = recordUiItemState.copy(
                        afterImage = recordUiItemState.afterImage.copy(
                            imageUri = uri.toString(),
                            shouldUseUrl = false,
                            shouldUseUri = true
                        )
                    )
                    onRecordUpdated(index, recordUiItemState)
                },
                onPermissionDenied = { /* Handle permission denied */ },
                shouldUseUri = recordUiItemState.afterImage.shouldUseUri,
                shouldUseUrl = recordUiItemState.afterImage.shouldUseUrl,
                contentScale = ContentScale.FillWidth
            )

            if (recordUiItemState.totalSpares.isNotEmpty()) {
                recordUiItemState.totalSpares.forEach { spare ->
                    val isSelected = recordUiItemState.addedSpares.any { it.spareId == spare.id }
                    var spareUiItemState by remember {
                        mutableStateOf(
                            SpareUiItem(
                                spare,
                                isSelected
                            )
                        )
                    }

                    SpareItem(
                        spareUiItem = spareUiItemState,
                        onCheckedChanged = { checked ->
                            spareUiItemState = spareUiItemState.copy(isSelected = checked)

                            recordUiItemState = if (checked) {
                                recordUiItemState.copy(
                                    addedSpares = recordUiItemState.addedSpares + SpareDetails(
                                        spareId = spare.id,
                                        spareName = spare.name,
                                        quantity = 1
                                    )
                                )
                            } else {
                                recordUiItemState.copy(
                                    addedSpares = recordUiItemState.addedSpares.filterNot { it.spareId == spare.id }
                                )
                            }
                            onRecordUpdated(index, recordUiItemState)
                        }, onSpareClicked = {}, onQuantityChange = { newQuantity ->
                            spareUiItemState = spareUiItemState.copy(requiredQuantity = newQuantity)
                            recordUiItemState = recordUiItemState.copy(
                                addedSpares = recordUiItemState.addedSpares.map {
                                    if (it.spareId == spare.id) {
                                        it.copy(quantity = newQuantity)
                                    } else it
                                }
                            )
                            onRecordUpdated(index, recordUiItemState)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
        }
    }
}