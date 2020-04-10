package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

import java.util.ArrayList;

public class CompoundExpr extends ExitableExpr{

    private ArrayList<Expression> children;

    public CompoundExpr(ArrayList<Expression> children){
        this.children = new ArrayList<>(children);
        for(Expression expr: children)
            expr.parentExpression = this;
    }

    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        this.scope = new PhiScope(parentScope);

        shouldExit = false;
        PhiObject result = PhiNull.NULL;
        for (Expression child : children) {
            result = child.eval(scope);
            if(shouldExit){
                return returnValue;
            }
        }
        return result;
    }
}
