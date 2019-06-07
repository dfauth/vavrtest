package com.github.dfauth.vavrtest;

import io.vavr.Lazy;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.match.annotation.Patterns;
import io.vavr.match.annotation.Unapply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static com.github.dfauth.vavrtest.TestCase_EmployeePatterns.$employee;
import static io.vavr.API.*;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;
import static org.testng.Assert.*;

public class TestCase {

    private static final Logger logger = LoggerFactory.getLogger(TestCase.class);

    @Test
    public void testOption() {
        Option<Object> noneOption = Option.of(null);
        Option<Object> someOption = Option.of("val");

        logger.info("noneOption is "+noneOption);
        logger.info("someOption is "+someOption);
        assertEquals("None", noneOption.toString());
        assertEquals("Some(val)", someOption.toString());
    }

    @Test
    public void testTuple() {
        Tuple2<String, Integer> java8 = Tuple.of("Java", 8);
        String element1 = java8._1;
        int element2 = java8._2();

        logger.info("element1 is "+element1);
        logger.info("element2 is "+element2);
        assertEquals("Java", element1);
        assertEquals(8, element2);
    }

    @Test
    public void testTry() {

        Try<Integer> result = Try.of(() -> 1 / 0);
        logger.info("try1 is "+result);
        assertTrue(result.isFailure());

        Try<Integer> result1 = Try.of(() -> 4 / 2);
        logger.info("try2 is "+result1);
        assertFalse(result1.isFailure());
    }

    @Test
    public void testList() {
        List<Integer> intList = List.of(1, 2, 3);

        assertEquals(3, intList.length());
        assertEquals(new Integer(1), intList.get(0));
        assertEquals(new Integer(2), intList.get(1));
        assertEquals(new Integer(3), intList.get(2));
        logger.info("intList.size() is "+intList.size());
        int sum = intList.sum().intValue();
        assertEquals(6, sum);
    }

    @Test
    public void testLazy() {
        Lazy<Double> lazy = Lazy.of(Math::random);
        assertFalse(lazy.isEvaluated());
        logger.info("lazy.isEvaluated(): "+lazy.isEvaluated());

        double val1 = lazy.get();
        assertTrue(lazy.isEvaluated());
        logger.info("lazy.isEvaluated(): "+lazy.isEvaluated()+" val1: "+val1);

        double val2 = lazy.get();
        logger.info("lazy.isEvaluated(): "+lazy.isEvaluated()+" val2: "+val2);
        assertEquals(val1, val2, 0);
    }

    @Test
    public void testMatch() {
        int input = 2;
        String output = Match(input).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(3), "three"),
                Case($(), "?"));

        assertEquals("two", output);
    }

    @Test
    public void testMatchnTry() {

        Try<Integer> result = Try.of(() -> 1 / 0);

        int result1 = Match(result).of(
                Case($Success($()), v -> {
                    logger.info("match'n'try success: " + v);
                    return v;
                }),
                Case($Failure($()), v -> {
                    logger.info("match'n'try success: " + v);
                    return 0;
                })
        );
        assertEquals(result1, 0);
    }

    @Test
    public void testUnapply() {
        Employee person = new Employee("Carl", 1);

        String result = Match(person).of(
                Case($employee($("Carl"), $()),
                        (name, id) -> "Carl has employee id "+id),
                Case($(),
                        () -> "not found"));

        assertEquals("Carl has employee id 1", result);
    }

    @Patterns
    static class Employee {

        private final String name;
        private final int id;

        @Unapply
        public static Tuple2<String, Integer> employee(Employee employee) {
            return new Tuple2<>(employee.name, employee.id);
        }

        public Employee(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

}
