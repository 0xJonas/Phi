package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiFloat;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;

public class NegationExpr extends Expression {

    private Expression body;

    public NegationExpr(Expression parentExpr, Expression body){
        super(parentExpr);
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
        PhiObject result = body.eval(parentScope);
        result = bindAndLookUp(result, parentScope);

        switch(result.getType()){
            case INT: return new PhiInt(-result.longValue());
            case FLOAT: return new PhiFloat(-result.doubleValue());
            default: throw new PhiException("Cannot negate type " + result.getType());
        }
    }
}
