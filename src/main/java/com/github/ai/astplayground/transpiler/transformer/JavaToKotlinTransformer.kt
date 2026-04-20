package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.parser.model.JCodeBlock
import com.github.ai.astplayground.transpiler.parser.model.JConstructor
import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.JField
import com.github.ai.astplayground.transpiler.parser.model.JInitializerBlock
import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode
import com.github.ai.astplayground.transpiler.parser.model.JMethod
import com.github.ai.astplayground.transpiler.parser.model.JParameter
import com.github.ai.astplayground.transpiler.parser.model.JTypeReference
import com.github.ai.astplayground.transpiler.parser.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.parser.model.isConstructorInvocation
import com.github.ai.astplayground.transpiler.parser.model.isLiteral
import com.github.ai.astplayground.transpiler.parser.model.isPrimitive
import com.github.ai.astplayground.transpiler.parser.model.isStatic
import com.github.ai.astplayground.transpiler.transformer.model.KExpression
import com.github.ai.astplayground.transpiler.transformer.model.KParameter
import com.github.ai.astplayground.transpiler.transformer.model.KTypeReference
import com.github.ai.astplayground.transpiler.transformer.model.KotlinAstNode

class JavaToKotlinTransformer {

    fun transform(javaAst: List<JavaAstNode>): List<KotlinAstNode> {
        val kotlinAst = javaAst.map { node -> transformNode(node) }

        val irRoots = kotlinAst.toIRNodes()
            .map { rootNode ->
                rootNode.traverseAndTransform { node ->
                    if (node.astNode is KotlinAstNode.KExpressionNode
                        && node.astNode.expression is KExpression.If
                    ) {

                        println("!!!!!!!!!!!!!!!!!!!! transform ---!!!!!!!!!")

                        node.astNode
                    } else {
                        node.astNode
                    }
                }
            }

        return kotlinAst
    }


//    private fun KotlinAstNode.toIRNode(parent: IRNode?): IRNode {
//        val irNode = IRNode(
//            parent = parent,
//            node = this,
//            nodes = mutableListOf()
//        )
//
//        for (node in nodes) {
//            val irChildNode = node.toIRNode(parent = irNode)
//            irNode.nodes.add(irChildNode)
//        }
//
//        return irNode
//    }

    private fun transformNode(node: JavaAstNode): KotlinAstNode {
        return when (node) {
            is JavaAstNode.Package -> KotlinAstNode.Package(
                name = node.name
            )

            is JavaAstNode.Import -> KotlinAstNode.Import(
                name = node.name,
                isStatic = node.isStatic,
                isAsterisk = node.isAsterisk
            )

            is JavaAstNode.JClass -> {
                val instanceMethods = node.methods
                    .filter { method -> !method.isStatic() }
                    .map { method -> transformMethod(method) }

                val staticMethods = node.methods
                    .filter { method -> method.isStatic() }
                    .map { method -> transformMethod(method) }

                val companionNode = if (staticMethods.isNotEmpty()) {
                    KotlinAstNode.CompanionObject(nodes = staticMethods)
                } else {
                    null
                }

                val fields = node.fields.map { field -> transformField(field) }
                val constructors = node.constructors.map { constructor ->
                    transformConstructor(constructor)
                }

                val nodes = buildList {
                    addAll(fields)
                    addAll(constructors)
                    addAll(instanceMethods)

                    if (companionNode != null) {
                        add(companionNode)
                    }
                }

                KotlinAstNode.KClass(
                    name = node.name,
                    modifiers = node.modifiers,
                    nodes = nodes
                )
            }
        }
    }

    private fun transformMethod(method: JMethod): KotlinAstNode.KMethod {
        val returnType = transformTypeReference(method.returnType, JInitializerBlock.Empty)

        return KotlinAstNode.KMethod(
            name = method.name,
            modifiers = method.modifiers,
            returnType = returnType,
            parameters = method.parameters.map { parameter -> transformParameter(parameter) },
            body = transformCodeBlock(method.body)
        )
    }

    private fun transformConstructor(constructor: JConstructor): KotlinAstNode.KConstructor {
        return KotlinAstNode.KConstructor(
            modifiers = constructor.modifiers,
            parameters = constructor.parameters.map { parameter ->
                transformParameter(parameter)
            },
            body = transformCodeBlock(constructor.body)
        )

    }

    private fun transformParameter(parameter: JParameter): KParameter {
        return KParameter(
            name = parameter.name,
            type = transformTypeReference(parameter.type, JInitializerBlock.Empty),
            isVarArgs = false
        )
    }

    private fun transformCodeBlock(block: JCodeBlock): KotlinAstNode.KCodeBlock {
        return when (block) {
            JCodeBlock.Empty -> KotlinAstNode.EmptyCodeBlock
            is JCodeBlock.Expressions -> KotlinAstNode.ExpressionsBlock(
                expressions = block.expressions
                    .map { expression ->
                        transformExpression(expression).toAstNode()
                    }
            )
        }
    }

    private fun transformField(field: JField): KotlinAstNode.KField {
        return KotlinAstNode.KField(
            name = field.name,
            modifiers = field.modifiers,
            type = transformTypeReference(type = field.type, initializer = field.initializer),
            initializer = transformInitializerBlock(field.initializer)
        )
    }

    private fun transformInitializerBlock(block: JInitializerBlock): KotlinAstNode.KCodeBlock {
        return when (block) {
            JInitializerBlock.Empty -> KotlinAstNode.EmptyCodeBlock
            is JInitializerBlock.ExpressionBlock -> KotlinAstNode.ExpressionsBlock(
                expressions = listOf(transformExpression(block.expression).toAstNode())
            )
        }
    }

    private fun transformExpressions(expressions: List<JExpression>): List<KExpression> {
        return expressions.map { expression -> transformExpression(expression) }
    }

    private fun KExpression.toAstNode(): KotlinAstNode.KExpressionNode {
        return KotlinAstNode.KExpressionNode(expression = this)
    }

    private fun transformExpression(expression: JExpression): KExpression {
        return when (expression) {
            JExpression.Empty -> KExpression.Empty
            is JExpression.Identifier -> KExpression.Identifier(
                expression.name,
                isUnsafeCall = false
            )

            is JExpression.Literal -> transformLiteral(expression)
            is JExpression.Return -> KExpression.Return(transformExpression(expression.expression))
            is JExpression.If -> transformIfExpression(expression)
            is JExpression.ForEachLoop -> KExpression.ForEachLoop(
                variable = KExpression.DeclareVariable(
                    name = expression.variable.name,
                    type = transformTypeReference(
                        expression.variable.type,
                        expression.variable.initializer
                    ),
                    initializer = transformInitializerBlock(expression.variable.initializer)
                ),
                iterable = transformExpression(expression.iterable),
                body = transformExpression(expression.body)
            )

            is JExpression.MethodInvocation -> KExpression.MethodInvocation(
                arguments = transformExpressions(expression.arguments),
                method = transformExpression(expression.method)
            )

            is JExpression.FieldAccess -> KExpression.FieldAccess(
                name = expression.name,
                expression = transformExpression(expression.expression)
            )

            is JExpression.BinaryExpression -> KExpression.BinaryExpression(
                operator = expression.operator,
                lhs = transformExpression(expression.lhs),
                rhs = transformExpression(expression.rhs)
            )
            // TODO: implement other cases
            else -> throw NotImplementedError("Not implemented for expression: $expression")
        }
    }

    private fun transformIfExpression(expression: JExpression.If): KExpression.If {
        return KExpression.If(
            condition = transformExpression(expression.condition),
            thenExpression = transformExpression(expression.thenExpression),
            elseExpression = transformExpression(expression.elseExpression)
        )
    }

    private fun transformLiteral(literal: JExpression.Literal): KExpression.Literal {
        return when (literal) {
            JExpression.Null -> KExpression.Null
            is JExpression.BooleanLiteral -> KExpression.BooleanLiteral(literal.value)
            is JExpression.ByteLiteral -> KExpression.ByteLiteral(literal.value)
            is JExpression.CharLiteral -> KExpression.CharLiteral(literal.value)
            is JExpression.IntLiteral -> KExpression.IntLiteral(literal.value)
            is JExpression.LongLiteral -> KExpression.LongLiteral(literal.value)
            is JExpression.FloatLiteral -> KExpression.FloatLiteral(literal.value)
            is JExpression.DoubleLiteral -> KExpression.DoubleLiteral(literal.value)
            is JExpression.StringLiteral -> KExpression.StringLiteral(literal.value)
        }
    }

    private fun transformTypeReference(
        type: JTypeReference,
        initializer: JInitializerBlock
    ): KTypeReference {
        val isNullable = !type.isPrimitive()
            && !initializer.isLiteral()
            && !initializer.isConstructorInvocation()

        val typeArguments = type.typeArguments
            .map { argType ->
                transformTypeReference(
                    argType,
                    initializer = JInitializerBlock.Empty
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
