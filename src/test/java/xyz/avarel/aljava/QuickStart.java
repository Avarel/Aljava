package xyz.avarel.aljava;

import java.util.List;

public class QuickStart {
    public static void main(String[] args) {
        Expression expr = new Expression("x");
        expr = expr.minus(3);
        expr = expr.plus("x");

        System.out.println(expr);

        Equation eq = new Equation(expr, 4);

        System.out.println(eq);

        List<Object> answers = eq.solveFor("x");
        System.out.println("x = " + answers.get(0));
    }
}
