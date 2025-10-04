package com.amc.amcapp.equipments.spares


import android.os.Parcelable
import com.amc.amcapp.model.Spare
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpareUiItem(
    val spare: Spare,
    var isSelected: Boolean = false,
    var requiredQuantity: Int = 0
) : Parcelable

fun Spare.toUiItem() = SpareUiItem(
    spare = this,
    isSelected = false
)

enum class SpareType {
    ELECTRONIC, PLATES, RODS, CABLES, OTHERS
}