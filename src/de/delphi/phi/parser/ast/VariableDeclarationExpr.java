package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.*;

public class VariableDeclarationExpr extends Expression {

    private ExpressionList content;

    public VariableDeclarationExpr(ExpressionList content){
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
        scope = new PhiScope(parentScope);
        PhiObject name = PhiNull.NULL;
        for(int i = 0; i < content.length(); i++){
            name = content.getName(i).eval(parentScope);
            if(name.getType() != Type.SYMBOL)
                throw new PhiTypeException("Variable name must be of type SYMBOL");
            if(!((PhiSymbol) name).isBound())
                name = new PhiSymbol(name.toString(), parentScope);
            ((PhiSymbol) name).declare();

            Expression valueExpr = content.getValue(i);
            if(valueExpr != null){
                PhiObject value = valueExpr.eval(scope);
                value = bindAndLookUp(value, scope);
                parentScope.setNamed(name.toString(), value);
            }
        }

        return bindAndLookUp(name, parentScope);
    }
}
