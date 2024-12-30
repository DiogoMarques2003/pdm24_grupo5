package com.kotlin.socialstore.ui.screens

import ForgotPasswordPage
import SubmitDonationPage
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import com.kotlin.socialstore.ui.elements.AdminBottomNavigationBar
import com.kotlin.socialstore.ui.elements.BeneficiaryBottomNavigationBar
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.viewModels.AwaitingApprovalViewModel
import com.kotlin.socialstore.viewModels.LoginViewModel
import com.kotlin.socialstore.viewModels.ProfileViewModel
import com.kotlin.socialstore.viewModels.RegisterViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val modifierCustom: Modifier =
        modifier.padding(start = UiConstants.defaultPadding, end = UiConstants.defaultPadding)
    val context = LocalContext.current

    // Check what is the first sreen of user
    var startDestination by remember { mutableStateOf<String>("home_screen") }
    var isStartDestinationDetermined by remember { mutableStateOf(false) }
    var userType = remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        val firebaseUser = FirebaseObj.getCurrentUser()

        if (firebaseUser != null) {
            val database = AppDatabase.getDatabase(context)
            val usersRepository = UsersRepository(database.usersDao())
            val user = usersRepository.getById(firebaseUser.uid).firstOrNull()

            if (user != null) {
                userType.value = user.accountType
                startDestination = if (user.active) "main_screen" else "awaiting_approval_screen"
            } else {
                FirebaseObj.logoutAccount()
                startDestination = "home_screen"
            }
        }
        isStartDestinationDetermined = true
    }

    if (isStartDestinationDetermined) {
        Scaffold(
            bottomBar = {
                val currentRoute =
                    navController.currentBackStackEntryAsState()?.value?.destination?.route
                if ((userType.value == DataConstants.AccountType.benefiaryy || userType.value == DataConstants.AccountType.volunteer )  && currentRoute in listOf(
                        "main_screen",
                        "submit_donation"
                    )
                ) {
                    BeneficiaryBottomNavigationBar(navController)
                } else if (userType.value == DataConstants.AccountType.admin && currentRoute in listOf(
                        "main_screen",
                        "profile_page_screen"
                    )
                ) {
                    AdminBottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login_screen") {
                    // Initialize view model
                    val loginViewModel = LoginViewModel(LocalContext.current, navController, userType)
                    LoginPage(navController, modifierCustom, loginViewModel)
                }
                composable("register_screen") {
                    // Initialize view model
                    val registerViewModel = RegisterViewModel(LocalContext.current, navController)
                    RegisterPage(navController, modifierCustom, registerViewModel)
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

                composable("awaiting_approval_screen") {
                    val awaitingApprovalViewModel =
                        AwaitingApprovalViewModel(LocalContext.current, navController)
                    AwaitingApprovalScreen(modifierCustom, awaitingApprovalViewModel)
                }

                composable("main_screen") {
                    MainScreen(navController, modifierCustom)
                }

                composable("profile_page_screen") {
                    val profileViewModel = ProfileViewModel(LocalContext.current)
                    ProfileScreen(navController, modifierCustom, profileViewModel)
                }

            }
        }
    } else {
        LoadIndicator(modifier)
    }
}

