package com.github.ai.astplayground.extension

const val LINE_END = "\n"
const val INDENT = "    "
const val WHITESPACE = " "

fun StringBuilder.getCurrentLine(): String? {
    val idx = this.lastIndexOf(LINE_END)

    return if (this.length > idx + LINE_END.length + 1) {
        this.substring(idx + LINE_END.length)
    } else {
        null
    }
}

fun String.getIndentationLevel(
    indentation: String = INDENT
): Int {
    if (!this.startsWith(INDENT)) {
        return 0
    }

    var indentLevel = 0
    var str = this
    while (str.startsWith(INDENT)) {
        str = str.removePrefix(INDENT)
        indentLevel += 1
    }

    return indentLevel
}

fun String.addSuffixWhenNotContains(
    suffix: String,
    notContains: String
): String {
    return if (this.contains(notContains)) {
        this
    } else {
        this + suffix
    }
}

fun String.addSuffixWhenEndsWith(
    suffix: String,
    endsWith: String
): String {
    return if (this.endsWith(endsWith)) {
        this + suffix
    } else {
        this
    }
}

fun String.addPrefixIfNeed(
    prefix: String
): String {
    return if (this.startsWith(prefix)) {
        this
    } else {
        prefix + this
    }
}

fun String.addSuffixIfNeed(
    suffix: String
): String {
    return if (this.endsWith(suffix)) {
        this
    } else {
        this + suffix
    }
}