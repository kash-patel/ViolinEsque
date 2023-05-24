package com.kashithekash.violinesque.ui.theme

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val DarkColorScheme = ViolinEsqueColors(
    background = Color.Black,
    fingerboard = Color.Transparent,
    fingerboardTouched = LightBlack,
    button = Color.Transparent,
    buttonTouched = LightBlack,
    string = DarkWhite,
    stringActive = Orange,
    sliderBackground = DarkGrey,
    sliderThumb = LightGrey,
    text = DarkWhite,
    textAlt = Grey,
    textButton = DarkWhite,
    textButtonTouched = Orange,
    isDark = true
)

private val LightColorScheme = ViolinEsqueColors(
    background = Color.White,
    fingerboard = Color.Transparent,
    fingerboardTouched = AlmostWhite,
    button = Color.Transparent,
    buttonTouched = AlmostWhite,
    string = LightGrey,
    stringActive = LightBlue,
    sliderBackground = LightGrey,
    sliderThumb = LightBlack,
    text = LightGrey,
    textAlt = DarkWhite,
    textButton = LightGrey,
    textButtonTouched = LightBlue,
    isDark = false
)

/*
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80

)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    */
/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    *//*

)
*/

/*
@Composable
fun ViolinEsqueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
}
*/

@Composable
fun ViolinEsqueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalViolinEsqueColors provides colorScheme) {
        MaterialTheme(
            content = content
        )
    }
}

object ViolinEsqueTheme {
    val colors: ViolinEsqueColors
    @Composable
    get() = LocalViolinEsqueColors.current
}

@Stable
class ViolinEsqueColors(
    background: Color,
    fingerboard: Color,
    fingerboardTouched: Color,
    button: Color,
    buttonTouched: Color,
    string: Color,
    stringActive: Color,
    sliderBackground: Color,
    sliderThumb: Color,
    text: Color,
    textAlt: Color,
    textButton: Color,
    textButtonTouched: Color,
    isDark: Boolean
) {
    var background by mutableStateOf(background)
        private set
    var fingerBoard by mutableStateOf(fingerboard)
        private set
    var fingerBoardTouched by mutableStateOf(fingerboardTouched)
        private set
    var button by mutableStateOf(button)
        private set
    var buttonTouched by mutableStateOf(buttonTouched)
        private set
    var string by mutableStateOf(string)
        private set
    var stringActive by mutableStateOf(stringActive)
        private set
    var sliderBackground by mutableStateOf(sliderBackground)
        private set
    var sliderThumb by mutableStateOf(sliderThumb)
        private set
    var text by mutableStateOf(text)
        private set
    var textAlt by mutableStateOf(textAlt)
        private set
    var textButton by mutableStateOf(textButton)
        private set
    var textButtonTouched by mutableStateOf(textButtonTouched)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update (other: ViolinEsqueColors) {
        background = other.background
        fingerBoard = other.fingerBoard
        fingerBoardTouched = other.buttonTouched
        button = other.button
        buttonTouched = other.buttonTouched
        string = other.string
        stringActive = other.stringActive
        sliderBackground = other.sliderBackground
        sliderThumb = other.sliderThumb
        text = other.text
        textAlt = other.textAlt
        textButton = other.textButton
        textButtonTouched = other.textButtonTouched
        isDark = other.isDark
    }

    fun copy (): ViolinEsqueColors = ViolinEsqueColors (
        background = background,
        fingerboard = fingerBoard,
        fingerboardTouched = fingerBoardTouched,
        button = button,
        buttonTouched = buttonTouched,
        string = string,
        stringActive = stringActive,
        sliderBackground = sliderBackground,
        sliderThumb = sliderThumb,
        text = text,
        textAlt = textAlt,
        textButton = textButton,
        textButtonTouched = textButtonTouched,
        isDark = isDark
    )
}

val LocalViolinEsqueColors = staticCompositionLocalOf {
    ViolinEsqueColors(
        background = Color.Unspecified,
        fingerboard = Color.Unspecified,
        fingerboardTouched = Color.Unspecified,
        button = Color.Unspecified,
        buttonTouched = Color.Unspecified,
        string = Color.Unspecified,
        stringActive = Color.Unspecified,
        sliderBackground = Color.Unspecified,
        sliderThumb = Color.Unspecified,
        text = Color.Unspecified,
        textAlt = Color.Unspecified,
        textButton = Color.Unspecified,
        textButtonTouched = Color.Unspecified,
        isDark = false
    )
}