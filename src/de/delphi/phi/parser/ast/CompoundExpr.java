package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

import java.util.ArrayList;

public class CompoundExpr extends ExitableExpr{

    public static final int EXIT_END = 0;
    public static final int EXIT_BREAK = 1;
    public static final int EXIT_CONTINUE = 2;
    public static final int EXIT_RETURN = 3;

    private ArrayList<Expression> children;

    public CompoundExpr(Expression parentExpr){
        super(parentExpr);
    }

    public ArrayList<Expression> getChildren(){
        return children;
    }

    public PhiObject eval(PhiScope parentScope){
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
