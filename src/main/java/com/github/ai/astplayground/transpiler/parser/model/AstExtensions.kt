package com.github.ai.astplayground.transpiler.parser.model

fun TypeReference.isPrimitiveByte() =
    name == "byte" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitiveChar() =
    name == "char" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitiveInt() =
    name == "int" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitiveLong() =
    name == "long" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitiveFloat() =
    name == "float" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitiveDouble() =
    name == "double" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitiveBoolean() =
    name == "boolean" && kind == TypeReferenceKind.PRIMITIVE

fun TypeReference.isPrimitive() =
    isPrimitiveBoolean()
        || isPrimitiveByte()
        || isPrimitiveChar()
        || isPrimitiveInt()
        || isPrimitiveLong()
        || isPrimitiveFloat()
        || isPrimitiveDouble()

fun InitializerBlock.isLiteral(): Boolean {
    return this is InitializerBlock.ExpressionBlock
        && expression is Expression.Literal
}

fun InitializerBlock.isConstructorInvocation(): Boolean {
    return this is InitializerBlock.ExpressionBlock
        && expression is Expression.ConstructorInvocation
}

fun Method.isStatic(): Boolean {
    return Modifier.STATIC in modifiers
}