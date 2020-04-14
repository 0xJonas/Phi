package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;

/**
 * Wraps another expression, to mark it as the direct body of a function. This is
 * needed to make ReturnExpr work correctly.
 */
public class FunctionBody extends ExitableExpr {

    private Expression body;

    public FunctionBody(Expression body){
        this.body = body;
        body.parentExpression = this;
    }

    @Override
    public Expression getChild(int index) {
        if(index == 0)
            return body;
        else
            throw new IndexOutOfBoundsException();
    }

    @Override
    public int countChildren() {
        return 1;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        PhiObject result = body.eval(parentScope);
        if(shouldExit)
            return returnValue;
        else
            return result;
    }
}
