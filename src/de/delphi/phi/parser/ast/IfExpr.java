package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class IfExpr extends Expression{

    private Expression condition, trueExpr, falseExpr;

    public IfExpr(Expression condition, Expression trueExpr, Expression falseExpr){
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;

        condition.parentExpression = this;
        trueExpr.parentExpression = this;
        falseExpr.parentExpression = this;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);

        PhiObject conditionResult = condition.eval(scope);
        conditionResult = bindAndLookUp(conditionResult, scope);
        if(conditionResult.longValue() != 0){
            return trueExpr.eval(scope);
        }else if(falseExpr != null){
            return falseExpr.eval(scope);
        }else
            return PhiNull.NULL;
    }
}
