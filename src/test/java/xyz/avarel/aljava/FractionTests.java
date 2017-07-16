package xyz.avarel.aljava;

import org.junit.Assert;
import org.junit.Test;

public class FractionTests {
    @Test(expected = ArithmeticException.class)
    public void invalid() {
        new Fraction(1, 0);
    }

    @Test
    public void negative() {
        Assert.assertEquals(new Fraction(1, 2), new Fraction(-1, -2));
        Assert.assertEquals(new Fraction(-1, 2), new Fraction(1, -2));
        Assert.assertEquals(new Fraction(-1, 2), new Fraction(-1, 2));
    }

    @Test
    public void print() {
        Assert.assertEquals(new Fraction(1, 1).toString(), "1");
        Assert.assertEquals(new Fraction(10, 5).reduce().toString(), "2");
        Assert.assertEquals(new Fraction(3, 2).toString(), "3/2");
        Assert.assertEquals(new Fraction(0, 5).toString(), "0");
    }

    @Test
    public void addition() {
        Assert.assertEquals(new Fraction(17, 7),
                new Fraction(3, 7).plus(new Fraction(2)));
        Assert.assertEquals(new Fraction(3, 5),
                new Fraction(12, 30).plus(new Fraction(1, 5)));
        Assert.assertEquals(new Fraction(17, 12),
                new Fraction(7, 12).plus(new Fraction(5, 6)));
    }

    @Test
    public void subtraction() {
        Assert.assertEquals(new Fraction(1),
                new Fraction(3, 2).minus(new Fraction(1, 2)));
        Assert.assertEquals(new Fraction(5, 7),
                new Fraction(41, 28).minus(new Fraction(3, 4)));
        Assert.assertEquals(new Fraction(8, 7),
                new Fraction(38,21).minus(new Fraction(2, 3)));
    }

    @Test
    public void multiplication() {
        Assert.assertEquals(new Fraction(1),
                new Fraction(3, 2).times(new Fraction(2, 3)));
        Assert.assertEquals(new Fraction(5, 7),
                new Fraction(15, 14).times(new Fraction(2, 3)));
        Assert.assertEquals(new Fraction(4, 5),
                new Fraction(6, 7).times(new Fraction(14, 15)));
    }

    @Test
    public void division() {
        Assert.assertEquals(new Fraction(1, 2),
                new Fraction(1).div(new Fraction(2)));
        Assert.assertEquals(new Fraction(1),
                new Fraction(3, 2).div(new Fraction(3, 2)));
        Assert.assertEquals(new Fraction(15, 14),
                new Fraction(5, 7).div(new Fraction(2, 3)));
        Assert.assertEquals(new Fraction(6, 7),
                new Fraction(4, 5).div(new Fraction(14, 15)));
    }

    @Test
    public void pow() {
        Assert.assertEquals(new Fraction(1),
                new Fraction(123, 534).pow(0));
        Assert.assertEquals(new Fraction(81,49),
                new Fraction(9, 7).pow(2));
        Assert.assertEquals(new Fraction(125, 27),
                new Fraction(3, 5).pow(-3));
    }

    @Test
    public void reduction() {
        Assert.assertEquals(new Fraction(2, 5), new Fraction(10, 25).reduce());
        Assert.assertEquals(new Fraction(1, 2), new Fraction(1, 2).reduce());
        Assert.assertEquals(new Fraction(3, 7), new Fraction(21, 49).reduce());
        Assert.assertEquals(new Fraction(9, 10), new Fraction(81, 90).reduce());
        Assert.assertEquals(new Fraction(16, 23), new Fraction(128,184).reduce());
    }
}
