package com.github.ai.astplayground.sample

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExpressionFactory
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.equal
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.literal
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.or
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.typedIdentifier
import com.github.ai.astplayground.assertionDsl.FieldFactory
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.invokeConstructor
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.method
import com.github.ai.astplayground.assertionDsl.InitializerFactory
import com.github.ai.astplayground.assertionDsl.InitializerFactory.initializer
import com.github.ai.astplayground.assertionDsl.Modifiers.static
import com.github.ai.astplayground.assertionDsl.ParametersFactory
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.int
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.parameterizedWith
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.string
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.assertionDsl.VariableFactory.asVariableOf
import com.github.ai.astplayground.parseAndAssert
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Expression.Null
import com.github.ai.astplayground.transpiler.model.InitializerBlock
import com.github.ai.astplayground.transpiler.model.Operator
import org.junit.jupiter.api.Test

class SampleTest {

    @Test
    fun `should work with class`() {
        parseAndAssert(
            input = """
                import java.util.ArrayList;
                import java.util.List;
                class Main {
                    static void main() {
                        List<Person> people = new ArrayList<>();

                        people.add(new Person("Alice", 30));
                        people.add(new Person("Bob", 25));
                        people.add(new Person("Charlie", 35));

                        for (Person person : people) {
                            System.out.println(person.getName() + " is " + person.getAge() + " years old.");
                        }

                        Person oldestPerson = findOldestPerson(people);
                        if (oldestPerson != null) {
                            System.out.println("The oldest person is " + oldestPerson.getName() + " at " + oldestPerson.getAge() + " years.");
                        }
                    }

                    static Person findOldestPerson(List<Person> people) {
                        if (people == null || people.isEmpty()) return null;

                        Person oldest = people.get(0);
                        for (Person person : people) {
                            if (person.getAge() > oldest.getAge()) {
                                oldest = person;
                            }
                        }

                        return oldest;
                    }
                }
                class Person {
                    String name;
                    int age;

                    Person(String name, int age) {
                        this.name = name;
                        this.age = age;
                    }

                    String getName() {
                        return name;
                    }

                    int getAge() {
                        return age;
                    }
                }
            """,
            expected = buildAst {
                import("java.util.ArrayList")
                import("java.util.List")

                `class`("Main") {
                    method("main", returns = void(), modifiers = static()) {
                        variable(
                            name = "people",
                            type = "List" parameterizedWith "Person",
                            initializer = InitializerFactory.constructor(
                                typedIdentifier("ArrayList")
                            )
                        )

                        val alice =
                            "Person" invokeConstructor listOf("Alice".literal(), 30.literal())
                        call("people" method "add" invoke alice)

                        val bob = "Person" invokeConstructor listOf("Bob".literal(), 25.literal())
                        call("people" method "add" invoke bob)

                        val charlie =
                            "Person" invokeConstructor listOf("Charlie".literal(), 35.literal())
                        call("people" method "add" invoke charlie)

                        foreach(
                            "person".asVariableOf("Person"),
                            "people".asIdentifier()
                        ) {
                            call(
                                "System" field "out" method "println" invoke Expression.BinaryExpression(
                                    Operator.PLUS,
                                    Expression.BinaryExpression(
                                        Operator.PLUS,
                                        Expression.BinaryExpression(
                                            Operator.PLUS,
                                            "person" method "getName" invoke Expression.Empty,
                                            " is ".literal()
                                        ),
                                        "person" method "getAge" invoke Expression.Empty
                                    ),
                                    " years old.".literal()
                                )
                            )
                        }

                        variable(
                            "oldestPerson",
                            "Person".asType(),
                            InitializerBlock.ExpressionBlock(
                                Expression.MethodInvocation(
                                    arguments = listOf("people".asIdentifier()),
                                    method = "findOldestPerson".asIdentifier()
                                )
                            )
                        )

                        `if`(
                            Expression.BinaryExpression(
                                Operator.NOT_EQUALS,
                                "oldestPerson".asIdentifier(),
                                Null
                            )
                        ) {
                            call(
                                "System" field "out" method "println" invoke Expression.BinaryExpression(
                                    Operator.PLUS,
                                    Expression.BinaryExpression(
                                        Operator.PLUS,
                                        Expression.BinaryExpression(
                                            Operator.PLUS,
                                            Expression.BinaryExpression(
                                                Operator.PLUS,
                                                "The oldest person is ".literal(),
                                                "oldestPerson" method "getName" invoke Expression.Empty
                                            ),
                                            " at ".literal()
                                        ),
                                        "oldestPerson" method "getAge" invoke Expression.Empty,
                                    ),
                                    " years.".literal()
                                )
                            )
                        }
                    }

                    method(
                        "findOldestPerson",
                        ParametersFactory.variable(
                            "people",
                            "List" parameterizedWith "Person"
                        ),
                        returns = "Person".asType(),
                        modifiers = static()
                    ) {
                        `if`(
                            ("people".asIdentifier() equal Null) or ("people" method "isEmpty" invoke Expression.Empty)
                        ) {
                            `return`(Null)
                        }

                        variable(
                            "oldest",
                            "Person".asType(),
                            initializer { "people" method "get" invoke 0.literal() }
                        )

                        foreach(
                            "person".asVariableOf("Person"),
                            "people".asIdentifier()
                        ) {
                            `if`(
                                Expression.BinaryExpression(
                                    Operator.GREATER_THAN,
                                    "person" method "getAge" invoke Expression.Empty,
                                    "oldest" method "getAge" invoke Expression.Empty
                                )
                            ) {
                                assign("oldest".asIdentifier(), "person".asIdentifier())
                            }
                        }

                        `return`("oldest".asIdentifier())
                    }
                }

                `class`("Person") {
                    field(FieldFactory.string("name"))
                    field(FieldFactory.int("age"))

                    constructor(
                        ParametersFactory.string("name"),
                        ParametersFactory.int("age")
                    ) {
                        assign("this" field "name", "name")
                        assign("this" field "age", "age")
                    }

                    method("getName", returns = string()) {
                        `return`(ExpressionFactory.identifier("name"))
                    }

                    method("getAge", returns = int()) {
                        `return`(ExpressionFactory.identifier("age"))
                    }
                }
            }
        )
    }
}
