package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.transformer.model.KotlinAstNode
import java.util.LinkedList
import org.koin.core.qualifier.qualifier

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

fun List<KotlinAstNode>.toIRNodes(): List<IRNode> {
    val astRoots = this

    val queue = LinkedList<Pair<IRNode?, KotlinAstNode>>()
        .apply {
            for (node in astRoots) {
                add(null to node)
            }
        }

    val roots = mutableListOf<IRNode>()
    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val (parent, node) = queue.removeFirst()

            val irNode = IRNode(
                parent = parent,
                astNode = node,
                nodes = mutableListOf()
            )

            parent?.nodes?.add(irNode)

            for (childNode in node.nodes) {
                queue.add(irNode to childNode)
            }

            if (parent == null) {
                roots.add(irNode)
            }
        }
    }

    return roots
}

fun IRNode.toAstNode(): KotlinAstNode {
    val root = this
    val queue = LinkedList<Pair<IRNode?, IRNode>>()
        .apply {
            add(null to root)
        }

    while (queue.isNotEmpty()) {
        repeat(queue.size) {
//            val ()
        }

    }


    return null!!
}

fun IRNode.traverseAndTransform(transform: (IRNode) -> KotlinAstNode): IRNode {
    val oldRoot = this
    val queue = LinkedList<Pair<IRNode?, IRNode>>()
        .apply {
            add(null to oldRoot)
        }

    var newRoot: IRNode? = null
    while (queue.isNotEmpty()) {
        repeat(queue.size) {
            val (newParent, oldNode) = queue.removeFirst()

            val newAstNode = transform.invoke(oldNode)
            val newNode = IRNode(
                parent = newParent,
                astNode = newAstNode,
                nodes = mutableListOf()
            )

            for (childNode in oldNode.nodes) {
                queue.add(newNode to childNode)
            }

            newParent?.nodes?.add(newNode)

            if (newRoot == null) newRoot = newNode
        }
    }

    return newRoot!!
}