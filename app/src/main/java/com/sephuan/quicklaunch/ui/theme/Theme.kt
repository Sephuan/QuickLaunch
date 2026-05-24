package com.sephuan.quicklaunch.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.data.ColorSource
import com.sephuan.quicklaunch.data.CustomColorScheme

private val blueLightScheme = lightColorScheme(
    primary = BlueLightPrimary, onPrimary = BlueLightOnPrimary,
    primaryContainer = BlueLightPrimaryContainer, onPrimaryContainer = BlueLightOnPrimaryContainer,
    secondary = BlueLightSecondary, onSecondary = BlueLightOnSecondary,
    secondaryContainer = BlueLightSecondaryContainer, onSecondaryContainer = BlueLightOnSecondaryContainer,
    tertiary = BlueLightTertiary, onTertiary = BlueLightOnTertiary,
    tertiaryContainer = BlueLightTertiaryContainer, onTertiaryContainer = BlueLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val blueDarkScheme = darkColorScheme(
    primary = BlueDarkPrimary, onPrimary = BlueDarkOnPrimary,
    primaryContainer = BlueDarkPrimaryContainer, onPrimaryContainer = BlueDarkOnPrimaryContainer,
    secondary = BlueDarkSecondary, onSecondary = BlueDarkOnSecondary,
    secondaryContainer = BlueDarkSecondaryContainer, onSecondaryContainer = BlueDarkOnSecondaryContainer,
    tertiary = BlueDarkTertiary, onTertiary = BlueDarkOnTertiary,
    tertiaryContainer = BlueDarkTertiaryContainer, onTertiaryContainer = BlueDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val greenLightScheme = lightColorScheme(
    primary = GreenLightPrimary, onPrimary = GreenLightOnPrimary,
    primaryContainer = GreenLightPrimaryContainer, onPrimaryContainer = GreenLightOnPrimaryContainer,
    secondary = GreenLightSecondary, onSecondary = GreenLightOnSecondary,
    secondaryContainer = GreenLightSecondaryContainer, onSecondaryContainer = GreenLightOnSecondaryContainer,
    tertiary = GreenLightTertiary, onTertiary = GreenLightOnTertiary,
    tertiaryContainer = GreenLightTertiaryContainer, onTertiaryContainer = GreenLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val greenDarkScheme = darkColorScheme(
    primary = GreenDarkPrimary, onPrimary = GreenDarkOnPrimary,
    primaryContainer = GreenDarkPrimaryContainer, onPrimaryContainer = GreenDarkOnPrimaryContainer,
    secondary = GreenDarkSecondary, onSecondary = GreenDarkOnSecondary,
    secondaryContainer = GreenDarkSecondaryContainer, onSecondaryContainer = GreenDarkOnSecondaryContainer,
    tertiary = GreenDarkTertiary, onTertiary = GreenDarkOnTertiary,
    tertiaryContainer = GreenDarkTertiaryContainer, onTertiaryContainer = GreenDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val orangeLightScheme = lightColorScheme(
    primary = OrangeLightPrimary, onPrimary = OrangeLightOnPrimary,
    primaryContainer = OrangeLightPrimaryContainer, onPrimaryContainer = OrangeLightOnPrimaryContainer,
    secondary = OrangeLightSecondary, onSecondary = OrangeLightOnSecondary,
    secondaryContainer = OrangeLightSecondaryContainer, onSecondaryContainer = OrangeLightOnSecondaryContainer,
    tertiary = OrangeLightTertiary, onTertiary = OrangeLightOnTertiary,
    tertiaryContainer = OrangeLightTertiaryContainer, onTertiaryContainer = OrangeLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val orangeDarkScheme = darkColorScheme(
    primary = OrangeDarkPrimary, onPrimary = OrangeDarkOnPrimary,
    primaryContainer = OrangeDarkPrimaryContainer, onPrimaryContainer = OrangeDarkOnPrimaryContainer,
    secondary = OrangeDarkSecondary, onSecondary = OrangeDarkOnSecondary,
    secondaryContainer = OrangeDarkSecondaryContainer, onSecondaryContainer = OrangeDarkOnSecondaryContainer,
    tertiary = OrangeDarkTertiary, onTertiary = OrangeDarkOnTertiary,
    tertiaryContainer = OrangeDarkTertiaryContainer, onTertiaryContainer = OrangeDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val roseLightScheme = lightColorScheme(
    primary = RoseLightPrimary, onPrimary = RoseLightOnPrimary,
    primaryContainer = RoseLightPrimaryContainer, onPrimaryContainer = RoseLightOnPrimaryContainer,
    secondary = RoseLightSecondary, onSecondary = RoseLightOnSecondary,
    secondaryContainer = RoseLightSecondaryContainer, onSecondaryContainer = RoseLightOnSecondaryContainer,
    tertiary = RoseLightTertiary, onTertiary = RoseLightOnTertiary,
    tertiaryContainer = RoseLightTertiaryContainer, onTertiaryContainer = RoseLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val roseDarkScheme = darkColorScheme(
    primary = RoseDarkPrimary, onPrimary = RoseDarkOnPrimary,
    primaryContainer = RoseDarkPrimaryContainer, onPrimaryContainer = RoseDarkOnPrimaryContainer,
    secondary = RoseDarkSecondary, onSecondary = RoseDarkOnSecondary,
    secondaryContainer = RoseDarkSecondaryContainer, onSecondaryContainer = RoseDarkOnSecondaryContainer,
    tertiary = RoseDarkTertiary, onTertiary = RoseDarkOnTertiary,
    tertiaryContainer = RoseDarkTertiaryContainer, onTertiaryContainer = RoseDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val violetLightScheme = lightColorScheme(
    primary = VioletLightPrimary, onPrimary = VioletLightOnPrimary,
    primaryContainer = VioletLightPrimaryContainer, onPrimaryContainer = VioletLightOnPrimaryContainer,
    secondary = VioletLightSecondary, onSecondary = VioletLightOnSecondary,
    secondaryContainer = VioletLightSecondaryContainer, onSecondaryContainer = VioletLightOnSecondaryContainer,
    tertiary = VioletLightTertiary, onTertiary = VioletLightOnTertiary,
    tertiaryContainer = VioletLightTertiaryContainer, onTertiaryContainer = VioletLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val violetDarkScheme = darkColorScheme(
    primary = VioletDarkPrimary, onPrimary = VioletDarkOnPrimary,
    primaryContainer = VioletDarkPrimaryContainer, onPrimaryContainer = VioletDarkOnPrimaryContainer,
    secondary = VioletDarkSecondary, onSecondary = VioletDarkOnSecondary,
    secondaryContainer = VioletDarkSecondaryContainer, onSecondaryContainer = VioletDarkOnSecondaryContainer,
    tertiary = VioletDarkTertiary, onTertiary = VioletDarkOnTertiary,
    tertiaryContainer = VioletDarkTertiaryContainer, onTertiaryContainer = VioletDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val tealLightScheme = lightColorScheme(
    primary = TealLightPrimary, onPrimary = TealLightOnPrimary,
    primaryContainer = TealLightPrimaryContainer, onPrimaryContainer = TealLightOnPrimaryContainer,
    secondary = TealLightSecondary, onSecondary = TealLightOnSecondary,
    secondaryContainer = TealLightSecondaryContainer, onSecondaryContainer = TealLightOnSecondaryContainer,
    tertiary = TealLightTertiary, onTertiary = TealLightOnTertiary,
    tertiaryContainer = TealLightTertiaryContainer, onTertiaryContainer = TealLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val tealDarkScheme = darkColorScheme(
    primary = TealDarkPrimary, onPrimary = TealDarkOnPrimary,
    primaryContainer = TealDarkPrimaryContainer, onPrimaryContainer = TealDarkOnPrimaryContainer,
    secondary = TealDarkSecondary, onSecondary = TealDarkOnSecondary,
    secondaryContainer = TealDarkSecondaryContainer, onSecondaryContainer = TealDarkOnSecondaryContainer,
    tertiary = TealDarkTertiary, onTertiary = TealDarkOnTertiary,
    tertiaryContainer = TealDarkTertiaryContainer, onTertiaryContainer = TealDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val amberLightScheme = lightColorScheme(
    primary = AmberLightPrimary, onPrimary = AmberLightOnPrimary,
    primaryContainer = AmberLightPrimaryContainer, onPrimaryContainer = AmberLightOnPrimaryContainer,
    secondary = AmberLightSecondary, onSecondary = AmberLightOnSecondary,
    secondaryContainer = AmberLightSecondaryContainer, onSecondaryContainer = AmberLightOnSecondaryContainer,
    tertiary = AmberLightTertiary, onTertiary = AmberLightOnTertiary,
    tertiaryContainer = AmberLightTertiaryContainer, onTertiaryContainer = AmberLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val amberDarkScheme = darkColorScheme(
    primary = AmberDarkPrimary, onPrimary = AmberDarkOnPrimary,
    primaryContainer = AmberDarkPrimaryContainer, onPrimaryContainer = AmberDarkOnPrimaryContainer,
    secondary = AmberDarkSecondary, onSecondary = AmberDarkOnSecondary,
    secondaryContainer = AmberDarkSecondaryContainer, onSecondaryContainer = AmberDarkOnSecondaryContainer,
    tertiary = AmberDarkTertiary, onTertiary = AmberDarkOnTertiary,
    tertiaryContainer = AmberDarkTertiaryContainer, onTertiaryContainer = AmberDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

private val indigoLightScheme = lightColorScheme(
    primary = IndigoLightPrimary, onPrimary = IndigoLightOnPrimary,
    primaryContainer = IndigoLightPrimaryContainer, onPrimaryContainer = IndigoLightOnPrimaryContainer,
    secondary = IndigoLightSecondary, onSecondary = IndigoLightOnSecondary,
    secondaryContainer = IndigoLightSecondaryContainer, onSecondaryContainer = IndigoLightOnSecondaryContainer,
    tertiary = IndigoLightTertiary, onTertiary = IndigoLightOnTertiary,
    tertiaryContainer = IndigoLightTertiaryContainer, onTertiaryContainer = IndigoLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
)
private val indigoDarkScheme = darkColorScheme(
    primary = IndigoDarkPrimary, onPrimary = IndigoDarkOnPrimary,
    primaryContainer = IndigoDarkPrimaryContainer, onPrimaryContainer = IndigoDarkOnPrimaryContainer,
    secondary = IndigoDarkSecondary, onSecondary = IndigoDarkOnSecondary,
    secondaryContainer = IndigoDarkSecondaryContainer, onSecondaryContainer = IndigoDarkOnSecondaryContainer,
    tertiary = IndigoDarkTertiary, onTertiary = IndigoDarkOnTertiary,
    tertiaryContainer = IndigoDarkTertiaryContainer, onTertiaryContainer = IndigoDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
)

@Composable
fun QuickLaunchTheme(
    content: @Composable () -> Unit
) {
    val app = LocalContext.current.applicationContext as App
    val useDark by app.isDarkTheme
    val colorSource by app.colorSourceState
    val scheme by app.customColorSchemeState

    val colorScheme = when {
        colorSource == ColorSource.MONET && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        !useDark -> when (scheme) {
            CustomColorScheme.GREEN -> greenLightScheme
            CustomColorScheme.ORANGE -> orangeLightScheme
            CustomColorScheme.ROSE -> roseLightScheme
            CustomColorScheme.VIOLET -> violetLightScheme
            CustomColorScheme.TEAL -> tealLightScheme
            CustomColorScheme.AMBER -> amberLightScheme
            CustomColorScheme.INDIGO -> indigoLightScheme
            else -> blueLightScheme
        }
        else -> when (scheme) {
            CustomColorScheme.GREEN -> greenDarkScheme
            CustomColorScheme.ORANGE -> orangeDarkScheme
            CustomColorScheme.ROSE -> roseDarkScheme
            CustomColorScheme.VIOLET -> violetDarkScheme
            CustomColorScheme.TEAL -> tealDarkScheme
            CustomColorScheme.AMBER -> amberDarkScheme
            CustomColorScheme.INDIGO -> indigoDarkScheme
            else -> blueDarkScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
