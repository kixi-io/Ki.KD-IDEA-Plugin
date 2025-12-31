package io.kixi.kd.intellij.ui

import java.awt.Color

/**
 * The Paints object contains colors, textures and gradients.
 *
 * The 8 base colors are equally spaced around the color wheel (45° apart),
 * starting from Aqua at 180° (cyan). All colors maintain the same
 * saturation (~79%) and lightness (~37%) as Aqua.
 */
object Paints {
    /**
     * Returns a lighter version of the color by blending toward white.
     * @param factor 0.0-1.0 value, where higher values make the color lighter
     *               (0.0 = original color, 1.0 = white)
     */
    fun Color.lighter(factor: Float): Color {
        val r = (this.red + (255 - this.red) * factor).toInt().coerceIn(0, 255)
        val g = (this.green + (255 - this.green) * factor).toInt().coerceIn(0, 255)
        val b = (this.blue + (255 - this.blue) * factor).toInt().coerceIn(0, 255)
        return Color(r, g, b)
    }

    /**
     * Returns a darker version of the color by blending toward black.
     * @param factor 0.0-1.0 value, where higher values make the color darker
     *               (0.0 = original color, 1.0 = black)
     */
    fun Color.darker(factor: Float): Color {
        val r = (this.red * (1 - factor)).toInt().coerceIn(0, 255)
        val g = (this.green * (1 - factor)).toInt().coerceIn(0, 255)
        val b = (this.blue * (1 - factor)).toInt().coerceIn(0, 255)
        return Color(r, g, b)
    }

    /**
     * Mixes this color with another color.
     * @param other The color to mix in
     * @param amount 0.0 = this color, 1.0 = other color
     */
    fun Color.mix(other: Color, amount: Float): Color {
        val r = (this.red + (other.red - this.red) * amount).toInt().coerceIn(0, 255)
        val g = (this.green + (other.green - this.green) * amount).toInt().coerceIn(0, 255)
        val b = (this.blue + (other.blue - this.blue) * amount).toInt().coerceIn(0, 255)
        return Color(r, g, b)
    }

    // Base colors - equally spaced around the color wheel (45° apart)
    val Aqua = Color(18, 153, 153)       // 180° - Cyan
    val Blue = Color(43, 76, 178)        // 225° - Blue
    val Violet = Color(111, 43, 178)     // 270° - Purple
    val Magenta = Color(170, 20, 133)    // 315° - Magenta/Pink
    val Red = Color(170, 20, 20)         // 0° - Red
    val FireOrange = Color(170, 133, 20).mix(Red, .33f)
    val Gold = Color(170, 133, 20)       // 45° - Orange/Gold
    val Lime = Color(125, 155, 20)       // 90° - Yellow-Green
    val Green = Color(18, 153, 51)       // 135° - Green
    val Gray = Color(100, 100, 100)      // Neutral gray

    // Light variants (33% lighter)
    val AquaLight = Aqua.lighter(.33f)
    val BlueLight = Blue.lighter(.33f)
    val VioletLight = Violet.lighter(.33f)
    val MagentaLight = Magenta.lighter(.33f)
    val RedLight = Red.lighter(.33f)
    val GoldLight = Gold.lighter(.33f)
    val LimeLight = Lime.lighter(.33f)
    val GreenLight = Green.lighter(.33f)
    val GrayLight = Gray.lighter(.33f)

    // Dark variants (33% darker)
    val AquaDark = Aqua.darker(.33f)
    val BlueDark = Blue.darker(.33f)
    val VioletDark = Violet.darker(.33f)
    val MagentaDark = Magenta.darker(.33f)
    val RedDark = Red.darker(.33f)
    val GoldDark = Gold.darker(.33f)
    val LimeDark = Lime.darker(.33f)
    val GreenDark = Green.darker(.33f)
    val GrayDark = Gray.darker(.33f)

    /** All base colors in hue order starting from Aqua (180°) */
    val colors = listOf(Aqua, Blue, Violet, Magenta, Red, Gold, Lime, Green, Gray)

    /** Light variant colors in hue order */
    val colorsLight = listOf(
        AquaLight, BlueLight, VioletLight, MagentaLight,
        RedLight, GoldLight, LimeLight, GreenLight, GrayLight
    )

    /** Dark variant colors in hue order */
    val colorsDark = listOf(
        AquaDark, BlueDark, VioletDark, MagentaDark,
        RedDark, GoldDark, LimeDark, GreenDark, GrayDark
    )

    /** Color names corresponding to colors list */
    val colorNames = listOf(
        "Aqua", "Blue", "Violet", "Magenta", "Red", "Gold", "Lime", "Green", "Gray"
    )

    /** Color names corresponding to colorsLight list */
    val colorLightNames = listOf("AquaLight", "BlueLight", "VioletLight", "MagentaLight", "RedLight",
        "GoldLight", "LimeLight", "GreenLight", "GrayLight")
}
