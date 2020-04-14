package de.delphi.phi.parser.ast;

import java.util.List;

abstract class BinaryExpr extends Expression{

    protected Expression[] operands;

    protected Integer[] operators;

    public BinaryExpr(List<Expression> operands, List<Integer> operators){
        this.operands = operands.toArray(new Expression[0]);
        this.operators = operators.toArray(new Integer[0]);

        for(Expression expr: operands)
            expr.parentExpression = this;
    }

    public BinaryExpr(Expression left, int operator, Expression right){
        operands = new Expression[]{left, right};
        operators = new Integer[]{0, operator};
        left.parentExpression = this;
        right.parentExpression = this;
    }

    @Override
    public Expression getChild(int index) {
        if(index >= operands.length || index < 0)
            throw new IndexOutOfBoundsException();
        else
            return operands[index];
    }

    @Override
    public int countChildren() {
        return operands.length;
    }
}
