package de.delphi.phi.data;

import de.delphi.phi.PhiException;

public abstract class PhiObject implements Cloneable{

    public abstract Type getType();

    public long longValue() throws PhiException{
        throw new PhiException("Cannot interpret " + getType().toString() + " as INT.");
    }

    public double doubleValue() throws PhiException{
        throw new PhiException("Cannot interpret " + getType().toString() + " as FLOAT.");
    }

    public void declare() throws PhiException{
        throw new PhiException("Cannot declare object of type " + getType().toString() + ".");
    }

    public PhiObject lookUp() throws PhiException{
        throw new PhiException("Cannot lookup object of type " + getType().toString() + ".");
    }

    public void assign(PhiObject value) throws PhiException{
        throw new PhiException("Cannot assign value to object of type " + getType().toString() + ".");
    }

    public PhiObject call(PhiCollection params) throws PhiException{
        throw new PhiException(getType().toString() + "cannot be called.");
    }

    public void createMember(PhiObject key) throws PhiException{
        throw new PhiException("Cannot add members to " + getType().toString() + ".");
    }

    public PhiObject getUnnamed(int index) throws PhiException{
        throw new PhiException("Cannot get members from " + getType().toString() + ".");
    }

    public PhiObject getNamed(String key) throws PhiException{
        throw new PhiException("Cannot get members from " + getType().toString() + ".");
    }

    public void setUnnamed(int index, PhiObject value) throws PhiException{
        throw new PhiException("Cannot set members on " + getType().toString() + ".");
    }

    public void setNamed(String key, PhiObject value) throws PhiException{
        throw new PhiException("Cannot set members on " + getType().toString() + ".");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
