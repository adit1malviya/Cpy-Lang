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
    LBRACKET,
    RBRACKET,
    SEMICOLON,

    SHOW,
    IF,
    ELSE,
    WHILE,

    FUNC,
    RETURN,
    COMMA,

    EOF
}