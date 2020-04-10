package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.data.Type;

public class MemberAccessExpr extends Expression {

    private Expression collectionExpr, nameExpr;

    public MemberAccessExpr(Expression collectionExpr, Expression nameExpr){
        this.collectionExpr = collectionExpr;
        this.nameExpr = nameExpr;

        collectionExpr.parentExpression = this;
        nameExpr.parentExpression = this;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);

        PhiObject collection = collectionExpr.eval(scope);
        collection = bindAndLookUp(collection, parentScope);

        PhiObject name = nameExpr.eval(scope);
        if(name.getType() != Type.SYMBOL)
            throw new PhiTypeException("Member name must be of type SYMBOL.");

        return new PhiSymbol(name.toString(), collection);
    }
}
