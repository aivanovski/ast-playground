package com.github.ai.astplayground.transpiler.serializer.model

import com.github.ai.astplayground.transpiler.parser.model.CodeBlock
import com.github.ai.astplayground.transpiler.parser.model.Constructor
import com.github.ai.astplayground.transpiler.parser.model.Field
import com.github.ai.astplayground.transpiler.parser.model.InitializerBlock
import com.github.ai.astplayground.transpiler.parser.model.Method
import com.github.ai.astplayground.transpiler.parser.model.Modifier
import com.github.ai.astplayground.transpiler.parser.model.Parameter
import com.github.ai.astplayground.transpiler.parser.model.TypeReference

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
        val fields: List<KField>,
        val constructors: List<KConstructor>,
        val methods: List<Method>
    ) : TypeDeclaration
}

data class KMethod(
    val name: String,
    val modifiers: Set<Modifier>,
    val returnType: TypeReference,
    val parameters: List<Parameter>,
    val body: CodeBlock
)

data class KField(
    val name: String,
    val modifiers: Set<Modifier>,
    val type: KTypeReference,
    val initializer: InitializerBlock,
)

data class KTypeReference(
    val isNullable: Boolean,
    val name: String,
    val typeArguments: List<KTypeReference> = emptyList()
)

data class KConstructor(
    val modifiers: Set<Modifier>,
    val parameters: List<KParameter>,
    val body: CodeBlock
)

data class KParameter(
    val name: String,
    val type: KTypeReference,
    val isVarArgs: Boolean,
)