package com.example.jatrilrun.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Participant : Screen("participant")
    object EmergencyContact : Screen("emergency-contact")
    object Consent : Screen("consent")
    object Summary : Screen("summary")
    object Payment : Screen("payment")
    object PaymentSuccess : Screen("payment-success")
    object PaymentFailed : Screen("payment-failed")
    object RacePass : Screen("race-pass")
    object PaymentWaiting : Screen("payment-waiting") {
        const val MethodArg = "method"
        val pattern = "$route/{$MethodArg}"
        fun createRoute(methodName: String) = "$route/$methodName"
    }
}
