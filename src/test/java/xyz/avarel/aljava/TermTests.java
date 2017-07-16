package xyz.avarel.aljava;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class TermTests {
    @Test
    public void simplification() {
        Term complicated = new Term(Arrays.asList(new Fraction(-5), new Fraction(2)),
                Arrays.asList(new Variable("y", 2), new Variable("x"), new Variable("x", 3)));
        Term simplified = new Term(Collections.singletonList(new Fraction(-10)),
                Arrays.asList(new Variable("x", 4), new Variable("y", 2)));
        Assert.assertEquals(simplified, complicated.simplify());
    }

    @Test
    public void addition() {
        Term simplified = new Term(Collections.singletonList(new Fraction(5)),
                Arrays.asList(new Variable("x", 4), new Variable("y", 2)));

        Term a = new Term(Collections.singletonList(new Fraction(2)),
                Arrays.asList(new Variable("x", 4), new Variable("y", 2)));

        Term b = new Term(Collections.singletonList(new Fraction(3)),
                Arrays.asList(new Variable("x", 2), new Variable("x", 2),
                        new Variable("y"), new Variable("y")));

        Assert.assertEquals(simplified, a.plus(b));
    }

    @Test
    public void multiplication() {
        Term a = new Term(new Variable("a"));
        Term b = new Term(Collections.singletonList(new Fraction(3)),
                Collections.singletonList(new Variable("a", 3)));

        Term finalResult = new Term(new Fraction(3), new Variable("a", 4));

        Assert.assertEquals(finalResult, a.times(b));
    }

    @Test
    public void print() {
        Term complicated = new Term(Arrays.asList(new Fraction(-5), new Fraction(2)),
                Arrays.asList(new Variable("y", 2), new Variable("x"), new Variable("x", 3)));
        Term simplified = new Term(Collections.singletonList(new Fraction(-10)),
                Arrays.asList(new Variable("x", 4), new Variable("y", 2)));

        Term variablesOnly = new Term(Collections.singletonList(new Fraction(1)),
                Arrays.asList(new Variable("x", 4), new Variable("y", 2)));

        Term constantOnly = new Term(new Fraction(2));

        System.out.println(complicated);
        System.out.println(complicated.simplify());
        System.out.println(simplified);
        System.out.println(variablesOnly);
        System.out.println(constantOnly);
    }
}
