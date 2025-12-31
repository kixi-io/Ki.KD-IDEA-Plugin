package io.kixi.kd.intellij

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

/**
 * Token types for KD (Ki Data) language.
 */
object KDTokenTypes {

    // Whitespace
    val WHITE_SPACE = KDElementType("WHITE_SPACE")

    // Comments
    val LINE_COMMENT = KDElementType("LINE_COMMENT")
    val BLOCK_COMMENT = KDElementType("BLOCK_COMMENT")

    // Strings
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

    // Keywords
    val TRUE = KDElementType("TRUE")
    val FALSE = KDElementType("FALSE")
    val NIL = KDElementType("NIL")
    val ON = KDElementType("ON")
    val OFF = KDElementType("OFF")

    // Date/Time
    val DATE = KDElementType("DATE")
    val DATETIME = KDElementType("DATETIME")

    // Duration
    val DURATION = KDElementType("DURATION")

    // Version
    val VERSION = KDElementType("VERSION")

    // URL and Email
    val URL = KDElementType("URL")
    val EMAIL = KDElementType("EMAIL")

    // Quantities and Currency
    val QUANTITY = KDElementType("QUANTITY")
    val CURRENCY = KDElementType("CURRENCY")

    // Special dot literals
    val BLOB = KDElementType("BLOB")
    val GEO = KDElementType("GEO")
    val COORDINATE = KDElementType("COORDINATE")
    val GRID = KDElementType("GRID")

    // Identifiers and Names (contextual)
    val IDENTIFIER = KDElementType("IDENTIFIER")
    val TAG_NAME = KDElementType("TAG_NAME")
    val ATTRIBUTE_KEY = KDElementType("ATTRIBUTE_KEY")
    val NAMESPACE = KDElementType("NAMESPACE")

    // Annotations
    val AT = KDElementType("AT")
    val ANNOTATION = KDElementType("ANNOTATION")

    // Operators
    val EQUALS = KDElementType("EQUALS")
    val RANGE_OP = KDElementType("RANGE_OP")           // ..
    val RANGE_EX_RIGHT = KDElementType("RANGE_EX_RIGHT") // ..<
    val RANGE_EX_LEFT = KDElementType("RANGE_EX_LEFT")   // <..
    val RANGE_EXCLUSIVE = KDElementType("RANGE_EXCLUSIVE") // <..<

    // Punctuation
    val COLON = KDElementType("COLON")
    val SEMICOLON = KDElementType("SEMICOLON")
    val DOT = KDElementType("DOT")
    val COMMA = KDElementType("COMMA")
    val BACKSLASH = KDElementType("BACKSLASH")

    // Brackets
    val LBRACE = KDElementType("LBRACE")
    val RBRACE = KDElementType("RBRACE")
    val LBRACKET = KDElementType("LBRACKET")
    val RBRACKET = KDElementType("RBRACKET")
    val LPAREN = KDElementType("LPAREN")
    val RPAREN = KDElementType("RPAREN")
    val LANGLE = KDElementType("LANGLE")
    val RANGLE = KDElementType("RANGLE")

    // Error
    val BAD_CHARACTER = KDElementType("BAD_CHARACTER")

    // Token sets for grouping
    val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
    val STRINGS = TokenSet.create(STRING, RAW_STRING, BLOCK_STRING, CHAR)
    val NUMBERS = TokenSet.create(INTEGER, LONG, FLOAT, DOUBLE, DECIMAL, HEX_NUMBER, BINARY_NUMBER)
    val KEYWORDS = TokenSet.create(TRUE, FALSE, NIL, ON, OFF)
    val BRACKETS = TokenSet.create(LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN, LANGLE, RANGLE)
}