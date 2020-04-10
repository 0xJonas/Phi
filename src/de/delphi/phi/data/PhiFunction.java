package de.delphi.phi.data;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.parser.ast.Expression;
import de.delphi.phi.parser.ast.FunctionBody;

public class PhiFunction extends PhiObject {

    private FunctionBody body;

    private PhiCollection creationScope;

    private ParameterList parameterList;

    public PhiFunction(PhiCollection creationScope, ParameterList parameterList, FunctionBody body){
        this.creationScope = creationScope;
        this.parameterList = parameterList;
        this.body = body;
    }

    @Override
    public Type getType() {
        return Type.FUNCTION;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public PhiObject call(PhiCollection params) throws PhiRuntimeException {
        PhiScope scope = parameterList.supplyParameters(params);
        scope.setParentScope(creationScope);

        PhiObject result = body.eval(scope);
        result = Expression.bindAndLookUp(result, scope);
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        //None of the fields should be changed once the function is created, so a shallow copy suffices
        return super.clone();
    }
}
