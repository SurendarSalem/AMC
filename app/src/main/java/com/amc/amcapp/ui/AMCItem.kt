package com.amc.amcapp.ui

import android.R.attr.clickable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.Status
import com.amc.amcapp.ui.theme.Dimens
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.ui.theme.Orange
import com.amc.amcapp.util.Avatar
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
    val dateText = remember(item.updatedAt) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        formatter.format(Instant.ofEpochMilli(item.updatedAt))
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
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = item.name,
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
                modifier = Modifier
                    .padding(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                StatusTag(status = item.status)
                Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
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
}

@Composable
private fun StatusTag(status: Status, modifier: Modifier = Modifier) {
    val (container, content, label) = when (status) {
        Status.PENDING -> Triple(
            MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface, "Pending"
        )

        Status.PROGRESS -> Triple(Orange, Color.White, "In Progress")
        Status.COMPLETED -> Triple(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, "Completed"
        )

        Status.CANCELLED -> Triple(
            MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError, "Cancelled"
        )
    }

    AssistChip(
        shape = RoundedCornerShape(16.dp),
        onClick = {}, label = {
            Text(
                text = label,
                fontSize = LocalDimens.current.tagTextSize.sp,
                color = content,
                maxLines = 1,
                modifier = Modifier.padding(LocalDimens.current.tagPadding.dp)
            )
        }, colors = AssistChipDefaults.assistChipColors(
            containerColor = container, labelColor = content
        ), modifier = Modifier
            .padding(LocalDimens.current.tagPadding.dp)
            .height(24.dp)
    )
}
