package com.example.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color

// Brand colours: the same in light and dark mode
val IncomeColor = Color(0xFF6C3CF4)   // purple: income, debit cards, primary actions
val ExpenseColor = Color(0xFFFF7A45)  // orange: expenses, credit cards
val PositiveGreen = Color(0xFF22A861) // "+" amounts; readable on both white and near-black
val DangerRed = Color(0xFFE5484D)     // delete, over-limit warnings

// Light scheme
val LightBackground = Color(0xFFF7F8FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceLow = Color(0xFFF1F2F8)      // list rows, detail panels
val LightSurfaceMid = Color(0xFFFFFFFF)      // floating nav bar
val LightSurfaceHigh = Color(0xFFE9EAF2)     // small buttons, toggles
val LightOnSurface = Color(0xFF16161D)
val LightOnSurfaceVariant = Color(0xFF6E6E80) // secondary text
val LightOutline = Color(0xFFB9BACB)
val LightOutlineVariant = Color(0xFFDEDFEA)

// Dark scheme: deep blue-black rather than pure black, so the purple/orange cards glow
val DarkBackground = Color(0xFF0E0E16)
val DarkSurface = Color(0xFF16161F)
val DarkSurfaceLow = Color(0xFF1B1B26)
val DarkSurfaceMid = Color(0xFF22222E)
val DarkSurfaceHigh = Color(0xFF2B2B39)
val DarkOnSurface = Color(0xFFECECF4)
val DarkOnSurfaceVariant = Color(0xFFA2A2B8)
val DarkOutline = Color(0xFF555569)
val DarkOutlineVariant = Color(0xFF34344A)
val DarkPrimary = Color(0xFF9B7BFF)          // lighter purple so it reads on dark backgrounds
