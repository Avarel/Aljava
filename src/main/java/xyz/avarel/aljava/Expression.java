package xyz.avarel.aljava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Expression implements TexElement{
    private final List<Term> terms;
    private final List<Fraction> constants;

    public Expression() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public Expression(String name) {
        this(new Term(new Variable(name)));
    }

    public Expression(int constant) {
        this(new Fraction(constant));
    }

    public Expression(Fraction constant) {
        this(Collections.emptyList(), Collections.singletonList(constant));
    }

    public Expression(Term term) {
        this(Collections.singletonList(term), Collections.emptyList());
    }

    public Expression(Term term, Fraction constant) {
        this(Collections.singletonList(term), Collections.singletonList(constant));
    }

    public Expression(List<Term> terms, List<Fraction> constants) {
        this.terms = terms;
        this.constants = constants;
    }

    public Fraction constant() {
        Fraction accumulator = new Fraction(0, 1);
        for (Fraction constant : constants) {
            accumulator = accumulator.plus(constant);
        }
        return accumulator;
    }

    public Expression simplify() {
        List<Term> simplifiedTerms = new ArrayList<>();
        for (Term term : terms) {
            simplifiedTerms.add(term.simplify());
        }

        Expression simplifiedExpr = new Expression(simplifiedTerms, constants)
                .sort()
                .combineLikeTerms()
                .moveTermsWithDegreeZeroToConstants()
                .removeTermsWithCoefficientZero();

        List<Fraction> newConstants = new ArrayList<>();
        Fraction constant = simplifiedExpr.constant();
        if (constant.getNumerator() != 0) {
            newConstants.add(constant);
        }

        return new Expression(simplifiedExpr.terms, newConstants);
    }

    public Expression plus(String other) {
        return plus(new Expression(other), true);
    }

    public Expression plus(int other) {
        return plus(new Expression(other), true);
    }

    public Expression plus(Term other) {
        return plus(new Expression(other), true);
    }

    public Expression plus(Fraction other) {
        return plus(new Expression(other), true);
    }

    public Expression plus(Expression other) {
        return plus(other, true);
    }

    public Expression plus(Expression other, boolean simplify) {
        List<Term> newTerms = new ArrayList<>(this.terms);
        newTerms.addAll(other.terms);

        List<Fraction> newConstants = new ArrayList<>(this.constants);
        newConstants.addAll(other.constants);

        Expression result = new Expression(newTerms, newConstants);
        return simplify ? result.simplify() : result;
    }

    public Expression minus(String other) {
        return minus(new Expression(other), true);
    }

    public Expression minus(int other) {
        return minus(new Expression(other), true);
    }

    public Expression minus(Term other) {
        return minus(new Expression(other), true);
    }

    public Expression minus(Fraction other) {
        return minus(new Expression(other), true);
    }

    public Expression minus(Expression other) {
        return minus(other, true);
    }

    public Expression minus(Expression other, boolean simplify) {
        return plus(other.times(-1), simplify);
    }

    public Expression times(String other) {
        return times(new Expression(other), true);
    }

    public Expression times(int other) {
        return times(new Expression(other), true);
    }

    public Expression times(Term other) {
        return times(new Expression(other), true);
    }

    public Expression times(Fraction other) {
        return times(new Expression(other), true);
    }

    public Expression times(Expression other) {
        return times(other, true);
    }

    public Expression times(Expression other, boolean simplify) {
        List<Term> newTerms = new ArrayList<>();

        for (Term thisTerm : this.terms) {
            for (Term otherTerm : other.terms) {
                newTerms.add(thisTerm.times(otherTerm));
            }

            for (Fraction otherConstant : other.constants) {
                newTerms.add(thisTerm.times(otherConstant));
            }
        }

        for (Fraction thisConstant : this.constants) {
            for (Term otherTerm : other.terms) {
                newTerms.add(otherTerm.times(thisConstant));
            }
        }

        List<Fraction> newConstants = new ArrayList<>();

        for (Fraction thisConstant : this.constants) {
            for (Fraction otherConstant : other.constants) {
                newConstants.add(thisConstant.times(otherConstant));
            }
        }

        Expression result = new Expression(newTerms, newConstants);
        return simplify ? result.simplify() : result;
    }

    public Expression div(String other) {
        return div(new Expression(other));
    }

    public Expression div(int other) {
        return div(new Fraction(other));
    }

    public Expression div(Term other) {
        return div(new Expression(other));
    }

    public Expression div(Fraction other) {
        List<Term> newTerms = new ArrayList<>();

        for (Term term : terms) {
            List<Fraction> newCoefficients = new ArrayList<>();

            for (Fraction coefficient : term.getCoefficients()) {
                newCoefficients.add(coefficient.div(other));
            }

            newTerms.add(new Term(newCoefficients, term.getVariables()));
        }

        List<Fraction> newConstants = new ArrayList<>();

        for (Fraction constant : constants) {
            newConstants.add(constant.div(other));
        }

        return new Expression(newTerms, newConstants);
    }

    public Expression div(Expression other) {
        return div(other, true);
    }

    public Expression div(Expression other, boolean simplify) {
        Expression num = this.simplify();
        Expression den = other.simplify();

        int denLen = den.terms.size() + den.constants.size();

        if (denLen != 1) {
            throw new ArithmeticException("Expressions can only be divided by monomials.");
        }

        if (den.terms.size() != 1) {
            // den constants must be 1
            return div(den.constants.get(0));
        }

        List<Term> newTerms = new ArrayList<>();

        for (Term numTerm : num.terms) {
            // The expressions have just been simplified - only one coefficient per term
            Fraction newCoefficient = numTerm.getCoefficients().get(0)
                    .div(den.terms.get(0).getCoefficients().get(0));


            List<Variable> variables = new ArrayList<>();

            // Cancel powers
            // new ArrayList just in case they are unreadable
            List<Variable> numVars = new ArrayList<>(numTerm.getVariables());
            List<Variable> denVars = new ArrayList<>(den.terms.get(0).getVariables());

            for (int i = 0; i < numVars.size(); i++) {
                Variable numVar = numVars.get(i);
                int newDegree = numVar.getDegree();
                for (int j = i + 1; j < terms.size(); j++) {
                    Variable denVar = denVars.get(j);
                    if (numVar.getName().equals(denVar.getName())) {
                        newDegree -= denVar.getDegree();
                        denVars.remove(j--);
                    }
                }
                numVars.remove(i--);
                variables.add(new Variable(numVar.getName(), newDegree));
            }

            // Inverse all degrees of remaining variables
            for (int i = 0; i < denVars.size(); i++) {
                Variable denVar = denVars.get(i);
                denVars.set(i, new Variable(denVar.getName(), -denVar.getDegree()));
            }

            variables.addAll(denVars);

            newTerms.add(new Term(Collections.singletonList(newCoefficient), variables));
        }

        for (Fraction constant : num.constants) {
            Fraction newCoefficient = constant
                    .div(den.terms.get(0).getCoefficients().get(0));

            List<Variable> variables = new ArrayList<>();
            List<Variable> denVars = new ArrayList<>(den.terms.get(0).getVariables());

            // Inverse all variables
            for (Variable denVar : denVars) {
                variables.add(new Variable(denVar.getName(), -denVar.getDegree()));
            }

            newTerms.add(new Term(Collections.singletonList(newCoefficient), variables));
        }

        Expression result = new Expression(newTerms, Collections.emptyList());
        return simplify ? result.simplify() : result;
    }

    public Expression pow(int n) {
        if (n == 0) {
            return new Expression(0);
        }

        Expression product = this;
        for (int i = 1; i < n; i++) {
            product = product.times(this);
        }
        return product;
    }

    public Expression pow(Expression other) {
        if (!(other.terms.isEmpty() && other.constants.size() == 1)) {
            throw new ArithmeticException("Expressions can only be raised to an integer");
        }

        Fraction constant = other.constants.get(0);

        double n = constant.toDouble();

        if (n % 1 != 0) {
            throw new ArithmeticException("Expressions can only be raised to an integer");
        }

        return pow((int) n);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < terms.size(); i++) {
            Term term = terms.get(i);

            if (i > 0) {
                if (!term.getCoefficients().isEmpty()) {
                    sb.append(term.getCoefficients().get(0).toDouble() < 0 ? " - " : " + ");

                    List<Fraction> newCoeff = new ArrayList<>();
                    if (!term.getCoefficients().isEmpty()) {
                        newCoeff.add(term.getCoefficients().get(0).abs());
                        newCoeff.addAll(term.getCoefficients().subList(1, term.getCoefficients().size()));
                    }

                    term = new Term(newCoeff, term.getVariables());
                } else {
                    sb.append(" + ");
                }
            } else {
                if (!term.getCoefficients().isEmpty() && term.getCoefficients().get(0).toDouble() < 0) {
                    sb.append('-');
                }

                List<Fraction> newCoeff = new ArrayList<>();

                if (!term.getCoefficients().isEmpty()) {
                    newCoeff.add(term.getCoefficients().get(0).abs());
                    newCoeff.addAll(term.getCoefficients().subList(1, term.getCoefficients().size()));
                }

                term = new Term(newCoeff, term.getVariables());
            }

            sb.append(term);
        }

        for (int i = 0; i < constants.size(); i++) {
            Fraction constant = constants.get(i);

            if (!terms.isEmpty() || i > 0) {
                sb.append(constant.toDouble() < 0 ? " - " : " + ");
            } else if (constant.toDouble() < 0) {
                sb.append('-');
            }

            sb.append(constant.abs());
        }

        return sb.toString();
    }

    @Override
    public String toTex() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < terms.size(); i++) {
            Term term = terms.get(i);

            if (i > 0) {
                if (!term.getCoefficients().isEmpty()) {
                    sb.append(term.getCoefficients().get(0).toDouble() < 0 ? " - " : " + ");

                    List<Fraction> newCoeff = new ArrayList<>();
                    if (!term.getCoefficients().isEmpty()) {
                        newCoeff.add(term.getCoefficients().get(0).abs());
                        newCoeff.addAll(term.getCoefficients().subList(1, term.getCoefficients().size()));
                    }

                    term = new Term(newCoeff, term.getVariables());
                } else {
                    sb.append(" + ");
                }
            } else {
                if (!term.getCoefficients().isEmpty() && term.getCoefficients().get(0).toDouble() < 0) {
                    sb.append('-');
                }

                List<Fraction> newCoeff = new ArrayList<>();
                if (!term.getCoefficients().isEmpty()) {
                    newCoeff.add(term.getCoefficients().get(0).abs());
                    newCoeff.addAll(term.getCoefficients().subList(1, term.getCoefficients().size()));
                }

                term = new Term(newCoeff, term.getVariables());
            }

            sb.append(term.toTex());
        }

        for (int i = 0; i < constants.size(); i++) {
            Fraction constant = constants.get(i);

            if (!terms.isEmpty() || i > 0) {
                sb.append(constant.toDouble() < 0 ? " - " : " + ");
            } else if (constant.toDouble() < 0) {
                sb.append('-');
            }

            sb.append(constant.abs().toTex());
        }

        return sb.toString();
    }

    private Expression removeTermsWithCoefficientZero() {
        List<Term> newTerms = new ArrayList<>();
        for (Term term : terms) {
            if (term.coefficient().getNumerator() != 0) {
                newTerms.add(term);
            }
        }
        return new Expression(newTerms, constants);
    }

    private Expression combineLikeTerms() {
        // change possible read-only into a writable list
        List<Term> terms = new ArrayList<>(this.terms);
        List<Term> newTerms = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            Term accumulator = terms.get(i);
            // Cherry pick the rest of the list
            // combine with accumulator and remove them from writable list
            for (int j = i + 1; j < terms.size(); j++) {
                Term otherTerm = terms.get(j);
                if (accumulator.canBeCombinedWith(otherTerm)) {
                    accumulator = accumulator.plus(otherTerm);
                    terms.remove(j--);
                }
            }
            newTerms.add(accumulator);
            terms.remove(i--);
        }
        return new Expression(newTerms, constants);
    }

    private Expression moveTermsWithDegreeZeroToConstants() {
        List<Term> keepTerms = new ArrayList<>();
        List<Fraction> newConstants = new ArrayList<>();

        for (Term term : terms) {
            if (term.maxDegree() == 0) {
                newConstants.add(term.coefficient());
            } else {
                keepTerms.add(term);
            }
        }

        newConstants.addAll(this.constants);

        return new Expression(keepTerms, newConstants);
    }

    private Expression sort() {
        List<Term> sortedTerms = new ArrayList<>(terms);
        sortedTerms.sort((a, b) -> {
            String aName = a.getVariables().isEmpty() ? "" : a.getVariables().get(0).getName();
            String bName = b.getVariables().isEmpty() ? "" : b.getVariables().get(0).getName();

            int value = aName.compareTo(bName);

            if (value != 0) return value;

            return b.maxDegree() - a.maxDegree();
        });
        return new Expression(sortedTerms, constants);
    }

    public boolean hasVariable(String name) {
        for (Term term : terms) {
            if (term.hasVariable(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyHasVariable(String name) {
        for (Term term : terms) {
            if (!term.onlyHasVariable(name)) {
                return false;
            }
        }
        return true;
    }

    public int maxDegree() {
        if (terms.isEmpty()) return 0;

        int degree = terms.get(0).maxDegree();
        for (int i = 1; i < terms.size(); i++) {
            Term term = terms.get(i);
            degree = Math.max(degree, term.maxDegree());
        }
        return degree;
    }

    public int maxDegreeOfVariable(String name) {
        if (terms.isEmpty()) return 0;

        int degree = terms.get(0).maxDegreeOfVariable(name);
        for (int i = 1; i < terms.size(); i++) {
            Term term = terms.get(i);
            degree = Math.max(degree, term.maxDegreeOfVariable(name));
        }
        return degree;
    }

    public boolean noCrossProducts() {
        for (Term term : terms) {
            if (term.getVariables().size() > 1) {
                return false;
            }
        }
        return true;
    }

    public boolean noCrossProductsWithVariable(String variable) {
        for (Term term : terms) {
            if (term.hasVariable(variable) && !term.onlyHasVariable(variable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Expression) {
            Expression me = this.simplify();
            Expression other = ((Expression) obj).simplify();
            return me.terms.equals(other.terms) && me.constants.equals(other.constants);
        }
        return this == obj;
    }

    public List<Term> getTerms() {
        return terms;
    }
}
