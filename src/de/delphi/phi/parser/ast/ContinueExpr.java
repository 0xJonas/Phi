package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiObject;

public class ContinueExpr extends Expression{

    private PhiObject retVal;

    public ContinueExpr(Expression parentExpr, PhiObject retVal){
        super(parentExpr);
        this.retVal = retVal;
    }

    private boolean isLoopExpression(Expression expr){
        return (expr instanceof WhileExpr) || (expr instanceof ForExpr);
    }

    @Override
    public PhiObject eval(PhiScope parentScope) {
        Expression current = this;
        while(!isLoopExpression(current)){
            if(current instanceof ExitableExpr)
                ((ExitableExpr) current).exit(retVal);
            current = current.parentExpression;
        }
        return retVal;
    }
}
