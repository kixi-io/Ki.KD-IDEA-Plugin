package io.kixi.kd.intellij

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.kixi.kd.intellij.ui.Paints
import java.awt.Font

/**
 * Syntax highlighter for Ki Data (KD) files.
 *
 * Maps KD token types to text attributes using colors from the Paints palette.
 * This provides semantic coloring for all KD language elements.
 */
class KDSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        // Helper function to create TextAttributesKey with color from Paints
        private fun createKey(name: String, color: java.awt.Color, style: Int = Font.PLAIN): TextAttributesKey {
            val attributes = TextAttributes().apply {
                foregroundColor = color
                fontType = style
            }
            return TextAttributesKey.createTextAttributesKey("KD_$name", attributes)
        }

        // Comment - Gray
        val COMMENT = createKey("COMMENT", Paints.Gray, Font.ITALIC)

        // Strings and Chars - Aqua
        val STRING = createKey("STRING", Paints.Aqua)
        val CHAR = createKey("CHAR", Paints.Aqua)

        // Numbers - Violet
        val NUMBER = createKey("NUMBER", Paints.Violet)

        // Quantities - Blue
        val QUANTITY = createKey("QUANTITY", Paints.Blue)

        // Units - BlueLight
        val UNIT = createKey("UNIT", Paints.BlueLight)

        // Currency - Gold
        val CURRENCY = createKey("CURRENCY", Paints.Gold)

        // Keywords - Violet
        val KEYWORD = createKey("KEYWORD", Paints.Violet, Font.BOLD)

        // Operators - Blue
        val OPERATOR = createKey("OPERATOR", Paints.Blue)

        // Namespace/Package - Lime
        val NAMESPACE = createKey("NAMESPACE", Paints.Lime)

        // Annotation - Green (like imports)
        val ANNOTATION = createKey("ANNOTATION", Paints.Green)

        // Tag name - Magenta
        val TAG_NAME = createKey("TAG_NAME", Paints.Magenta)

        // Attribute key - MagentaLight
        val ATTRIBUTE_KEY = createKey("ATTRIBUTE_KEY", Paints.MagentaLight)

        // Identifier - default (no special color)
        val IDENTIFIER = createKey("IDENTIFIER", Paints.GrayLight)

        // Date/Time - VioletLight
        val DATETIME = createKey("DATETIME", Paints.VioletLight)

        // Duration - VioletLight
        val DURATION = createKey("DURATION", Paints.VioletLight)

        // Version - LimeLight
        val VERSION = createKey("VERSION", Paints.LimeLight)

        // URL - AquaLight
        val URL = createKey("URL", Paints.AquaLight)

        // Email - AquaLight
        val EMAIL = createKey("EMAIL", Paints.AquaLight)

        // Special literals (blob, geo, coordinate, grid) - GreenLight
        val SPECIAL_LITERAL = createKey("SPECIAL_LITERAL", Paints.GreenLight)

        // Punctuation - Gray
        val PUNCTUATION = createKey("PUNCTUATION", Paints.Gray)

        // Brackets - GrayLight
        val BRACKETS = createKey("BRACKETS", Paints.GrayLight)

        // Bad character - Red
        val BAD_CHARACTER = createKey("BAD_CHARACTER", Paints.Red)

        // Empty array for tokens with no special highlighting
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = KDLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (tokenType == null) return EMPTY_KEYS

        return when (tokenType) {
            // Comments
            KDTokenTypes.LINE_COMMENT,
            KDTokenTypes.BLOCK_COMMENT -> arrayOf(COMMENT)

            // Strings and Characters
            KDTokenTypes.STRING,
            KDTokenTypes.RAW_STRING,
            KDTokenTypes.BLOCK_STRING -> arrayOf(STRING)
            KDTokenTypes.CHAR -> arrayOf(CHAR)

            // Numbers
            KDTokenTypes.INTEGER,
            KDTokenTypes.LONG,
            KDTokenTypes.FLOAT,
            KDTokenTypes.DOUBLE,
            KDTokenTypes.DECIMAL,
            KDTokenTypes.HEX_NUMBER,
            KDTokenTypes.BINARY_NUMBER -> arrayOf(NUMBER)

            // Quantities and Units
            KDTokenTypes.QUANTITY -> arrayOf(QUANTITY)
            KDTokenTypes.UNIT -> arrayOf(UNIT)

            // Currency
            KDTokenTypes.CURRENCY,
            KDTokenTypes.CURRENCY_PREFIX -> arrayOf(CURRENCY)

            // Keywords
            KDTokenTypes.TRUE,
            KDTokenTypes.FALSE,
            KDTokenTypes.NIL,
            KDTokenTypes.ON,
            KDTokenTypes.OFF -> arrayOf(KEYWORD)

            // Date/Time
            KDTokenTypes.DATE,
            KDTokenTypes.DATETIME -> arrayOf(DATETIME)

            // Duration
            KDTokenTypes.DURATION -> arrayOf(DURATION)

            // Version
            KDTokenTypes.VERSION -> arrayOf(VERSION)

            // URL and Email
            KDTokenTypes.URL -> arrayOf(URL)
            KDTokenTypes.EMAIL -> arrayOf(EMAIL)

            // Special literals
            KDTokenTypes.BLOB,
            KDTokenTypes.GEO,
            KDTokenTypes.COORDINATE,
            KDTokenTypes.GRID -> arrayOf(SPECIAL_LITERAL)

            // Identifiers and names
            KDTokenTypes.IDENTIFIER -> arrayOf(IDENTIFIER)
            KDTokenTypes.TAG_NAME -> arrayOf(TAG_NAME)
            KDTokenTypes.ATTRIBUTE_KEY -> arrayOf(ATTRIBUTE_KEY)

            // Namespace
            KDTokenTypes.NAMESPACE -> arrayOf(NAMESPACE)

            // Annotation
            KDTokenTypes.ANNOTATION,
            KDTokenTypes.ANNOTATION_NAME,
            KDTokenTypes.AT -> arrayOf(ANNOTATION)

            // Operators
            KDTokenTypes.EQUALS,
            KDTokenTypes.RANGE_OP,
            KDTokenTypes.RANGE_EX_RIGHT,
            KDTokenTypes.RANGE_EX_LEFT,
            KDTokenTypes.RANGE_EXCLUSIVE -> arrayOf(OPERATOR)

            // Punctuation
            KDTokenTypes.COLON,
            KDTokenTypes.SEMICOLON,
            KDTokenTypes.DOT,
            KDTokenTypes.BACKSLASH -> arrayOf(PUNCTUATION)

            // Brackets
            KDTokenTypes.LBRACE,
            KDTokenTypes.RBRACE,
            KDTokenTypes.LBRACKET,
            KDTokenTypes.RBRACKET,
            KDTokenTypes.LPAREN,
            KDTokenTypes.RPAREN,
            KDTokenTypes.LANGLE,
            KDTokenTypes.RANGLE -> arrayOf(BRACKETS)

            // Bad character
            KDTokenTypes.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)

            else -> EMPTY_KEYS
        }
    }
}
