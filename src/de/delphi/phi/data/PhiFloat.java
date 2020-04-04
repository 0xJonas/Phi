package de.delphi.phi.data;

public class PhiFloat extends PhiObject {

    private double value;

    public PhiFloat(double value){
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
