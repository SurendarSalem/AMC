package com.amc.amcapp.ui.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.Status
import com.amc.amcapp.ui.theme.Orange
import com.amc.amcapp.util.Avatar
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AMCItem(
    item: AMC,
    onClick: (() -> Unit)? = null,
) {
    val dateText = remember(item.updatedAt) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        formatter.format(
            java.time.Instant.ofEpochMilli(item.updatedAt)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top
        ) {
            Avatar(imageUrl = item.gymImage, name = item.assignedName)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                // Title + Status row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    StatusTag(status = item.status)
                }

                Spacer(Modifier.height(8.dp))

                // Meta row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Assignee: ${item.assignedName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "  •  Updated: $dateText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusTag(status: Status, modifier: Modifier = Modifier) {
    val (container, content) = when (status) {
        Status.PENDING -> Color.LightGray to Color.Black
        Status.PROGRESS -> Orange to Color.White
        Status.COMPLETED -> Color.Green to Color.White
        Status.CANCELLED -> Color.Red to Color.White
    }
    val label = when (status) {
        Status.PENDING -> "Pending"
        Status.PROGRESS -> "In Progress"
        Status.COMPLETED -> "Completed"
        Status.CANCELLED -> "Cancelled"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            maxLines = 1
        )
    }
}
