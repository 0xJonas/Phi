package de.delphi.phi.data;

import de.delphi.phi.PhiRuntimeException;

public class PhiSymbol extends PhiObject{

    private String name;

    protected PhiCollection collection;

    public PhiSymbol(String value){
        this.name = value;
    }

    public PhiSymbol(String value, PhiCollection collection){
        this.name = value;
        this.collection = collection;
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
        return collection != null;
    }

    @Override
    public void declare() {
        if(isBound())
            collection.createMember(this);
        else
            throw new PhiRuntimeException("Symbol is not bound to a collection.");
    }

    @Override
    public PhiObject lookUp() {
        if(isBound())
            return collection.getNamed(name);
        else
            throw new PhiRuntimeException("Symbol is not bound to a collection.");
    }

    @Override
    public void assign(PhiObject value) {
        if(isBound())
            collection.setNamed(name, value);
        else
            throw new PhiRuntimeException("Symbol is not bound to a collection.");
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