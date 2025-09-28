package com.amc.amcapp.model

import android.net.Uri
import android.os.Parcelable
import com.amc.amcapp.Equipment
import com.amc.amcapp.equipments.spares.Spare
import kotlinx.parcelize.Parcelize
import kotlin.String

@Parcelize
data class AMC(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val gymId: String = "",
    val gymName: String = "",
    val status: Status = Status.PENDING,
    val createdDate: Long = 0L,
    val createdTime: String = "",
    val updatedAt: Long = 0L,
    val assignedId: String = "",
    val assignedName: String = "",
    val gymImage: String = "",
    val assigneeImage: String = "",
    val equipments: List<Equipment> = emptyList(),
    var recordItems: List<RecordItem> = emptyList()
) : Parcelable

enum class Status {
    PENDING, PROGRESS, COMPLETED, CANCELLED
}

@Parcelize
data class RecordItem(
    var equipmentId: String = "",
    var equipmentName: String = "",
    var beforeImageUrl: String = "",
    var afterImageUrl: String = "",
    var beforeImageUri: String = "",
    var afterImageUri: String = "",
    var addedSpares: List<SpareDetails> = emptyList()
) : Parcelable

@Parcelize
data class RecordUiItem(
    val recordItem: RecordItem, var beforeImage: RecordImage, var afterImage: RecordImage
) : Parcelable

@Parcelize
data class RecordImage(
    val imageUrl: String = "",
    val imageUri: String = "",
    val shouldUseUrl: Boolean = false,
    val shouldUseUri: Boolean = false
) : Parcelable

fun RecordUiItem.toRecordItem() = RecordItem(
    equipmentId = recordItem.equipmentId,
    equipmentName = recordItem.equipmentName,
    beforeImageUrl = beforeImage.imageUrl,
    afterImageUrl = afterImage.imageUrl,
    beforeImageUri = beforeImage.imageUri,
    afterImageUri = afterImage.imageUri
)

@Parcelize
data class SpareDetails(
    val spareId: String, val spareName: String, val quantity: Int
) : Parcelable