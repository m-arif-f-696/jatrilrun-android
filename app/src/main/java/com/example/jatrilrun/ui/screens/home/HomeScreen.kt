package com.example.jatrilrun.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jatrilrun.ui.components.CommonButton
import com.example.jatrilrun.ui.components.JrBottomNavigationBar
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrSpacing

@Composable
fun HomeScreen(
    onNavigateToPayment: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            JrBottomNavigationBar(
                selectedRoute = selectedTab,
                onTabSelected = { route ->
                    selectedTab = route
                }
            )
        },
        containerColor = JatrilrunTheme.colors.backgroundPrimary
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(JrSpacing.S24),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                "home" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Selamat Datang di Jatrilrun!",
                            style = JatrilrunTheme.typography.heading,
                            color = JatrilrunTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(JrSpacing.S12))

                        Text(
                            text = "Pengguna: ${user?.name ?: "Guest"}",
                            style = JatrilrunTheme.typography.body,
                            color = JatrilrunTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(JrSpacing.S24))

                        CommonButton(text = "Ke Halaman Pembayaran", onClick = onNavigateToPayment)
                        Spacer(modifier = Modifier.height(JrSpacing.S12))

                        CommonButton(text = "Keluar", onClick = {
                            viewModel.logout()
                            onLogout()
                        })
                    }
                }
                "event" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Daftar Event",
                            style = JatrilrunTheme.typography.heading,
                            color = JatrilrunTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(JrSpacing.S12))
                        Text(
                            text = "Belum ada event tersedia.",
                            style = JatrilrunTheme.typography.body,
                            color = JatrilrunTheme.colors.textSecondary
                        )
                    }
                }
                "tickets" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Tiket Saya",
                            style = JatrilrunTheme.typography.heading,
                            color = JatrilrunTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(JrSpacing.S12))
                        Text(
                            text = "Anda belum memiliki tiket.",
                            style = JatrilrunTheme.typography.body,
                            color = JatrilrunTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
