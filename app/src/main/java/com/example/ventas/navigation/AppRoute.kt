package com.example.ventas.navigation

sealed class AppRoute(val route: String) {
    data object Onboarding : AppRoute("onboarding")
    data object Home : AppRoute("home")

    // CRUD Productos
    data object ProductList : AppRoute("product_list")
    data object ProductNew : AppRoute("product_new")
    data class ProductEdit(val id: Long) : AppRoute("product_edit/{id}") {
        companion object { fun path(id: Long) = "product_edit/$id" }
    }

    // CRUD Clientes
    data object ClientList : AppRoute("client_list")
    data object ClientNew : AppRoute("client_new")
    data class ClientEdit(val id: Long) : AppRoute("client_edit/{id}") {
        companion object { fun path(id: Long) = "client_edit/$id" }
    }
    // Ventas
    data object SaleList : AppRoute("sale_list")
    data object SaleNew : AppRoute("sale_new")
    data class SaleDetailRoute(val id: Long) : AppRoute("sale_detail/{id}") {
        companion object { fun path(id: Long) = "sale_detail/$id" }
    }
}