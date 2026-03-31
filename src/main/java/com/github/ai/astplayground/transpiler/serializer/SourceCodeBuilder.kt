package com.github.ai.astplayground.transpiler.serializer

class SourceCodeBuilder {

    private val content = StringBuilder()

    fun newLine() {
        content.append("\n")
    }

    fun appendLine(line: String) {
        if (content.isNotEmpty()) {
            content.append("\n")
        }
        content.append(line)
    }

    fun append(word: String) {
        content.append(word)
    }

    fun appendBlock(block: String) {
        content.append("{\n")
        content.append(block)
        content.append("}")
    }

    fun appendBlock(blockContent: SourceCodeBuilder.() -> Unit) {
        val block = SourceCodeBuilder()
            .apply {
                blockContent.invoke(this)
            }
            .build()

        if (block.isNotBlank()) {
            if (content.isNotEmpty() && !content.last().isWhitespace()) {
                content.append(" ")
            }
            content.append("{$block\n}")
        }
    }

    fun build(): String {
        return content.toString()
    }
}