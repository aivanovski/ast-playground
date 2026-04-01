package com.github.ai.astplayground.transpiler.model.exception

import com.sun.source.tree.Tree

open class TranspilerException(
    message: String
) : Exception(message)

class AstSerializationException(
    message: String,
    node: Any
) : TranspilerException(
    "$message [${formatNodeType(node)}]: $node"
)

class AstParsingException(
    message: String,
    node: Any
) : TranspilerException(
    "$message [${formatNodeType(node)}]: $node"
)

private fun formatNodeType(node: Any): String {
    return when (node) {
        is Tree -> "type=${node::class.java.name}, kind=${node.kind?.name}"
        else -> node::class.java.name
    }
}