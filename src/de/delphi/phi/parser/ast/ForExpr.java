package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class ForExpr extends ExitableExpr{

    private Expression init, condition, iteration, body;

    public ForExpr(Expression parentExpr){
        super(parentExpr);
    }

    public void setInit(Expression init){
        this.init = init;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public void setIteration(Expression iteration) {
        this.iteration = iteration;
    }

    public void setBody(Expression body) {
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) {
        this.scope = new PhiScope(parentScope);

        shouldExit = false;
        PhiObject result = PhiNull.NULL;
        for(init.eval(scope); condition.eval(scope).longValue() != 0; iteration.eval(scope)){
            result = body.eval(scope);
            if(shouldExit){
                return returnValue;
            }
        }
        return result;
    }
}
