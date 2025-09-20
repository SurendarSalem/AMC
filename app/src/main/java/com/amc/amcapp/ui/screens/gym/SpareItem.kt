package com.amc.amcapp.ui.screens.gym

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.amc.amcapp.ComplaintUiItem
import com.amc.amcapp.Equipment
import com.amc.amcapp.EquipmentUiItem
import com.amc.amcapp.equipments.spares.SpareUiItem
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Avatar

@Composable
fun SpareItem(
    spareUiItem: SpareUiItem,
    onCheckedChanged: (Boolean) -> Unit,
    onSpareClicked: (spare: SpareUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSpareClicked(spareUiItem)
            }) {
        Text(spareUiItem.spare.name)
        Spacer(modifier = Modifier.weight(1F))
        Checkbox(checked = spareUiItem.isSelected, onCheckedChange = onCheckedChanged)
    }
}

@Composable
fun EquipmentItem(
    equipmentUiItem: EquipmentUiItem,
    onCheckedChanged: (Boolean) -> Unit,
    onEquipmentClicked: (EquipmentUiItem) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalDimens.current.spacingMedium.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEquipmentClicked(equipmentUiItem) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    name = equipmentUiItem.equipment.name,
                    imageUrl = equipmentUiItem.equipment.imageUrl
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = equipmentUiItem.equipment.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Checkbox(
                enabled = equipmentUiItem.enabled,
                checked = equipmentUiItem.isSelected,
                onCheckedChange = { checked -> onCheckedChanged(checked) })
        }
    }
}


@Composable
fun ComplaintItem(
    complaintUiItem: ComplaintUiItem,
    onCheckedChanged: (Boolean) -> Unit,
    onComplaintClicked: (spareUiItem: ComplaintUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onComplaintClicked(complaintUiItem)
            }) {
        Text(complaintUiItem.complaint.name)
        Spacer(modifier = Modifier.weight(1F))
        Checkbox(checked = complaintUiItem.isSelected, onCheckedChange = onCheckedChanged)
    }
}