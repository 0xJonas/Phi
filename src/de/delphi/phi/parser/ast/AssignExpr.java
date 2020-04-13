package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiCollection;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.data.Type;

public class AssignExpr extends Expression {

    public static final int OP_ASSIGN = 0;
    public static final int OP_ASSIGN_ADD = 1;
    public static final int OP_ASSIGN_SUB = 2;
    public static final int OP_ASSIGN_MUL = 3;
    public static final int OP_ASSIGN_DIV = 4;
    public static final int OP_ASSIGN_MOD = 5;
    public static final int OP_ASSIGN_AND = 6;
    public static final int OP_ASSIGN_OR = 7;
    public static final int OP_ASSIGN_XOR = 8;
    public static final int OP_ASSIGN_SHIFT_LEFT = 9;
    public static final int OP_ASSIGN_SHIFT_RIGHT = 10;

    private final Expression leftExpr, rightExpr;

    private final int operator;

    public AssignExpr(Expression leftExpr, int operator, Expression rightExpr){
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.operator = operator;

        leftExpr.parentExpression = this;
        rightExpr.parentExpression = this;
    }

    public AssignExpr(Expression leftExpr, Expression rightExpr){
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        operator = OP_ASSIGN;

        leftExpr.parentExpression = this;
        rightExpr.parentExpression = this;
    }

    @Override
    public PhiObject eval(PhiCollection parentScope) throws PhiRuntimeException {
        scope = new PhiScope(parentScope);

        PhiObject left = leftExpr.eval(scope);
        if(left.getType() != Type.SYMBOL)
            throw new PhiTypeException("Values can only be assigned to SYMBOLs");
        if(!((PhiSymbol) left).isBound())
            left = new PhiSymbol(left.toString(), parentScope);
        PhiObject leftValue = bindAndLookUp(left, parentScope);

        PhiObject right = rightExpr.eval(scope);
        right = bindAndLookUp(right, scope);

        Expression synthesized;
        PhiObject assignModifyResult = right;
        switch(operator){
            case OP_ASSIGN_ADD:
                synthesized = new AddExpr(new Atom(leftValue), AddExpr.OP_ADD, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_SUB:
                synthesized = new AddExpr(new Atom(leftValue), AddExpr.OP_SUB, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_MUL:
                synthesized = new MulExpr(new Atom(leftValue), MulExpr.OP_MUL, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_DIV:
                synthesized = new MulExpr(new Atom(leftValue), MulExpr.OP_DIV, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_MOD:
                synthesized = new MulExpr(new Atom(leftValue), MulExpr.OP_MOD, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_AND:
                synthesized = new AndExpr(new Atom(leftValue), new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_OR:
                synthesized = new OrExpr(new Atom(leftValue), new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_XOR:
                synthesized = new XorExpr(new Atom(leftValue), new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_SHIFT_LEFT:
                synthesized = new ShiftExpr(new Atom(leftValue), ShiftExpr.OP_SHIFT_LEFT, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
            case OP_ASSIGN_SHIFT_RIGHT:
                synthesized = new ShiftExpr(new Atom(leftValue), ShiftExpr.OP_SHIFT_RIGHT, new Atom(right));
                assignModifyResult = synthesized.eval(scope);
                break;
        }

        ((PhiSymbol) left).assign(assignModifyResult);

        return right;
    }
}
