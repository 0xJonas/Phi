package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class ForExpr extends ExitableExpr{

    private Expression init, condition, iteration, body;

    public ForExpr(Expression init, Expression condition, Expression iteration, Expression body){
        this.init = init;
        this.condition = condition;
        this.iteration = iteration;
        this.body = body;

        init.parentExpression = this;
        condition.parentExpression = this;
        iteration.parentExpression = this;
        body.parentExpression = this;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
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
