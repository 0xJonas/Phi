package de.delphi.phi.data;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
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

    public PhiObject call(PhiCollection params) throws PhiException {
        PhiCollection defaultValues = parameterList.getDefaultValues();
        PhiScope scope = new PhiScope(defaultValues);

        PhiCollection superClasses = new PhiCollection();
        superClasses.createMember(new PhiInt(0));
        superClasses.setUnnamed(0, creationScope);
        defaultValues.setNamed("super", superClasses);

        for(int i = 0; i < parameterList.getParameterCount(); i++){
            String paramName = parameterList.getName(i);
            scope.createMember(new PhiSymbol(paramName));
            scope.setNamed(paramName, params.getUnnamed(i));
        }

        for(String name: params.memberNames()){
            if(parameterList.contains(name))
                scope.setNamed(name, params.getNamed(name));
        }

        return body.eval(scope);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        //None of the fields should be changed once the function is created, so a shallow copy suffices
        return super.clone();
    }
}
