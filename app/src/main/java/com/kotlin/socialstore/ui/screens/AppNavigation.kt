package com.kotlin.socialstore.ui.screens

import DonationItem
import ForgotPasswordPage
import SubmitDonationPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_screen") {
        composable("login_screen") {
            LoginPage(navController, modifier)
        }
        composable("register_screen") {
            RegisterPage(navController, modifier)
        }
        composable("donation_success") {
            DonationSuccessPage(navController, modifier)
        }
        composable("submit_donation") {
            SubmitDonationPage(
                navController,
                modifier,
                onSubmitDonations = { items ->
                    
                }
            )
        }
       composable("forgot_password_screen") {
            ForgotPasswordPage(navController, modifier)
        }
        composable("home_screen") {
            HomePage(navController, modifier)
        }

    }
}