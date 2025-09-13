package com.amc.amcapp.ui.screens.gym

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amc.amcapp.ComplaintUiItem
import com.amc.amcapp.equipments.spares.SpareUiItem

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