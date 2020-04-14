package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.*;

import java.util.ArrayList;

public class FunctionDefinitionExpr extends Expression {

    private ExpressionList params;

    private FunctionBody body;

    public FunctionDefinitionExpr(ExpressionList params, FunctionBody body){
        this.params = params;
        this.body = body;

        for(int i = 0; i < params.length(); i++){
            params.getName(i).parentExpression = this;
            Expression valueExpr = params.getValue(i);
            if(valueExpr != null)
                valueExpr.parentExpression = this;
        }
        body.parentExpression = this;
    }

    @Override
    public Expression getChild(int index) {
        if(index == 0)
            return body;
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

        ArrayList<String> names = new ArrayList<>();
        PhiScope defaultValues = new PhiScope();
        for(int i = 0; i < params.length(); i++){
            PhiObject name = params.getName(i).eval(scope);
            if(name.getType() != Type.SYMBOL)
                throw new PhiTypeException("Function parameter names must be of type symbol");
            names.add(name.toString());

            Expression valueExpr = params.getValue(i);
            if(valueExpr != null){
                PhiObject value = valueExpr.eval(scope);
                value = bindAndLookUp(value, scope);
                defaultValues.createMember(name);
                defaultValues.setNamed(name.toString(), value);
            }
        }

        ParameterList paramList = new ParameterList(names, defaultValues);
        return new PhiFunction(parentScope, paramList, body);
    }
}
