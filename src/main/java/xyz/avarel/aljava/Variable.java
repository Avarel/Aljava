package xyz.avarel.aljava;

public class Variable implements TexElement {
    private final String name;
    private final int degree;

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
