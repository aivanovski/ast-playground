package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.parser.model.Field
import com.github.ai.astplayground.transpiler.parser.model.InitializerBlock
import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode
import com.github.ai.astplayground.transpiler.parser.model.TypeReference
import com.github.ai.astplayground.transpiler.parser.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.parser.model.isConstructorInvocation
import com.github.ai.astplayground.transpiler.parser.model.isLiteral
import com.github.ai.astplayground.transpiler.parser.model.isPrimitive
import com.github.ai.astplayground.transpiler.serializer.model.KField
import com.github.ai.astplayground.transpiler.serializer.model.KTypeReference
import com.github.ai.astplayground.transpiler.serializer.model.KotlinAstNode

class JavaToKotlinTransformer {

    fun transform(javaAst: List<JavaAstNode>): List<KotlinAstNode> {
        return javaAst.map { node ->
            when (node) {
                is JavaAstNode.Package -> KotlinAstNode.Package(
                    name = node.name
                )

                is JavaAstNode.Import -> KotlinAstNode.Import(
                    name = node.name,
                    isStatic = node.isStatic,
                    isAsterisk = node.isAsterisk
                )

                is JavaAstNode.Class -> KotlinAstNode.Class(
                    name = node.name,
                    modifiers = node.modifiers,
                    fields = node.fields.map { field -> transformField(field) },
                    constructors = node.constructors,
                    methods = node.methods
                )
            }
        }
    }

    private fun transformField(field: Field): KField {
        return KField(
            name = field.name,
            modifiers = field.modifiers,
            type = transformTypeReference(type = field.type, initializer = field.initializer),
            initializer = field.initializer
        )
    }

    private fun transformTypeReference(
        type: TypeReference,
        initializer: InitializerBlock
    ): KTypeReference {
        val isNullable = !type.isPrimitive()
            && !initializer.isLiteral()
            && !initializer.isConstructorInvocation()

        val typeArguments = type.typeArguments
            .map { argType ->
                transformTypeReference(
                    argType,
                    initializer = InitializerBlock.Empty
                )
            }

        val name = when (type.kind) {
            TypeReferenceKind.PRIMITIVE -> type.name.first().uppercase() + type.name.drop(1)
            TypeReferenceKind.VOID -> "Unit"
            else -> type.name
        }

        return KTypeReference(
            isNullable = isNullable,
            name = name,
            typeArguments = typeArguments
        )
    }

}
