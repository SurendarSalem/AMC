package com.amc.amcapp.model

import android.net.Uri
import android.os.Parcelable
import com.amc.amcapp.Equipment
import com.google.firebase.firestore.Exclude
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
    val equipmentIds: List<String> = emptyList(),
    @get:Exclude var equipments: List<Equipment> = emptyList(),
    var recordItems: List<RecordItem> = emptyList(),
    var amcPackageDetails: AmcPackageDetails? = null
) : Parcelable

enum class Status(val label: String) {
    PENDING("Pending"), PROGRESS("Progress"), APPROVED("Approved"), COMPLETED("Completed"), CANCELLED(
        "Cancelled"
    )
}

@Parcelize
data class AmcPackageDetails(
    var id: String = "",
    var amcPackageName: String = "",
    var price: Double = 0.0,
) : Parcelable

@Parcelize
data class RecordItem(
    var equipmentId: String = "",
    var equipmentName: String = "",
    var beforeImageUrl: String = "",
    var afterImageUrl: String = "",
    var beforeImageUri: String = "",
    var afterImageUri: String = "",
    var addedSpares: List<SpareDetails> = emptyList(),
) : Parcelable

@Parcelize
data class RecordUiItem(
    val recordItem: RecordItem,
    var beforeImage: RecordImage,
    var afterImage: RecordImage,
    var addedSpares: List<SpareDetails> = emptyList(),
    var totalSpares: List<Spare> = emptyList(),
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
    afterImageUri = afterImage.imageUri,
    addedSpares = addedSpares
)

@Parcelize
data class SpareDetails(
    val spareId: String = "", val spareName: String = "", val quantity: Int = 0
) : Parcelable {}