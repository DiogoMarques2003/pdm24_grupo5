package com.kotlin.socialstore.ui.screens

import DonationItem
import ForgotPasswordPage
import SubmitDonationPage
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private val defaultPadding = 16.dp

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val modifierCustom: Modifier = modifier.padding(start = defaultPadding, end = defaultPadding)

    NavHost(navController = navController, startDestination = "home_screen") {
        composable("login_screen") {
            LoginPage(navController, modifierCustom)
        }
        composable("register_screen") {
            RegisterPage(navController, modifierCustom)
        }
        composable("donation_success") {
            DonationSuccessPage(navController, modifierCustom)
        }
        composable("submit_donation") {
            SubmitDonationPage(
                navController,
                modifierCustom,
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

    }
}