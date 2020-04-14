package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public class WhileExpr extends ExitableExpr{

    private Expression condition, body;

    public WhileExpr(Expression condition, Expression body){
        this.condition = condition;
        this.body = body;

        condition.parentExpression = this;
        body.parentExpression = this;
    }

    @Override
    public Expression getChild(int index) {
        switch(index){
            case 0: return condition;
            case 1: return body;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int countChildren() {
        return 2;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
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
