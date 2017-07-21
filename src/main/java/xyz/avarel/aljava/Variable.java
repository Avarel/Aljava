package xyz.avarel.aljava;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Variable implements TexElement {
    private final String name;
    private final int degree;

    private static final Set<String> GREEK_LETTERS = new HashSet<>(Arrays.asList(
            "alpha",
            "beta",
            "gamma",
            "Gamma",
            "delta",
            "Delta",
            "epsilon",
            "varepsilon",
            "zeta",
            "eta",
            "theta",
            "vartheta",
            "Theta",
            "iota",
            "kappa",
            "lambda",
            "Lambda",
            "mu",
            "nu",
            "xi",
            "Xi",
            "pi",
            "Pi",
            "rho",
            "varrho",
            "sigma",
            "Sigma",
            "tau",
            "upsilon",
            "Upsilon",
            "phi",
            "varphi",
            "Phi",
            "chi",
            "psi",
            "Psi",
            "omega",
            "Omega"
    ));

    public Variable(String name) {
        this(name, 1);
    }

    public Variable(String name, int degree) {
        this.name = name;
        this.degree = degree;
    }

    public String getName() {
        return name;
    }

    public int getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        switch (degree) {
            case 0:
                return "";
            case 1:
                return name;
            default:
                return name + "^" + degree;
        }
    }

    @Override
    public String toTex() {
        String name = this.name;
        if (GREEK_LETTERS.contains(name)) {
            name = "\\" + name;
        }

        switch (degree) {
            case 0:
                return "";
            case 1:
                return name;
            default:
                return name + "^{" + degree + "}";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            Variable other = (Variable) obj;
            return this.name.equals(other.name) && this.degree == other.degree;
        }
        return this == obj;
    }
}
