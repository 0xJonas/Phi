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
