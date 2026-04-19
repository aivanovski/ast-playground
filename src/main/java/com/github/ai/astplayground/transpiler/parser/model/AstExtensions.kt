package com.github.ai.astplayground.transpiler.parser.model

fun JTypeReference.isPrimitiveByte() =
    name == "byte" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitiveChar() =
    name == "char" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitiveInt() =
    name == "int" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitiveLong() =
    name == "long" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitiveFloat() =
    name == "float" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitiveDouble() =
    name == "double" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitiveBoolean() =
    name == "boolean" && kind == TypeReferenceKind.PRIMITIVE

fun JTypeReference.isPrimitive() =
    isPrimitiveBoolean()
        || isPrimitiveByte()
        || isPrimitiveChar()
        || isPrimitiveInt()
        || isPrimitiveLong()
        || isPrimitiveFloat()
        || isPrimitiveDouble()

fun JInitializerBlock.isLiteral(): Boolean {
    return this is JInitializerBlock.ExpressionBlock
        && expression is JExpression.Literal
}

fun JInitializerBlock.isConstructorInvocation(): Boolean {
    return this is JInitializerBlock.ExpressionBlock
        && expression is JExpression.ConstructorInvocation
}

fun JMethod.isStatic(): Boolean {
    return Modifier.STATIC in modifiers
}