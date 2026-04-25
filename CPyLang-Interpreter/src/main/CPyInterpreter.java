package main;

import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import runtime.SymbolTable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class CPyInterpreter {

    /**
     * Runs the given CPy source code and returns the output (or error message).
     * Returns a RunResult with the output text and whether it was an error.
     */
    public RunResult run(String code) {
        // Capture System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        System.setOut(new PrintStream(baos));
        System.setErr(new PrintStream(baos));

        try {
            Lexer l = new Lexer(code);
            List<Token> t = l.tokenize();
            SymbolTable s = new SymbolTable();
            Parser p = new Parser(t, s);
            p.parse();

            System.out.flush();
            System.setOut(oldOut);
            System.setErr(oldErr);

            return new RunResult(baos.toString(), false);
        } catch (Exception e) {
            System.out.flush();
            System.setOut(oldOut);
            System.setErr(oldErr);

            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            return new RunResult("Error: " + errorMsg, true);
        }
    }

    public static class RunResult {
        public final String output;
        public final boolean isError;

        public RunResult(String output, boolean isError) {
            this.output = output;
            this.isError = isError;
        }
    }
}