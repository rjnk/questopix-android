package com.rejnek.oog.ui.theme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.rejnek.oog.ui.theme.backgroundDark
import com.rejnek.oog.ui.theme.backgroundDarkHighContrast
import com.rejnek.oog.ui.theme.backgroundDarkMediumContrast
import com.rejnek.oog.ui.theme.backgroundLight
import com.rejnek.oog.ui.theme.backgroundLightHighContrast
import com.rejnek.oog.ui.theme.backgroundLightMediumContrast
import com.rejnek.oog.ui.theme.errorContainerDark
import com.rejnek.oog.ui.theme.errorContainerDarkHighContrast
import com.rejnek.oog.ui.theme.errorContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.errorContainerLight
import com.rejnek.oog.ui.theme.errorContainerLightHighContrast
import com.rejnek.oog.ui.theme.errorContainerLightMediumContrast
import com.rejnek.oog.ui.theme.errorDark
import com.rejnek.oog.ui.theme.errorDarkHighContrast
import com.rejnek.oog.ui.theme.errorDarkMediumContrast
import com.rejnek.oog.ui.theme.errorLight
import com.rejnek.oog.ui.theme.errorLightHighContrast
import com.rejnek.oog.ui.theme.errorLightMediumContrast
import com.rejnek.oog.ui.theme.inverseOnSurfaceDark
import com.rejnek.oog.ui.theme.inverseOnSurfaceDarkHighContrast
import com.rejnek.oog.ui.theme.inverseOnSurfaceDarkMediumContrast
import com.rejnek.oog.ui.theme.inverseOnSurfaceLight
import com.rejnek.oog.ui.theme.inverseOnSurfaceLightHighContrast
import com.rejnek.oog.ui.theme.inverseOnSurfaceLightMediumContrast
import com.rejnek.oog.ui.theme.inversePrimaryDark
import com.rejnek.oog.ui.theme.inversePrimaryDarkHighContrast
import com.rejnek.oog.ui.theme.inversePrimaryDarkMediumContrast
import com.rejnek.oog.ui.theme.inversePrimaryLight
import com.rejnek.oog.ui.theme.inversePrimaryLightHighContrast
import com.rejnek.oog.ui.theme.inversePrimaryLightMediumContrast
import com.rejnek.oog.ui.theme.inverseSurfaceDark
import com.rejnek.oog.ui.theme.inverseSurfaceDarkHighContrast
import com.rejnek.oog.ui.theme.inverseSurfaceDarkMediumContrast
import com.rejnek.oog.ui.theme.inverseSurfaceLight
import com.rejnek.oog.ui.theme.inverseSurfaceLightHighContrast
import com.rejnek.oog.ui.theme.inverseSurfaceLightMediumContrast
import com.rejnek.oog.ui.theme.onBackgroundDark
import com.rejnek.oog.ui.theme.onBackgroundDarkHighContrast
import com.rejnek.oog.ui.theme.onBackgroundDarkMediumContrast
import com.rejnek.oog.ui.theme.onBackgroundLight
import com.rejnek.oog.ui.theme.onBackgroundLightHighContrast
import com.rejnek.oog.ui.theme.onBackgroundLightMediumContrast
import com.rejnek.oog.ui.theme.onErrorContainerDark
import com.rejnek.oog.ui.theme.onErrorContainerDarkHighContrast
import com.rejnek.oog.ui.theme.onErrorContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.onErrorContainerLight
import com.rejnek.oog.ui.theme.onErrorContainerLightHighContrast
import com.rejnek.oog.ui.theme.onErrorContainerLightMediumContrast
import com.rejnek.oog.ui.theme.onErrorDark
import com.rejnek.oog.ui.theme.onErrorDarkHighContrast
import com.rejnek.oog.ui.theme.onErrorDarkMediumContrast
import com.rejnek.oog.ui.theme.onErrorLight
import com.rejnek.oog.ui.theme.onErrorLightHighContrast
import com.rejnek.oog.ui.theme.onErrorLightMediumContrast
import com.rejnek.oog.ui.theme.onPrimaryContainerDark
import com.rejnek.oog.ui.theme.onPrimaryContainerDarkHighContrast
import com.rejnek.oog.ui.theme.onPrimaryContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.onPrimaryContainerLight
import com.rejnek.oog.ui.theme.onPrimaryContainerLightHighContrast
import com.rejnek.oog.ui.theme.onPrimaryContainerLightMediumContrast
import com.rejnek.oog.ui.theme.onPrimaryDark
import com.rejnek.oog.ui.theme.onPrimaryDarkHighContrast
import com.rejnek.oog.ui.theme.onPrimaryDarkMediumContrast
import com.rejnek.oog.ui.theme.onPrimaryLight
import com.rejnek.oog.ui.theme.onPrimaryLightHighContrast
import com.rejnek.oog.ui.theme.onPrimaryLightMediumContrast
import com.rejnek.oog.ui.theme.onSecondaryContainerDark
import com.rejnek.oog.ui.theme.onSecondaryContainerDarkHighContrast
import com.rejnek.oog.ui.theme.onSecondaryContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.onSecondaryContainerLight
import com.rejnek.oog.ui.theme.onSecondaryContainerLightHighContrast
import com.rejnek.oog.ui.theme.onSecondaryContainerLightMediumContrast
import com.rejnek.oog.ui.theme.onSecondaryDark
import com.rejnek.oog.ui.theme.onSecondaryDarkHighContrast
import com.rejnek.oog.ui.theme.onSecondaryDarkMediumContrast
import com.rejnek.oog.ui.theme.onSecondaryLight
import com.rejnek.oog.ui.theme.onSecondaryLightHighContrast
import com.rejnek.oog.ui.theme.onSecondaryLightMediumContrast
import com.rejnek.oog.ui.theme.onSurfaceDark
import com.rejnek.oog.ui.theme.onSurfaceDarkHighContrast
import com.rejnek.oog.ui.theme.onSurfaceDarkMediumContrast
import com.rejnek.oog.ui.theme.onSurfaceLight
import com.rejnek.oog.ui.theme.onSurfaceLightHighContrast
import com.rejnek.oog.ui.theme.onSurfaceLightMediumContrast
import com.rejnek.oog.ui.theme.onSurfaceVariantDark
import com.rejnek.oog.ui.theme.onSurfaceVariantDarkHighContrast
import com.rejnek.oog.ui.theme.onSurfaceVariantDarkMediumContrast
import com.rejnek.oog.ui.theme.onSurfaceVariantLight
import com.rejnek.oog.ui.theme.onSurfaceVariantLightHighContrast
import com.rejnek.oog.ui.theme.onSurfaceVariantLightMediumContrast
import com.rejnek.oog.ui.theme.onTertiaryContainerDark
import com.rejnek.oog.ui.theme.onTertiaryContainerDarkHighContrast
import com.rejnek.oog.ui.theme.onTertiaryContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.onTertiaryContainerLight
import com.rejnek.oog.ui.theme.onTertiaryContainerLightHighContrast
import com.rejnek.oog.ui.theme.onTertiaryContainerLightMediumContrast
import com.rejnek.oog.ui.theme.onTertiaryDark
import com.rejnek.oog.ui.theme.onTertiaryDarkHighContrast
import com.rejnek.oog.ui.theme.onTertiaryDarkMediumContrast
import com.rejnek.oog.ui.theme.onTertiaryLight
import com.rejnek.oog.ui.theme.onTertiaryLightHighContrast
import com.rejnek.oog.ui.theme.onTertiaryLightMediumContrast
import com.rejnek.oog.ui.theme.outlineDark
import com.rejnek.oog.ui.theme.outlineDarkHighContrast
import com.rejnek.oog.ui.theme.outlineDarkMediumContrast
import com.rejnek.oog.ui.theme.outlineLight
import com.rejnek.oog.ui.theme.outlineLightHighContrast
import com.rejnek.oog.ui.theme.outlineLightMediumContrast
import com.rejnek.oog.ui.theme.outlineVariantDark
import com.rejnek.oog.ui.theme.outlineVariantDarkHighContrast
import com.rejnek.oog.ui.theme.outlineVariantDarkMediumContrast
import com.rejnek.oog.ui.theme.outlineVariantLight
import com.rejnek.oog.ui.theme.outlineVariantLightHighContrast
import com.rejnek.oog.ui.theme.outlineVariantLightMediumContrast
import com.rejnek.oog.ui.theme.primaryContainerDark
import com.rejnek.oog.ui.theme.primaryContainerDarkHighContrast
import com.rejnek.oog.ui.theme.primaryContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.primaryContainerLight
import com.rejnek.oog.ui.theme.primaryContainerLightHighContrast
import com.rejnek.oog.ui.theme.primaryContainerLightMediumContrast
import com.rejnek.oog.ui.theme.primaryDark
import com.rejnek.oog.ui.theme.primaryDarkHighContrast
import com.rejnek.oog.ui.theme.primaryDarkMediumContrast
import com.rejnek.oog.ui.theme.primaryLight
import com.rejnek.oog.ui.theme.primaryLightHighContrast
import com.rejnek.oog.ui.theme.primaryLightMediumContrast
import com.rejnek.oog.ui.theme.scrimDark
import com.rejnek.oog.ui.theme.scrimDarkHighContrast
import com.rejnek.oog.ui.theme.scrimDarkMediumContrast
import com.rejnek.oog.ui.theme.scrimLight
import com.rejnek.oog.ui.theme.scrimLightHighContrast
import com.rejnek.oog.ui.theme.scrimLightMediumContrast
import com.rejnek.oog.ui.theme.secondaryContainerDark
import com.rejnek.oog.ui.theme.secondaryContainerDarkHighContrast
import com.rejnek.oog.ui.theme.secondaryContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.secondaryContainerLight
import com.rejnek.oog.ui.theme.secondaryContainerLightHighContrast
import com.rejnek.oog.ui.theme.secondaryContainerLightMediumContrast
import com.rejnek.oog.ui.theme.secondaryDark
import com.rejnek.oog.ui.theme.secondaryDarkHighContrast
import com.rejnek.oog.ui.theme.secondaryDarkMediumContrast
import com.rejnek.oog.ui.theme.secondaryLight
import com.rejnek.oog.ui.theme.secondaryLightHighContrast
import com.rejnek.oog.ui.theme.secondaryLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceBrightDark
import com.rejnek.oog.ui.theme.surfaceBrightDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceBrightDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceBrightLight
import com.rejnek.oog.ui.theme.surfaceBrightLightHighContrast
import com.rejnek.oog.ui.theme.surfaceBrightLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerDark
import com.rejnek.oog.ui.theme.surfaceContainerDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighDark
import com.rejnek.oog.ui.theme.surfaceContainerHighDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighLight
import com.rejnek.oog.ui.theme.surfaceContainerHighLightHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighestDark
import com.rejnek.oog.ui.theme.surfaceContainerHighestDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighestDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighestLight
import com.rejnek.oog.ui.theme.surfaceContainerHighestLightHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerHighestLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerLight
import com.rejnek.oog.ui.theme.surfaceContainerLightHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowDark
import com.rejnek.oog.ui.theme.surfaceContainerLowDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowLight
import com.rejnek.oog.ui.theme.surfaceContainerLowLightHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowestDark
import com.rejnek.oog.ui.theme.surfaceContainerLowestDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowestDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowestLight
import com.rejnek.oog.ui.theme.surfaceContainerLowestLightHighContrast
import com.rejnek.oog.ui.theme.surfaceContainerLowestLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceDark
import com.rejnek.oog.ui.theme.surfaceDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceDimDark
import com.rejnek.oog.ui.theme.surfaceDimDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceDimDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceDimLight
import com.rejnek.oog.ui.theme.surfaceDimLightHighContrast
import com.rejnek.oog.ui.theme.surfaceDimLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceLight
import com.rejnek.oog.ui.theme.surfaceLightHighContrast
import com.rejnek.oog.ui.theme.surfaceLightMediumContrast
import com.rejnek.oog.ui.theme.surfaceVariantDark
import com.rejnek.oog.ui.theme.surfaceVariantDarkHighContrast
import com.rejnek.oog.ui.theme.surfaceVariantDarkMediumContrast
import com.rejnek.oog.ui.theme.surfaceVariantLight
import com.rejnek.oog.ui.theme.surfaceVariantLightHighContrast
import com.rejnek.oog.ui.theme.surfaceVariantLightMediumContrast
import com.rejnek.oog.ui.theme.tertiaryContainerDark
import com.rejnek.oog.ui.theme.tertiaryContainerDarkHighContrast
import com.rejnek.oog.ui.theme.tertiaryContainerDarkMediumContrast
import com.rejnek.oog.ui.theme.tertiaryContainerLight
import com.rejnek.oog.ui.theme.tertiaryContainerLightHighContrast
import com.rejnek.oog.ui.theme.tertiaryContainerLightMediumContrast
import com.rejnek.oog.ui.theme.tertiaryDark
import com.rejnek.oog.ui.theme.tertiaryDarkHighContrast
import com.rejnek.oog.ui.theme.tertiaryDarkMediumContrast
import com.rejnek.oog.ui.theme.tertiaryLight
import com.rejnek.oog.ui.theme.tertiaryLightHighContrast
import com.rejnek.oog.ui.theme.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

