/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aljava.lexer;

import xyz.avarel.aljava.exceptions.MathException;
import xyz.avarel.aljava.exceptions.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lexer implements Iterator<Token>, Iterable<Token> {
    private Reader reader;
    private List<Token> tokens;

    private Entry[] history;
    private int previous;

    private boolean eof;
    private long lineIndex;
    private long index;
    private long line;
    private char current;

    public Lexer(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public Lexer(String s) {
        this(new StringReader(s));
    }

    public Lexer(Reader reader) {
        this(reader, 2);
    }

    public Lexer(Reader reader, int historyBuffer) {
        this.reader = reader.markSupported()
                ? reader
                : new BufferedReader(reader);
        this.eof = false;
        this.tokens = new ArrayList<>();

        history = new Entry[historyBuffer];

        this.current = 0;
        this.index = -1;
        this.lineIndex = 0;
        this.line = 1;
    }

    /**
     * @return The next token.
     */
    @Override
    public Token next() {
        return readToken();
    }

    @Override
    public boolean hasNext() {
        return !(previous == 0 && this.eof);
    }

    @Override
    public Iterator<Token> iterator() {
        return this;
    }

    private Token readToken() {
        if (!hasNext()) return make(TokenType.EOF);

        char c = advance();

        while (Character.isSpaceChar(c)) c = advance();

        switch (c) {
            case '(': return make(TokenType.LEFT_PAREN);
            case ')': return make(TokenType.RIGHT_PAREN);

            case '+': return make(TokenType.PLUS);
            case '-': return make(TokenType.MINUS);
            case '*': return make(TokenType.TIMES);
            case '/': return make(TokenType.DIV);
            case '^': return make(TokenType.POW);

            case '=': return make(TokenType.EQUALS);

            case '\0':
            case (char) -1: return make(TokenType.EOF);

            default:
                if (Character.isDigit(c)) {
                    return nextNumber(c);
                } else if (Character.isLetter(c)) {
                    return nextName(c);
                } else {

                    throw new SyntaxException("Unrecognized `" + c + "`", getPosition());
                }
        }
    }

    private Token nextComment() {
        while (hasNext() && peek() != '\n') {
            advance();
        }
        return null;
    }

    private Token nextNumber(char init) {
        char c;

        StringBuilder sb = new StringBuilder();
        sb.append(init);

        while (true) {
            c = advance();

            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                back();
                return make(TokenType.INT, sb.toString());
            }
        }
    }

    private Token nextName(char init) {
        return nextName(String.valueOf(init));
    }

    private Token nextName(String init) {
        StringBuilder sb = new StringBuilder(init);

        char c;
        while (true) {
            c = advance();

            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
            } else {
                break;
            }
        }

        back();

        String value = sb.toString();
        return make(TokenType.VARIABLE, value);
    }

    private Token make(TokenType type) {
        return make(new Position(index, line, lineIndex), type);
    }

    private Token make(TokenType type, String value) {
        return make(new Position(index - value.length(), line, lineIndex - value.length()), type, value);
    }

    private Token make(Position position, TokenType type) {
        return new Token(position, type);
    }

    private Token make(Position position, TokenType type, String value) {
        return new Token(position, type, value);
    }

    // useful for lexer-phase desugaring
    private void queue(char character) {
        System.arraycopy(history, previous + 1, history, 3, history.length - 3);
        history[previous] = new Entry(index, line, lineIndex, character);
        back();
    }

    /**
     * Get the readToken character in the source string.
     *
     * @return The readToken character, or 0 if past the end of the source string.
     */
    private char advance() {
        int c;
        if (this.previous != 0) {
            this.previous--;

            Entry entry = history[previous];

            current = entry.character;
            index = entry.index;
            line = entry.line;
            lineIndex = entry.lineIndex;

            return this.current;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
                throw new SyntaxException("Exception occurred while lexing", getPosition(), exception);
            }

            if (c <= 0) { // End of stream
                this.eof = true;
                c = 0;
            }
        }

        this.index += 1;
        if (this.current == '\r') {
            this.line += 1;
            this.lineIndex = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            this.line += 1;
            this.lineIndex = 0;
        } else {
            this.lineIndex += 1;
        }
        this.current = (char) c;

        System.arraycopy(history, 0, history, 1, history.length - 1);

        history[0] = new Entry(index, line, lineIndex, current);

        return this.current;
    }


    /**
     * Consume the next character, and check that
     * it matches a specified character.
     *
     * @param c The character to match.
     * @return The character.
     */
    private char advance(char c) {
        char n = this.advance();
        if (n != c) {
            throw new SyntaxException("Expected '" + c + "' and instead saw '" + n + "'", getPosition());
        }
        return n;
    }


    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     * @throws MathException Substring bounds error if there are not
     *                      n characters remaining in the source string.
     */
    private String advance(int n) {
        if (n == 0) {
            return "";
        }

        char[] chars = new char[n];
        int pos = 0;

        while (pos < n) {
            chars[pos] = this.advance();
            if (!this.hasNext()) {
                throw new SyntaxException("Substring bounds error", getPosition());
            }
            pos += 1;
        }
        return new String(chars);
    }

    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @return A character, or 0 if there are no more characters.
     */
    private char advanceClean() {
        while (true) {
            char c = this.advance();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    /**
     * @return The next character.
     */
    private char peek() {
        char c = advance();
        back();
        return c;
    }

    /**
     * Peek and advance if the prompt is the same as the peeked character.
     *
     * @param prompt The character to match.
     * @return if the prompt is the same as the peeked character.
     */
    private boolean match(char prompt) {
        if (advance() == prompt) {
            return true;
        }
        back();
        return false;
    }

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the readToken number or identifier.
     */
    private void back() {
        previous++;
    }

    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     *
     * @param delimiter A delimiter character.
     * @return A string.
     */
    private String advanceTo(char delimiter) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = this.advance();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     *
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    private String advanceTo(String delimiters) {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = this.advance();
            if (delimiters.indexOf(c) >= 0 || c == 0 ||
                    c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Skip characters until the readToken character is the requested character.
     * If the requested character is not found, no characters are skipped.
     *
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    private char skipTo(char to) {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.lineIndex;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                c = this.advance();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.lineIndex = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while (c != to);
        } catch (IOException exception) {
            throw new SyntaxException("Exception occurred while lexing", getPosition(), exception);
        }
        this.back();
        return c;
    }

    /**
     * Make a printable string of this AJELexer.
     * @return " at {index} [character {character} line {line}]"
     */
    @Override
    public String toString() {
        return getPosition().toString();
    }

    public Position getPosition() {
        return new Position(index, line, lineIndex);
    }

    private static final class Entry {
        private final long index;
        private final long line;
        private final long lineIndex;
        private final char character;

        private Entry(long index, long line, long lineIndex, char character) {
            this.index = index;
            this.line = line;
            this.lineIndex = lineIndex;
            this.character = character;
        }

        @Override
        public String toString() {
            return String.valueOf(character);
        }
    }
}