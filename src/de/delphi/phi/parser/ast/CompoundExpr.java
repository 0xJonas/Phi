package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

import java.util.ArrayList;

public class CompoundExpr extends ExitableExpr{

    private ArrayList<Expression> children;

    public CompoundExpr(Expression parentExpr, ArrayList<Expression> children){
        super(parentExpr);
        this.children = new ArrayList<>(children);
    }

    public PhiObject eval(PhiScope parentScope) throws PhiException {
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
