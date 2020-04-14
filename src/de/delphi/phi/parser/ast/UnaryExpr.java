package de.delphi.phi.parser.ast;

abstract class UnaryExpr extends Expression{

    protected Expression body;

    public UnaryExpr(Expression body){
        this.body = body;
        body.parentExpression = this;
    }

    @Override
    public Expression getChild(int index) {
        if(index == 0)
            return body;
        else
            throw new IndexOutOfBoundsException();
    }

    @Override
    public int countChildren() {
        return 1;
    }
}
