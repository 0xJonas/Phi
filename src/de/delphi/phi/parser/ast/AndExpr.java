package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

import java.util.List;

public class AndExpr extends Expression {

    private Expression[] operands;

    public AndExpr(List<Expression> operands){
        this.operands = operands.toArray(new Expression[0]);
        for(Expression expr: operands)
            expr.parentExpression = this;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);
        PhiObject result = operands[0].eval(scope);
        result = bindAndLookUp(result, scope);
        if(result.getType() != Type.INT)
            throw new PhiTypeException("& operator can not perform on " + result.getType());

        for (int i = 1; i < operands.length; i++) {
            PhiObject po2 = operands[i].eval(scope);
            po2 = bindAndLookUp(po2, scope);
            if (po2.getType() != Type.INT) {
                throw new PhiTypeException("& operator can not perform on " + po2.getType());
            }
            result = new PhiInt(result.longValue() & po2.longValue());
        }
        return result;
    }
}
