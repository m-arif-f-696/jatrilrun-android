package com.example.jatrilrun.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jatrilrun.ui.screens.auth.LoginScreen
import com.example.jatrilrun.ui.screens.auth.RegisterScreen
import com.example.jatrilrun.ui.screens.home.HomeScreen
import com.example.jatrilrun.ui.screens.payment.PaymentScreen
import com.example.jatrilrun.ui.screens.payment.LivePaymentScreen
import com.example.jatrilrun.ui.screens.auth.ParticipantScreen
import com.example.jatrilrun.ui.screens.auth.EmergencyContactScreen
import com.example.jatrilrun.ui.screens.checkout.ConsentScreen
import com.example.jatrilrun.ui.screens.checkout.SummaryScreen
import com.example.jatrilrun.ui.screens.payment.PaymentMethod
import com.example.jatrilrun.ui.screens.payment.PaymentWaitingScreen
import com.example.jatrilrun.ui.screens.payment.PaymentFailedScreen
import com.example.jatrilrun.ui.screens.payment.PaymentSuccessScreen
import com.example.jatrilrun.ui.screens.racepass.RacePassScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jatrilrun.ui.screens.checkout.RegistrationExitGuard
import com.example.jatrilrun.ui.screens.checkout.RegistrationViewModel
import com.example.jatrilrun.ui.screens.checkout.toCheckoutSummary
import com.example.jatrilrun.ui.screens.racepass.RacePass

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var registrationFlow by rememberSaveable { mutableStateOf(false) }

    // Satu draft pendaftaran untuk seluruh alur (kategori -> ... -> pembayaran).
    val registration: RegistrationViewModel = hiltViewModel()
    val draft by registration.draft.collectAsState()
    val tickets by registration.tickets.collectAsState()
    val liveSubmitState by registration.submitState.collectAsState()
    val summary = draft.toCheckoutSummary()

    fun leaveRegistration() {
        registrationFlow = false
        navController.goHome()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                selectedCategory = selectedCategory.takeIf { registrationFlow },
                allowDemo = draft.serverEventId.isBlank(),
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
                allowDemo = draft.serverEventId.isBlank(),
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
                    registration.selectCategory(category)
                    registrationFlow = true
                    navController.navigate(Screen.Login.route) { launchSingleTop = true }
                },
                onNavigateToServerRegistration = { event, category ->
                    registration.selectServerCategory(event, category)
                    selectedCategory = category.type?.name ?: "Kategori"
                    registrationFlow = true
                    navController.navigate(Screen.Login.route) { launchSingleTop = true }
                },
                onOpenRacePass = { navController.navigate(Screen.RacePass.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Consent.route) {
            ConsentScreen(
                onBack = { navController.popBackStack() },
                onContinue = {
                    registration.acceptConsent()
                    navController.navigate(Screen.Summary.route)
                }
            )
        }
        composable(Screen.Summary.route) {
            SummaryScreen(
                summary = summary,
                isLive = draft.serverEventId.isNotBlank(),
                liveState = liveSubmitState,
                onBack = { navController.popBackStack() },
                onEditParticipant = {
                    // Kembali ke layar peserta yang masih ada di back stack supaya isian tidak hilang.
                    if (!navController.popBackStack(Screen.Participant.route, inclusive = false)) {
                        navController.navigate(Screen.Participant.route) { launchSingleTop = true }
                    }
                },
                onContinue = {
                    if (draft.serverEventId.isNotBlank()) {
                        registration.submitLiveRegistration { id ->
                            navController.navigate(Screen.LivePayment.createRoute(id))
                        }
                    } else {
                        navController.navigate(Screen.Payment.route)
                    }
                }
            )
        }
        composable(
            route = Screen.LivePayment.pattern,
            arguments = listOf(navArgument(Screen.LivePayment.RegistrationArg) { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString(Screen.LivePayment.RegistrationArg).orEmpty()
            LivePaymentScreen(
                registrationId = id,
                onBack = { navController.popBackStack() },
                onHome = { leaveRegistration() }
            )
        }
        composable(Screen.Payment.route) {
            PaymentScreen(
                summary = summary,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate(Screen.PaymentWaiting.createRoute(method.name))
                }
            )
        }
        composable(
            route = Screen.PaymentWaiting.pattern,
            arguments = listOf(navArgument(Screen.PaymentWaiting.MethodArg) { type = NavType.StringType })
        ) { entry ->
            val method = PaymentMethod.entries.firstOrNull {
                it.name == entry.arguments?.getString(Screen.PaymentWaiting.MethodArg)
            } ?: PaymentMethod.BCA
            PaymentWaitingScreen(
                method = method,
                summary = summary,
                onBack = { navController.popBackStack() },
                onChangeMethod = { navController.popBackStack() },
                // DEMO: sementara selalu "berhasil". TODO: cek status ke backend, lalu ke Success / Failed.
                onCheckStatus = {
                    registration.markPaidDemo() // DEMO: hapus saat status asli dari backend
                    navController.navigate(Screen.PaymentSuccess.route) {
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.PaymentSuccess.route) {
            // Back sistem dari layar sukses ke Beranda, bukan ke Ringkasan yang sudah basi.
            BackHandler { leaveRegistration() }
            PaymentSuccessScreen(
                summary = summary,
                onBack = { leaveRegistration() },
                onViewRacePass = { navController.navigate(Screen.RacePass.route) }
            )
        }
        composable(Screen.PaymentFailed.route) {
            PaymentFailedScreen(
                summary = summary,
                onBack = { navController.popBackStack() },
                onTryOtherMethod = {
                    if (!navController.popBackStack(Screen.Payment.route, inclusive = false)) {
                        navController.navigate(Screen.Payment.route)
                    }
                },
                onCheckStatus = {
                    registration.markPaidDemo() // DEMO: hapus saat status asli dari backend
                    navController.navigate(Screen.PaymentSuccess.route) { launchSingleTop = true }
                }
            )
        }
        composable(Screen.RacePass.route) {
            RacePassScreen(
                pass = tickets.lastOrNull() ?: RacePass.Sample,
                onBack = { navController.popBackStack() },
                onBackToHome = { leaveRegistration() }
            )
        }
        composable(Screen.Participant.route) {
            // Langkah pertama: back membuka bottom sheet "Tinggalkan pendaftaran?" (Figma 07).
            RegistrationExitGuard(onExit = { leaveRegistration() }) { requestExit ->
                ParticipantScreen(
                    initial = draft.participant,
                    requiresServerProfile = draft.serverEventId.isNotBlank(),
                    onBack = requestExit,
                    onContinue = { form ->
                        registration.saveParticipant(form)
                        navController.navigate(Screen.EmergencyContact.route)
                    }
                )
            }
        }
        composable(Screen.EmergencyContact.route) {
            EmergencyContactScreen(
                initial = draft.emergency,
                onBack = { navController.popBackStack() },
                onContinue = { form ->
                    registration.saveEmergency(form)
                    navController.navigate(Screen.Consent.route)
                }
            )
        }
    }
}

private fun NavHostController.goHome() {
    if (!popBackStack(Screen.Home.route, inclusive = false)) {
        navigate(Screen.Home.route) { launchSingleTop = true }
    }
}
