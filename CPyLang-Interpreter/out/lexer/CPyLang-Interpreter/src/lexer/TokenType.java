package lexer;

public enum TokenType {
    IDENTIFIER,
    NUMBER,
    STRING,

    ASSIGN,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,

    GREATER,
    LESS,
    EQUAL,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    SEMICOLON,

    PRINT,
    IF,
    ELSE,
    WHILE,

    EOF
}