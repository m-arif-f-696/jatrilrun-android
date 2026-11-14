package com.example.jatrilrun.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jatrilrun.ui.screens.auth.LoginScreen
import com.example.jatrilrun.ui.screens.auth.RegisterScreen
import com.example.jatrilrun.ui.screens.home.HomeScreen
import com.example.jatrilrun.ui.screens.payment.PaymentScreen
import com.example.jatrilrun.ui.screens.auth.ParticipantScreen
import com.example.jatrilrun.ui.screens.auth.EmergencyContactScreen
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var registrationFlow by rememberSaveable { mutableStateOf(false) }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                selectedCategory = selectedCategory.takeIf { registrationFlow },
                onBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    if (registrationFlow) {
                        navController.navigate(Screen.Participant.route)
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    if (registrationFlow) {
                        navController.navigate(Screen.Participant.route)
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                onNavigateToLogin = { category ->
                    selectedCategory = category
                    registrationFlow = true
                    navController.navigate(Screen.Login.route) { launchSingleTop = true }
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Payment.route) {
            PaymentScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Participant.route) {
            ParticipantScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.EmergencyContact.route) }
            )
        }
        composable(Screen.EmergencyContact.route) {
            EmergencyContactScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.Payment.route) }
            )
        }
    }
}
