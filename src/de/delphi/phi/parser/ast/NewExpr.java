package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

public class NewExpr extends UnaryExpr {

    public NewExpr(Expression body){
        super(body);
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
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
            throw new PhiTypeException("Cannot instantiate type " + result.getType());
    }
}
