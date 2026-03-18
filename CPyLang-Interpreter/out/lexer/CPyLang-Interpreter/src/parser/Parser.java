package parser;

import lexer.*;
import runtime.SymbolTable;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int pos = 0;
    private SymbolTable table;

    public Parser(List<Token> tokens, SymbolTable table) {
        this.tokens = tokens;
        this.table = table;
    }

    private Token current() {
        return tokens.get(pos);
    }

    private void advance() {
        pos++;
    }

    public void parse() {
        while (current().type != TokenType.EOF) {
            statement();
        }
    }

    private void statement() {
        if (current().type == TokenType.IDENTIFIER) {
            assignment();
        }
        else if (current().type == TokenType.PRINT) {
            printStmt();
        }
        else if (current().type == TokenType.IF) {
            ifStmt();
        }
        else if (current().type == TokenType.WHILE) {
            whileStmt();
        }
        else {
            advance();
        }
    }

    private void assignment() {
        String var = current().value;
        advance(); // var

        advance(); // =

        int value = expression();

        table.set(var, value);

        advance(); // ;
    }

    // 🔥 UPDATED PRINT FUNCTION
    private void printStmt() {
        advance(); // print
        advance(); // (

        Token t = current();

        if (t.type == TokenType.STRING) {
            System.out.println(t.value);
            advance();
        } else {
            int value = expression();
            System.out.println(value);
        }

        advance(); // )
        advance(); // ;
    }

    private int expression() {
        int left = term();

        while (current().type == TokenType.PLUS ||
               current().type == TokenType.MINUS) {

            Token op = current();
            advance();

            int right = term();

            if (op.type == TokenType.PLUS) left += right;
            else left -= right;
        }

        return left;
    }

    private int term() {
        int left = factor();

        while (current().type == TokenType.MULTIPLY ||
               current().type == TokenType.DIVIDE) {

            Token op = current();
            advance();

            int right = factor();

            if (op.type == TokenType.MULTIPLY) left *= right;
            else left /= right;
        }

        return left;
    }

    private int factor() {
        Token t = current();

        if (t.type == TokenType.NUMBER) {
            advance();
            return Integer.parseInt(t.value);
        }

        else if (t.type == TokenType.IDENTIFIER) {
            advance();
            return table.get(t.value);
        }

        return 0;
    }

    private boolean condition() {
        int left = expression();

        Token op = current();
        advance();

        int right = expression();

        if (op.type == TokenType.GREATER) return left > right;
        if (op.type == TokenType.LESS) return left < right;
        if (op.type == TokenType.EQUAL) return left == right;

        return false;
    }

    private void ifStmt() {
        advance(); // if
        advance(); // (

        boolean cond = condition();

        advance(); // )
        advance(); // {

        if (cond) {
            while (current().type != TokenType.RBRACE) {
                statement();
            }
        } else {
            while (current().type != TokenType.RBRACE) advance();
        }

        advance(); // }

        if (current().type == TokenType.ELSE) {
            advance();
            advance(); // {

            if (!cond) {
                while (current().type != TokenType.RBRACE) {
                    statement();
                }
            } else {
                while (current().type != TokenType.RBRACE) advance();
            }

            advance(); // }
        }
    }

    private void whileStmt() {
        advance(); // while
        advance(); // (

        int condStart = pos;

        boolean cond = condition();

        advance(); // )
        advance(); // {

        int bodyStart = pos;

        while (cond) {

            pos = bodyStart;

            while (current().type != TokenType.RBRACE) {
                statement();
            }

            pos = condStart;
            cond = condition();

            advance(); // )
            advance(); // {
        }

        while (current().type != TokenType.RBRACE) advance();
        advance(); // }
    }
}