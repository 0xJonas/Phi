package de.delphi.phi.data;

import de.delphi.phi.PhiAccessException;
import de.delphi.phi.PhiRuntimeException;

public class PhiSymbol extends PhiObject{

    private String name;

    protected PhiObject location;

    public PhiSymbol(String value){
        this.name = value;
    }

    public PhiSymbol(String value, PhiObject location){
        this.name = value;
        this.location = location;
    }

    @Override
    public Type getType() {
        return Type.SYMBOL;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isBound(){
        return location != null;
    }

    public void declare() throws PhiRuntimeException{
        if(isBound() && location.getType() == Type.COLLECTION)
            ((PhiCollection) location).createMember(this);
        else
            throw new PhiAccessException("Symbol is not bound to a collection.");
    }

    public PhiObject lookUp() throws PhiRuntimeException{
        if(isBound())
            return location.getNamed(name);
        else
            throw new PhiAccessException("Symbol is not bound to a location.");
    }

    public void assign(PhiObject value) throws PhiRuntimeException{
        if(isBound())
            location.setNamed(name, value);
        else
            throw new PhiAccessException("Symbol is not bound to a location.");
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof PhiSymbol))
            return false;
        PhiSymbol other = (PhiSymbol) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
