package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
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
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        scope = new PhiScope(parentScope);

        ArrayList<String> names = new ArrayList<>();
        PhiCollection defaultValues = new PhiCollection();
        for(int i = 0; i < params.length(); i++){
            PhiObject name = params.getName(i).eval(scope);
            if(name.getType() != Type.SYMBOL)
                throw new PhiException("Function parameter names must be of type symbol");
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
