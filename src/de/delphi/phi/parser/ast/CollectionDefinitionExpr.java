package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

public class CollectionDefinitionExpr extends Expression {

    private ExpressionList content;

    public CollectionDefinitionExpr(Expression parentExpr, ExpressionList content){
        super(parentExpr);
        this.content = content;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        PhiCollection collection = new PhiCollection(parentScope);

        for(int i = 0; i < content.length(); i++){
            PhiObject name = content.getName(i).eval(collection);

            Expression valueExpr = content.getValue(i);
            if(valueExpr != null){
                if(name.getType() != Type.SYMBOL)
                    throw new PhiException("Name of collection member must be of type SYMBOL.");

                PhiObject value = valueExpr.eval(collection);
                collection.createMember(name);
                collection.setNamed(name.toString(), value);
            }else{
                collection.createMember(new PhiInt(i));
                collection.setUnnamed(i, name);
            }
        }

        return collection;
    }
}
