package com.nasahacker.nasaeditor.view.widget

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nasahacker.nasaeditor.R
import com.nasahacker.nasaeditor.util.Constants.COMMENT_TYPE
import com.nasahacker.nasaeditor.util.Constants.CSS_PROPERTY
import com.nasahacker.nasaeditor.util.Constants.CSS_VALUE
import com.nasahacker.nasaeditor.util.Constants.HTML_TAG
import com.nasahacker.nasaeditor.util.Constants.JS_KEYWORD
import com.nasahacker.nasaeditor.util.Constants.NUMBER_TYPE
import com.nasahacker.nasaeditor.util.Constants.STRING_TYPE
import java.util.regex.Pattern

class CodeEditorView : AppCompatEditText {

    private var htmlTagColor: Int = 0
    private var cssPropertyColor: Int = 0
    private var cssValueColor: Int = 0
    private var jsKeywordColor: Int = 0
    private var stringColor: Int = 0
    private var numberColor: Int = 0
    private var commentColor: Int = 0

    private var currentTheme: Theme = Theme.VS_CODE // Default theme

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        setThemeColors(context, currentTheme)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                highlightSyntax(editable)
            }
        })
    }

    fun setTheme(context: Context, theme: Theme) {
        currentTheme = theme
        setThemeColors(context, theme)
        invalidate() // Refresh the view
    }

    private fun setThemeColors(context: Context, theme: Theme) {
        htmlTagColor = getColorForTheme(context, theme, HTML_TAG)
        cssPropertyColor = getColorForTheme(context, theme, CSS_PROPERTY)
        cssValueColor = getColorForTheme(context, theme, CSS_VALUE)
        jsKeywordColor = getColorForTheme(context, theme, JS_KEYWORD)
        stringColor = getColorForTheme(context, theme, STRING_TYPE)
        numberColor = getColorForTheme(context, theme, NUMBER_TYPE)
        commentColor = getColorForTheme(context, theme, COMMENT_TYPE)
    }

    private fun getColorForTheme(context: Context, theme: Theme, colorType: String): Int {
        val colorResId = when (theme) {
            Theme.DRACULA -> when (colorType) {
                HTML_TAG -> R.color.dracula_html_tag
                CSS_PROPERTY -> R.color.dracula_css_property
                CSS_VALUE -> R.color.dracula_css_value
                JS_KEYWORD -> R.color.dracula_js_keyword
                STRING_TYPE -> R.color.dracula_string
                NUMBER_TYPE -> R.color.dracula_number
                COMMENT_TYPE -> R.color.dracula_comment
                else -> R.color.black // Default fallback
            }

            Theme.VS_CODE -> when (colorType) {
                HTML_TAG -> R.color.vs_code_html_tag
                CSS_PROPERTY -> R.color.vs_code_css_property
                CSS_VALUE -> R.color.vs_code_css_value
                JS_KEYWORD -> R.color.vs_code_js_keyword
                STRING_TYPE -> R.color.vs_code_string
                NUMBER_TYPE -> R.color.vs_code_number
                COMMENT_TYPE -> R.color.vs_code_comment
                else -> R.color.black // Default fallback
            }

            Theme.MONOKAI -> when (colorType) {
                HTML_TAG -> R.color.monokai_html_tag
                CSS_PROPERTY -> R.color.monokai_css_property
                CSS_VALUE -> R.color.monokai_css_value
                JS_KEYWORD -> R.color.monokai_js_keyword
                STRING_TYPE -> R.color.monokai_string
                NUMBER_TYPE -> R.color.monokai_number
                COMMENT_TYPE -> R.color.monokai_comment
                else -> R.color.black // Default fallback
            }

            Theme.SOLARIZED_DARK -> when (colorType) {
                HTML_TAG -> R.color.solarized_dark_html_tag
                CSS_PROPERTY -> R.color.solarized_dark_css_property
                CSS_VALUE -> R.color.solarized_dark_css_value
                JS_KEYWORD -> R.color.solarized_dark_js_keyword
                STRING_TYPE -> R.color.solarized_dark_string
                NUMBER_TYPE -> R.color.solarized_dark_number
                COMMENT_TYPE -> R.color.solarized_dark_comment
                else -> R.color.black // Default fallback
            }

            Theme.SOLARIZED_LIGHT -> when (colorType) {
                HTML_TAG -> R.color.solarized_light_html_tag
                CSS_PROPERTY -> R.color.solarized_light_css_property
                CSS_VALUE -> R.color.solarized_light_css_value
                JS_KEYWORD -> R.color.solarized_light_js_keyword
                STRING_TYPE -> R.color.solarized_light_string
                NUMBER_TYPE -> R.color.solarized_light_number
                COMMENT_TYPE -> R.color.solarized_light_comment
                else -> R.color.black // Default fallback
            }

            Theme.OCEANIC_NEXT -> when (colorType) {
                HTML_TAG -> R.color.oceanic_next_html_tag
                CSS_PROPERTY -> R.color.oceanic_next_css_property
                CSS_VALUE -> R.color.oceanic_next_css_value
                JS_KEYWORD -> R.color.oceanic_next_js_keyword
                STRING_TYPE -> R.color.oceanic_next_string
                NUMBER_TYPE -> R.color.oceanic_next_number
                COMMENT_TYPE -> R.color.oceanic_next_comment
                else -> R.color.black // Default fallback
            }
        }
        return ContextCompat.getColor(context, colorResId)
    }

    private fun highlightSyntax(editable: Editable) {
        clearSpans(editable)

        // HTML tags
        val htmlTags = arrayOf(
            "html",
            "head",
            "body",
            "div",
            "span",
            "h1",
            "h2",
            "h3",
            "h4",
            "h5",
            "h6",
            "p",
            "a",
            "img",
            "ul",
            "ol",
            "li",
            "table",
            "tr",
            "td",
            "th",
            "form",
            "input",
            "button",
            "label",
            "meta",
            "link",
            "title",
            "script",
            "style",
            "footer",
            "header",
            "nav",
            "section",
            "article",
            "aside",
            "details",
            "summary",
            "blockquote",
            "cite",
            "code",
            "pre",
            "em",
            "strong",
            "small",
            "sub",
            "sup",
            "abbr",
            "address",
            "b",
            "i",
            "u",
            "q",
            "del",
            "ins",
            "kbd",
            "s",
            "mark",
            "figure",
            "figcaption",
            "audio",
            "video",
            "source",
            "track",
            "canvas",
            "svg",
            "iframe",
            "object",
            "embed",
            "param",
            "map",
            "area",
            "time",
            "progress",
            "meter",
            "output",
            "select",
            "option",
            "optgroup",
            "textarea",
            "fieldset",
            "legend",
            "datalist",
            "keygen",
            "wbr",
            "br",
            "hr",
            "base",
            "col",
            "colgroup",
            "caption",
            "thead",
            "tbody",
            "tfoot",
            "bdo",
            "bdi",
            "ruby",
            "rt",
            "rp",
            "data",
            "main",
            "picture",
            "source",
            "template",
            "slot",
            "summary",
            "menu",
            "menuitem"
        )

        for (tag in htmlTags) {
            highlightPattern(editable, "</?$tag\\b[^>]*>", htmlTagColor)
        }

        // CSS properties and values
        val cssProperties = arrayOf(
            "color",
            "background-color",
            "background-image",
            "background-position",
            "background-size",
            "background-repeat",
            "background-attachment",
            "background-origin",
            "background-clip",
            "border",
            "border-color",
            "border-width",
            "border-style",
            "border-radius",
            "border-top",
            "border-right",
            "border-bottom",
            "border-left",
            "border-top-color",
            "border-right-color",
            "border-bottom-color",
            "border-left-color",
            "border-top-width",
            "border-right-width",
            "border-bottom-width",
            "border-left-width",
            "border-top-style",
            "border-right-style",
            "border-bottom-style",
            "border-left-style",
            "padding",
            "padding-top",
            "padding-right",
            "padding-bottom",
            "padding-left",
            "margin",
            "margin-top",
            "margin-right",
            "margin-bottom",
            "margin-left",
            "width",
            "height",
            "max-width",
            "min-width",
            "max-height",
            "min-height",
            "font",
            "font-family",
            "font-size",
            "font-weight",
            "font-style",
            "font-variant",
            "font-size-adjust",
            "font-stretch",
            "line-height",
            "letter-spacing",
            "word-spacing",
            "text-align",
            "text-decoration",
            "text-transform",
            "text-indent",
            "text-shadow",
            "text-overflow",
            "white-space",
            "word-break",
            "word-wrap",
            "overflow",
            "overflow-x",
            "overflow-y",
            "display",
            "visibility",
            "position",
            "top",
            "right",
            "bottom",
            "left",
            "z-index",
            "float",
            "clear",
            "clip",
            "content",
            "counter-increment",
            "counter-reset",
            "cursor",
            "direction",
            "flex",
            "flex-basis",
            "flex-direction",
            "flex-flow",
            "flex-grow",
            "flex-shrink",
            "flex-wrap",
            "justify-content",
            "align-items",
            "align-self",
            "align-content",
            "order",
            "grid",
            "grid-area",
            "grid-template",
            "grid-template-rows",
            "grid-template-columns",
            "grid-template-areas",
            "grid-auto-rows",
            "grid-auto-columns",
            "grid-auto-flow",
            "grid-gap",
            "grid-row",
            "grid-column",
            "grid-row-start",
            "grid-row-end",
            "grid-column-start",
            "grid-column-end",
            "gap",
            "row-gap",
            "column-gap",
            "align-items",
            "justify-items",
            "align-content",
            "justify-content",
            "transform",
            "transform-origin",
            "transition",
            "transition-delay",
            "transition-duration",
            "transition-property",
            "transition-timing-function",
            "animation",
            "animation-name",
            "animation-duration",
            "animation-timing-function",
            "animation-delay",
            "animation-iteration-count",
            "animation-direction",
            "animation-fill-mode",
            "animation-play-state",
            "backface-visibility",
            "box-shadow",
            "box-sizing",
            "resize",
            "object-fit",
            "object-position",
            "opacity",
            "outline",
            "outline-color",
            "outline-style",
            "outline-width",
            "outline-offset",
            "overflow-wrap",
            "perspective",
            "perspective-origin",
            "pointer-events",
            "quotes",
            "scroll-behavior",
            "scroll-snap-align",
            "scroll-snap-stop",
            "scroll-snap-type",
            "tab-size",
            "table-layout",
            "text-align-last",
            "text-combine-upright",
            "text-decoration-color",
            "text-decoration-line",
            "text-decoration-style",
            "text-decoration-skip",
            "text-emphasis",
            "text-emphasis-color",
            "text-emphasis-position",
            "text-emphasis-style",
            "text-orientation",
            "text-rendering",
            "text-shadow",
            "text-size-adjust",
            "text-underline-position",
            "unicode-bidi",
            "user-select",
            "vertical-align",
            "writing-mode",
            "zoom"
        )

        for (property in cssProperties) {
            highlightPattern(editable, "\\b$property\\b", cssPropertyColor)
        }

        // CSS values like hex colors, numbers with units, etc.
        val cssValues = arrayOf(
            "#[0-9a-fA-F]{3,6}",
            "\\b[0-9]+(px|em|rem|%)?\\b",
            "rgba?\\([^\\)]*\\)",
            "url\\([^\\)]*\\)"
        )
        for (value in cssValues) {
            highlightPattern(editable, value, cssValueColor)
        }

        // JavaScript keywords
        val jsKeywords = arrayOf(
            "var",
            "let",
            "const",
            "function",
            "if",
            "else",
            "for",
            "while",
            "do",
            "return",
            "true",
            "false",
            "null",
            "undefined",
            "new",
            "this",
            "class",
            "constructor",
            "extends",
            "super",
            "import",
            "export",
            "default",
            "from",
            "as",
            "in",
            "of",
            "with",
            "try",
            "catch",
            "finally",
            "throw",
            "switch",
            "case",
            "break",
            "default",
            "continue",
            "debugger",
            "delete",
            "typeof",
            "instanceof",
            "void",
            "yield",
            "await",
            "async",
            "static",
            "get",
            "set",
            "implements",
            "interface",
            "package",
            "private",
            "protected",
            "public",
            "enum",
            "abstract",
            "volatile",
            "synchronized",
            "goto",
            "native",
            "strictfp",
            "transient",
            "assert",
            "boolean",
            "byte",
            "char",
            "double",
            "final",
            "float",
            "int",
            "long",
            "short",
            "boolean",
            "extends",
            "import",
            "enum",
            "interface",
            "package",
            "private",
            "protected",
            "public",
            "await",
            "async",
            "super",
            "arguments",
            "document",
            "window",
            "console",
            "alert",
            "prompt",
            "confirm",
            "eval",
            "isNaN",
            "isFinite",
            "parseInt",
            "parseFloat",
            "decodeURI",
            "decodeURIComponent",
            "encodeURI",
            "encodeURIComponent",
            "escape",
            "unescape",
            "Math",
            "Date",
            "RegExp",
            "String",
            "Number",
            "Boolean",
            "Object",
            "Array",
            "Function",
            "Map",
            "Set",
            "WeakMap",
            "WeakSet",
            "Symbol",
            "Error",
            "EvalError",
            "RangeError",
            "ReferenceError",
            "SyntaxError",
            "TypeError",
            "URIError",
            "JSON",
            "NaN",
            "Infinity"
        )

        for (keyword in jsKeywords) {
            highlightPattern(editable, "\\b$keyword\\b", jsKeywordColor)
        }

        // Strings in JavaScript and CSS
        highlightPattern(editable, "\"[^\"]*\"|'[^']*'", stringColor)

        // Numbers in JavaScript and CSS
        highlightPattern(editable, "\\b\\d+\\b", numberColor)

        // Comments (single line and multi-line)
        highlightPattern(editable, "//[^\n]*", commentColor)
        highlightPattern(editable, "/\\*[^*]*\\*/", commentColor)
    }

    private fun highlightPattern(editable: Editable, pattern: String, color: Int) {
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(editable)
        while (matcher.find()) {
            editable.setSpan(
                ForegroundColorSpan(color),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun clearSpans(editable: Editable) {
        val spans = editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
        for (span in spans) {
            editable.removeSpan(span)
        }
    }

    fun getCurrentTheme(): CodeEditorView.Theme {
        return currentTheme
    }


    enum class Theme {
        DRACULA,
        VS_CODE,
        MONOKAI,
        SOLARIZED_DARK,
        SOLARIZED_LIGHT,
        OCEANIC_NEXT
    }
}
