package xyz.avarel.aljava;

import java.util.*;

public class Term implements TexElement {
    private final List<Fraction> coefficients;
    private final List<Variable> variables;

    // Workaround to make parsing a bit easier
    // In an Expression this would be collasped into a normal fraction
    public Term(Fraction constant) {
        this(Collections.singletonList(constant), Collections.emptyList());
    }

    public Term(Variable variable) {
        this(Collections.emptyList(), Collections.singletonList(variable));
    }

    public Term(Fraction coefficient, Variable variable) {
        this(Collections.singletonList(coefficient), Collections.singletonList(variable));
    }

    public Term(List<Fraction> coefficients, List<Variable> variables) {
        this.coefficients = coefficients;
        this.variables = variables;
    }

    public List<Fraction> getCoefficients() {
        return coefficients;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public Term simplify() {
        List<Fraction> coefficient = Collections.singletonList(coefficient());
        return new Term(coefficient, variables).combineVariables();
    }

    public Fraction coefficient() {
        Fraction coefficient = new Fraction(1);
        for (Fraction f : coefficients) {
            coefficient = coefficient.times(f);
        }
        return coefficient;
    }

    private Term combineVariables() {
        // Sorted map will put the variables in alphabetic order as per convention
        Map<String, Integer> uniqueVars = new TreeMap<>();
        for (Variable var : variables) {
            String name = var.getName();
            if (uniqueVars.containsKey(name)) {
                uniqueVars.put(name, uniqueVars.get(name) + var.getDegree());
            } else {
                uniqueVars.put(name, var.getDegree());
            }
        }

        List<Variable> newVars = new ArrayList<>(uniqueVars.size());
        for (Map.Entry<String, Integer> entry : uniqueVars.entrySet()) {
            newVars.add(new Variable(entry.getKey(), entry.getValue()));
        }
        return new Term(coefficients, newVars);
    }

    public Term plus(Term other) {
        Term thisSimple = this.simplify();
        Term otherSimple = other.simplify();
        if (thisSimple.canBeCombinedWith(otherSimple)) {
            return new Term(Collections.singletonList(thisSimple.coefficient().plus(otherSimple.coefficient())), thisSimple.variables);
        } else {
            throw new ArithmeticException(this.toString() + " can not be combined with " + other.toString());
        }
    }

    public Term minus(Term other) {
        Term thisSimple = simplify();
        Term otherSimple = other.simplify();
        if (thisSimple.canBeCombinedWith(otherSimple)) {
            return new Term(Collections.singletonList(thisSimple.coefficient().minus(otherSimple.coefficient())), thisSimple.variables);
        } else {
            throw new ArithmeticException(this.toString() + " can not be combined with " + other.toString());
        }
    }

    public Term times(Term other) {
        List<Fraction> newCoefficients = new ArrayList<>(this.coefficients);
        newCoefficients.addAll(other.coefficients);

        List<Variable> newVariables = new ArrayList<>(this.variables);
        newVariables.addAll(other.variables);

        return new Term(newCoefficients, newVariables).simplify();
    }

    public Term times(Fraction other) {
        List<Fraction> newCoefficients = new ArrayList<>(this.coefficients);
        newCoefficients.add(other);

        return new Term(newCoefficients, this.variables).simplify();
    }

    public Term div(Fraction other) {
        List<Fraction> newCoefficients = new ArrayList<>();
        for (Fraction coefficient : coefficients) {
            newCoefficients.add(coefficient.div(other));
        }

        return new Term(newCoefficients, this.variables).simplify();
    }

    public int maxDegree() {
        List<Variable> simplified = simplify().variables;
        if (simplified.isEmpty()) return 0;
        int degree = simplified.get(0).getDegree();
        for (int i = 1; i < simplified.size(); i++) {
            degree = Math.max(degree, simplified.get(i).getDegree());
        }
        return degree;
    }

    public int maxDegreeOfVariable(String name) {
        List<Variable> simplified = simplify().variables;

        if (simplified.isEmpty()) return 0;

        int degree = simplified.get(0).getDegree();
        for (int i = 1; i < simplified.size(); i++) {
            Variable variable = simplified.get(i);
            if (variable.getName().equals(name)) {
                degree = Math.max(degree, variable.getDegree());
            }
        }
        return degree;
    }

    public boolean hasVariable(String name) {
        for (Variable variable : variables) {
            if (variable.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyHasVariable(String name) {
        if (variables.size() < 1) return false;
        for (Variable variable : variables) {
            if (!variable.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public boolean canBeCombinedWith(Term other) {
        if (this.variables.size() != other.variables.size()) {
            return false;
        }

        int matches = 0;
        for (Variable outer : this.variables) {
            for (Variable inner : other.variables) {
                if (outer.equals(inner)) {
                    matches++;
                }
            }
        }

        return matches == variables.size();
    }

    // todo eval(Map<String, Fraction> variables)
    // evaluate the expression with substituted values

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < coefficients.size(); i++) {
            Fraction f = coefficients.get(i);

            if (f.abs().toDouble() != 1) {
                sb.append(f);

                if ((i != coefficients.size() - 1 && maxDegree() == 1) || f.getDenominator() != 1) {
                    sb.append(" * ");
                }
            }
        }

        for (int i = 0; i < variables.size(); i++) {
            sb.append(variables.get(i));

            if (i != variables.size() - 1) {
                sb.append("*");
            }
        }

        return sb.toString();
    }

    @Override
    public String toTex() {
        String op = " \\cdot ";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coefficients.size(); i++) {
            Fraction f = coefficients.get(i);

            if (f.abs().toDouble() != 1) {
                sb.append(f.toTex());
            }
        }

        for (Variable variable : variables) {
            sb.append(variable.toTex());
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Term) {
            Term me = this.simplify();
            Term other = ((Term) obj).simplify();
            return me.coefficients.equals(other.coefficients) && me.variables.equals(other.variables);
        }
        return this == obj;
    }
}
