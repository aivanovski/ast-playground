package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class PersonTest {

//    @Test
//    fun `should work with simple example`() {
//        transpileJavaAndAssert(
//            input = """
//                import java.util.List;
//
//                public class Test {
//                    String findLongest(List<String> values) {
//                        if (values.isEmpty()) return null;
//
//                        String result = null;
//                        for (String value : values) {
//                            if (value.length() > result.length()) {
//                                result = result;
//                            }
//                        }
//
//                        return result;
//                    }
//                }
//            """,
//            expected = """
//                import java.util.List
//
//                class JavaTest2 {
//                    fun findLongest(values: List<String?>?): String? {
//                        if (values!!.isEmpty()) return null
//
//                        var result: String? = null
//                        for (value in values!!) {
//                            if (value!!.length > result!!.length) {
//                                result = value
//                            }
//                        }
//
//                        return result
//                    }
//                }
//            """
//        )
//    }

    //    @Test
    //    fun `should work with class`() {
    //        transpileJavaAndAssert(
    //            input = """
    //                package test;
//                import java.util.List;
//                class Main {
//                    static Person findOldestPerson(List<Person> people) {
//                        if (people == null || people.isEmpty()) return null;
//
//                        Person oldest = people.get(0);
//                        for (Person person : people) {
//                            if (person.getAge() > oldest.getAge()) {
//                                oldest = person;
//                            }
//                        }
//
//                        return oldest;
//                    }
//                }
//                class Person {
//                    String name;
//                    int age;
//
//                    Person(String name, int age) {
//                        this.name = name;
//                        this.age = age;
//                    }
//
//                    String getName() {
//                        return name;
//                    }
//
//                    int getAge() {
//                        return age;
//                    }
//                }
//            """,
//            expected = """
//                package test
//                import java.util.List
//                class Main {
//                    companion object {
//                        fun findOldestPerson(people: List<Person>?): Person? {
//                            if (people == null || people.isEmpty()) {
//                                return null
//                            }
//
//                            var oldest: Person? = people?.get(0)
//                            for (person in (people ?: emptyList())) {
//                                if (person.getAge() > oldest.getAge()) {
//                                    oldest = person
//                                }
//                            }
//
//                            return oldest
//                        }
//                    }
//                }
//                class Person {
//                    var name: String? = null
//                    var age: Int = 0
//
//                    constructor(name: String?, age: Int) {
//                        this.name = name
//                        this.age = age
//                    }
//
//                    fun getName(): String? {
//                        return name
//                    }
//
//                    fun getAge(): Int {
//                        return age
//                    }
//                }
//            """
//        )
//    }
}