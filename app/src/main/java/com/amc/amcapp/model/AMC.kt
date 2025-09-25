package com.amc.amcapp.model

import android.net.Uri
import android.os.Parcelable
import com.amc.amcapp.Equipment
import com.amc.amcapp.equipments.spares.Spare
import kotlinx.parcelize.Parcelize

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
    val recordItems: List<RecordItem> = emptyList()
) : Parcelable

enum class Status {
    PENDING, PROGRESS, COMPLETED, CANCELLED
}

@Parcelize
data class RecordItem(
    val equipmentId: String,
    val equipmentName: String,
    val beforeImageUrl: String,
    val afterImageUrl: String,
    val allocatesSpares: List<Spare> = emptyList(),
    val requireSpares: List<SpareDetails> = emptyList()
) : Parcelable

@Parcelize
data class RecordUiItem(
    val recordItem: RecordItem, var beforeImage: RecordImage, var afterImage: RecordImage
) : Parcelable

@Parcelize
data class RecordImage(
    val imageUrl: String? = null,
    val imageUri: Uri? = null,
    val shouldUseUrl: Boolean = false,
    val shouldUseUri: Boolean = false
) : Parcelable

fun RecordItem.toRecordUiItem() = RecordUiItem(
    recordItem = this,
    beforeImage = RecordImage(beforeImageUrl),
    afterImage = RecordImage(afterImageUrl)
)


@Parcelize
data class SpareDetails(
    val spareId: String, val spareName: String, val quantity: Int
) : Parcelable