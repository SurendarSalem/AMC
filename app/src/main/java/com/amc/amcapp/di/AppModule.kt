package com.amc.amcapp.di

import com.amc.amcapp.AuthRepository
import com.amc.amcapp.viewmodel.AuthViewModel
import com.amc.amcapp.viewmodel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { SplashViewModel() }
}
