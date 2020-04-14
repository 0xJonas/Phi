package de.delphi.phi.parser.ast;

import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;

public class Atom extends Expression {

    private PhiObject content;

    public Atom(PhiObject content){
        this.content = content;
    }


    @Override
    public Expression getChild(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int countChildren() {
        return 0;
    }

    @Override
    public PhiObject eval(PhiCollection scope) {
        return content;
    }
}
