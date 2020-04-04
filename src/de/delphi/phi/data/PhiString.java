package de.delphi.phi.data;

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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
