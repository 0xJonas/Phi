package de.delphi.phi.parser;

import de.delphi.phi.data.PhiObject;

class Literal extends Token {

    public PhiObject content;

    public Literal(String lexeme, PhiObject content, int line, int col){
        super(Tag.LITERAL, lexeme, line, col);
        this.content = content;
    }
}
