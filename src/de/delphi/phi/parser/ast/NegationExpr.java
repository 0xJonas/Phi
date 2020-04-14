package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiFloat;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;

public class NegationExpr extends UnaryExpr {

    public NegationExpr(Expression body){
        super(body);
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        PhiObject result = body.eval(parentScope);
        result = bindAndLookUp(result, parentScope);

        switch(result.getType()){
            case INT: return new PhiInt(-result.longValue());
            case FLOAT: return new PhiFloat(-result.doubleValue());
            default: throw new PhiTypeException("Cannot negate type " + result.getType());
        }
    }
}
