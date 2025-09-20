package com.amc.amcapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.amc.amcapp.equipments.spares.Spare

sealed class ListType(
    val table: String,
    val clazz: Class<*>,
    val itemContent: @Composable (Any) -> Unit
) {
    object Spares : ListType(
        table = "spares",
        clazz = Spare::class.java,
        itemContent = { item ->
            Text((item as Spare).name) // cast inside lambda
        }
    )
}
enum class ListTypeKey { SPARES, AMCS, COMPLAINTS, USERS, EQUIPMENTS }
