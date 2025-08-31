package com.amc.amcapp.di

import com.amc.amcapp.AuthRepository
import com.amc.amcapp.ComplaintRepository
import com.amc.amcapp.data.AmcRepository
import com.amc.amcapp.data.IAmcRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.equipments.EquipmentsRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.gym.AddGymViewModel
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.gym.GymRepository
import com.amc.amcapp.ui.screens.amc.UserListViewModel
import com.amc.amcapp.viewmodel.AMCListViewModel
import com.amc.amcapp.viewmodel.AddUserViewModel
import com.amc.amcapp.viewmodel.LoginViewModel
import com.amc.amcapp.viewmodel.ForgotPasswordViewModel
import com.amc.amcapp.viewmodel.LandingViewModel
import com.amc.amcapp.viewmodel.SignUpViewModel
import com.amc.amcapp.viewmodel.SplashViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SplashViewModel(get(), get()) }

    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }
    viewModel {
        LoginViewModel(
            authRepository = get(), preferenceHelper = get(), userRepository = get()
        )
    }
    viewModel {
        SignUpViewModel(
            authRepository = get(), preferenceHelper = get(), userRepository = get()
        )
    }
    viewModel {
        LandingViewModel(
            authRepository = get(), preferenceHelper = get(), userRepository = get()
        )
    }

    single<IAmcRepository> { AmcRepository(get()) }
    single { PreferenceHelper(androidContext()) }
    single<IUserRepository> { UserRepository(androidContext(), get()) }
    single { ComplaintRepository(get()) }
    single { Firebase.firestore }
    single<IEquipmentsRepository> { EquipmentsRepository(get()) }
    single { GymRepository(get()) }
    viewModel { EquipmentsListViewModel(get()) }
    viewModel { AddGymViewModel(get()) }
    viewModel { AddEquipmentViewModel(get()) }
    viewModel { AMCListViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { UserListViewModel(get()) }
    viewModel { AddUserViewModel(get(), get()) }

}
