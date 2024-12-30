package com.kotlin.socialstore.ui.screens

import ForgotPasswordPage
import StockViewModel
import SubmitDonationPage
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlin.socialstore.viewModels.LoginViewModel
import com.kotlin.socialstore.viewModels.ProductsCatalogViewModel
import com.kotlin.socialstore.viewModels.RegisterViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val modifierCustom: Modifier = modifier.padding(start = UiConstants.defaultPadding, end = UiConstants.defaultPadding)

    NavHost(navController = navController, startDestination = "home_screen") {
        composable("login_screen") {
            // Initialize view model
            val loginViewModel = LoginViewModel(LocalContext.current)
            LoginPage(navController, modifierCustom, loginViewModel)
        }
        composable("register_screen") {
            // Initialize view model
            val registerViewModel = RegisterViewModel(LocalContext.current)
            RegisterPage(navController, modifierCustom, registerViewModel)
        }
        composable("donation_success") {
            DonationSuccessPage(navController, modifierCustom)
        }
        composable("submit_donation") {
            val stockViewModel = StockViewModel(LocalContext.current)

            SubmitDonationPage(
                navController,
                modifierCustom,
                stockViewModel,
                onSubmitDonations = { items ->

                }
            )
        }
        composable("forgot_password_screen") {
            ForgotPasswordPage(navController, modifierCustom)
        }
        composable("home_screen") {
            HomePage(navController, modifierCustom)
        }
        composable("products_screen") {
            val productsViewmodel = ProductsCatalogViewModel(LocalContext.current)
            ProductsCatalogPage(navController, modifierCustom, productsViewmodel)
        }
        composable("manage_stock") {
            val stockViewModel = StockViewModel(LocalContext.current)
            ManageStockPage(navController, modifierCustom, stockViewModel)
        }

    }
}