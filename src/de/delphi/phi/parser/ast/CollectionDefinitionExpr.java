package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

public class CollectionDefinitionExpr extends Expression {

    private ExpressionList content;

    public CollectionDefinitionExpr(ExpressionList content){
        this.content = content;
        for(int i = 0; i < content.length(); i++){
            content.getName(i).parentExpression = this;
            Expression valueExpr = content.getValue(i);
            if(valueExpr != null)
                valueExpr.parentExpression = this;
        }
    }

    @Override
    public Expression getChild(int index) {
        if(index >= 2 * content.length() || index < 0)
            throw new IndexOutOfBoundsException();
        if((index & 1) == 0){
            return content.getName(index >> 1);
        }else{
            return content.getValue(index >> 1);
        }
    }

    @Override
    public int countChildren() {
        return 2 * content.length();
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        PhiCollection collection = new PhiCollection(parentScope);

        for(int i = 0; i < content.length(); i++){
            PhiObject name = content.getName(i).eval(collection);

            Expression valueExpr = content.getValue(i);
            if(valueExpr != null){
                if(name.getType() != Type.SYMBOL)
                    throw new PhiTypeException("Name of collection member must be of type SYMBOL.");

                PhiObject value = valueExpr.eval(collection);
                value = bindAndLookUp(value, collection);
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
