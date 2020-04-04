package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiObject;

public abstract class Expression {

    protected Expression parentExpression;

    protected PhiScope scope;

    public Expression(Expression parentExpression){
        this.parentExpression = parentExpression;
    }

    public abstract PhiObject eval(PhiScope parentScope);
}
