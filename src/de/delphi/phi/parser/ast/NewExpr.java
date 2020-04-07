package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

public class NewExpr extends Expression {

    private Expression body;

    public NewExpr(Expression parentExpr, Expression body){
        super(parentExpr);
        this.body = body;
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
        PhiObject result = body.eval(parentScope);
        result = bindAndLookUp(result, parentScope);

        if(result.getType() == Type.COLLECTION)
            try {
                return (PhiObject) result.clone();
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
                return PhiNull.NULL;
            }
        else
            throw new PhiException("Cannot instantiate type " + result.getType());
    }
}
