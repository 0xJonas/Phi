package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.data.Type;

public class AssignExpr extends Expression {

    private Expression leftExpr, rightExpr;

    public AssignExpr(Expression leftExpr, Expression rightExpr){
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;

        leftExpr.parentExpression = this;
        rightExpr.parentExpression = this;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        scope = new PhiScope(parentScope);

        PhiObject left = leftExpr.eval(scope);
        if(left.getType() != Type.SYMBOL)
            throw new PhiException("Values can only be assigned to SYMBOLs");
        if(!((PhiSymbol) left).isBound())
            left = new PhiSymbol(left.toString(), parentScope);

        PhiObject right = rightExpr.eval(scope);
        right = bindAndLookUp(right, scope);

        ((PhiSymbol) left).assign(right);

        return right;
    }
}
