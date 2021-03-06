package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class BreakExpr extends Expression {

    private Expression returnExpr;

    public BreakExpr(Expression returnExpr){
        this.returnExpr = returnExpr;
        if(returnExpr != null)
            returnExpr.parentExpression = this;
    }

    private boolean isLoopExpression(Expression expr){
        return (expr instanceof WhileExpr) || (expr instanceof ForExpr);
    }

    @Override
    public Expression getChild(int index) {
        if(returnExpr != null && index == 0)
            return returnExpr;
        else
            throw new IndexOutOfBoundsException();
    }

    @Override
    public int countChildren() {
        return returnExpr != null ? 1 : 0;
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
        //break actual loop
        ((ExitableExpr) current).exit(retVal);
        return retVal;
    }
}
