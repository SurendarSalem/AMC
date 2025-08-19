package com.amc.amcapp.model

data class AMC(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val gymId: String = "",
    val gymName: String = "",
    val status: Status = Status.PENDING,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val assignedId: String = "",
    val assignedName: String = "",
    val gymImage: String = "",
    val assigneeImage: String = ""
)

enum class Status {
    PROGRESS, PENDING, COMPLETED, CANCELLED
}