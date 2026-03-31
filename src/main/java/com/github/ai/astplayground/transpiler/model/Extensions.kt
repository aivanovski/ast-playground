package com.github.ai.astplayground.transpiler.model

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
