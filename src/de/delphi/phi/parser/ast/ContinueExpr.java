package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class ContinueExpr extends Expression{

    private Expression returnExpr;

    public ContinueExpr(Expression parentExpr, Expression returnExpr){
        super(parentExpr);
        this.returnExpr = returnExpr;
    }

    private boolean isLoopExpression(Expression expr){
        return (expr instanceof WhileExpr) || (expr instanceof ForExpr);
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
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
