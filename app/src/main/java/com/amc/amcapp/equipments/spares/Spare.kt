package com.amc.amcapp.equipments.spares

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Spare(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val spareType: SpareType? = null
): Parcelable


@Parcelize
data class SpareUiItem(
    val spare: Spare,
    var isSelected: Boolean = false
): Parcelable

fun Spare.toUiItem() = SpareUiItem(
    spare = this,
    isSelected = false
)

enum class SpareType {
    ELECTRONIC, PLATES, RODS, CABLES, OTHERS
}