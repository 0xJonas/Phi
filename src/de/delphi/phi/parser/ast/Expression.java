package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.data.Type;

public abstract class Expression {

    protected Expression parentExpression;

    protected PhiScope scope;

    public static PhiObject bindAndLookUp(PhiObject obj, PhiCollection scope) throws PhiException{
        if(obj.getType() == Type.SYMBOL) {
            //Bind symbol to current scope if not bound already
            if(!((PhiSymbol) obj).isBound())
                obj = new PhiSymbol(obj.toString(), scope);
            obj = obj.lookUp();
        }
        return obj;
    }

    public abstract PhiObject eval(PhiCollection parentScope) throws PhiException;
}
