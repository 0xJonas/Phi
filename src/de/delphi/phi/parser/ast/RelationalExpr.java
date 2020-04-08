package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

import java.util.List;

public class RelationalExpr extends Expression {

    public static final int OP_EQUALS = 0;
    public static final int OP_NOT_EQUALS = 1;
    public static final int OP_LESS_THAN = 2;
    public static final int OP_LESS_EQUALS = 3;
    public static final int OP_GREATER_THAN = 4;
    public static final int OP_GREATER_EQUALS = 5;

    private Expression[] operands;

    private Integer[] operators;

    public RelationalExpr(Expression parentExpr, List<Expression> operands, List<Integer> operators){
        super(parentExpr);
        this.operands = operands.toArray(new Expression[0]);
        this.operators = operators.toArray(new Integer[0]);
    }

    private boolean opEquals(PhiObject left, PhiObject right) throws PhiException{
        Type commonType = Type.coerceTypes(left.getType(), right.getType());
        switch(commonType){
            case INT: return left.longValue() == right.longValue();
            case FLOAT: return left.doubleValue() == right.doubleValue();
            case STRING:
            case SYMBOL:
                return left.toString().equals(right.toString());
            default: throw new PhiException("Cannot compare types " + left.getType() + " and " + right.getType());
        }
    }

    private boolean opLess(PhiObject left, PhiObject right) throws PhiException{
        Type commonType = Type.coerceTypes(left.getType(), right.getType());
        switch(commonType){
            case INT: return left.longValue() < right.longValue();
            case FLOAT: return left.doubleValue() < right.doubleValue();
            case STRING:
            case SYMBOL:
                return left.toString().compareTo(right.toString()) < 0;
            default: throw new PhiException("Cannot compare types " + left.getType() + " and " + right.getType());
        }
    }

    private boolean relOp(PhiObject left, int op, PhiObject right) throws PhiException{
        switch(op){
            case OP_EQUALS: return opEquals(left, right);
            case OP_NOT_EQUALS: return !opEquals(left, right);
            case OP_LESS_THAN: return opLess(left, right);
            case OP_LESS_EQUALS: return opLess(left, right) || opEquals(left, right);
            case OP_GREATER_THAN: return !(opLess(left, right) || opEquals(left, right));
            case OP_GREATER_EQUALS: return !opLess(left, right);
            default: throw new PhiException("Bad operand " + op);
        }
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiException {
        scope = new PhiScope(parentScope);

        PhiObject left = operands[0].eval(scope);
        left = bindAndLookUp(left, scope);

        PhiObject right = operands[1].eval(scope);
        right = bindAndLookUp(right, scope);

        //Result is not the left side of the next operator, but the overall result
        //This enables expressions like  0 < alpha < 5
        boolean result = relOp(left, operators[1], right);

        for(int i = 2; i < operands.length; i++){
            left = right;
            right = operands[i].eval(scope);
            right = bindAndLookUp(right, scope);
            result = result && relOp(left, operators[i], right);
        }
        return result ? PhiInt.TRUE : PhiInt.FALSE;
    }
}
