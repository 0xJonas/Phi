package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class BreakExpr extends Expression {

    private Expression returnExpr;

    public BreakExpr(Expression parentExpr, Expression returnExpr){
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
            scope = new PhiScope(parentScope);
            retVal = returnExpr.eval(scope);
        }

        Expression current = this;
        while(!isLoopExpression(current)){
            if(current instanceof ExitableExpr)
                ((ExitableExpr) current).exit(retVal);
            current = current.parentExpression;
        }
        //break actual loop
        ((ExitableExpr) current).exit(retVal);
        return retVal;
    }
}
