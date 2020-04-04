package de.delphi.phi.data;

import de.delphi.phi.PhiScope;
import de.delphi.phi.parser.ast.Expression;

import java.util.Set;

public class PhiFunction extends PhiObject {

    private Expression body;

    private PhiScope creationScope, paramScope;

    @Override
    public Type getType() {
        return Type.FUNCTION;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public PhiObject call(PhiCollection params) {
        PhiScope paramScopeCopy = null;
        try{
            paramScopeCopy = (PhiScope) paramScope.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        long numUnnamedParams = params.getNamed("length").longValue();
        for(int i = 0; i < numUnnamedParams; i++){
            paramScopeCopy.setUnnamed(i, params.getUnnamed(i));
        }

        Set<String> realParamNames = paramScopeCopy.memberNames();
        for(String name: params.memberNames()){
            if(realParamNames.contains(name))
                paramScopeCopy.setNamed(name, params.getNamed(name));
        }

        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        //TODO implement
        return super.clone();
    }
}
