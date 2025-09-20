package com.amc.amcapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.equipments.spares.Spare
import com.amc.amcapp.equipments.spares.toUiItem
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.toUiItem
import com.amc.amcapp.ui.SearchListScreen
import com.amc.amcapp.ui.screens.amc.UserItem
import com.amc.amcapp.ui.screens.gym.ComplaintItem
import com.amc.amcapp.ui.screens.gym.EquipmentItem
import com.amc.amcapp.ui.screens.gym.SpareItem
import com.amc.amcapp.util.Constants
import com.amc.amcapp.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ListItemScreen(navController: NavHostController, onTitleUpdated: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val key =
        navController.previousBackStackEntry?.savedStateHandle?.get<ListTypeKey>("listTypeKey")
            ?: return

    val (table, clazz, filterBy) = when (key) {
        ListTypeKey.SPARES -> Triple("spares", Spare::class.java, Pair("", ""))
        ListTypeKey.COMPLAINTS -> Triple("complaints", Complaint::class.java, Pair("", ""))
        ListTypeKey.USERS -> Triple(
            "users", User::class.java, Pair("userType", UserType.TECHNICIAN.name)
        )

        ListTypeKey.AMCS -> Triple("amc", AMC::class.java, Pair("", ""))
        ListTypeKey.EQUIPMENTS -> Triple("equipments", Equipment::class.java, Pair("", ""))
    }
    val vm: SearchViewModel<Any> = koinViewModel { parametersOf(clazz) }

    LaunchedEffect(Unit) { vm.fetchAll(table, filterBy) }
    val handle = navController.previousBackStackEntry?.savedStateHandle

    SearchListScreen(
        searchViewModel = vm, listItem = { item ->
            when (key) {
                ListTypeKey.SPARES -> {
                    onTitleUpdated("Select Spares")
                    var selectedSpares = handle?.get<List<Spare>>("selectedSpares") ?: emptyList()

                    var spareUiItems by remember(item) {
                        mutableStateOf((item as Spare).toUiItem().apply {
                            isSelected = selectedSpares.contains(item)
                        })
                    }
                    SpareItem(spareUiItem = spareUiItems, onCheckedChanged = { isChecked ->
                        scope.launch {
                            selectedSpares =
                                handle?.get<List<Spare>>("selectedSpares") ?: emptyList()
                            spareUiItems = spareUiItems.copy(isSelected = isChecked)

                            if (isChecked) {
                                val updatedList = selectedSpares + item
                                handle?.set("selectedSpares", updatedList)
                            } else {
                                val updatedList = selectedSpares - item
                                handle?.set("selectedSpares", updatedList)
                            }
                        }
                    }, onSpareClicked = { spare ->

                    })
                }

                ListTypeKey.COMPLAINTS -> {
                    onTitleUpdated("Select Complaints")
                    var selectedComplaints =
                        handle?.get<List<Complaint>>("selectedComplaints") ?: emptyList()

                    var complaintsUiItem by remember {
                        mutableStateOf((item as Complaint).toUiItem().apply {
                            isSelected = selectedComplaints.contains(item)
                        })
                    }
                    ComplaintItem(
                        complaintUiItem = complaintsUiItem,
                        onCheckedChanged = { isChecked ->
                            scope.launch {
                                selectedComplaints =
                                    handle?.get<List<Complaint>>("selectedComplaints")
                                        ?: emptyList()
                                complaintsUiItem = complaintsUiItem.copy(isSelected = isChecked)

                                if (isChecked) {
                                    val updatedList = selectedComplaints + item
                                    handle?.set("selectedComplaints", updatedList)
                                } else {
                                    val updatedList = selectedComplaints - item
                                    handle?.set("selectedComplaints", updatedList)
                                }
                            }
                        },
                        onComplaintClicked = { complaintItem ->

                        })
                }

                ListTypeKey.USERS -> {
                    onTitleUpdated("Select Technician")
                    UserItem(item as User) { user ->
                        handle?.set("selectedTechnician", user)
                        navController.popBackStack()
                    }
                }

                ListTypeKey.EQUIPMENTS -> {
                    onTitleUpdated("Select Equipments")
                    var selectedEquipments = handle?.get<List<Equipment>>(Constants.SELECTED_EQUIPMENTS) ?: emptyList()

                    var equipmentUiItem by remember {
                        mutableStateOf((item as Equipment).toUiItem().apply {
                            isSelected = selectedEquipments.contains(item)
                        })
                    }
                    EquipmentItem(
                        equipmentUiItem = equipmentUiItem,
                        onCheckedChanged = { isChecked ->
                            scope.launch {
                                selectedEquipments =
                                    handle?.get<List<Equipment>>(Constants.SELECTED_EQUIPMENTS)
                                        ?: emptyList()
                                equipmentUiItem = equipmentUiItem.copy(isSelected = isChecked)

                                if (isChecked) {
                                    val updatedList = selectedEquipments + item
                                    handle?.set(Constants.SELECTED_EQUIPMENTS, updatedList)
                                } else {
                                    val updatedList = selectedEquipments - item
                                    handle?.set(Constants.SELECTED_EQUIPMENTS, updatedList)
                                }
                            }
                        },
                        onEquipmentClicked = { spare ->

                        })
                }

                ListTypeKey.AMCS -> TODO()
            }
        }, errorMessage = "No items found"
    )
}