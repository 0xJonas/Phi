package de.delphi.phi.data;

import de.delphi.phi.PhiAccessException;

public class PhiString extends PhiObject {

    private String value;

    public PhiString(String value){
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public PhiObject getUnnamed(int index) throws PhiAccessException {
        if(index < 0 | index >= value.length())
            throw new PhiAccessException("String index is out of bounds: length " + value.length() + ", index " + index);
        return new PhiInt(value.codePointAt(index));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
