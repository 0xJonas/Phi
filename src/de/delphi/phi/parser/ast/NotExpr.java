package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

public class NotExpr extends Expression {

    private Expression body;

    public NotExpr(Expression parentExpr, Expression body){
        super(parentExpr);
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
        PhiObject result = body.eval(parentScope);
        result = bindAndLookUp(result, parentScope);

        if(result.getType() == Type.INT)
            return new PhiInt(~result.longValue());
        else
            throw new PhiException("Cannot invert type " + result.getType());
    }
}
