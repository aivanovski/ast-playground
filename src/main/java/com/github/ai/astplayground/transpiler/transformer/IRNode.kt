package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.transformer.model.KotlinAstNode

class IRNode(
    val parent: IRNode?,
    val astNode: KotlinAstNode,
    val nodes: MutableList<IRNode>
)