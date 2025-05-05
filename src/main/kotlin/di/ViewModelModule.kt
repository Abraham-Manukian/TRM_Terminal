//package di
//
//import org.koin.dsl.module
//import ui.viewmodel.ConnectionViewModel
//import ui.viewmodel.MainMenuViewModel
//import ui.viewmodel.RequestViewModel
//import ui.viewmodel.SelectRegistersViewModel
//
///**
// * Модуль Koin для регистрации ViewModel компонентов
// */
//val viewModelModule = module {
//    // Регистрация ViewModel согласно стандарту ScreenModel
//    factory { ConnectionViewModel(get(), get(), get()) }
//
//    // Регистрация существующих ViewModel
//    factory { MainMenuViewModel() }
//    factory { RequestViewModel(get(), get(), get()) }
//    factory { SelectRegistersViewModel() }
//}