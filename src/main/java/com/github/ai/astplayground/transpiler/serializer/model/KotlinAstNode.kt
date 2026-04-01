package com.github.ai.astplayground.transpiler.serializer.model

import com.github.ai.astplayground.transpiler.parser.model.Constructor
import com.github.ai.astplayground.transpiler.parser.model.Field
import com.github.ai.astplayground.transpiler.parser.model.Method
import com.github.ai.astplayground.transpiler.parser.model.Modifier

sealed interface KotlinAstNode {

    data class Package(
        val name: String
    ) : KotlinAstNode

    data class Import(
        val name: String,
        val isStatic: Boolean,
        val isAsterisk: Boolean,
    ) : KotlinAstNode

    sealed interface TypeDeclaration : KotlinAstNode {
        val name: String
        val modifiers: Set<Modifier>
    }

    data class Class(
        override val name: String,
        override val modifiers: Set<Modifier>,
        val fields: List<Field>,
        val constructors: List<Constructor>,
        val methods: List<Method>
    ) : TypeDeclaration
}
