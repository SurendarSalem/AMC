package com.amc.amcapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AmcPackage(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val duration: Int = 0,
) : Parcelable