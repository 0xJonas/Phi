package de.delphi.phi.data;

import de.delphi.phi.PhiException;

public class PhiUnnamedSymbol extends PhiSymbol {

    private int index;

    public PhiUnnamedSymbol(int index, PhiCollection collection) {
        super("@" + index, collection);
        this.index = index;
    }

    @Override
    public void declare() throws PhiException {
        collection.createMember(new PhiInt(index));
    }

    @Override
    public PhiObject lookUp() throws PhiException{
        return collection.getUnnamed(index);
    }

    @Override
    public void assign(PhiObject value) throws PhiException{
        collection.setUnnamed(index, value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
