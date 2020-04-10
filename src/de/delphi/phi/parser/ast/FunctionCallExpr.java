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
