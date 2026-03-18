package main;

import utils.FileLoader;

public class Main {

    public static void main(String[] args) {

        String code = FileLoader.load("programs/program.cpy");

        CPyInterpreter interpreter = new CPyInterpreter();
        interpreter.run(code);
    }
}