package com.amc.amcapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class User(
    var firebaseId: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var name: String = "",
    var userType: UserType = UserType.CUSTOMER,
    var imageUrl: String = ""
) : Parcelable

@Parcelize
data class GymOwner(
  var address: String = ""
) : User(), Parcelable


enum class UserType(val label: String) {
    ADMIN("Admin"), GYM_OWNER("Gym Owner"), CUSTOMER("Customer"), TECHNICIAN("Technician"), SALES_PERSON(
        "Sales Person"
    )
}