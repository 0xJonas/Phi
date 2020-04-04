package de.delphi.phi.data;

import de.delphi.phi.PhiRuntimeException;

public abstract class PhiObject implements Cloneable{

    public abstract Type getType();

    public long longValue(){
        throw new PhiRuntimeException("Cannot interpret " + getType().toString() + " as INT.");
    }

    public double doubleValue(){
        throw new PhiRuntimeException("Cannot interpret " + getType().toString() + " as FLOAT.");
    }

    public void declare(){
        throw new PhiRuntimeException("Cannot declare object of type " + getType().toString() + ".");
    }

    public PhiObject lookUp(){
        throw new PhiRuntimeException("Cannot lookup object of type " + getType().toString() + ".");
    }

    public void assign(PhiObject value){
        throw new PhiRuntimeException("Cannot assign value to object of type " + getType().toString() + ".");
    }

    public PhiObject call(PhiCollection params){
        throw new PhiRuntimeException(getType().toString() + "cannot be called.");
    }

    public void createMember(PhiObject key){
        throw new PhiRuntimeException("Cannot add members to " + getType().toString() + ".");
    }

    public PhiObject getUnnamed(int index){
        throw new PhiRuntimeException("Cannot get members from " + getType().toString() + ".");
    }

    public PhiObject getNamed(String key){
        throw new PhiRuntimeException("Cannot get members from " + getType().toString() + ".");
    }

    public void setUnnamed(int index, PhiObject value){
        throw new PhiRuntimeException("Cannot set members on " + getType().toString() + ".");
    }

    public void setNamed(String key, PhiObject value){
        throw new PhiRuntimeException("Cannot set members on " + getType().toString() + ".");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
