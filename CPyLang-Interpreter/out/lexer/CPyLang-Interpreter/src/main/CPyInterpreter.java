package main;

import lexer.*;
import parser.Parser;
import runtime.SymbolTable;

import java.util.List;

public class CPyInterpreter {

    public void run(String code) {
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        SymbolTable symbolTable = new SymbolTable();

        Parser parser = new Parser(tokens, symbolTable);
        parser.parse();
    }
}