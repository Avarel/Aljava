package xyz.avarel.aljava;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Equation implements TexElement {
    private final Expression lhs;
    private final Expression rhs;

    public Equation(Expression lhs, int rhs) {
        this(lhs, new Expression(rhs));
    }

    public Equation(Expression lhs, String rhs) {
        this(lhs, new Expression(rhs));
    }

    public Equation(Expression lhs, Fraction rhs) {
        this(lhs, new Expression(rhs));
    }

    public Equation(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public List<Object> solveFor(String variable) {
        if (!lhs.hasVariable(variable) && !rhs.hasVariable(variable)) {
            throw new ArithmeticException("Variable (" + variable + ") does not exist on either side of the equation.");
        }

        if (isLinear() || variableCanBeIsolated(variable)) {
            Term solvingFor = new Term(new Variable(variable));
            Expression newLhs = new Expression();
            Expression newRhs = new Expression();

            for (Term term : rhs.getTerms()) {
                if (term.canBeCombinedWith(solvingFor)) {
                    newLhs = newLhs.minus(term);
                } else {
                    newRhs = newRhs.plus(term);
                }
            }

            for (Term term : lhs.getTerms()) {
                if (term.canBeCombinedWith(solvingFor)) {
                    newLhs = newLhs.plus(term);
                } else {
                    newRhs = newRhs.minus(term);
                }
            }

            newRhs = newRhs.minus(lhs.constant());
            newRhs = newRhs.plus(rhs.constant());

            if (newLhs.getTerms().isEmpty()) {
                if (newLhs.constant().equals(newRhs.constant())) {
                    return Collections.singletonList(new Expression(new Fraction(1,1)));
                } else {
                    throw new ArithmeticException("No solution");
                }
            }

            newRhs = newRhs.div(newLhs.getTerms().get(0).coefficient());

            if (newRhs.getTerms().isEmpty()) {
                return Collections.singletonList(new Expression(newRhs.constant().reduce()));
            }

            newRhs = newRhs.simplify();
            return Collections.singletonList(newRhs);
        }

        // Otherwise, move everything to the LHS.
        Expression newLhs = lhs.minus(rhs);

        if (newLhs.getTerms().isEmpty()) {
            if (newLhs.constant().toDouble() == 0) {
                return Collections.singletonList(new Expression(new Fraction(1,1)));
            } else {
                throw new ArithmeticException("No solution");
            }
        } else if (isQuadratic(variable)) {
            Fraction a = new Fraction(0);
            Fraction b = new Fraction(0);

            for (Term term : newLhs.getTerms()) {
                if (term.maxDegree() == 2) {
                    a = term.coefficient();
                } else if (term.maxDegree() == 1) {
                    b = term.coefficient();
                }
            }

            Fraction c = newLhs.constant();

            Fraction discriminant = b.pow(2).minus(a.times(c).times(4));

            if (discriminant.toDouble() > 0) {
                if (discriminant.sqrtIsRational()) {
                    Fraction sqrtDiscriminant = discriminant.sqrt();

                    Fraction r1 = b.times(-1).minus(sqrtDiscriminant).div(a.times(2));
                    Fraction r2 = b.times(-1).plus(sqrtDiscriminant).div(a.times(2));

                    return Arrays.asList(r1.reduce(), r2.reduce());
                } else {
                    double sqrtDiscriminant = Math.sqrt(discriminant.toDouble());

                    double a_ = a.toDouble();
                    double b_ = b.toDouble();

                    double r1 = (-b_ - sqrtDiscriminant) / (2 * a_);
                    double r2 = (-b_ + sqrtDiscriminant) / (2 * a_);

                    return Arrays.asList(r1, r2);
                }
            } else if (discriminant.toDouble() == 0) {
                // Only 1 root, vertex formula
                return Collections.singletonList(new Expression(b.times(-1).div(a.times(2)).reduce()));
            } else {
                return Collections.emptyList();
            }
        }

        throw new ArithmeticException("Equation (" + lhs + " = " + rhs + ") must be linear or quadratic.");
    }

    @Override
    public String toString() {
        return lhs + " = " + rhs;
    }

    @Override
    public String toTex() {
        return lhs.toTex() + " = " + rhs.toTex();
    }

    public int maxDegree() {
        return Math.max(lhs.maxDegree(), rhs.maxDegree());
    }

    public int maxDegreeOfVariable(String variable) {
        return Math.max(lhs.maxDegreeOfVariable(variable), rhs.maxDegreeOfVariable(variable));
    }

    public boolean noCrossProducts() {
        return lhs.noCrossProducts() && rhs.noCrossProducts();
    }

    public boolean noCrossProductsWithVariable(String variable) {
        return lhs.noCrossProductsWithVariable(variable) && rhs.noCrossProductsWithVariable(variable);
    }

    public boolean onlyHasVariable(String variable) {
        return lhs.onlyHasVariable(variable) && rhs.onlyHasVariable(variable);
    }

    public boolean variableCanBeIsolated(String variable) {
        return maxDegreeOfVariable(variable) == 1 && noCrossProductsWithVariable(variable);
    }

    public boolean isLinear() {
        return maxDegree() == 1 && noCrossProducts();
    }

    public boolean isQuadratic(String variable) {
        return maxDegree() == 2 && onlyHasVariable(variable);
    }

    public boolean isCubic(String variable) {
        return maxDegree() == 3 && onlyHasVariable(variable);
    }
}
