package com.amc.amcapp

import com.google.firebase.database.Exclude
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
    val type: String,
    val imageUrl: String? = null,
    val description: String? = null,
    val availableComplaints: List<Complaint>,
    @Exclude val addedComplaints: List<Complaint>
) {
    constructor() : this(
        id = "",
        gymId = "",
        name = "",
        type = "",
        imageUrl = null,
        description = null,
        availableComplaints = emptyList(),
        addedComplaints = emptyList()
    )
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
    val id: String, val title: String, val description: String, val price: Double
)

data class Location(
    val latitude: Double, val longitude: Double
)
