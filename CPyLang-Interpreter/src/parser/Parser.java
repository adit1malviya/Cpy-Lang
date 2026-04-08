package parser;

import lexer.*;
import runtime.SymbolTable;
import java.util.*;

public class Parser {

    private List<Token> tokenSequence;
    private int cursor;
    private SymbolTable variableMap;

    private Map<String, Integer> functionMap = new HashMap<>();

    private int returnValue = 0;
    private boolean hasReturn = false;

    public Parser(List<Token> tokenSequence, SymbolTable symTab) {
        this.tokenSequence = tokenSequence;
        this.variableMap = symTab;
        this.cursor = 0;
    }

    private Token activeToken() {
        return this.tokenSequence.get(this.cursor);
    }

    private void step() {
        this.cursor++;
    }

    public void parse() {
        while (activeToken().type != TokenType.EOF) {
            processNode();
        }
    }

    private void processNode() {
        TokenType kind = activeToken().type;

        if (kind == TokenType.IDENTIFIER) {
            if (tokenSequence.get(cursor + 1).type == TokenType.LPAREN) {
                parseFunctionCall();
            } else {
                parseAssignment();
            }
        }
        else if (kind == TokenType.PRINT) {
            parsePrint();
        }
        else if (kind == TokenType.IF) {
            parseIfNode();
        }
        else if (kind == TokenType.WHILE) {
            parseWhileNode();
        }
        else if (kind == TokenType.FUNC) {
            parseFunctionDef();
        }
        else if (kind == TokenType.RETURN) {
            parseReturn();
        }
        else {
            step();
        }
    }

    // 🔥 FUNCTION DEF
    private void parseFunctionDef() {
        step(); // func

        String name = activeToken().value;
        step(); // name

        step(); // (

        while (activeToken().type != TokenType.RPAREN) step();

        step(); // )
        step(); // {

        functionMap.put(name, cursor);

        while (activeToken().type != TokenType.RBRACE) step();

        step(); // }
    }

    // 🔥 FUNCTION CALL WITH RETURN
    private int parseFunctionCall() {
        String name = activeToken().value;
        step(); // name

        step(); // (

        while (activeToken().type != TokenType.RPAREN) step();

        step(); // )

        int returnPos = cursor;

        cursor = functionMap.get(name);

        hasReturn = false;

        while (activeToken().type != TokenType.RBRACE) {
            processNode();
            if (hasReturn) break;
        }

        cursor = returnPos;

        return returnValue;
    }

    // 🔥 RETURN
    private void parseReturn() {
        step(); // return

        returnValue = evalExpression();
        hasReturn = true;

        step(); // ;
    }

    private void parseAssignment() {
        String var = activeToken().value;
        step(); // var
        step(); // =

        int value;

        if (activeToken().type == TokenType.IDENTIFIER &&
            tokenSequence.get(cursor + 1).type == TokenType.LPAREN) {
            value = parseFunctionCall();
        } else {
            value = evalExpression();
        }

        variableMap.set(var, value);

        step(); // ;
    }

    private void parsePrint() {
        step(); // print
        step(); // (

        if (activeToken().type == TokenType.STRING) {
            System.out.println(activeToken().value);
            step();
        } else {
            int val = evalExpression();
            System.out.println(val);
        }

        step(); // )
        step(); // ;
    }

    private int evalExpression() {
        int left = evalTerm();

        while (activeToken().type == TokenType.PLUS ||
               activeToken().type == TokenType.MINUS) {

            TokenType op = activeToken().type;
            step();

            int right = evalTerm();

            if (op == TokenType.PLUS) left += right;
            else left -= right;
        }

        return left;
    }

    private int evalTerm() {
        int left = evalFactor();

        while (activeToken().type == TokenType.MULTIPLY ||
               activeToken().type == TokenType.DIVIDE) {

            TokenType op = activeToken().type;
            step();

            int right = evalFactor();

            if (op == TokenType.MULTIPLY) left *= right;
            else left /= right;
        }

        return left;
    }

    private int evalFactor() {
        Token t = activeToken();

        if (t.type == TokenType.NUMBER) {
            step();
            return Integer.parseInt(t.value);
        }

        if (t.type == TokenType.IDENTIFIER) {
            if (tokenSequence.get(cursor + 1).type == TokenType.LPAREN) {
                return parseFunctionCall();
            }
            step();
            return variableMap.get(t.value);
        }

        return 0;
    }

    private boolean checkTruthValue() {
        int left = evalExpression();

        TokenType op = activeToken().type;
        step();

        int right = evalExpression();

        if (op == TokenType.LESS) return left < right;
        if (op == TokenType.GREATER) return left > right;
        if (op == TokenType.EQUAL) return left == right;

        return false;
    }

    private void parseIfNode() {
        step(); step();

        boolean cond = checkTruthValue();

        step(); step();

        if (cond) {
            while (activeToken().type != TokenType.RBRACE) processNode();
        } else {
            while (activeToken().type != TokenType.RBRACE) step();
        }

        step();

        if (activeToken().type == TokenType.ELSE) {
            step(); step();

            if (!cond) {
                while (activeToken().type != TokenType.RBRACE) processNode();
            } else {
                while (activeToken().type != TokenType.RBRACE) step();
            }

            step();
        }
    }

    private void parseWhileNode() {
        step(); step();

        int condPos = cursor;
        boolean cond = checkTruthValue();

        step(); step();

        int bodyPos = cursor;

        while (cond) {
            cursor = bodyPos;

            while (activeToken().type != TokenType.RBRACE) processNode();

            cursor = condPos;
            cond = checkTruthValue();

            step(); step();
        }

        while (activeToken().type != TokenType.RBRACE) step();
        step();
    }
}