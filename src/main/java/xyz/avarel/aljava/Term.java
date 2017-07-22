package xyz.avarel.aljava;

import java.util.*;

public class Term implements TexElement {
    private final List<Fraction> coefficients;
    private final List<Variable> variables;

    public Term() {
        this(new Fraction(0));
    }

    public Term(Fraction constant) {
        this(Collections.singletonList(constant), Collections.emptyList());
    }

    public Term(Variable variable) {
        this(Collections.singletonList(new Fraction(1)), Collections.singletonList(variable));
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
        if (this.canBeCombinedWith(other)) {
            return new Term(Collections.singletonList(this.coefficient().plus(other.coefficient())), this.variables);
        } else {
            throw new ArithmeticException(this.toString() + " can not be combined with " + other.toString());
        }
    }

    public Term minus(Term other) {
        if (this.canBeCombinedWith(other)) {
            return new Term(Collections.singletonList(this.coefficient().minus(other.coefficient())), this.variables);
        } else {
            throw new ArithmeticException(this.toString() + " can not be combined with " + other.toString());
        }
    }

    public Term times(Term other) {
        return times(other, true);
    }

    public Term times(Term other, boolean simplify) {
        List<Fraction> newCoefficients = new ArrayList<>(this.coefficients);
        newCoefficients.addAll(other.coefficients);

        List<Variable> newVariables = new ArrayList<>(this.variables);
        newVariables.addAll(other.variables);

        Term result =  new Term(newCoefficients, newVariables).simplify();
        return simplify ? result.simplify() : result;
    }

    public Term times(Fraction other) {
        return times(other, true);
    }

    public Term times(Fraction other, boolean simplify) {
        List<Fraction> newCoefficients = new ArrayList<>(this.coefficients);
        newCoefficients.add(other);

        Term result = new Term(newCoefficients, this.variables);
        return simplify ? result.simplify() : result;
    }

    public Term div(Fraction other) {
        return div(other, true);
    }

    public Term div(Fraction other, boolean simplify) {
        List<Fraction> newCoefficients = new ArrayList<>();
        for (Fraction coefficient : coefficients) {
            newCoefficients.add(coefficient.div(other));
        }

        Term result = new Term(newCoefficients, this.variables).simplify();
        return simplify ? result.simplify() : result;
    }

    public int maxDegree() {
        List<Variable> simplified = variables;
        if (simplified.isEmpty()) return 0;
        int degree = simplified.get(0).getDegree();
        for (int i = 1; i < simplified.size(); i++) {
            degree = Math.max(degree, simplified.get(i).getDegree());
        }
        return degree;
    }

    public int maxDegreeOfVariable(String name) {
        List<Variable> simplified = variables;

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

            if (variables.isEmpty() || f.abs().toDouble() != 1) {
                sb.append(f);

                if ((i != coefficients.size() - 1 && maxDegree() == 1) || f.getDenominator() != 1) {
                    if (i != coefficients.size() - 1 || !variables.isEmpty()) {
                        sb.append(" * ");
                    }
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
        StringBuilder sb = new StringBuilder();

        for (Fraction f : coefficients) {
            if (variables.isEmpty() || f.abs().toDouble() != 1) {
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
            Term me = this;
            Term other = ((Term) obj);
            return me.coefficients.equals(other.coefficients) && me.variables.equals(other.variables);
        }
        return this == obj;
    }
}
