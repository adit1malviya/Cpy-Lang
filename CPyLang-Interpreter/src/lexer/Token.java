package lexer;

public class Token {
    
    public TokenType type;
    public String value;

    public Token(TokenType type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token[");
        sb.append(this.type.toString());
        sb.append(", \"");
        sb.append(this.value);
        sb.append("\"]");
        return sb.toString();
    }
}