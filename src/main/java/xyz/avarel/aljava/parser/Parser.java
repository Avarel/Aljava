package xyz.avarel.aljava.parser;

import xyz.avarel.aljava.Equation;
import xyz.avarel.aljava.Expression;
import xyz.avarel.aljava.exceptions.SyntaxException;
import xyz.avarel.aljava.lexer.Lexer;
import xyz.avarel.aljava.lexer.Token;
import xyz.avarel.aljava.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private final List<Token> tokens;

    private Token last;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.tokens = new ArrayList<>();
    }

    private Token getLast() {
        return last;
    }

    private List<Token> getTokens() {
        return tokens;
    }

    private Lexer getLexer() {
        return lexer;
    }

    private Token eat(TokenType expected) {
        Token token = peek(0);
        if (token.getType() != expected) {
            throw new SyntaxException("Expected token " + expected + " but found " + token.getType(), token.getPosition());
        }
        return eat();
    }

    private boolean match(TokenType expected) {
        Token token = peek(0);
        if (token.getType() != expected) {
            return false;
        }
        eat();
        return true;
    }

    private Token eat() {
        // Make sure we've read the token.
        peek(0);

        return last = tokens.remove(0);
    }


    private Token peek(int distance) {
        // Read in as many as needed.
        while (distance >= tokens.size()) {
            tokens.add(lexer.next());
        }

        // Get the queued token.
        return tokens.get(distance);
    }

    private boolean peek(TokenType... tokens) {
        for (int i = 0; i < tokens.length; i++) {
            if (peek(i).getType() != tokens[i]) return false;
        }

        return true;
    }

    private boolean peekAny(TokenType... tokens) {
        for (TokenType token : tokens) {
            if (peek(token)) return true;
        }

        return false;
    }

    private boolean nextIs(TokenType type) {
        return peek(0).getType() == type;
    }

    public Equation parseEquation() {
        Expression lhs = parseExpression();
        eat(TokenType.EQUALS);
        Expression rhs = parseExpression();

        if (!getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw new SyntaxException("Unexpected " + t, t.getPosition());
            }
        }

        return new Equation(lhs, rhs);
    }

    public Expression parse() {
        Expression value = parseExpression();

        if (!getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw new SyntaxException("Unexpected " + t, t.getPosition());
            }
        }

        return value;
    }

    public Expression parseExpression() {
        Expression value = parseTerm();
        while (true) {
            if (match(TokenType.PLUS)) value = value.plus(parseTerm());
            else if (match(TokenType.MINUS)) value = value.minus(parseTerm());
            else return value;
        }
    }

    public Expression parseTerm() {
        Expression value = parseFactor();
        while (true) {
            if (match(TokenType.TIMES)) value = value.times(parseFactor());
            else if (match(TokenType.DIV)) value = value.div(parseFactor());
            else return value;
        }
    }

    public Expression parseFactor() {
        if (match(TokenType.PLUS)) return parseFactor();
        if (match(TokenType.MINUS)) return parseFactor().times(-1);

        Expression value;

        if (match(TokenType.LEFT_PAREN)) {
            value = parseExpression();
            match(TokenType.RIGHT_PAREN);
        } else if (match(TokenType.INT)) {
            value = new Expression(Integer.parseInt(getLast().getString()));
        } else if (match(TokenType.VARIABLE)) {
            value = new Expression(getLast().getString());
        } else {
            throw new RuntimeException("Unexpected: " + getLast().getType());
        }

        if (nextIs(TokenType.LEFT_PAREN)) {
            value = value.times(parseExpression());
        } else if (match(TokenType.POW)) {
            value = value.pow(parseFactor());
        } else if (nextIs(TokenType.VARIABLE)) {
            value = value.times(parseFactor());
        }

        return value;
    }
}
