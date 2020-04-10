package de.delphi.phi;

import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;

public class PhiScope extends PhiCollection{

    private PhiCollection parentScope;

    public PhiScope(){

    }

    public PhiScope(PhiCollection parentScope){
        setParentScope(parentScope);
    }

    public void setParentScope(PhiCollection parentScope){
        this.parentScope = parentScope;
        try {
            PhiCollection superClassList = new PhiCollection();
            superClassList.createMember(new PhiInt(0));
            superClassList.setUnnamed(0, parentScope);
            super.setNamed("super", superClassList);
        }catch(PhiException e){
            e.printStackTrace();
        }
    }

    @Override
    public PhiObject getNamed(String key) throws PhiAccessException{
        if(key.equals("this") || key.equals("length") || key.equals("super"))
            return parentScope.getNamed(key);
        return super.getNamed(key);
    }

    @Override
    public void setNamed(String key, PhiObject value) throws PhiAccessException, PhiStructureException{
        if(key.equals("this") || key.equals("length") || key.equals("super")) {
            parentScope.getNamed(key);
            return;
        }
        super.setNamed(key, value);
    }
}
