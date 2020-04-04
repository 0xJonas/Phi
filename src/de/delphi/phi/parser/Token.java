package de.delphi.phi.parser;

class Token {

    Tag tag;
    String lexeme;
    int line, col;

    public Token(Tag tag, String lexeme, int line, int col){
        this.tag = tag;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;
    }

    @Override
    public String toString() {
        return String.format("%s: \"%s\" line: %d, col: %d", tag.toString(), lexeme, line, col);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Token))
            return false;
        Token other = (Token) obj;
        return this.tag == other.tag
                && this.lexeme.equals(other.lexeme)
                && this.line == other.line
                && this.col == other.col;
    }
}
