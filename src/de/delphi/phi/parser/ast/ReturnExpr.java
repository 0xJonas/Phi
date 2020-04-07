package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class ReturnExpr extends Expression {

    private Expression returnExpr;

    public ReturnExpr(Expression parentExpr, Expression returnExpr){
        super(parentExpr);
        this.returnExpr = returnExpr;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
        PhiObject retVal = PhiNull.NULL;
        if(returnExpr != null){
            retVal = returnExpr.eval(parentScope);
            retVal = bindAndLookUp(retVal, parentScope);
        }

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
