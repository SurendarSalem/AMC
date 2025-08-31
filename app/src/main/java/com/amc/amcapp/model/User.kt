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

@Parcelize
class GymOwner(
    override var firebaseId: String = "",
    override var email: String = "",
    override var password: String = "",
    override var confirmPassword: String = "",
    override var name: String = "",
    override var userType: UserType = UserType.GYM_OWNER,
    override var imageUrl: String = "",
    override var phoneNumber: String = "",
    override var address: String = ""

) : User(
    firebaseId, email, password, confirmPassword, name, userType, imageUrl, phoneNumber, address
)


enum class UserType(val label: String) {
    ADMIN("Admin"), GYM_OWNER("Gym Owner"), CUSTOMER("Customer"), TECHNICIAN("Technician"), SALES_PERSON(
        "Sales Person"
    )
}