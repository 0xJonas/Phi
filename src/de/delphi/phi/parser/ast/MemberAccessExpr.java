package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
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
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        scope = new PhiScope(parentScope);

        PhiObject collection = collectionExpr.eval(scope);
        collection = bindAndLookUp(collection, parentScope);
        if(collection.getType() != Type.COLLECTION)
            throw new PhiException(collection.getType() + " does not contain members.");

        PhiObject name = nameExpr.eval(scope);
        if(name.getType() != Type.SYMBOL)
            throw new PhiException("Member name must be of type SYMBOL.");

        return new PhiSymbol(name.toString(), (PhiCollection) collection);
    }
}
