package io.kixi.kd.intellij.ui

import io.kixi.kd.intellij.ui.Paints.darker
import io.kixi.kd.intellij.ui.Paints.lighter
import java.awt.*
import javax.swing.*

/**
 * A simple Swing application to display all colors from the Paints object.
 */
fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Paints Colors")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()

        val panel = JPanel()
        panel.layout = GridLayout(0, 1, 5, 5)
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val colors = Paints.colors + Paints.colorsLight
        val colorNames = Paints.colorNames + Paints.colorLightNames

        for ((index, color) in colors.withIndex()) {
            panel.add(createColorRow(colorNames[index], color))
        }

        frame.add(panel, BorderLayout.CENTER)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}

/**
 * Creates a row displaying a color swatch, name, and RGB values.
 */
private fun createColorRow(name: String, color: Color): JPanel {
    val row = JPanel()
    row.layout = FlowLayout(FlowLayout.LEFT, 10, 5)

    // Color swatch
    val swatch = JPanel()
    swatch.preferredSize = Dimension(60, 40)
    swatch.background = color
    swatch.border = BorderFactory.createLineBorder(Color.DARK_GRAY)

    // Name label
    val nameLabel = JLabel(name)
    nameLabel.preferredSize = Dimension(80, 20)
    nameLabel.font = Font("SansSerif", Font.BOLD, 14)

    // RGB values
    val rgbText = "RGB(${color.red}, ${color.green}, ${color.blue})"
    val rgbLabel = JLabel(rgbText)
    rgbLabel.preferredSize = Dimension(150, 20)
    rgbLabel.font = Font("Monospaced", Font.PLAIN, 14)

    // Hex value
    val hexText = String.format("#%02X%02X%02X", color.red, color.green, color.blue)
    val hexLabel = JLabel(hexText)
    hexLabel.font = Font("Monospaced", Font.PLAIN, 14)

    row.add(swatch)
    row.add(nameLabel)
    row.add(rgbLabel)
    row.add(hexLabel)

    return row
}