package com.amc.amcapp.di

import com.amc.amcapp.AuthRepository
import com.amc.amcapp.ComplaintRepository
import com.amc.amcapp.IComplaintRepository
import com.amc.amcapp.data.AmcPackageRepository
import com.amc.amcapp.data.AmcRepository
import com.amc.amcapp.data.IAmcPackageRepository
import com.amc.amcapp.data.IAmcRepository
import com.amc.amcapp.data.ISearchRepository
import com.amc.amcapp.data.ISparesRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.SearchRepository
import com.amc.amcapp.data.SparesRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.equipments.EquipmentsRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.gym.AddGymViewModel
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.gym.GymRepository
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.screens.amc.AddAmcViewModel
import com.amc.amcapp.ui.screens.amc.UserListViewModel
import com.amc.amcapp.ui.screens.service.AddServiceViewModel
import com.amc.amcapp.ui.screens.service.IServiceRepository
import com.amc.amcapp.ui.screens.service.ServiceRepository
import com.amc.amcapp.util.FirebaseHelper
import com.amc.amcapp.viewmodel.AMCListViewModel
import com.amc.amcapp.viewmodel.AddAmcPackageViewModel
import com.amc.amcapp.viewmodel.AddSpareViewModel
import com.amc.amcapp.viewmodel.AddUserViewModel
import com.amc.amcapp.viewmodel.AmcPackageListViewModel
import com.amc.amcapp.viewmodel.LoginViewModel
import com.amc.amcapp.viewmodel.ForgotPasswordViewModel
import com.amc.amcapp.viewmodel.LandingViewModel
import com.amc.amcapp.viewmodel.SearchViewModel
import com.amc.amcapp.viewmodel.SignUpViewModel
import com.amc.amcapp.viewmodel.SparesListViewModel
import com.amc.amcapp.viewmodel.SplashViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.get
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
    single { FirebaseHelper() }
    single<IAmcRepository> { AmcRepository(get(), get()) }
    single<IAmcPackageRepository> { AmcPackageRepository(get()) }
    single { PreferenceHelper(androidContext()) }
    single<IUserRepository> { UserRepository(androidContext(), get()) }
    single<IServiceRepository> { ServiceRepository() }
    viewModel { AddServiceViewModel(get()) }
    single { ComplaintRepository(get()) }
    single { Firebase.firestore }
    single<IComplaintRepository> { ComplaintRepository(get()) }
    single<IEquipmentsRepository> { EquipmentsRepository(get()) }
    single { GymRepository(get()) }
    viewModel { (user: User?) ->
        EquipmentsListViewModel(user, get(), get())
    }
    viewModel { AddGymViewModel(get()) }
    viewModel { AddEquipmentViewModel(get(), get()) }
    viewModel { AMCListViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { UserListViewModel(get(), get()) }
    viewModel { AddUserViewModel(get(), get(), get()) }
    viewModel { AddAmcViewModel(get(), get()) }

    /*AMC Package*/
    viewModel { AmcPackageListViewModel(get()) }
    viewModel { AddAmcPackageViewModel(get()) }

    /*Spares*/
    single<ISparesRepository> { SparesRepository(get()) }
    viewModel { SparesListViewModel(get()) }
    viewModel { AddSpareViewModel(get(), get()) }

    factory { (clazz: Class<*>) ->
        @Suppress("UNCHECKED_CAST")
        SearchRepository(
            firestore = get(),
            clazz = clazz
        ) as ISearchRepository<Any>
    }

    // ViewModel factory for any type
    viewModel { (clazz: Class<*>) ->
        @Suppress("UNCHECKED_CAST")
        (SearchViewModel<Any>(
            searchRepository = get { parametersOf(clazz) }
        ))
    }
}

