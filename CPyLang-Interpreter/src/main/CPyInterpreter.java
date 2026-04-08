package main;

import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import runtime.SymbolTable;
import java.util.List;

public class CPyInterpreter {

    public void run(String code)
    {
        Lexer l = new Lexer(code);

        List<Token> t = l.tokenize();

        SymbolTable s = new SymbolTable();

        Parser p = new Parser(t, s);

        p.parse();
    }
}