package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class WhileExpr extends ExitableExpr{

    private Expression condition, body;

    public WhileExpr(Expression parentExpr, Expression condition, Expression body){
        super(parentExpr);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
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
