package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class WhileExpr extends ExitableExpr{

    private Expression condition, body;

    public WhileExpr(Expression parentExpr){
        super(parentExpr);
    }

    public void setCondition(Expression condition){
        this.condition = condition;
    }

    public void setBody(Expression body){
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) {
        this.scope = new PhiScope(parentScope);

        shouldExit = false;
        PhiObject result = PhiNull.NULL;
        while(condition.eval(scope).longValue() != 0) {
            result = body.eval(scope);
            if(shouldExit){
                return returnValue;
            }
        }

        return result;
    }
}
