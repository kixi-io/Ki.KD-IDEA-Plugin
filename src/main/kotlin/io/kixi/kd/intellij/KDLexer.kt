package io.kixi.kd.intellij

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * Simple, robust lexer for Ki Data (KD) files.
 */
class KDLexer : LexerBase() {

    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentToken: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        advance()
    }

    override fun getState(): Int = 0
    override fun getTokenType(): IElementType? = currentToken
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    override fun advance() {
        tokenStart = tokenEnd
        if (tokenStart >= bufferEnd) {
            currentToken = null
            return
        }

        val ch = buffer[tokenStart]

        // Always advance at least one character
        tokenEnd = tokenStart + 1
        currentToken = when {
            ch.isWhitespace() -> {
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) tokenEnd++
                KDTokenTypes.WHITE_SPACE
            }
            ch == '#' -> {
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\n') tokenEnd++
                KDTokenTypes.LINE_COMMENT
            }
            ch == '/' && tokenEnd < bufferEnd && buffer[tokenEnd] == '/' -> {
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\n') tokenEnd++
                KDTokenTypes.LINE_COMMENT
            }
            ch == '/' && tokenEnd < bufferEnd && buffer[tokenEnd] == '*' -> {
                tokenEnd++ // skip *
                var depth = 1
                while (tokenEnd < bufferEnd && depth > 0) {
                    if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '*' && buffer[tokenEnd + 1] == '/') {
                        tokenEnd += 2
                        depth--
                    } else if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '/' && buffer[tokenEnd + 1] == '*') {
                        tokenEnd += 2
                        depth++
                    } else {
                        tokenEnd++
                    }
                }
                KDTokenTypes.BLOCK_COMMENT
            }
            ch == '"' -> {
                if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '"' && buffer[tokenEnd + 1] == '"') {
                    tokenEnd += 2 // skip ""
                    while (tokenEnd + 2 < bufferEnd) {
                        if (buffer[tokenEnd] == '"' && buffer[tokenEnd + 1] == '"' && buffer[tokenEnd + 2] == '"') {
                            tokenEnd += 3
                            break
                        }
                        tokenEnd++
                    }
                    if (tokenEnd + 2 >= bufferEnd) tokenEnd = bufferEnd
                    KDTokenTypes.BLOCK_STRING
                } else {
                    while (tokenEnd < bufferEnd && buffer[tokenEnd] != '"' && buffer[tokenEnd] != '\n') {
                        if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
                        tokenEnd++
                    }
                    if (tokenEnd < bufferEnd && buffer[tokenEnd] == '"') tokenEnd++
                    KDTokenTypes.STRING
                }
            }
            ch == '@' && tokenEnd < bufferEnd && buffer[tokenEnd] == '"' -> {
                tokenEnd++ // skip "
                if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '"' && buffer[tokenEnd + 1] == '"') {
                    tokenEnd += 2
                    while (tokenEnd + 2 < bufferEnd) {
                        if (buffer[tokenEnd] == '"' && buffer[tokenEnd + 1] == '"' && buffer[tokenEnd + 2] == '"') {
                            tokenEnd += 3
                            break
                        }
                        tokenEnd++
                    }
                    if (tokenEnd + 2 >= bufferEnd) tokenEnd = bufferEnd
                } else {
                    while (tokenEnd < bufferEnd && buffer[tokenEnd] != '"') tokenEnd++
                    if (tokenEnd < bufferEnd) tokenEnd++
                }
                KDTokenTypes.RAW_STRING
            }
            ch == '@' -> KDTokenTypes.AT
            ch == '\'' -> {
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '\\') {
                    tokenEnd++
                    if (tokenEnd < bufferEnd) tokenEnd++
                } else if (tokenEnd < bufferEnd && buffer[tokenEnd] != '\'') {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '\'') tokenEnd++
                KDTokenTypes.CHAR
            }
            ch == '`' -> {
                if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '`' && buffer[tokenEnd + 1] == '`') {
                    tokenEnd += 2
                    while (tokenEnd + 2 < bufferEnd) {
                        if (buffer[tokenEnd] == '`' && buffer[tokenEnd + 1] == '`' && buffer[tokenEnd + 2] == '`') {
                            tokenEnd += 3
                            break
                        }
                        tokenEnd++
                    }
                    if (tokenEnd + 2 >= bufferEnd) tokenEnd = bufferEnd
                } else {
                    while (tokenEnd < bufferEnd && buffer[tokenEnd] != '`') tokenEnd++
                    if (tokenEnd < bufferEnd) tokenEnd++
                }
                KDTokenTypes.STRING
            }
            ch == '<' -> {
                if (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetter() || buffer[tokenEnd] == '/')) {
                    while (tokenEnd < bufferEnd && buffer[tokenEnd] != '>') tokenEnd++
                    if (tokenEnd < bufferEnd) tokenEnd++
                    KDTokenTypes.URL
                } else {
                    KDTokenTypes.LANGLE
                }
            }
            isCurrencyPrefix(ch) -> {
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '_' || buffer[tokenEnd] == '.')) tokenEnd++
                KDTokenTypes.CURRENCY
            }
            ch.isDigit() || ((ch == '-' || ch == '+') && tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) -> {
                scanNumberLike()
            }
            ch == '.' && tokenEnd < bufferEnd && buffer[tokenEnd] == '.' -> {
                tokenEnd++
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '<') tokenEnd++
                KDTokenTypes.RANGE_OP
            }
            ch == '.' && tokenEnd < bufferEnd && buffer[tokenEnd].isLetter() -> {
                scanDotLiteral()
            }
            ch.isLetter() || ch == '_' -> {
                scanIdentifier()
            }
            ch == '=' -> KDTokenTypes.EQUALS
            ch == ':' -> KDTokenTypes.COLON
            ch == ';' -> KDTokenTypes.SEMICOLON
            ch == '\\' -> KDTokenTypes.BACKSLASH
            ch == '{' -> KDTokenTypes.LBRACE
            ch == '}' -> KDTokenTypes.RBRACE
            ch == '[' -> KDTokenTypes.LBRACKET
            ch == ']' -> KDTokenTypes.RBRACKET
            ch == '(' -> KDTokenTypes.LPAREN
            ch == ')' -> KDTokenTypes.RPAREN
            ch == '>' -> KDTokenTypes.RANGLE
            ch == '.' -> KDTokenTypes.DOT
            ch == ',' -> KDTokenTypes.COMMA
            else -> KDTokenTypes.BAD_CHARACTER
        }
    }

    private fun isCurrencyPrefix(ch: Char): Boolean {
        return ch == '$' || ch == '€' || ch == '¥' || ch == '£' || ch == '₿' || ch == 'Ξ'
    }

    private fun scanNumberLike(): IElementType {
        var hasSlash = false
        var hasAt = false
        var hasColon = false
        var dotCount = 0

        while (tokenEnd < bufferEnd) {
            val c = buffer[tokenEnd]
            when {
                c.isDigit() || c == '_' -> tokenEnd++
                c == '/' -> { hasSlash = true; tokenEnd++ }
                c == '@' -> { hasAt = true; tokenEnd++ }
                c == ':' -> { hasColon = true; tokenEnd++ }
                c == '.' && tokenEnd + 1 < bufferEnd && buffer[tokenEnd + 1] != '.' -> { dotCount++; tokenEnd++ }
                c == '-' || c == '+' -> {
                    if (hasAt) {
                        tokenEnd++
                        while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] == '/' || buffer[tokenEnd] == ':')) tokenEnd++
                    }
                    break
                }
                c.isLetter() -> {
                    // Could be unit suffix, type suffix, or duration unit
                    val suffixStart = tokenEnd
                    while (tokenEnd < bufferEnd && buffer[tokenEnd].isLetter()) tokenEnd++
                    val suffix = buffer.substring(suffixStart, tokenEnd)

                    if (suffix in listOf("day", "days", "h", "min", "s", "ms", "ns")) {
                        if (tokenEnd < bufferEnd && buffer[tokenEnd] == ':') {
                            while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == ':' || buffer[tokenEnd] == '.')) tokenEnd++
                        }
                        return KDTokenTypes.DURATION
                    }
                    if (suffix in listOf("L", "f", "F", "d", "D", "bd", "BD")) {
                        return when (suffix) {
                            "L" -> KDTokenTypes.LONG
                            "f", "F" -> KDTokenTypes.FLOAT
                            "d", "D" -> KDTokenTypes.DOUBLE
                            else -> KDTokenTypes.DECIMAL
                        }
                    }
                    // Assume quantity
                    return KDTokenTypes.QUANTITY
                }
                else -> break
            }
        }

        return when {
            hasSlash && hasAt -> KDTokenTypes.DATETIME
            hasSlash -> KDTokenTypes.DATE
            hasColon -> KDTokenTypes.DURATION
            dotCount >= 2 -> KDTokenTypes.VERSION
            dotCount == 1 -> KDTokenTypes.DOUBLE
            else -> KDTokenTypes.INTEGER
        }
    }

    private fun scanDotLiteral(): IElementType {
        val nameStart = tokenEnd
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isLetter()) tokenEnd++
        val name = buffer.substring(nameStart, tokenEnd).lowercase()

        if (name !in listOf("blob", "geo", "coordinate", "grid")) {
            return KDTokenTypes.IDENTIFIER
        }

        // Skip optional <Type>
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '<') {
            while (tokenEnd < bufferEnd && buffer[tokenEnd] != '>') tokenEnd++
            if (tokenEnd < bufferEnd) tokenEnd++
        }

        // Skip whitespace
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) tokenEnd++

        // Must have (
        if (tokenEnd >= bufferEnd || buffer[tokenEnd] != '(') {
            return KDTokenTypes.IDENTIFIER
        }

        tokenEnd++ // skip (
        var depth = 1
        while (tokenEnd < bufferEnd && depth > 0) {
            when (buffer[tokenEnd]) {
                '(' -> { depth++; tokenEnd++ }
                ')' -> { depth--; tokenEnd++ }
                '"' -> skipStringInDot()
                else -> tokenEnd++
            }
        }

        return when (name) {
            "blob" -> KDTokenTypes.BLOB
            "geo" -> KDTokenTypes.GEO
            "coordinate" -> KDTokenTypes.COORDINATE
            "grid" -> KDTokenTypes.GRID
            else -> KDTokenTypes.IDENTIFIER
        }
    }

    private fun skipStringInDot() {
        if (tokenEnd >= bufferEnd || buffer[tokenEnd] != '"') return
        tokenEnd++
        while (tokenEnd < bufferEnd && buffer[tokenEnd] != '"') {
            if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
            tokenEnd++
        }
        if (tokenEnd < bufferEnd) tokenEnd++
    }

    private fun scanIdentifier(): IElementType {
        while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] == '_' || buffer[tokenEnd] == '$')) {
            tokenEnd++
        }
        val text = buffer.substring(tokenStart, tokenEnd)

        // Check for URL
        if (tokenEnd + 2 < bufferEnd && buffer[tokenEnd] == ':' && buffer[tokenEnd + 1] == '/' && buffer[tokenEnd + 2] == '/') {
            while (tokenEnd < bufferEnd && !buffer[tokenEnd].isWhitespace() && buffer[tokenEnd] !in "{}[](),;") tokenEnd++
            return KDTokenTypes.URL
        }

        // Check for email
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '@') {
            tokenEnd++
            while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] == '.' || buffer[tokenEnd] == '-')) tokenEnd++
            return KDTokenTypes.EMAIL
        }

        // Check for namespace
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == ':' && (tokenEnd + 1 >= bufferEnd || buffer[tokenEnd + 1] != '=')) {
            return KDTokenTypes.NAMESPACE
        }

        return when (text) {
            "true", "on" -> KDTokenTypes.TRUE
            "false", "off" -> KDTokenTypes.FALSE
            "nil", "null" -> KDTokenTypes.NIL
            else -> KDTokenTypes.IDENTIFIER
        }
    }
}