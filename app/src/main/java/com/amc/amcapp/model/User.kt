package com.amc.amcapp.model

data class User(
    var firebaseId: String,
    val email: String,
    val password: String,
    val name: String,
    val userType: UserType
) {
    constructor() : this("", "", "", "", UserType.CUSTOMER) // Default constructor for Firebase
}


enum class UserType(val label: String) {
    ADMIN("Admin"),
    GYM_OWNER("Gym Owner"),
    CUSTOMER("Customer"),
    TECHNICIAN("Technician"),
    SALES_PERSON("Sales Person")
}