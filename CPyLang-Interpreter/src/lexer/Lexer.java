package lexer;

import java.util.*;

public class Lexer {

    private String input;
    private int pos = 0;

    public Lexer(String input) {
        super();
        this.input = input;
    }

    private char currentChar() {
        boolean outOfBounds = input.length() <= pos;
        if (outOfBounds) {
            return '\0';
        }
        return input.charAt(pos);
    }

    private void advance() {
        pos += 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        for (;;) {
            char ch = currentChar();
            
            if (ch == '\0') {
                break;
            }

            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
                advance();
                continue;
            }

            if (ch == '"') {
                advance(); 

                StringBuilder sb = new StringBuilder();

                for (;;) {
                    char next = currentChar();
                    if (next == '"' || next == '\0') {
                        break;
                    }
                    sb.append(next);
                    advance();
                }

                advance(); 
                tokens.add(new Token(TokenType.STRING, sb.toString()));
                continue;
            }

            if (Character.isLetter(ch)) {
                StringBuilder sb = new StringBuilder();

                for (;;) {
                    char c = currentChar();
                    if (!Character.isLetterOrDigit(c)) {
                        break;
                    }
                    sb.append(c);
                    advance();
                }

                String word = sb.toString();

                if ("print".equals(word)) tokens.add(new Token(TokenType.PRINT, word));
                else if ("func".equals(word)) tokens.add(new Token(TokenType.FUNC, word));
                else if ("if".equals(word)) tokens.add(new Token(TokenType.IF, word));
                else if ("else".equals(word)) tokens.add(new Token(TokenType.ELSE, word));
                else if ("while".equals(word)) tokens.add(new Token(TokenType.WHILE, word));
                else if ("return".equals(word)) tokens.add(new Token(TokenType.RETURN, word));
                else tokens.add(new Token(TokenType.IDENTIFIER, word));
                
                continue;
            }

            if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();

                do {
                    sb.append(currentChar());
                    advance();
                } while (Character.isDigit(currentChar()));

                tokens.add(new Token(TokenType.NUMBER, sb.toString()));
                continue;
            }

            if (ch == '=') {
                advance();
                if (currentChar() != '=') {
                    tokens.add(new Token(TokenType.ASSIGN, "="));
                } else {
                    tokens.add(new Token(TokenType.EQUAL, "=="));
                    advance();
                }
                continue;
            }

            if (ch == '+') { advance(); tokens.add(new Token(TokenType.PLUS, "+")); continue; }
            if (ch == '-') { advance(); tokens.add(new Token(TokenType.MINUS, "-")); continue; }
            if (ch == '*') { advance(); tokens.add(new Token(TokenType.MULTIPLY, "*")); continue; }
            if (ch == '/') { advance(); tokens.add(new Token(TokenType.DIVIDE, "/")); continue; }

            if (ch == '<') { advance(); tokens.add(new Token(TokenType.LESS, "<")); continue; }
            if (ch == '>') { advance(); tokens.add(new Token(TokenType.GREATER, ">")); continue; }

            if (ch == '(') { advance(); tokens.add(new Token(TokenType.LPAREN, "(")); continue; }
            if (ch == ')') { advance(); tokens.add(new Token(TokenType.RPAREN, ")")); continue; }
            if (ch == '{') { advance(); tokens.add(new Token(TokenType.LBRACE, "{")); continue; }
            if (ch == '}') { advance(); tokens.add(new Token(TokenType.RBRACE, "}")); continue; }
            if (ch == ';') { advance(); tokens.add(new Token(TokenType.SEMICOLON, ";")); continue; }
            if (ch == ',') { advance(); tokens.add(new Token(TokenType.COMMA, ",")); continue; }

            throw new RuntimeException("Invalid char: " + ch);
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}