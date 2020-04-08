package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

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
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        scope = new PhiScope(parentScope);
        PhiObject name = PhiNull.NULL;
        for(int i = 0; i < content.length(); i++){
            name = content.getName(i).eval(parentScope);
            if(name.getType() != Type.SYMBOL)
                throw new PhiException("Variable name must be of type SYMBOL");
            parentScope.createMember(name);

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
