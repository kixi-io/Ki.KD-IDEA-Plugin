package io.kixi.kd.intellij

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * Parser-based lexer for Ki Data (KD) files.
 *
 * This lexer uses parsing logic derived from the KDParser to accurately identify
 * tokens with proper context awareness. This allows it to distinguish between
 * tag names, attribute keys, annotation names, and other contextual elements.
 *
 * The KDParser has been tested with 1000+ unit tests, making this approach
 * more reliable than a simple regex-based lexer.
 *
 * Version 2.2.0 - Added support for .snip() literals.
 */
class KDLexer : LexerBase() {

    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentToken: IElementType? = null
    private var state: Int = STATE_TAG_START

    companion object {
        // Lexer states for context tracking
        const val STATE_TAG_START = 0          // At start of tag - next identifier is tag name
        const val STATE_AFTER_TAG_NAME = 1     // After tag name - values or attributes follow
        const val STATE_AFTER_AT = 2           // After @ - next identifier is annotation name
        const val STATE_IN_CHILDREN = 3        // Inside { } block
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.state = initialState
        advance()
    }

    override fun getState(): Int = state
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

        currentToken = scanToken()

        // Safety: ensure we always advance at least one character
        if (tokenEnd == tokenStart && tokenStart < bufferEnd) {
            tokenEnd = tokenStart + 1
            currentToken = KDTokenTypes.BAD_CHARACTER
        }
    }

    private fun peek(offset: Int = 0): Char? {
        val pos = tokenEnd + offset
        return if (pos in 0 until bufferEnd) buffer[pos] else null
    }

    private fun scanToken(): IElementType {
        val ch = peek() ?: return KDTokenTypes.BAD_CHARACTER

        // Whitespace - newline resets to tag start
        if (ch.isWhitespace()) {
            return scanWhitespace()
        }

        // Comments
        if (ch == '#') {
            return scanLineComment()
        }
        if (ch == '/' && peek(1) == '/') {
            return scanLineComment()
        }
        if (ch == '/' && peek(1) == '*') {
            return scanBlockComment()
        }

        // Annotation start
        if (ch == '@') {
            // Check if this is @" (raw string) or @annotation
            if (peek(1) == '"') {
                return scanRawString()
            }
            tokenEnd++
            state = STATE_AFTER_AT
            return KDTokenTypes.AT
        }

        // After @ - this is an annotation name
        if (state == STATE_AFTER_AT && isIdentifierStart(ch)) {
            return scanAnnotationName()
        }

        // Strings
        if (ch == '"') {
            state = STATE_AFTER_TAG_NAME
            return if (peek(1) == '"' && peek(2) == '"') {
                scanBlockString()
            } else {
                scanSimpleString()
            }
        }

        // Backtick strings
        if (ch == '`') {
            state = STATE_AFTER_TAG_NAME
            return scanBacktickString()
        }

        // Character literals
        if (ch == '\'') {
            state = STATE_AFTER_TAG_NAME
            return scanChar()
        }

        // URL in angle brackets
        if (ch == '<') {
            val next = peek(1)
            if (next != null && (next.isLetter() || next == '/')) {
                state = STATE_AFTER_TAG_NAME
                return scanBracketedUrl()
            }
            tokenEnd++
            return KDTokenTypes.LANGLE
        }

        // Currency prefix ($, €, ¥, £, ₿, Ξ)
        if (isCurrencyPrefix(ch)) {
            state = STATE_AFTER_TAG_NAME
            return scanCurrency()
        }

        // Signed currency: -$100, +€50
        if ((ch == '-' || ch == '+') && peek(1)?.let { isCurrencyPrefix(it) } == true) {
            state = STATE_AFTER_TAG_NAME
            return scanCurrency()
        }

        // Numbers, dates, durations, versions, quantities
        if (ch.isDigit() || ((ch == '-' || ch == '+') && peek(1)?.isDigit() == true)) {
            state = STATE_AFTER_TAG_NAME
            return scanNumberLike()
        }

        // Dot literals: .blob(), .geo(), .coordinate(), .grid(), .snip()
        if (ch == '.' && peek(1)?.isLetter() == true) {
            val result = tryScanDotLiteral()
            if (result != null) {
                state = STATE_AFTER_TAG_NAME
                return result
            }
        }

        // Range operators (check before identifiers)
        if (ch == '.' && peek(1) == '.') {
            return scanRangeOperator()
        }
        if (ch == '<' && peek(1) == '.') {
            return scanRangeOperator()
        }

        // Identifiers - context determines if tag name, attribute key, or value
        if (isIdentifierStart(ch)) {
            return scanIdentifierWithContext()
        }

        // Underscore (open range marker or identifier)
        if (ch == '_') {
            tokenEnd++
            while (peek()?.let { isIdentifierPart(it) } == true) tokenEnd++
            return KDTokenTypes.IDENTIFIER
        }

        // Punctuation and operators
        return scanPunctuation(ch)
    }

    // ========================================================================
    // Whitespace and Comments
    // ========================================================================

    private fun scanWhitespace(): IElementType {
        var hadNewline = false
        while (peek()?.isWhitespace() == true) {
            if (peek() == '\n') hadNewline = true
            tokenEnd++
        }
        if (hadNewline) {
            state = STATE_TAG_START
        }
        return KDTokenTypes.WHITE_SPACE
    }

    private fun scanLineComment(): IElementType {
        while (peek() != null && peek() != '\n') tokenEnd++
        return KDTokenTypes.LINE_COMMENT
    }

    private fun scanBlockComment(): IElementType {
        tokenEnd += 2 // skip /*
        var depth = 1
        while (depth > 0 && peek() != null) {
            if (peek() == '/' && peek(1) == '*') {
                tokenEnd += 2
                depth++
            } else if (peek() == '*' && peek(1) == '/') {
                tokenEnd += 2
                depth--
            } else {
                tokenEnd++
            }
        }
        return KDTokenTypes.BLOCK_COMMENT
    }

    // ========================================================================
    // Strings
    // ========================================================================

    private fun scanSimpleString(): IElementType {
        tokenEnd++ // skip opening "
        while (peek() != null && peek() != '"' && peek() != '\n') {
            if (peek() == '\\' && peek(1) != null) {
                tokenEnd += 2
            } else {
                tokenEnd++
            }
        }
        if (peek() == '"') tokenEnd++
        return KDTokenTypes.STRING
    }

    private fun scanRawString(): IElementType {
        tokenEnd += 2 // skip @"
        if (peek() == '"' && peek(1) == '"') {
            // Raw block string @"""..."""
            tokenEnd += 2
            while (peek() != null) {
                if (peek() == '"' && peek(1) == '"' && peek(2) == '"') {
                    tokenEnd += 3
                    break
                }
                tokenEnd++
            }
        } else {
            // Simple raw string @"..."
            while (peek() != null && peek() != '"' && peek() != '\n') tokenEnd++
            if (peek() == '"') tokenEnd++
        }
        return KDTokenTypes.RAW_STRING
    }

    private fun scanBlockString(): IElementType {
        tokenEnd += 3 // skip """
        while (peek() != null) {
            if (peek() == '"' && peek(1) == '"' && peek(2) == '"') {
                tokenEnd += 3
                break
            }
            tokenEnd++
        }
        return KDTokenTypes.BLOCK_STRING
    }

    private fun scanBacktickString(): IElementType {
        if (peek() == '`' && peek(1) == '`' && peek(2) == '`') {
            tokenEnd += 3
            while (peek() != null) {
                if (peek() == '`' && peek(1) == '`' && peek(2) == '`') {
                    tokenEnd += 3
                    break
                }
                tokenEnd++
            }
        } else {
            tokenEnd++ // skip opening `
            while (peek() != null && peek() != '`') tokenEnd++
            if (peek() == '`') tokenEnd++
        }
        return KDTokenTypes.STRING
    }

    private fun scanChar(): IElementType {
        tokenEnd++ // skip opening '
        if (peek() == '\\') {
            tokenEnd++
            if (peek() == 'u') {
                tokenEnd++
                repeat(4) { if (peek()?.isLetterOrDigit() == true) tokenEnd++ }
            } else if (peek() != null) {
                tokenEnd++
            }
        } else if (peek() != null && peek() != '\'') {
            tokenEnd++
        }
        if (peek() == '\'') tokenEnd++
        return KDTokenTypes.CHAR
    }

    // ========================================================================
    // URLs and Emails
    // ========================================================================

    private fun scanBracketedUrl(): IElementType {
        tokenEnd++ // skip <
        while (peek() != null && peek() != '>') tokenEnd++
        if (peek() == '>') tokenEnd++
        return KDTokenTypes.URL
    }

    // ========================================================================
    // Currency
    // ========================================================================

    private fun isCurrencyPrefix(ch: Char): Boolean {
        return ch == '$' || ch == '€' || ch == '¥' || ch == '£' || ch == '₿' || ch == 'Ξ'
    }

    private fun scanCurrency(): IElementType {
        // Handle optional sign
        if (peek() == '-' || peek() == '+') tokenEnd++
        // Skip currency symbol
        if (peek()?.let { isCurrencyPrefix(it) } == true) tokenEnd++
        // Scan number
        while (peek()?.let { it.isDigit() || it == '_' || it == '.' } == true) tokenEnd++
        // Handle exponent
        if (peek() == 'e' || peek() == 'E') {
            tokenEnd++
            if (peek() == '+' || peek() == '-') tokenEnd++
            while (peek()?.isDigit() == true) tokenEnd++
        }
        // Handle type specifier
        if (peek() == ':' && peek(1)?.let { it == 'i' || it == 'L' || it == 'd' || it == 'f' } == true) {
            tokenEnd += 2
        }
        return KDTokenTypes.CURRENCY
    }

    // ========================================================================
    // Numbers, Dates, Durations, Versions, Quantities
    // ========================================================================

    private fun scanNumberLike(): IElementType {
        // Handle sign
        if (peek() == '-' || peek() == '+') tokenEnd++

        // Hex
        if (peek() == '0' && (peek(1) == 'x' || peek(1) == 'X')) {
            tokenEnd += 2
            while (peek()?.let { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' || it == '_' } == true) tokenEnd++
            if (peek() == 'L') tokenEnd++
            return KDTokenTypes.HEX_NUMBER
        }

        // Binary
        if (peek() == '0' && (peek(1) == 'b' || peek(1) == 'B')) {
            tokenEnd += 2
            while (peek()?.let { it == '0' || it == '1' || it == '_' } == true) tokenEnd++
            if (peek() == 'L') tokenEnd++
            return KDTokenTypes.BINARY_NUMBER
        }

        var hasSlash = false
        var hasAt = false
        var hasColon = false
        var dotCount = 0

        // Scan main body
        while (peek() != null) {
            val c = peek()!!
            when {
                c.isDigit() || c == '_' -> tokenEnd++
                c == '/' -> { hasSlash = true; tokenEnd++ }
                c == '@' -> { hasAt = true; tokenEnd++ }
                c == ':' -> { hasColon = true; tokenEnd++ }
                c == '.' && peek(1) != '.' -> { dotCount++; tokenEnd++ }
                c == '-' || c == '+' -> {
                    if (hasAt) {
                        // Timezone offset or KiTZ suffix
                        tokenEnd++
                        while (peek()?.let { it.isLetterOrDigit() || it == '/' || it == ':' || it == '_' } == true) {
                            tokenEnd++
                        }
                    }
                    break
                }
                else -> break
            }
        }

        // Date or DateTime
        if (hasSlash) {
            return if (hasAt) KDTokenTypes.DATETIME else KDTokenTypes.DATE
        }

        // Duration (compound format like 12:30:00)
        if (hasColon && !hasAt) {
            // Consume trailing letters (day/days)
            while (peek()?.isLetter() == true) tokenEnd++
            return KDTokenTypes.DURATION
        }

        // Check suffix
        val suffix = peek()

        // Duration units
        if (suffix != null && suffix.isLetter()) {
            val suffixStart = tokenEnd
            var i = 0
            while (peek(i)?.isLetter() == true && i < 10) i++
            val suffixText = buffer.substring(suffixStart, minOf(suffixStart + i, bufferEnd))

            when {
                suffixText.startsWith("day") -> {
                    while (peek()?.isLetter() == true) tokenEnd++
                    if (peek() == ':') {
                        // Compound duration with day prefix
                        while (peek()?.let { it.isDigit() || it == ':' || it == '.' || it == '_' } == true) {
                            tokenEnd++
                        }
                    }
                    return KDTokenTypes.DURATION
                }
                suffixText == "h" || suffixText == "min" || suffixText == "s" ||
                        suffixText == "ms" || suffixText == "ns" -> {
                    while (peek()?.isLetter() == true) tokenEnd++
                    return KDTokenTypes.DURATION
                }
            }
        }

        // Type suffixes
        when {
            suffix == 'L' -> { tokenEnd++; return KDTokenTypes.LONG }
            suffix == 'f' || suffix == 'F' -> { tokenEnd++; return KDTokenTypes.FLOAT }
            (suffix == 'd' || suffix == 'D') && peek(1) != 'a' -> { tokenEnd++; return KDTokenTypes.DOUBLE }
            (suffix == 'b' || suffix == 'B') && (peek(1) == 'd' || peek(1) == 'D') -> {
                tokenEnd += 2
                return KDTokenTypes.DECIMAL
            }
        }

        // Quantity (number + unit)
        if (suffix != null && (suffix.isLetter() || suffix == '°' || suffix == 'ℓ')) {
            while (peek()?.let { it.isLetter() || it == '°' || it == 'ℓ' || it == '²' || it == '³' } == true) {
                tokenEnd++
            }
            // Type specifier
            if (peek() == ':' && peek(1)?.let { it == 'i' || it == 'L' || it == 'd' || it == 'f' } == true) {
                tokenEnd += 2
            }
            return KDTokenTypes.QUANTITY
        }

        // Version (2+ dots, or 1+ dots with qualifier)
        if (dotCount >= 2) {
            if (peek() == '-') {
                tokenEnd++
                while (peek()?.let { it.isLetterOrDigit() || it == '-' || it == '_' } == true) tokenEnd++
            }
            return KDTokenTypes.VERSION
        }
        if (dotCount >= 1 && peek() == '-') {
            tokenEnd++
            while (peek()?.let { it.isLetterOrDigit() || it == '-' || it == '_' } == true) tokenEnd++
            return KDTokenTypes.VERSION
        }

        return if (dotCount > 0) KDTokenTypes.DOUBLE else KDTokenTypes.INTEGER
    }

    // ========================================================================
    // Dot Literals (.blob, .geo, .coordinate, .grid, .snip)
    // ========================================================================

    private fun tryScanDotLiteral(): IElementType? {
        val startPos = tokenEnd
        tokenEnd++ // skip .

        val nameStart = tokenEnd
        while (peek()?.isLetter() == true) tokenEnd++
        val name = buffer.substring(nameStart, tokenEnd).lowercase()

        // Recognize all dot literals including snip
        if (name !in listOf("blob", "geo", "coordinate", "grid", "snip")) {
            tokenEnd = startPos
            return null
        }

        // Optional type parameter for .grid<Type>
        if (name == "grid" && peek() == '<') {
            tokenEnd++
            while (peek() != null && peek() != '>') tokenEnd++
            if (peek() == '>') tokenEnd++
        }

        // Skip whitespace before (
        while (peek()?.let { it == ' ' || it == '\t' } == true) tokenEnd++

        if (peek() != '(') {
            tokenEnd = startPos
            return null
        }

        // Consume parentheses content
        tokenEnd++ // skip (
        var depth = 1
        while (depth > 0 && peek() != null) {
            when (peek()) {
                '"' -> skipStringInDotLiteral()
                '\'' -> skipCharInDotLiteral()
                '(' -> { depth++; tokenEnd++ }
                ')' -> { depth--; tokenEnd++ }
                else -> tokenEnd++
            }
        }

        return when (name) {
            "blob" -> KDTokenTypes.BLOB
            "geo" -> KDTokenTypes.GEO
            "coordinate" -> KDTokenTypes.COORDINATE
            "grid" -> KDTokenTypes.GRID
            "snip" -> KDTokenTypes.SNIP
            else -> KDTokenTypes.IDENTIFIER
        }
    }

    private fun skipStringInDotLiteral() {
        if (peek() != '"') return
        tokenEnd++
        if (peek() == '"' && peek(1) == '"') {
            tokenEnd += 2
            while (peek() != null) {
                if (peek() == '"' && peek(1) == '"' && peek(2) == '"') {
                    tokenEnd += 3
                    return
                }
                tokenEnd++
            }
        } else {
            while (peek() != null && peek() != '"' && peek() != '\n') {
                if (peek() == '\\') tokenEnd++
                tokenEnd++
            }
            if (peek() == '"') tokenEnd++
        }
    }

    private fun skipCharInDotLiteral() {
        if (peek() != '\'') return
        tokenEnd++
        if (peek() == '\\') tokenEnd++
        if (peek() != null && peek() != '\'') tokenEnd++
        if (peek() == '\'') tokenEnd++
    }

    // ========================================================================
    // Range Operators
    // ========================================================================

    private fun scanRangeOperator(): IElementType {
        return when {
            peek() == '<' && peek(1) == '.' && peek(2) == '.' && peek(3) == '<' -> {
                tokenEnd += 4
                KDTokenTypes.RANGE_EXCLUSIVE
            }
            peek() == '<' && peek(1) == '.' && peek(2) == '.' -> {
                tokenEnd += 3
                KDTokenTypes.RANGE_EX_LEFT
            }
            peek() == '.' && peek(1) == '.' && peek(2) == '<' -> {
                tokenEnd += 3
                KDTokenTypes.RANGE_EX_RIGHT
            }
            peek() == '.' && peek(1) == '.' -> {
                tokenEnd += 2
                KDTokenTypes.RANGE_OP
            }
            else -> {
                tokenEnd++
                KDTokenTypes.DOT
            }
        }
    }

    // ========================================================================
    // Identifiers with Context
    // ========================================================================

    private fun isIdentifierStart(ch: Char): Boolean {
        return ch.isLetter() || ch == '_' || ch.isSurrogate()
    }

    private fun isIdentifierPart(ch: Char): Boolean {
        return ch.isLetterOrDigit() || ch == '_' || ch == '$' || ch.isSurrogate()
    }

    private fun scanAnnotationName(): IElementType {
        while (peek()?.let { isIdentifierPart(it) } == true) tokenEnd++

        // Handle namespace in annotation (ns:name)
        if (peek() == ':' && peek(1) != '=' && peek(1) != '/') {
            tokenEnd++ // skip :
            while (peek()?.let { isIdentifierPart(it) } == true) tokenEnd++
        }

        state = STATE_TAG_START
        return KDTokenTypes.ANNOTATION
    }

    private fun scanIdentifierWithContext(): IElementType {
        val startPos = tokenEnd

        // Consume identifier
        while (peek()?.let { isIdentifierPart(it) } == true) tokenEnd++
        val text = buffer.substring(startPos, tokenEnd)

        // Check for naked URL: identifier://...
        if (peek() == ':' && peek(1) == '/' && peek(2) == '/') {
            while (peek() != null && !peek()!!.isWhitespace() &&
                peek() !in listOf('{', '}', '[', ']', '(', ')', ',', ';')) {
                tokenEnd++
            }
            state = STATE_AFTER_TAG_NAME
            return KDTokenTypes.URL
        }

        // Check for email: identifier followed by more local-part chars then @domain
        if (peek() == '@' || peek() == '.' || peek() == '+' || peek() == '-' || peek() == '%') {
            val savedPos = tokenEnd
            // Continue consuming local part
            while (peek()?.let { it.isLetterOrDigit() || it == '.' || it == '+' || it == '-' || it == '%' || it == '_' } == true) {
                tokenEnd++
            }
            if (peek() == '@') {
                tokenEnd++ // skip @
                val domainStart = tokenEnd
                while (peek()?.let { it.isLetterOrDigit() || it == '-' || it == '.' } == true) {
                    tokenEnd++
                }
                val domain = buffer.substring(domainStart, tokenEnd)
                if (domain.contains('.') && !domain.endsWith('.')) {
                    state = STATE_AFTER_TAG_NAME
                    return KDTokenTypes.EMAIL
                }
            }
            // Not a valid email, restore
            tokenEnd = savedPos
        }

        // Check for namespace prefix (ns:name)
        if (peek() == ':' && peek(1) != '=' && peek(1) != '/') {
            tokenEnd++ // consume :
            val nameStart = tokenEnd
            while (peek()?.let { isIdentifierPart(it) } == true) tokenEnd++

            // Now check what follows the full ns:name
            val afterName = lookAheadPastWhitespace()

            return if (afterName == '=') {
                // This is an attribute key with namespace
                state = STATE_AFTER_TAG_NAME
                KDTokenTypes.ATTRIBUTE_KEY
            } else if (state == STATE_TAG_START || state == STATE_IN_CHILDREN) {
                // Tag name with namespace
                state = STATE_AFTER_TAG_NAME
                KDTokenTypes.TAG_NAME
            } else {
                state = STATE_AFTER_TAG_NAME
                KDTokenTypes.IDENTIFIER
            }
        }

        // Check what follows to determine token type
        val nextChar = lookAheadPastWhitespace()

        // Keywords
        when (text) {
            "true", "on" -> {
                state = STATE_AFTER_TAG_NAME
                return if (text == "true") KDTokenTypes.TRUE else KDTokenTypes.ON
            }
            "false", "off" -> {
                state = STATE_AFTER_TAG_NAME
                return if (text == "false") KDTokenTypes.FALSE else KDTokenTypes.OFF
            }
            "nil", "null" -> {
                state = STATE_AFTER_TAG_NAME
                return KDTokenTypes.NIL
            }
            "Infinity", "NaN" -> {
                state = STATE_AFTER_TAG_NAME
                return KDTokenTypes.DOUBLE
            }
        }

        // If followed by =, it's an attribute key
        if (nextChar == '=') {
            state = STATE_AFTER_TAG_NAME
            return KDTokenTypes.ATTRIBUTE_KEY
        }

        // If at tag start position or in children block, it's a tag name
        if (state == STATE_TAG_START || state == STATE_IN_CHILDREN) {
            state = STATE_AFTER_TAG_NAME
            return KDTokenTypes.TAG_NAME
        }

        // Otherwise, it's a general identifier (value)
        state = STATE_AFTER_TAG_NAME
        return KDTokenTypes.IDENTIFIER
    }

    /**
     * Looks ahead past whitespace (but not newlines) to see the next character.
     * Does NOT advance tokenEnd.
     */
    private fun lookAheadPastWhitespace(): Char? {
        var pos = tokenEnd
        while (pos < bufferEnd && (buffer[pos] == ' ' || buffer[pos] == '\t')) {
            pos++
        }
        return if (pos < bufferEnd) buffer[pos] else null
    }

    // ========================================================================
    // Punctuation
    // ========================================================================

    private fun scanPunctuation(ch: Char): IElementType {
        tokenEnd++
        return when (ch) {
            '=' -> KDTokenTypes.EQUALS
            ':' -> KDTokenTypes.COLON
            ';' -> {
                state = STATE_TAG_START
                KDTokenTypes.SEMICOLON
            }
            '\\' -> KDTokenTypes.BACKSLASH
            '{' -> {
                state = STATE_IN_CHILDREN
                KDTokenTypes.LBRACE
            }
            '}' -> {
                state = STATE_TAG_START
                KDTokenTypes.RBRACE
            }
            '[' -> KDTokenTypes.LBRACKET
            ']' -> KDTokenTypes.RBRACKET
            '(' -> KDTokenTypes.LPAREN
            ')' -> KDTokenTypes.RPAREN
            '>' -> KDTokenTypes.RANGLE
            '.' -> KDTokenTypes.DOT
            ',' -> KDTokenTypes.COMMA
            else -> KDTokenTypes.BAD_CHARACTER
        }
    }
}