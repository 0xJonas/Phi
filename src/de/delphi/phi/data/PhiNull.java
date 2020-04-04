package de.delphi.phi.data;

public class PhiNull extends PhiObject {

    public static final PhiNull NULL = new PhiNull();

    private PhiNull(){}

    @Override
    public Type getType() {
        return Type.NULL;
    }

    @Override
    public String toString() {
        return "NULL";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
