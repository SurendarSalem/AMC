package com.amc.amcapp

import com.google.firebase.firestore.Exclude
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

data class Equipment(
    val id: String,
    val gymId: String,
    val name: String,
    var imageUrl: String = "",
    val description: String = "",
    val equipmentType: EquipmentType?,
    val addedComplaints: List<Complaint> = emptyList()
) {
    constructor() : this(
        id = "",
        gymId = "",
        name = "",
        imageUrl = "",
        description = "",
        equipmentType = null,
        addedComplaints = emptyList()
    )
}

enum class EquipmentType(val label: String) {
    CARDIO("Cardio"), CHEST("Chest"), DUMBBELL("Dumbbell")
}

data class Service(
    val id: String,
    val gymId: String,
    val date: Date,
    val name: String,
    val description: String,
    val equipments: List<Equipment>,
    val total: Double,
)

data class Complaint(
    val id: String, var name: String = "Surendar", val description: String, val price: Double
) {
    constructor() : this(
        id = "", name = "Surendar", description = "", price = 0.0
    )
}

data class Location(
    val latitude: Double, val longitude: Double
)
