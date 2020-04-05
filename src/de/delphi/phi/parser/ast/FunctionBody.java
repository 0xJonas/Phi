package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiObject;

/**
 * Wraps another expression, to mark it as the direction body of a function. This is
 * needed to make ReturnExpr work correctly.
 */
public class FunctionBody extends ExitableExpr {

    private Expression body;

    public FunctionBody(Expression body){
        super(null);
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) {
        PhiObject result = body.eval(parentScope);
        if(shouldExit)
            return returnValue;
        else
            return result;
    }
}
