package io.kixi.kd.intellij

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

/**
 * Token types for the Ki Data (KD) language.
 *
 * These tokens are used by the lexer and syntax highlighter to identify
 * different elements in KD files.
 */
object KDTokenTypes {

    // Base element type class for KD tokens
    class KDElementType(debugName: String) : IElementType(debugName, KDLanguage)

    // Comments
    val LINE_COMMENT = KDElementType("LINE_COMMENT")
    val BLOCK_COMMENT = KDElementType("BLOCK_COMMENT")

    // Strings and Characters
    val STRING = KDElementType("STRING")
    val RAW_STRING = KDElementType("RAW_STRING")
    val BLOCK_STRING = KDElementType("BLOCK_STRING")
    val CHAR = KDElementType("CHAR")

    // Numbers
    val INTEGER = KDElementType("INTEGER")
    val LONG = KDElementType("LONG")
    val FLOAT = KDElementType("FLOAT")
    val DOUBLE = KDElementType("DOUBLE")
    val DECIMAL = KDElementType("DECIMAL")
    val HEX_NUMBER = KDElementType("HEX_NUMBER")
    val BINARY_NUMBER = KDElementType("BINARY_NUMBER")

    // Quantities and Units
    val QUANTITY = KDElementType("QUANTITY")
    val UNIT = KDElementType("UNIT")
    val CURRENCY = KDElementType("CURRENCY")
    val CURRENCY_PREFIX = KDElementType("CURRENCY_PREFIX")

    // Keywords
    val TRUE = KDElementType("TRUE")
    val FALSE = KDElementType("FALSE")
    val NIL = KDElementType("NIL")
    val ON = KDElementType("ON")
    val OFF = KDElementType("OFF")

    // Date/Time/Duration
    val DATE = KDElementType("DATE")
    val DATETIME = KDElementType("DATETIME")
    val DURATION = KDElementType("DURATION")

    // Version
    val VERSION = KDElementType("VERSION")

    // Special literals
    val URL = KDElementType("URL")
    val EMAIL = KDElementType("EMAIL")
    val BLOB = KDElementType("BLOB")
    val GEO = KDElementType("GEO")
    val COORDINATE = KDElementType("COORDINATE")
    val GRID = KDElementType("GRID")

    // Identifiers and namespaces
    val IDENTIFIER = KDElementType("IDENTIFIER")
    val NAMESPACE = KDElementType("NAMESPACE")
    val TAG_NAME = KDElementType("TAG_NAME")
    val ATTRIBUTE_KEY = KDElementType("ATTRIBUTE_KEY")

    // Annotations
    val ANNOTATION = KDElementType("ANNOTATION")
    val ANNOTATION_NAME = KDElementType("ANNOTATION_NAME")

    // Operators and punctuation
    val EQUALS = KDElementType("EQUALS")
    val COLON = KDElementType("COLON")
    val AT = KDElementType("AT")
    val DOT = KDElementType("DOT")
    val RANGE_OP = KDElementType("RANGE_OP")          // ..
    val RANGE_EX_RIGHT = KDElementType("RANGE_EX_RIGHT") // ..<
    val RANGE_EX_LEFT = KDElementType("RANGE_EX_LEFT")   // <..
    val RANGE_EXCLUSIVE = KDElementType("RANGE_EXCLUSIVE") // <..<
    val SEMICOLON = KDElementType("SEMICOLON")
    val BACKSLASH = KDElementType("BACKSLASH")

    // Brackets and braces
    val LBRACE = KDElementType("LBRACE")
    val RBRACE = KDElementType("RBRACE")
    val LBRACKET = KDElementType("LBRACKET")
    val RBRACKET = KDElementType("RBRACKET")
    val LPAREN = KDElementType("LPAREN")
    val RPAREN = KDElementType("RPAREN")
    val LANGLE = KDElementType("LANGLE")
    val RANGLE = KDElementType("RANGLE")

    // Whitespace and bad characters
    val WHITE_SPACE = TokenType.WHITE_SPACE
    val BAD_CHARACTER = TokenType.BAD_CHARACTER

    // Token sets for grouping
    val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
    val STRINGS = TokenSet.create(STRING, RAW_STRING, BLOCK_STRING, CHAR)
    val NUMBERS = TokenSet.create(INTEGER, LONG, FLOAT, DOUBLE, DECIMAL, HEX_NUMBER, BINARY_NUMBER)
    val KEYWORDS = TokenSet.create(TRUE, FALSE, NIL, ON, OFF)
    val OPERATORS = TokenSet.create(EQUALS, RANGE_OP, RANGE_EX_RIGHT, RANGE_EX_LEFT, RANGE_EXCLUSIVE)
    val COMMA = KDElementType("COMMA")
    val BRACKETS = TokenSet.create(LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN, LANGLE, RANGLE)
    val PUNCTUATION = TokenSet.create(COLON, SEMICOLON, DOT, BACKSLASH, AT)
}
