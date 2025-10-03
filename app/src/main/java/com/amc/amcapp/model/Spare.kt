package com.amc.amcapp.model

import android.net.Uri
import android.os.Parcelable
import com.amc.amcapp.equipments.spares.SpareType
import kotlinx.parcelize.Parcelize


@Parcelize
data class Spare(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stockQuantity: Int = 0,
    val imageUrl: String = "",
    val spareType: SpareType? = null
) : Parcelable


@Parcelize
data class SpareUiState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stockQuantity: Int = 0,
    var imageUrl: String = "",
    var imageUri: Uri? = null,
    val shouldUseUri: Boolean = false,
    val shouldUseUrl: Boolean = false
) : Parcelable

fun SpareUiState.toSpare(): Spare {
    return Spare(
        id = id,
        name = name,
        description = description,
        price = price,
        stockQuantity = stockQuantity,
        imageUrl = imageUrl
    )
}