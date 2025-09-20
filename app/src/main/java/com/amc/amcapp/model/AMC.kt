package com.amc.amcapp.model

import android.os.Parcelable
import com.amc.amcapp.Equipment
import kotlinx.parcelize.Parcelize

@Parcelize
data class AMC(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val gymId: String = "",
    val gymName: String = "",
    val status: Status = Status.PENDING,
    val createdDate: Long = 0L,
    val createdTime: String = "",
    val updatedAt: Long = 0L,
    val assignedId: String = "",
    val assignedName: String = "",
    val gymImage: String = "",
    val assigneeImage: String = "",
    val equipments: List<Equipment> = emptyList()
) : Parcelable

enum class Status {
    PROGRESS, PENDING, COMPLETED, CANCELLED
}