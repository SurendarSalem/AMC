package com.amc.amcapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import okhttp3.Address

@Parcelize
open class User(
    open var firebaseId: String = "",
    open var email: String = "",
    open var password: String = "",
    open var confirmPassword: String = "",
    open var name: String = "",
    open var userType: UserType = UserType.CUSTOMER,
    open var imageUrl: String = "",
    open var phoneNumber: String = "",
    open var address: String = ""
) : Parcelable


enum class UserType(val label: String) {
    ADMIN("Admin"), GYM_OWNER("Gym Owner"), CUSTOMER("Customer"), TECHNICIAN("Technician"), SALES_PERSON(
        "Sales Person"
    )
}