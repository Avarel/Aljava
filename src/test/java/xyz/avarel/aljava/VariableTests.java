package xyz.avarel.aljava;

import org.junit.Assert;
import org.junit.Test;

public class VariableTests {
    @Test
    public void stringRepresentation() {
        Assert.assertEquals("x", new Variable("x").toString());
        Assert.assertEquals("y", new Variable("y", 1).toString());
        Assert.assertEquals("z^2", new Variable("z", 2).toString());
        Assert.assertEquals("", new Variable("x", 0).toString());
    }
}
