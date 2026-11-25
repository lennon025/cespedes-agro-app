package com.cespedesagro.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CespedesColors.Acento,
    onPrimary = CespedesColors.Primario,
    secondary = CespedesColors.Aviso,
    background = CespedesColors.Primario,
    surface = CespedesColors.Secundario,
    onBackground = CespedesColors.TextoClaro,
    onSurface = CespedesColors.TextoClaro,
    error = CespedesColors.Peligro,
    outline = CespedesColors.BordeFuerte
)

@Composable
fun CespedesAgroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme, 
        typography = CespedesTypography, 
        content = content
    )
}
