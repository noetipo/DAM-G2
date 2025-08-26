// com/example/ventas/ui/sale/RememberSaleVM.kt
package com.example.ventas.ui.sale

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun rememberSaleVM(): SaleViewModel {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as Application
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SaleViewModel(app) as T
        }
    })
}
