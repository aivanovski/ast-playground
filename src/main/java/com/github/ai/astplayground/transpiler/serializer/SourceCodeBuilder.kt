package com.github.ai.astplayground.transpiler.serializer

class SourceCodeBuilder {

    private val content = StringBuilder()

    fun append(line: String) {
        if (content.isNotEmpty()) {
            content.append("\n")
        }
        content.append(line)
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
            if (content.isNotEmpty() && content.last().isLetterOrDigit()) {
                content.append(" ")
            }
            content.append("{\n$block\n}")
        }
    }

    fun build(): String {
        return content.toString()
    }
}