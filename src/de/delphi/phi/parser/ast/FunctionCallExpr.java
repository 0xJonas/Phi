package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.*;

public class FunctionCallExpr extends Expression {

    private Expression functionExpr;

    private ExpressionList params;

    public FunctionCallExpr(Expression functionExpr, ExpressionList params){
        this.functionExpr = functionExpr;
        this.params = params;

        functionExpr.parentExpression = this;
        for(int i = 0; i < params.length(); i++){
            params.getName(i).parentExpression = this;
            Expression valueExpr = params.getValue(i);
            if(valueExpr != null)
                valueExpr.parentExpression = this;
        }
    }

    @Override
    public Expression getChild(int index) {
        if(index == 0)
            return functionExpr;
        else if(index > params.length() || index < 0){
            int paramIndex = index - 1;
            if((paramIndex & 1) == 0)
                return params.getName(paramIndex >> 1);
            else
                return params.getValue(paramIndex >> 1);
        }else
            throw new IndexOutOfBoundsException();
    }

    @Override
    public int countChildren() {
        return 2 * params.length() + 1;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);

        PhiObject function = functionExpr.eval(scope);
        if(function.getType() != Type.FUNCTION)
            throw new PhiTypeException(function.getType() + " is not callable.");

        PhiCollection paramCollection = new PhiCollection();
        int numUnnamed = 0;
        for(int i = 0; i < params.length(); i++){
            PhiObject paramName = params.getName(i).eval(scope);

            Expression valueExpr = params.getValue(i);
            if(valueExpr != null){
                PhiObject paramValue = valueExpr.eval(scope);
                paramValue = bindAndLookUp(paramValue, scope);
                paramCollection.createMember(paramName);
                paramCollection.setNamed(paramName.toString(), paramValue);
            }else{
                paramName = bindAndLookUp(paramName, scope);
                paramCollection.createMember(new PhiInt(numUnnamed));
                paramCollection.setUnnamed(numUnnamed, paramName);
                numUnnamed++;
            }
        }

        return ((PhiFunction) function).call(paramCollection);
    }
}
