package com.example.jatrilrun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrSpacing
import com.example.jatrilrun.ui.theme.JrDimens

data class BottomNavItem(
    val title: String,
    val iconRes: Int,
    val route: String
)

@Preview(showBackground = true)
@Composable
fun JrBottomNavigationBar(
    selectedRoute: String = "home",
    onTabSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("Beranda", R.drawable.home, "home"),
        BottomNavItem("Event", R.drawable.calendar, "event"),
        BottomNavItem("Tiket saya", R.drawable.ticket, "tickets")
    )

    Row(
        modifier = modifier
            .border(width = JrDimens.BorderWidth, color = JatrilrunTheme.colors.border)
            .fillMaxWidth()
            .height(76.dp)
            .background(color = JatrilrunTheme.colors.backgroundPrimary)
            .padding(start = JrSpacing.None, top = JrSpacing.None, end = JrSpacing.None, bottom = JrSpacing.None),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = selectedRoute == item.route
            val color = if (isSelected) JatrilrunTheme.colors.textBrand else JatrilrunTheme.colors.textSecondary

            Column(
                modifier = Modifier
                    .width(130.dp)
                    .height(72.dp)
                    .clickable { onTabSelected(item.route) }
                    .padding(JrSpacing.S8),

                verticalArrangement = Arrangement.spacedBy(JrSpacing.S4, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = item.title,
                    style = JatrilrunTheme.typography.caption,
                    color = color
                )
            }
        }
    }
}
