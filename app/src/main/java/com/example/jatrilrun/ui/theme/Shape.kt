package com.example.jatrilrun.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Radius: small 8, medium 12, large 20, pill 999
object JrShapes {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(20.dp)
    val Pill = RoundedCornerShape(percent = 50) // setara radius 999
    val BottomSheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
}

val JrMaterialShapes = Shapes(
    extraSmall = JrShapes.Small,
    small = JrShapes.Small,
    medium = JrShapes.Medium,
    large = JrShapes.Large,
    extraLarge = JrShapes.Large,
)