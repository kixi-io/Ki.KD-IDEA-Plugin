package io.kixi.kd.intellij

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

/**
 * Color settings page for Ki Data (KD) syntax highlighting.
 *
 * This page appears in Settings > Editor > Color Scheme > KD and allows
 * users to customize the colors used for syntax highlighting.
 *
 * Version 2.2.0 - Added Snip literals to the color settings.
 */
class KDColorSettingsPage : ColorSettingsPage {

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Comments", KDSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Strings", KDSyntaxHighlighter.STRING),
            AttributesDescriptor("Characters", KDSyntaxHighlighter.CHAR),
            AttributesDescriptor("Numbers", KDSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Quantities", KDSyntaxHighlighter.QUANTITY),
            AttributesDescriptor("Units", KDSyntaxHighlighter.UNIT),
            AttributesDescriptor("Currency", KDSyntaxHighlighter.CURRENCY),
            AttributesDescriptor("Keywords", KDSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Operators", KDSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Namespace", KDSyntaxHighlighter.NAMESPACE),
            AttributesDescriptor("Annotations", KDSyntaxHighlighter.ANNOTATION),
            AttributesDescriptor("Tag Names", KDSyntaxHighlighter.TAG_NAME),
            AttributesDescriptor("Attribute Keys", KDSyntaxHighlighter.ATTRIBUTE_KEY),
            AttributesDescriptor("Identifiers", KDSyntaxHighlighter.IDENTIFIER),
            AttributesDescriptor("Date/Time", KDSyntaxHighlighter.DATETIME),
            AttributesDescriptor("Duration", KDSyntaxHighlighter.DURATION),
            AttributesDescriptor("Version", KDSyntaxHighlighter.VERSION),
            AttributesDescriptor("URL", KDSyntaxHighlighter.URL),
            AttributesDescriptor("Email", KDSyntaxHighlighter.EMAIL),
            AttributesDescriptor("Special Literals", KDSyntaxHighlighter.SPECIAL_LITERAL),
            AttributesDescriptor("Snip Literals", KDSyntaxHighlighter.SNIP),
            AttributesDescriptor("Punctuation", KDSyntaxHighlighter.PUNCTUATION),
            AttributesDescriptor("Brackets", KDSyntaxHighlighter.BRACKETS),
            AttributesDescriptor("Bad Character", KDSyntaxHighlighter.BAD_CHARACTER)
        )

        private val DEMO_TEXT = """
# Ki Data (KD) Example File
// Line comment with //
/* Block comment
   spanning multiple lines */

# Tags and values
tagName "Hello, World!"
tagWithNamespace ns:name "value"

# Boolean keywords
enabled true
disabled false
status nil

# Numbers
count 42
big_number 1_000_000L
pi 3.14159
rate 0.5f
price 99.99bd
hex 0xFF00FF
binary 0b10101010

# Dates and times
birthday 1990/6/15
meeting 2024/3/15@14:30:00
scheduled 2024/3/15@9:00:00-US/PST

# Durations
timeout 30s
delay 5min
uptime 2day:14:30:00

# Versions
version 2.5.3
release 1.0.0-beta-2

# Quantities
distance 5.2km
weight 75.5kg
temperature 22.5°C

# Currency
amount ${'$'}100.00
euros €50.25
bitcoin ₿0.005

# Strings
simple "Hello"
raw @"C:\path\to\file"
block ${"\"\"\""}
    Multi-line
    block string
${"\"\"\""}
char 'A'

# URL and Email
website <https://example.com>
contact user@example.com

# Special literals
data .blob(SGVsbG8gV29ybGQ=)
location .geo(35.6762, 139.6503)
cell .coordinate(x=5, y=10)

# Snip literals (external document references)
.snip(components/navbar)
.snip(../shared/database)
.snip("path with spaces/config")
.snip(pages/home, expand=true)

# Collections
list [1, 2, 3, 4, 5]
map [name="Alice", age=30]

# Ranges
range 1..100
exclusive 0..<10

# Annotations
@test
@config(debug=true, log="output.txt")
myTag "value" enabled=true

# Tag with children
parent {
    child1 "first"
    child2 "second"
    # Nested snip
    .snip(partials/footer)
}

# App structure with snips
app {
    header {
        .snip(components/navbar)
    }
    content {
        .snip(pages/home, expand=true)
    }
    footer {
        .snip(components/footer)
    }
}
"""
    }

    override fun getIcon(): Icon = KDIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = KDSyntaxHighlighter()

    override fun getDemoText(): String = DEMO_TEXT

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Ki Data (KD)"
}