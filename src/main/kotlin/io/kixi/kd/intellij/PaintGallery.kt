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

        // Add each color from Paints
        val colors = listOf(
            "Aqua" to Paints.Aqua.darker(.2f),
            "Blue" to Paints.Blue.darker(.1f),
            "Violet" to Paints.Violet.darker(.1f),
            "Magenta" to Paints.Magenta.darker(.2f),
            "Red" to Paints.Red.darker(.1f),
            "Orange" to Paints.Orange.darker(.2f),
            "Lime" to Paints.Lime.darker(.2f),
            "Green" to Paints.Green.darker(.2f),

            "AquaLight" to Paints.Aqua.lighter(.33f),
            "BlueLight" to Paints.Blue.lighter(.33f),
            "VioletLight" to Paints.Violet.lighter(.33f),
            "MagentaLight" to Paints.Magenta.lighter(.33f),
            "RedLight" to Paints.Red.lighter(.33f),
            "OrangeLight" to Paints.Orange.lighter(.33f),
            "LimeLight" to Paints.Lime.lighter(.33f),
            "GreenLight" to Paints.Green.lighter(.33f)
        )

        for ((name, color) in colors) {
            panel.add(createColorRow(name, color))
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