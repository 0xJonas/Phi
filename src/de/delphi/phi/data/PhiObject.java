package de.delphi.phi.data;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiTypeException;

public abstract class PhiObject implements Cloneable{

    public abstract Type getType();

    public long longValue() throws PhiRuntimeException {
        throw new PhiTypeException("Cannot interpret " + getType().toString() + " as INT.");
    }

    public double doubleValue() throws PhiRuntimeException {
        throw new PhiTypeException("Cannot interpret " + getType().toString() + " as FLOAT.");
    }

    public PhiObject getUnnamed(int index) throws PhiRuntimeException{
        throw new PhiTypeException("Cannot get members from " + getType().toString() + ".");
    }

    public PhiObject getNamed(String key) throws PhiRuntimeException{
        throw new PhiTypeException("Cannot get members from " + getType().toString() + ".");
    }

    public void setUnnamed(int index, PhiObject value) throws PhiRuntimeException{
        throw new PhiTypeException("Cannot set members on " + getType().toString() + ".");
    }

    public void setNamed(String key, PhiObject value) throws PhiRuntimeException{
        throw new PhiTypeException("Cannot set members on " + getType().toString() + ".");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
