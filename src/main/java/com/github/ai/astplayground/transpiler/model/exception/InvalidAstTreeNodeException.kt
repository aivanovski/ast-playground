package com.github.ai.astplayground.transpiler.model.exception

import com.sun.source.tree.Tree

class InvalidAstTreeNodeException(
    message: String,
    node: Any
) : Exception(
    "$message [${formatNodeType(node)}]: $node"
)

private fun formatNodeType(node: Any): String {
    return when (node) {
        is Tree -> {
            "type=${node::class.java.simpleName}, kind=${node.kind?.name}"
        }

        else -> node::class.java.simpleName
    }
}