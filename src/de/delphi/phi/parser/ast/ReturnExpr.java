package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class ReturnExpr extends Expression {

    private PhiObject retVal = PhiNull.NULL;

    public ReturnExpr(Expression parentExpr){
        super(parentExpr);
    }

    public void setReturnValue(PhiObject retVal){
        this.retVal = retVal;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) {
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
