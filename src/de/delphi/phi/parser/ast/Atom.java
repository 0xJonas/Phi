package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiObject;

public class Atom extends Expression {

    private PhiObject content;

    public Atom(Expression parentExpr){
        super(parentExpr);
    }

    public void setContent(PhiObject content){
        this.content = content;
    }

    @Override
    public PhiObject eval(PhiScope scope) {
        return content;
    }
}
