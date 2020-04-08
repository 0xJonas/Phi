package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.data.Type;

public class QuoteExpr extends Expression {

    private Expression body;

    public QuoteExpr(Expression parentExpr, Expression body){
        super(parentExpr);
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        PhiObject value = body.eval(parentScope);
        if(value.getType() != Type.SYMBOL)
            throw new PhiException("Only SYMBOL type can be quoted.");

        PhiSymbol temporary = new PhiSymbol("." + value.toString());
        parentScope.createMember(temporary);
        parentScope.setNamed(temporary.toString(), value);
        return temporary;
    }
}
