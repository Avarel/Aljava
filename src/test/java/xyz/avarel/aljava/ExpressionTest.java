package xyz.avarel.aljava;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class ExpressionTest {
    @Test
    public void simplification() {
        Expression complicated = new Expression(
                Arrays.asList(
                        new Term(new Variable("x")),
                        new Term(new Variable("x")),
                        new Term(new Fraction(-4)),
                        new Term(new Fraction(9))
                )
        );

        Expression simplified = new Expression(
                Arrays.asList(
                        new Term(new Fraction(2), new Variable("x")),
                        new Term(new Fraction(5))
                )
        );

        Assert.assertEquals(simplified, complicated.simplify());
    }

    @Test
    public void addition() {
        Expression a = new Expression(
                Arrays.asList(
                        new Term(new Variable("x")),
                        new Term(new Variable("y", 3)),
                        new Term(new Fraction(-4)),
                        new Term(new Fraction(9))
                )
        );

        Expression b = new Expression(
                Arrays.asList(
                        new Term(new Variable("x")),
                        new Term(new Variable("x", 2)),
                        new Term(new Variable("y")),
                        new Term(new Fraction(5))
                )
        );

        Expression expected = new Expression(
                Arrays.asList(
                        new Term(new Variable("y", 3)),
                        new Term(new Variable("x", 2)),
                        new Term(new Fraction(2), new Variable("x")),
                        new Term(new Variable("y")),
                        new Term(new Fraction(10))
                )
        );

        Assert.assertEquals(expected, a.plus(b));
    }

    @Test
    public void multiplication() {
        Expression a = new Expression(
                Arrays.asList(
                        new Term(new Fraction(2), new Variable("x")),
                        new Term(new Fraction(3))
                )
        );

        Expression b = new Expression(
                Arrays.asList(
                        new Term(new Fraction(3), new Variable("x")),
                        new Term(new Fraction(2))
                )
        );

        Expression expected = new Expression(
                Arrays.asList(
                        new Term(new Fraction(6), new Variable("x", 2)),
                        new Term(new Fraction(13), new Variable("x")),
                        new Term(new Fraction(6))
                )
        );

        Assert.assertEquals(expected, a.times(b));
    }

    @Test
    public void division1() {
        Expression a = new Expression(
                new Term(new Fraction(2), new Variable("x"))
        );

        Expression b = new Expression("x");

        Expression expected = new Expression(2);

        Assert.assertEquals(expected, a.div(b));
    }

    @Test
    public void division2() {
        Expression a = new Expression(
                Arrays.asList(
                        new Term(new Fraction(2), new Variable("x")),
                        new Term(new Fraction(2), new Variable("y"))
                )
        );

        Expression b = new Expression(2);

        Expression expected = new Expression(
                Arrays.asList(
                        new Term(new Variable("x")),
                        new Term(new Variable("y"))
                )
        );

        Assert.assertEquals(expected, a.div(b));
    }

    @Test
    public void division3() {
        Expression a = new Expression(
                Arrays.asList(
                        new Term(new Fraction(2), new Variable("x")),
                        new Term(new Fraction(2), new Variable("y")),
                        new Term(new Fraction(6))
                )
        );

        Expression b = new Expression(2);

        Expression expected = new Expression(
                Arrays.asList(
                        new Term(new Variable("x")),
                        new Term(new Variable("y")),
                        new Term(new Fraction(3))
                )
        );

        System.out.println(expected);

        Assert.assertEquals(expected, a.div(b));
    }

    @Test
    public void division4() {
        Expression a = new Expression(2);

        Expression b = new Expression(
                Collections.singletonList(
                        new Term(
                                Collections.singletonList(
                                        new Fraction(2)
                                ),
                                Arrays.asList(
                                        new Variable("x"),
                                        new Variable("y")
                                )
                        )
                )
        );

        Expression expected = new Expression(
                Collections.singletonList(
                        new Term(
                                Collections.emptyList(),
                                Arrays.asList(
                                        new Variable("x", -1),
                                        new Variable("y", -1)
                                )
                        )
                )
        );

        Assert.assertEquals(expected, a.div(b));
    }

    @Test
    public void division5() {
        Expression a = new Expression(new Term(new Variable("x", 9)));

        Expression b = new Expression(new Term(new Variable("x", 4)));

        Expression expected = new Expression(new Term(new Variable("x", 5)));

        Assert.assertEquals(expected, a.div(b));
    }
}
