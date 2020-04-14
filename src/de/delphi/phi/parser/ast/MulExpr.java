package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.*;

import java.util.List;

public class MulExpr extends BinaryExpr {

    public static final int OP_MUL = 0;
    public static final int OP_DIV = 1;
    public static final int OP_MOD = 2;

    public MulExpr(List<Expression> operands, List<Integer> operators){
        super(operands, operators);
    }

    public MulExpr(Expression left, int operator, Expression right){
        super(left, operator, right);
    }

    private PhiObject mul(PhiObject po1, PhiObject po2) throws PhiRuntimeException{
        Type commonType = Type.coerceTypes(po1.getType(), po2.getType());
        switch(commonType){
            case INT: return new PhiInt(po1.longValue() * po2.longValue());
            case FLOAT: return new PhiFloat(po1.doubleValue() * po2.doubleValue());
            default: throw new PhiTypeException("* operator can not operate on types " + po1.getType() + ", " + po2.getType());
        }
    }

    private PhiObject div(PhiObject po1, PhiObject po2) throws PhiRuntimeException{
        Type commonType = Type.coerceTypes(po1.getType(), po2.getType());
        switch(commonType){
            case INT: {
                if(po2.longValue() == 0)
                    throw new PhiRuntimeException("Division by 0");
                return new PhiInt(po1.longValue() / po2.longValue());
            }
            case FLOAT: return new PhiFloat(po1.doubleValue() / po2.doubleValue());
            default: throw new PhiTypeException("/ operator can not operate on types " + po1.getType() + ", " + po2.getType());
        }
    }

    private PhiObject mod(PhiObject po1, PhiObject po2) throws PhiRuntimeException{
        Type commonType = Type.coerceTypes(po1.getType(), po2.getType());
        switch(commonType){
            case INT: {
                if(po2.longValue() == 0)
                    throw new PhiRuntimeException("Division by 0");
                return new PhiInt(po1.longValue() % po2.longValue());
            }
            case FLOAT: return new PhiFloat(po1.doubleValue() % po2.doubleValue());
            default: throw new PhiTypeException("% operator can not operate on types " + po1.getType() + ", " + po2.getType());
        }
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);
        PhiObject result = operands[0].eval(scope);
        result = bindAndLookUp(result, scope);

        for(int i = 1; i < operands.length; i++){
            PhiObject po2 = operands[i].eval(scope);

            po2 = bindAndLookUp(po2, scope);

            switch(operators[i]) {
                case OP_MUL: result = mul(result, po2); break;
                case OP_DIV: result = div(result, po2); break;
                case OP_MOD: result = mod(result, po2); break;
            }
        }
        return result;
    }
}
