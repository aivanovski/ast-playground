package com.github.ai.astplayground.transpiler.transformer

import java.util.LinkedList

fun IRNode.traverse(onVisit: (IRNode) -> Unit) {
    val root = this

    val stack = LinkedList<IRNode>()
        .apply {
            add(root)
        }

    while (stack.isNotEmpty()) {
        val node = stack.removeFirst()

        onVisit.invoke(node)

        for (childNode in node.nodes) {
            stack.push(childNode)
        }
    }
}

fun IRNode.map(transform: (IRNode) -> IRNode): IRNode {
    // TODO: implement
    return this
}