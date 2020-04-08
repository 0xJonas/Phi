package de.delphi.phi.data;

import de.delphi.phi.PhiException;

public class PhiUnnamedSymbol extends PhiSymbol {

    private int index;

    public PhiUnnamedSymbol(int index, PhiObject location) {
        super("@" + index, location);
        this.index = index;
    }

    @Override
    public void declare() throws PhiException {
        if(location.getType() == Type.COLLECTION)
            ((PhiCollection) location).createMember(new PhiInt(index));
        else
            throw new PhiException("Unnamed symbol is not bound to a collection.");
    }

    @Override
    public PhiObject lookUp() throws PhiException{
        return location.getUnnamed(index);
    }

    @Override
    public void assign(PhiObject value) throws PhiException{
        location.setUnnamed(index, value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
