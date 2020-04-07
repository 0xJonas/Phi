package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.*;

import java.util.List;

public class AddExpr extends Expression {

    public static final int OP_ADD = 0;
    public static final int OP_SUB = 1;

    private Expression[] operands;

    private Integer[] operators;

    public AddExpr(Expression parentExpr, List<Expression> operands, List<Integer> operators) {
        super(parentExpr);
        this.operands = operands.toArray(new Expression[0]);
        this.operators = operators.toArray(new Integer[0]);
    }

    private PhiObject add(PhiObject po1, PhiObject po2) throws PhiException{
        Type commonType = Type.coerceTypes(po1.getType(), po2.getType());
        switch(commonType){
            case INT: return new PhiInt(po1.longValue() + po2.longValue());
            case FLOAT: return new PhiFloat(po1.doubleValue() + po2.doubleValue());
            case STRING: return new PhiString(po1.toString() + po2.toString());
            case SYMBOL: return new PhiSymbol(po1.toString() + po2.toString());
            default: throw new PhiException("+ operator can not operate on types " + po1.getType() + ", " + po2.getType());
        }
    }

    private PhiObject sub(PhiObject po1, PhiObject po2) throws PhiException{
        Type commonType = Type.coerceTypes(po1.getType(), po2.getType());
        switch(commonType){
            case INT: return new PhiInt(po1.longValue() - po2.longValue());
            case FLOAT: return new PhiFloat(po1.doubleValue() - po2.doubleValue());
            default: throw new PhiException("- operator can not operate on types " + po1.getType() + ", " + po2.getType());
        }
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
        scope = new PhiScope(parentScope);
        PhiObject result = PhiInt.FALSE; // = 0
        for(int i = 0; i < operands.length; i++){
            PhiObject po2 = operands[i].eval(scope);

            po2 = bindAndLookUp(po2, scope);

            switch (operators[i]){
                case OP_ADD: result = add(result, po2); break;
                case OP_SUB: result = sub(result, po2); break;
            }
        }
        return result;
    }
}
