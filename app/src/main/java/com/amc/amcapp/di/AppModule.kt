package com.amc.amcapp.di

import com.amc.amcapp.AuthRepository
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.equipments.EquipmentsRepository
import com.amc.amcapp.gym.AddGymViewModel
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.gym.GymRepository
import com.amc.amcapp.viewmodel.AuthViewModel
import com.amc.amcapp.viewmodel.SplashViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SplashViewModel() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { AuthRepository(get()) }
    single { Firebase.firestore }
    single { EquipmentsRepository(get()) }
    viewModel { AuthViewModel(get()) }
    single { GymRepository(get()) }
    viewModel { EquipmentsListViewModel(get()) }
    viewModel { AddGymViewModel(get()) }
    viewModel { AddEquipmentViewModel(get()) }
}
