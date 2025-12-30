package io.kixi.kd.intellij.ui

import io.kixi.kd.intellij.ui.Paints.darker
import io.kixi.kd.intellij.ui.Paints.lighter
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

    // Base color: Aqua at 180° hue
    val Aqua = Color(20, 170, 170)

    // 7 additional colors at 45° intervals around the color wheel
    // Color(20, 57, 170) lightened by 0.5 -> Color(137, 156, 212)
    val Blue = Color(20, 57, 170).lighter( 0.25f)    // 225°
    // If you want to see the color in IDE preview, use the computed value:
    // val Blue = Color(137, 156, 212)    // 225° - lightened
    val Violet = Color(95, 20, 170)     // 270°
    val Magenta = Color(170, 20, 133)   // 315°
    val Red = Color(170, 20, 20)        // 0°/360°
    val Orange = Color(170, 133, 20)    // 45°
    val Lime = Color(95, 170, 20).mix(Paints.Orange, 0.33f)       // Color(95, 170, 20)       // 90°
    val Green = Color(20, 170, 57)      // 135°

    /** All 8 base colors in hue order starting from Aqua (180°) */
    val baseColors = listOf(Aqua, Blue, Violet, Magenta, Red, Orange, Lime, Green)

    /** Color names corresponding to baseColors list */
    val colorNames = listOf("Aqua", "Blue", "Violet", "Magenta", "Red", "Orange", "Lime", "Green")
}

fun main(args: Array<String>) {
    println(Paints.Blue)
    println(Paints.Violet)
    println(Paints.Magenta)
    println(Paints.Red)

    println(Paints.Blue.lighter( 0.30f))
    println(Paints.Violet.lighter( 0.30f))
    println(Paints.Magenta.lighter( 0.30f))
    println(Paints.Red.lighter( 0.30f))
}