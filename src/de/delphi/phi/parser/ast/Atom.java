package de.delphi.phi.parser.ast;

import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;

public class Atom extends Expression {

    private PhiObject content;

    public Atom(PhiObject content){
        this.content = content;
    }

    @Override
    public PhiObject eval(PhiCollection scope) {
        return content;
    }
}
