package com.github.ai.astplayground.samples

import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class SamplesTest {

    @Test
    fun `should convert full class`() {
        val input = """
            import java.util.ArrayList;
            public class Main {
                public static void main() {
                    ArrayList<Person> people = new ArrayList<>();

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

                public static Person findOldestPerson(ArrayList<Person> people) {
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
                private String name;
                private int age;

                public Person(String name, int age) {
                    this.name = name;
                    this.age = age;
                }

                public String getName() {
                    return name;
                }

                public int getAge() {
                    return age;
                }
            }
        """

        val output = """
            import java.util.ArrayList
            class Main {
                companion object {
                    fun main() {
                        var people: ArrayList<Person>? = ArrayList()

                        people?.add(Person("Alice", 30))
                        people?.add(Person("Bob", 25))
                        people?.add(Person("Charlie", 35))

                        for (person in people) {
                            System.out.println(person.getName() + " is " + person.getAge() + " years old.")
                        }

                        var oldestPerson: Person? = findOldestPerson(people)
                        if (oldestPerson != null) {
                            System.out.println("The oldest person is " + oldestPerson.getName() + " at " + oldestPerson.getAge() + " years.")
                        }
                    }

                    fun findOldestPerson(people: ArrayList<Person>?): Person? {
                        if (people == null || people.isEmpty()) return null

                        var oldest: Person? = people?.get(0)
                        for (person in people) {
                            if (person.getAge() > oldest.getAge()) {
                                oldest = person
                            }
                        }

                        return oldest
                    }
                }
            }
            class Person {
                private var name: String? = null
                private var age: Int = 0

                constructor(name: String?, age: Int) {
                    this.name = name
                    this.age = age
                }

                fun getName(): String? {
                    return name
                }

                fun getAge(): Int {
                    return age
                }
            }
        """

        transpileAndAssert(input, output)
    }
}