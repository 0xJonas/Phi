package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class ContinueExpr extends Expression{

    private Expression returnExpr;

    public ContinueExpr(Expression returnExpr){
        this.returnExpr = returnExpr;
        if(returnExpr != null)
            returnExpr.parentExpression = this;
    }

    private boolean isLoopExpression(Expression expr){
        return (expr instanceof WhileExpr) || (expr instanceof ForExpr);
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        PhiObject retVal = PhiNull.NULL;
        if(returnExpr != null){
            retVal = returnExpr.eval(parentScope);
            retVal = bindAndLookUp(retVal, parentScope);
        }

        Expression current = this;
        while(!isLoopExpression(current)){
            if(current instanceof ExitableExpr)
                ((ExitableExpr) current).exit(retVal);
            current = current.parentExpression;
        }
        return retVal;
    }
}
