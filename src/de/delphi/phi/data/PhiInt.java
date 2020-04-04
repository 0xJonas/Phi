package de.delphi.phi.data;

public class PhiInt extends PhiObject {

    public static final PhiInt TRUE = new PhiInt(1);
    public static final PhiInt FALSE = new PhiInt(0);

    private long value;

    public PhiInt(long value){
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.INT;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return (double) value;
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
