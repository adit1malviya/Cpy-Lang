package lexer;

import java.util.*;

public class Lexer {

    private String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    private char currentChar() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos);
    }

    private void advance() {
        pos++;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (currentChar() != '\0') {
            char ch = currentChar();

            if (Character.isWhitespace(ch)) {
                advance();
            }

            // 🔥 STRING SUPPORT
            else if (ch == '"') {
                advance(); // skip "

                StringBuilder sb = new StringBuilder();

                while (currentChar() != '"' && currentChar() != '\0') {
                    sb.append(currentChar());
                    advance();
                }

                advance(); // skip closing "

                tokens.add(new Token(TokenType.STRING, sb.toString()));
            }

            else if (Character.isLetter(ch)) {
                StringBuilder sb = new StringBuilder();

                while (Character.isLetterOrDigit(currentChar())) {
                    sb.append(currentChar());
                    advance();
                }

                String word = sb.toString();

                switch (word) {
                    case "print": tokens.add(new Token(TokenType.PRINT, word)); break;
                    case "if": tokens.add(new Token(TokenType.IF, word)); break;
                    case "else": tokens.add(new Token(TokenType.ELSE, word)); break;
                    case "while": tokens.add(new Token(TokenType.WHILE, word)); break;
                    default: tokens.add(new Token(TokenType.IDENTIFIER, word));
                }
            }

            else if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();

                while (Character.isDigit(currentChar())) {
                    sb.append(currentChar());
                    advance();
                }

                tokens.add(new Token(TokenType.NUMBER, sb.toString()));
            }

            else if (ch == '=') {
                advance();
                if (currentChar() == '=') {
                    advance();
                    tokens.add(new Token(TokenType.EQUAL, "=="));
                } else {
                    tokens.add(new Token(TokenType.ASSIGN, "="));
                }
            }

            else if (ch == '+') { tokens.add(new Token(TokenType.PLUS, "+")); advance(); }
            else if (ch == '-') { tokens.add(new Token(TokenType.MINUS, "-")); advance(); }
            else if (ch == '*') { tokens.add(new Token(TokenType.MULTIPLY, "*")); advance(); }
            else if (ch == '/') { tokens.add(new Token(TokenType.DIVIDE, "/")); advance(); }

            else if (ch == '>') { tokens.add(new Token(TokenType.GREATER, ">")); advance(); }
            else if (ch == '<') { tokens.add(new Token(TokenType.LESS, "<")); advance(); }

            else if (ch == '(') { tokens.add(new Token(TokenType.LPAREN, "(")); advance(); }
            else if (ch == ')') { tokens.add(new Token(TokenType.RPAREN, ")")); advance(); }
            else if (ch == '{') { tokens.add(new Token(TokenType.LBRACE, "{")); advance(); }
            else if (ch == '}') { tokens.add(new Token(TokenType.RBRACE, "}")); advance(); }
            else if (ch == ';') { tokens.add(new Token(TokenType.SEMICOLON, ";")); advance(); }

            else {
                throw new RuntimeException("Invalid char: " + ch);
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}