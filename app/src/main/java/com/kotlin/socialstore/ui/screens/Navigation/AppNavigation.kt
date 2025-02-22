package com.kotlin.socialstore.ui.screens.Navigation

import CheckOutViewModel
import DashboardPage
import DashboardViewModel
import com.kotlin.socialstore.viewModels.Donations.DonationViewModel
import ForgotPasswordPage
import android.os.Build
import ManageUsersPage
import ManageUsersViewModel
import com.kotlin.socialstore.viewModels.Products.StockViewModel
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
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
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import com.kotlin.socialstore.ui.elements.navigationBar.AdminBottomNavigationBar
import com.kotlin.socialstore.ui.elements.navigationBar.BeneficiaryBottomNavigationBar
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.screens.Donations.DonationDetailsScreen
import com.kotlin.socialstore.ui.screens.AwaitingApprovalScreen
import com.kotlin.socialstore.ui.screens.Donations.DonationSuccessPage
import com.kotlin.socialstore.ui.screens.Donations.InsertItemsDonationScreen
import com.kotlin.socialstore.ui.screens.Donations.ListDonationsScreen
import com.kotlin.socialstore.ui.screens.Donations.SubmitDonationPage
import com.kotlin.socialstore.ui.screens.Users.EditProfileScreen
import com.kotlin.socialstore.ui.screens.HomePage
import com.kotlin.socialstore.ui.screens.LoginPage
import com.kotlin.socialstore.ui.screens.MainScreen
import com.kotlin.socialstore.ui.screens.ManageHousehold
import com.kotlin.socialstore.ui.screens.Products.CheckOutScreen
import com.kotlin.socialstore.ui.screens.Products.ManageStockPage
import com.kotlin.socialstore.ui.screens.Products.ProductsCatalogPage
import com.kotlin.socialstore.ui.screens.ProfileScreen
import com.kotlin.socialstore.ui.screens.QRCodeReaderScreen
import com.kotlin.socialstore.ui.screens.RegisterPage
import com.kotlin.socialstore.ui.screens.Users.EditUserAsAdminScreen
import com.kotlin.socialstore.ui.screens.SchedulePage
import com.kotlin.socialstore.ui.screens.SettingsScreen
import com.kotlin.socialstore.ui.screens.Users.CheckInScreen
import com.kotlin.socialstore.viewModels.AwaitingApprovalViewModel
import com.kotlin.socialstore.viewModels.CheckInViewModel
import com.kotlin.socialstore.viewModels.Donations.DonationDetailsViewModel
import com.kotlin.socialstore.viewModels.Donations.InsertItemsDonationViewModel
import com.kotlin.socialstore.viewModels.Donations.ListDonationsViewModel
import com.kotlin.socialstore.viewModels.LoginViewModel
import com.kotlin.socialstore.viewModels.MainPageViewModel
import com.kotlin.socialstore.viewModels.ManageHouseholdViewModel
import com.kotlin.socialstore.viewModels.Products.ProductsCatalogViewModel
import com.kotlin.socialstore.viewModels.ProfileViewModel
import com.kotlin.socialstore.viewModels.RegisterViewModel
import com.kotlin.socialstore.viewModels.ScheduleViewModel
import kotlinx.coroutines.flow.firstOrNull

//@RequiresApi(Build.VERSION_CODES.O)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalGetImage::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
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
    Scaffold(
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState()?.value?.destination?.route
            if ((userType.value == DataConstants.AccountType.benefiaryy || userType.value == DataConstants.AccountType.volunteer) && currentRoute in listOf(
                    "main_screen",
                    "list_donations_screen"
                )
            ) {
                BeneficiaryBottomNavigationBar(navController)
            } else if (userType.value == DataConstants.AccountType.admin && currentRoute in listOf(
                    "main_screen",
                    "profile_page_screen",
                    "list_donations_screen"
                )
            ) {
                AdminBottomNavigationBar(navController)
            }

        }
    ) { innerPadding ->
        val modifierCustom = Modifier
            .padding(innerPadding)
            .padding(start = UiConstants.defaultPadding, end = UiConstants.defaultPadding)

        if (isStartDestinationDetermined) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("login_screen") {
                    // Initialize view model
                    val loginViewModel =
                        LoginViewModel(LocalContext.current, navController, userType)
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
                    val donationViewModel = DonationViewModel(LocalContext.current, navController)
                    SubmitDonationPage(navController, modifierCustom, donationViewModel)
                }

                composable("dashboard_screen") {
                    val dashboardViewModel = DashboardViewModel(LocalContext.current, navController)
                    DashboardPage(navController, modifierCustom, dashboardViewModel)
                }

                composable("awaiting_approval_screen") {
                    val awaitingApprovalViewModel =
                        AwaitingApprovalViewModel(LocalContext.current, navController)
                    AwaitingApprovalScreen(modifierCustom, awaitingApprovalViewModel)
                }

                composable("main_screen") {
                    val mainPageViewModel = MainPageViewModel(LocalContext.current)
                    MainScreen(navController, modifierCustom, mainPageViewModel)
                }

                composable("manage_users") {
                    val manageUsersViewModel =
                        ManageUsersViewModel(LocalContext.current, navController)
                    ManageUsersPage(navController, modifierCustom, manageUsersViewModel)
                }

                composable("profile_page_screen") {
                    val profileViewModel = ProfileViewModel(LocalContext.current)
                    ProfileScreen(navController, modifierCustom, profileViewModel)
                }

                composable("settings_screen"){
                    SettingsScreen(modifierCustom, navController)
                }

                composable("edit_profile_screen") {
                    val profileViewModel = ProfileViewModel(context)
                    EditProfileScreen(navController, modifierCustom, profileViewModel)
                }
                composable("edit_profile_as_admin_screen/{userID}") { backstageEntry ->
                    val userID = backstageEntry.arguments?.getString("userID")

                    if (userID == null) {
                        LaunchedEffect(Unit) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.user_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    } else {
                        val profileViewModel = ProfileViewModel(context, userID)
                        EditUserAsAdminScreen(navController, modifierCustom, profileViewModel)
                    }
                }

                composable("qrcode_reader_screen/{next_screen}") { backStackEntry ->
                    val nextScreen = backStackEntry.arguments?.getString("next_screen")
                    QRCodeReaderScreen(modifierCustom, nextScreen ?: "home_screen", navController)
                }
                composable("forgot_password_screen") {
                    ForgotPasswordPage(navController, modifierCustom)
                }

                composable("home_screen") {
                    HomePage(navController, modifierCustom)
                }

                composable("checkout/{userID}") { backstageEntry ->
                    val userID = backstageEntry.arguments?.getString("userID")

                    if (userID == null) {
                        LaunchedEffect(Unit) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.user_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    } else {
                        val checkOutViewModel = CheckOutViewModel(context, userID)
                        CheckOutScreen(navController, modifierCustom, checkOutViewModel)
                    }
                }

                composable("manage_stock") {
                    val stockViewModel = StockViewModel(LocalContext.current)
                    ManageStockPage(navController, modifierCustom, stockViewModel)
                }

                composable("products_screen") {
                    val productsViewmodel = ProductsCatalogViewModel(LocalContext.current)
                    ProductsCatalogPage(navController, modifierCustom, productsViewmodel)
                }

                composable("list_donations_screen") {
                    val listDonationsViewModel = ListDonationsViewModel(LocalContext.current)
                    ListDonationsScreen(navController, modifierCustom, listDonationsViewModel)

                }

                composable("manage_household") {
                    val manageHouseholdViewModel = ManageHouseholdViewModel(LocalContext.current)
                    ManageHousehold(navController, manageHouseholdViewModel)
                }

                composable("donation_screen/{donationId}") { backstageEntry ->
                    val donationId = backstageEntry.arguments?.getString("donationId")

                    if (donationId == null) {
                        LaunchedEffect(Unit) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.donation_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    } else {
                        val donationDetailsViewModel = DonationDetailsViewModel(context, donationId)
                        DonationDetailsScreen(
                            navController,
                            modifierCustom,
                            donationDetailsViewModel
                        )
                    }
                }
                composable("schedule_screen") {
                    val scheduleViewModel = ScheduleViewModel(context)
                    SchedulePage(modifierCustom, navController, scheduleViewModel)
                }

                composable("donation_insert_product/{donationId}") { backstageEntry ->
                    val donationId = backstageEntry.arguments?.getString("donationId")

                    if (donationId == null) {
                        LaunchedEffect(Unit) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.donation_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    } else {
                        val insertItemsDonationViewModel =
                            InsertItemsDonationViewModel(context, navController, donationId)
                        InsertItemsDonationScreen(
                            navController,
                            modifierCustom,
                            insertItemsDonationViewModel
                        )
                    }
                }

                composable("check_in/{userId}") { backstageEntry ->
                    val userId = backstageEntry.arguments?.getString("userId")

                    if (userId == null) {
                        LaunchedEffect(Unit) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.user_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    } else {
                        val checkInViewModel = CheckInViewModel(context, userId)
                        CheckInScreen(navController, modifierCustom, checkInViewModel)
                    }
                }
            }
        } else {
            LoadIndicator(modifierCustom)
        }
    }
}
