package com.amc.amcapp

import android.os.Parcelable
import com.amc.amcapp.equipments.spares.Spare
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.sql.Date

data class Gym(
    var id: String,
    val ownerId: String,
    val ownerName: String,
    val name: String = "Sai Gym",
    val address: String,
    val location: Location,
    val phoneNumber: String,
    val description: String,
    val email: String,
    val imageUrl: String,
    @Exclude val services: List<Service>,
    @Exclude val equipments: List<Equipment>
) {
    constructor() : this(
        id = "",
        ownerId = "",
        ownerName = "",
        name = "Sai Gym",
        address = "",
        location = Location(0.0, 0.0),
        phoneNumber = "",
        description = "",
        email = "",
        imageUrl = "",
        services = emptyList(),
        equipments = emptyList()
    )
}

@Parcelize
data class Equipment(
    val id: String,
    val gymId: String,
    val name: String,
    var imageUrl: String = "",
    val description: String = "",
    val equipmentType: EquipmentType?,
    val complaints: List<Complaint> = emptyList(),
    val spares: List<Spare> = emptyList()
) : Parcelable {
    constructor() : this(
        id = "",
        gymId = "",
        name = "",
        imageUrl = "",
        description = "",
        equipmentType = null,
        complaints = emptyList(),
        spares = emptyList()
    )
}

enum class EquipmentType(val label: String) {
    CARDIO("Cardio"), CHEST("Chest"), DUMBBELL("Dumbbell")
}

data class Service(
    val id: String = "",
    val gymId: String = "",
    val createdDate: Date = Date(System.currentTimeMillis()),
    val updatedDate: Date = Date(System.currentTimeMillis()),
    val name: String = "",
    val description: String = "",
    val equipments: List<String> = emptyList(),
    val imageUrls: List<ImageUrls> = emptyList(),
    val total: Double = 0.0,
)

data class ImageUrls(
    val equipmentId: String, val urls: List<String>
)

data class Complaints(
    val equipmentId: String, val complaints: List<String>
)


data class ComplaintUiItem(
    val complaint: Complaint,
    var isSelected: Boolean = false
)

@Parcelize
data class Complaint(
    val id: String, var name: String = "", val description: String, val price: Double
) : Parcelable {
    constructor() : this(
        id = "", name = "Surendar", description = "", price = 0.0
    )
}

fun Complaint.toUiItem() = ComplaintUiItem(
    complaint = this,
    isSelected = false
)

data class Location(
    val latitude: Double, val longitude: Double
)
