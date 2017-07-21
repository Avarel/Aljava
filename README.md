Aljava [![Download](https://api.bintray.com/packages/avarel/maven/Aljava/images/download.svg)](https://bintray.com/avarel/maven/Aljava/_latestVersion) [![Build Status](https://travis-ci.org/Avarel/Aljava.svg?branch=master)](https://travis-ci.org/Avarel/Aljava)
===
This is a port of **[algebra.js](https://github.com/nicolewhite/algebra.js)** for Java.
Aljava closely mimics algebra.js and should support most of its features.
Aljava supports LaTex rendering by outputting a string that can be parsed by a
LaTex renderer such as **[JLatexMath](https://github.com/opencollab/jlatexmath)**.

Quick Start
---
```java
import xyz.avarel.aljava.Expression;

public class QuickStart {
    public static void main(String[] args) {
        Expression expr = new Expression("x");
        expr = expr.minus(3);
        expr = expr.plus("x");

        System.out.println(expr);
        // 2x - 3

        Equation eq = new Equation(expr, 4);

        System.out.println(eq);
        // 2x - 3 = 4

        List<Object> answers = eq.solveFor("x");
        System.out.println("x = " + answers.get(0));
        // x = 7/2
    }
}

```

Check out the quickstart code at [`Quickstart.java`](/src/test/java/xyz/avarel/aljava/QuickStart.java).

Parser
===
Incomplete, but it's functional! Check it out at [`EvalLoop.java`](/src/test/java/xyz/avarel/aljava/EvalLoop.java).