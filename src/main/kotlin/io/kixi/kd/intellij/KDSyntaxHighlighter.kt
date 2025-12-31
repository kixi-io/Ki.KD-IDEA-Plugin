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
 * Provides semantic coloring for all KD language elements with proper context
 * awareness for tag names, attribute keys, annotations, and values.
 *
 * Color scheme (favoring base colors):
 * - Tag names: Aqua (document structure)
 * - Attribute keys: Lime (properties)
 * - Annotations: Green (metadata)
 * - Strings/Chars: Gold (text data)
 * - Numbers: Violet (numeric data)
 * - Keywords: Violet bold (true/false/nil)
 * - Dates/Durations: Blue (temporal)
 * - Versions: Lime (semantic versioning)
 * - URLs/Emails: Aqua (links)
 * - Currency: Gold (monetary)
 * - Quantities: Violet (numbers with units)
 * - Special literals: Green (blob/geo/coordinate/grid)
 * - Operators: Blue (=, range ops)
 * - Punctuation: Blue (not gray - visible)
 * - Brackets: Magenta (structure delimiters)
 * - Comments: Gray italic
 */
class KDSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        // Helper function to create TextAttributesKey with color from Paints
        private fun key(name: String, color: java.awt.Color, style: Int = Font.PLAIN): TextAttributesKey {
            val attributes = TextAttributes().apply {
                foregroundColor = color
                fontType = style
            }
            return TextAttributesKey.createTextAttributesKey("KD_$name", attributes)
        }

        // =====================================================================
        // Document Structure
        // =====================================================================

        // Tag names - Aqua (the primary structural element)
        val TAG_NAME = key("TAG_NAME", Paints.Aqua)

        // Attribute keys - Lime (property names)
        val ATTRIBUTE_KEY = key("ATTRIBUTE_KEY", Paints.Lime)

        // Annotations - Green (metadata)
        val ANNOTATION = key("ANNOTATION", Paints.Green)

        // Namespace prefix - Magenta (qualified names)
        val NAMESPACE = key("NAMESPACE", Paints.Magenta)

        // =====================================================================
        // Values and Literals
        // =====================================================================

        // Strings and Characters - Gold (text data)
        val STRING = key("STRING", Paints.FireOrange)
        val CHAR = key("CHAR", Paints.Gold)

        // Numbers - Violet (numeric data)
        val NUMBER = key("NUMBER", Paints.Violet)

        // Keywords - Violet bold (true, false, nil, on, off)
        val KEYWORD = key("KEYWORD", Paints.Violet, Font.BOLD)

        // Date and DateTime - Blue (temporal data)
        val DATETIME = key("DATETIME", Paints.Blue)

        // Duration - Blue (time spans)
        val DURATION = key("DURATION", Paints.Blue)

        // Version - Lime (semantic versions like numbers)
        val VERSION = key("VERSION", Paints.Lime)

        // URL and Email - Aqua (links, like tag names since they're distinct)
        val URL = key("URL", Paints.Aqua)
        val EMAIL = key("EMAIL", Paints.Aqua)

        // Currency - Gold (monetary values, like numbers but distinct)
        val CURRENCY = key("CURRENCY", Paints.Gold)

        // Quantities - Violet (numbers with units)
        val QUANTITY = key("QUANTITY", Paints.Violet)

        // Quantities - Violet (numbers with units)
        val UNIT = key("UNIT", Paints.Green)

        // Special dot literals - Green (blob, geo, coordinate, grid)
        val SPECIAL_LITERAL = key("SPECIAL_LITERAL", Paints.Green)

        // General identifier (when used as value) - Gold (like bare strings)
        val IDENTIFIER = key("IDENTIFIER", Paints.Gold)

        // =====================================================================
        // Operators and Punctuation
        // =====================================================================

        // Operators - Blue (=, range operators)
        val OPERATOR = key("OPERATOR", Paints.Blue)

        // Punctuation - Blue (visible, not gray)
        val PUNCTUATION = key("PUNCTUATION", Paints.Blue)

        // Brackets - Magenta (structural delimiters)
        val BRACKETS = key("BRACKETS", Paints.Magenta)

        // =====================================================================
        // Comments and Errors
        // =====================================================================

        // Comments - Gray italic
        val COMMENT = key("COMMENT", Paints.Gray, Font.ITALIC)

        // Bad character - Red
        val BAD_CHARACTER = key("BAD_CHARACTER", Paints.Red)

        // Empty array for tokens with no highlighting
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = KDLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (tokenType == null) return EMPTY_KEYS

        return when (tokenType) {
            // =====================================================================
            // Document Structure
            // =====================================================================
            KDTokenTypes.TAG_NAME -> arrayOf(TAG_NAME)
            KDTokenTypes.ATTRIBUTE_KEY -> arrayOf(ATTRIBUTE_KEY)
            KDTokenTypes.ANNOTATION -> arrayOf(ANNOTATION)
            KDTokenTypes.AT -> arrayOf(ANNOTATION)
            KDTokenTypes.NAMESPACE -> arrayOf(NAMESPACE)

            // =====================================================================
            // Comments
            // =====================================================================
            KDTokenTypes.LINE_COMMENT,
            KDTokenTypes.BLOCK_COMMENT -> arrayOf(COMMENT)

            // =====================================================================
            // Strings and Characters
            // =====================================================================
            KDTokenTypes.STRING,
            KDTokenTypes.RAW_STRING,
            KDTokenTypes.BLOCK_STRING -> arrayOf(STRING)
            KDTokenTypes.CHAR -> arrayOf(CHAR)

            // =====================================================================
            // Numbers
            // =====================================================================
            KDTokenTypes.INTEGER,
            KDTokenTypes.LONG,
            KDTokenTypes.FLOAT,
            KDTokenTypes.DOUBLE,
            KDTokenTypes.DECIMAL,
            KDTokenTypes.HEX_NUMBER,
            KDTokenTypes.BINARY_NUMBER -> arrayOf(NUMBER)

            // =====================================================================
            // Keywords
            // =====================================================================
            KDTokenTypes.TRUE,
            KDTokenTypes.FALSE,
            KDTokenTypes.NIL,
            KDTokenTypes.ON,
            KDTokenTypes.OFF -> arrayOf(KEYWORD)

            // =====================================================================
            // Date/Time and Duration
            // =====================================================================
            KDTokenTypes.DATE,
            KDTokenTypes.DATETIME -> arrayOf(DATETIME)
            KDTokenTypes.DURATION -> arrayOf(DURATION)

            // =====================================================================
            // Version
            // =====================================================================
            KDTokenTypes.VERSION -> arrayOf(VERSION)

            // =====================================================================
            // URL and Email
            // =====================================================================
            KDTokenTypes.URL -> arrayOf(URL)
            KDTokenTypes.EMAIL -> arrayOf(EMAIL)

            // =====================================================================
            // Currency and Quantities
            // =====================================================================
            KDTokenTypes.CURRENCY -> arrayOf(CURRENCY)
            KDTokenTypes.QUANTITY -> arrayOf(QUANTITY)

            // =====================================================================
            // Special Literals
            // =====================================================================
            KDTokenTypes.BLOB,
            KDTokenTypes.GEO,
            KDTokenTypes.COORDINATE,
            KDTokenTypes.GRID -> arrayOf(SPECIAL_LITERAL)

            // =====================================================================
            // Identifiers (values)
            // =====================================================================
            KDTokenTypes.IDENTIFIER -> arrayOf(IDENTIFIER)

            // =====================================================================
            // Operators
            // =====================================================================
            KDTokenTypes.EQUALS,
            KDTokenTypes.RANGE_OP,
            KDTokenTypes.RANGE_EX_RIGHT,
            KDTokenTypes.RANGE_EX_LEFT,
            KDTokenTypes.RANGE_EXCLUSIVE -> arrayOf(OPERATOR)

            // =====================================================================
            // Punctuation
            // =====================================================================
            KDTokenTypes.COLON,
            KDTokenTypes.SEMICOLON,
            KDTokenTypes.DOT,
            KDTokenTypes.COMMA,
            KDTokenTypes.BACKSLASH -> arrayOf(PUNCTUATION)

            // =====================================================================
            // Brackets
            // =====================================================================
            KDTokenTypes.LBRACE,
            KDTokenTypes.RBRACE,
            KDTokenTypes.LBRACKET,
            KDTokenTypes.RBRACKET,
            KDTokenTypes.LPAREN,
            KDTokenTypes.RPAREN,
            KDTokenTypes.LANGLE,
            KDTokenTypes.RANGLE -> arrayOf(BRACKETS)

            // =====================================================================
            // Errors
            // =====================================================================
            KDTokenTypes.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)

            // =====================================================================
            // Default
            // =====================================================================
            else -> EMPTY_KEYS
        }
    }
}