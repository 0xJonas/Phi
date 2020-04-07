package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;

import java.util.List;

public class ShiftExpr extends Expression {

    public static final int OP_SHIFT_LEFT = 0;
    public static final int OP_SHIFT_RIGHT = 1;

    private Expression[] operands;

    private Integer[] operators;

    public ShiftExpr(Expression parentExpr, List<Expression> operands, List<Integer> operators){
        super(parentExpr);
        this.operands = operands.toArray(new Expression[0]);
        this.operators = operators.toArray(new Integer[0]);
    }

    @Override
    public PhiObject eval(PhiScope parentScope) throws PhiException {
        scope = new PhiScope(parentScope);

        PhiObject result = operands[0].eval(scope);
        result = bindAndLookUp(result, scope);
        if(result.getType() != Type.INT)
            throw new PhiException("Shift operator can not perform on " + result.getType());

        for(int i = 1; i < operands.length; i++){
            PhiObject po2 = operands[i].eval(scope);
            po2 = bindAndLookUp(po2, scope);

            if(po2.getType() != Type.INT)
                throw new PhiException("Shift operator can not perform on " + po2.getType());

            switch(operators[i]){
                case OP_SHIFT_LEFT: {
                    result = new PhiInt(result.longValue() << po2.longValue());
                    break;
                }
                case OP_SHIFT_RIGHT:{
                    result = new PhiInt(result.longValue() >>> po2.longValue());
                    break;
                }
            }
        }
        return result;
    }
}