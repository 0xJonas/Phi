package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;

public class ReturnExpr extends UnaryExpr {

    public ReturnExpr(Expression body){
        super(body);
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        PhiObject retVal = body.eval(parentScope);
        retVal = bindAndLookUp(retVal, parentScope);

        Expression current = this;
        while(!(current instanceof FunctionBody)){
            if(current instanceof ExitableExpr)
                ((ExitableExpr) current).exit(retVal);
            current = current.parentExpression;
        }
        //Set return value of the function
        ((ExitableExpr) current).exit(retVal);
        return retVal;
    }
}
