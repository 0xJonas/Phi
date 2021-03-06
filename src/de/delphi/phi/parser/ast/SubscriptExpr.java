package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiUnnamedSymbol;
import de.delphi.phi.data.Type;

public class SubscriptExpr extends Expression {

    private Expression collectionExpr, indexExpr;

    public SubscriptExpr(Expression collectionExpr, Expression indexExpr){
        this.collectionExpr = collectionExpr;
        this.indexExpr = indexExpr;

        collectionExpr.parentExpression = this;
        indexExpr.parentExpression = this;
    }

    @Override
    public Expression getChild(int index) {
        switch(index){
            case 0: return collectionExpr;
            case 1: return indexExpr;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int countChildren() {
        return 2;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);

        PhiObject collection = collectionExpr.eval(scope);
        collection = bindAndLookUp(collection, parentScope);

        PhiObject index = indexExpr.eval(scope);
        index = bindAndLookUp(index, scope);
        if(index.getType() != Type.INT)
            throw new PhiTypeException("Subscript must be of type INT.");

        return new PhiUnnamedSymbol((int) index.longValue(), collection);
    }
}
