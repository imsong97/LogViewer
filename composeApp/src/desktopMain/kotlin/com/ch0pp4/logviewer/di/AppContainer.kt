package com.ch0pp4.logviewer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.ch0pp4.logviewer.LogViewModel
import com.logviewer.data.provider.LogFileProvider
import com.logviewer.data.provider.LogFileProviderImpl
import kotlin.reflect.KClass

class AppContainer {

    // data layer
    val logFileProvider: LogFileProvider = LogFileProviderImpl()

    // viewmodels
    fun getLogViewModel(viewModelStore: ViewModelStore): LogViewModel = VMProvider(
        store = viewModelStore,
        instance = LogViewModel(logFileProvider)
    )
}

@Suppress("UNCHECKED_CAST")
private fun <T : ViewModel> VMProvider(
    store: ViewModelStore,
    instance: ViewModel
): T {
    val owner = object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = store
    }
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            return if (modelClass.isInstance(instance)) {
                instance as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel: ${modelClass.qualifiedName}")
            }
        }
    }
    return ViewModelProvider.create(owner, factory)[(instance as T)::class]
}