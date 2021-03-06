package xyz.avarel.aljava;

public class Fraction implements TexElement {
    private final int numerator;
    private final int denominator;

    public Fraction(int n) {
        this(n, 1);
    }

    public Fraction(int numerator, int denominator) {
        if (denominator < 0) {
            denominator = -denominator;
            numerator = -numerator;
        }

        this.numerator = numerator;
        this.denominator = denominator;

        if (denominator == 0) {
            throw new ArithmeticException("Division by zero");
        }
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public Fraction reduce() {
        int gcd = gcd(numerator, denominator);
        return new Fraction(numerator / gcd, denominator / gcd);
    }

    public Fraction reciprocal() {
        return new Fraction(denominator, numerator);
    }

    public Fraction abs() {
        return new Fraction(Math.abs(numerator), Math.abs(denominator));
    }



    public Fraction plus(int other) {
        return plus(other, true);
    }

    public Fraction plus(int other, boolean reduce) {
        return plus(new Fraction(other), reduce);
    }

    public Fraction plus(Fraction other) {
        return plus(other, true);
    }

    public Fraction plus(Fraction other, boolean reduce) {
        if (this.denominator == other.denominator) {
            Fraction result = new Fraction(this.numerator + other.numerator, denominator);
            return reduce ? result.reduce() : result;
        }

        int lcm = lcm(this.denominator, other.denominator);
        int a = lcm / this.denominator;
        int b = lcm / other.denominator;

        Fraction result = new Fraction(this.numerator * a + other.numerator * b, lcm);
        return reduce ? result.reduce() : result;
    }



    public Fraction minus(int other) {
        return minus(other, true);
    }

    public Fraction minus(int other, boolean reduce) {
        return minus(new Fraction(other), reduce);
    }

    public Fraction minus(Fraction other) {
        return minus(other, true);
    }

    public Fraction minus(Fraction other, boolean reduce) {
        return plus(new Fraction(-other.numerator, other.denominator), reduce);
    }



    public Fraction times(int other) {
        return times(other, true);
    }

    public Fraction times(int other, boolean reduce) {
        return times(new Fraction(other), reduce);
    }

    public Fraction times(Fraction other) {
        return times(other, true);
    }

    public Fraction times(Fraction other, boolean reduce) {
        Fraction result = new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
        return reduce ? result.reduce() : result;
    }



    public Fraction div(int other) {
        return div(other, true);
    }

    public Fraction div(int other, boolean reduce) {
        return div(new Fraction(other), reduce);
    }

    public Fraction div(Fraction other) {
        return div(other, true);
    }

    public Fraction div(Fraction other, boolean reduce) {
        return times(other.reciprocal(), reduce);
    }



    public Fraction pow(int n) {
        return pow(n, true);
    }

    public Fraction pow(int n, boolean reduce) {
        if (n >= 0) {
            Fraction result = new Fraction((int) Math.pow(numerator, n), (int) Math.pow(denominator, n));
            return reduce ? result.reduce() : result;
        } else {
            return pow(Math.abs(n)).reciprocal();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Fraction) {
            Fraction other = (Fraction) obj;

            return numerator == other.numerator
                    && denominator == other.denominator;
        }
        return obj.equals(numerator / denominator);
    }

    @Override
    public String toString() {
        if (numerator == 0) {
            return "0";
        } else if (denominator == 1) {
            return String.valueOf(numerator);
        } else if (denominator == -1) {
            return String.valueOf(-numerator);
        }
        return numerator + "/" + denominator;
    }

    @Override
    public String toTex() {
        if (numerator == 0) {
            return "0";
        } else if (denominator == 1) {
            return String.valueOf(numerator);
        } else if (denominator == -1) {
            return String.valueOf(-numerator);
        }

        if (numerator < 0 && denominator < 0) {
            return "\\frac{" + -numerator + "}{" + -denominator + "}";
        } else if (numerator < 0 || denominator < 0) {
            return "-\\frac{" + numerator + "}{" + denominator + "}";
        }

        return "\\frac{" + numerator + "}{" + denominator + "}";
    }

    public double toDouble() {
        return (double) numerator / denominator;
    }


    protected Fraction sqrt() {
        if (!sqrtIsRational()) {
            throw new IllegalStateException("Internal error");
        }

        return new Fraction((int) Math.sqrt(numerator), (int) Math.sqrt(denominator));
    }

    public boolean sqrtIsRational() {
        double num = Math.sqrt(numerator);
        double den = Math.sqrt(denominator);

        return num % 1 == 0 && den % 1 == 0;
    }

    private static int gcd(int a, int b)  {
        while (a != 0 && b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
        return a + b;
    }

    private static int lcm(int a, int b) {
        return a * b / gcd(a, b);
    }
}
