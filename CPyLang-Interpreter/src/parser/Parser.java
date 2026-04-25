package parser;

import lexer.*;
import runtime.SymbolTable;
import java.util.*;

public class Parser {

    private List<Token> tokens;
    private int index;
    private SymbolTable table;

    private Map<String, Integer> functions = new HashMap<>();

    private int retVal = 0;
    private boolean returned = false;

    public Parser(List<Token> tokens, SymbolTable table) {
        this.tokens = tokens;
        this.table = table;
        this.index = 0;
    }

    private Token current() {
        return tokens.get(index);
    }

    private void next() {
        index++;
    }

    private void expect(TokenType t) {
        if (current().type != t) {
            throw new RuntimeException(
                "Syntax Error near '" + current().value +
                "' expected " + t + " but found " + current().type
            );
        }
        next();
    }

    public void parse() {
        while (current().type != TokenType.EOF) {
            statement();
        }
    }

    private void statement() {
        TokenType type = current().type;

        if (type == TokenType.IDENTIFIER) {
            if (tokens.get(index + 1).type == TokenType.LPAREN) {
                callFunction();
            } else {
                assign();
            }
        } 
        else if (type == TokenType.SHOW) {
            showStmt();
        } 
        else if (type == TokenType.IF) {
            ifStmt();
        } 
        else if (type == TokenType.WHILE) {
            whileStmt();
        } 
        else if (type == TokenType.FUNC) {
            defineFunction();
        } 
        else if (type == TokenType.RETURN) {
            returnStmt();
        } 
        else {
            throw new RuntimeException("Unexpected token: " + current().type);
        }
    }

    private void defineFunction() {
        next();

        String name = current().value;
        next();

        expect(TokenType.LPAREN);

        while (current().type != TokenType.RPAREN) {
            next();
        }

        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);

        functions.put(name, index);

        while (current().type != TokenType.RBRACE) {
            next();
        }

        expect(TokenType.RBRACE);
    }

    private int callFunction() {
        String name = current().value;
        next();

        expect(TokenType.LPAREN);

        while (current().type != TokenType.RPAREN) {
            next();
        }

        expect(TokenType.RPAREN);

        int back = index;

        index = functions.get(name);
        returned = false;

        while (current().type != TokenType.RBRACE) {
            statement();
            if (returned) break;
        }

        index = back;

        return retVal;
    }

    private void returnStmt() {
        next();

        retVal = expression();
        returned = true;

        expect(TokenType.SEMICOLON);
    }

    private List<Integer> parseArray() {
        List<Integer> arr = new ArrayList<>();

        expect(TokenType.LBRACKET);

        while (current().type != TokenType.RBRACKET) {
            arr.add(expression());

            if (current().type == TokenType.COMMA) {
                next();
            }
        }

        expect(TokenType.RBRACKET);

        return arr;
    }

    private void assign() {
        String name = current().value;
        next();

        expect(TokenType.ASSIGN);

        Object value;

        if (current().type == TokenType.LBRACKET) {
            value = parseArray();
        }
        else if (current().type == TokenType.IDENTIFIER &&
                tokens.get(index + 1).type == TokenType.LPAREN) {
            value = callFunction();
        }
        else {
            value = expression();
        }

        table.set(name, value);

        expect(TokenType.SEMICOLON);
    }

    private void showStmt() {
    next();

    expect(TokenType.LPAREN);

    if (current().type == TokenType.STRING) {
        System.out.println(current().value);
        next();
    } 
    else {
        Object val;

        if (current().type == TokenType.IDENTIFIER &&
            tokens.get(index + 1).type != TokenType.PLUS &&
            tokens.get(index + 1).type != TokenType.MINUS &&
            tokens.get(index + 1).type != TokenType.MULTIPLY &&
            tokens.get(index + 1).type != TokenType.DIVIDE &&
            tokens.get(index + 1).type != TokenType.LBRACKET) {

            val = table.get(current().value);
            next();
        }
        else {
            val = expression();
        }

        System.out.println(val);
    }

    expect(TokenType.RPAREN);
    expect(TokenType.SEMICOLON);
}

    private int expression() {
        int val = term();

        while (current().type == TokenType.PLUS ||
               current().type == TokenType.MINUS) {

            TokenType op = current().type;
            next();

            int right = term();

            if (op == TokenType.PLUS) val += right;
            else val -= right;
        }

        return val;
    }

    private int term() {
        int val = factor();

        while (current().type == TokenType.MULTIPLY ||
               current().type == TokenType.DIVIDE) {

            TokenType op = current().type;
            next();

            int right = factor();

            if (op == TokenType.MULTIPLY) val *= right;
            else val /= right;
        }

        return val;
    }

    private int factor() {
        Token t = current();

        if (t.type == TokenType.NUMBER) {
            next();
            return Integer.parseInt(t.value);
        }

        if (t.type == TokenType.IDENTIFIER) {

            String name = t.value;

            if (tokens.get(index + 1).type == TokenType.LPAREN) {
                return callFunction();
            }

            next();

            Object val = table.get(name);

            if (current().type == TokenType.LBRACKET) {

                next();

                int idx = expression();

                expect(TokenType.RBRACKET);

                List<?> arr = (List<?>) val;

                return (int) arr.get(idx);
            }

            return (val instanceof Integer) ? (int) val : 0;
        }

        return 0;
    }

    private boolean condition() {
        int left = expression();

        TokenType op = current().type;
        next();

        int right = expression();

        if (op == TokenType.LESS) return left < right;
        if (op == TokenType.GREATER) return left > right;
        if (op == TokenType.EQUAL) return left == right;

        return false;
    }

    private void ifStmt() {
        next();

        expect(TokenType.LPAREN);

        boolean res = condition();

        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);

        if (res) {
            while (current().type != TokenType.RBRACE) {
                statement();
            }
        } else {
            while (current().type != TokenType.RBRACE) {
                next();
            }
        }

        expect(TokenType.RBRACE);

        if (current().type == TokenType.ELSE) {
            next();

            expect(TokenType.LBRACE);

            if (!res) {
                while (current().type != TokenType.RBRACE) {
                    statement();
                }
            } else {
                while (current().type != TokenType.RBRACE) {
                    next();
                }
            }

            expect(TokenType.RBRACE);
        }
    }

    private void whileStmt() {
        next();

        expect(TokenType.LPAREN);

        int condStart = index;
        boolean cond = condition();

        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);

        int bodyStart = index;

        while (cond) {
            index = bodyStart;

            while (current().type != TokenType.RBRACE) {
                statement();
            }

            index = condStart;
            cond = condition();

            expect(TokenType.RPAREN);
            expect(TokenType.LBRACE);
        }

        while (current().type != TokenType.RBRACE) {
            next();
        }

        expect(TokenType.RBRACE);
    }
}