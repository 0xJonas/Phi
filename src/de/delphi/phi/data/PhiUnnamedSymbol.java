package de.delphi.phi.data;

public class PhiUnnamedSymbol extends PhiSymbol {

    private int index;

    public PhiUnnamedSymbol(int index, PhiCollection collection) {
        super("@" + index, collection);
        this.index = index;
    }

    @Override
    public void declare() {
        collection.createMember(new PhiInt(index));
    }

    @Override
    public PhiObject lookUp() {
        return collection.getUnnamed(index);
    }

    @Override
    public void assign(PhiObject value) {
        collection.setUnnamed(index, value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
