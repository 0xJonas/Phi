package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.*;
import org.junit.Test;

import java.util.List;

import static de.delphi.phi.Polyfill.assertThrows;
import static org.junit.Assert.assertEquals;

public class AddExprTest {

    private PhiScope createScope() throws PhiException {
        PhiScope scope = new PhiScope();
        scope.createMember(new PhiSymbol("a"));
        scope.createMember(new PhiSymbol(".a"));
        scope.createMember(new PhiSymbol(".b"));
        scope.setNamed("a", new PhiInt(5));
        scope.setNamed(".a", new PhiSymbol("a"));
        scope.setNamed(".b", new PhiSymbol("b"));
        return scope;
    }

    @Test
    public void testIntAdd() throws PhiException{
        AddExpr expr = new AddExpr(
                List.of(
                        new Atom(new PhiSymbol("a")),
                        new Atom(new PhiInt(3)),
                        new Atom(new PhiInt(2))),
                List.of(AddExpr.OP_ADD, AddExpr.OP_ADD, AddExpr.OP_SUB));
        PhiScope scope = createScope();
        PhiObject result = expr.eval(scope);
        assertEquals(Type.INT, result.getType());
        assertEquals(6, result.longValue());
    }

    @Test
    public void testFloatAdd() throws PhiException{
        AddExpr expr = new AddExpr(
                List.of(
                        new Atom(new PhiSymbol("a")),
                        new Atom(new PhiFloat(2.5)),
                        new Atom(new PhiInt(2))),
                List.of(AddExpr.OP_ADD, AddExpr.OP_ADD, AddExpr.OP_SUB));
        PhiScope scope = createScope();
        PhiObject result = expr.eval(scope);
        assertEquals(Type.FLOAT, result.getType());
        assertEquals(5.5, result.doubleValue(), 1e-9);
    }

    @Test
    public void testStringConcat() throws PhiException{
        AddExpr expr1 = new AddExpr(
                List.of(
                        new Atom(new PhiString("a")),
                        new Atom(new PhiString("b"))),
                List.of(AddExpr.OP_ADD, AddExpr.OP_ADD));
        PhiScope scope = createScope();
        PhiObject result = expr1.eval(scope);
        assertEquals(Type.STRING, result.getType());
        assertEquals("ab", result.toString());

        AddExpr expr2 = new AddExpr(
                List.of(
                        new Atom(new PhiString("a")),
                        new Atom(new PhiString("b"))),
                List.of(AddExpr.OP_ADD, AddExpr.OP_SUB));
        assertThrows("Subtracting strings succeeded", PhiException.class,
                ()->expr2.eval(scope)
        );
    }

    @Test
    public void testSymbolConcat() throws PhiException{
        AddExpr expr1 = new AddExpr(
                List.of(
                        new Atom(new PhiSymbol(".a")),
                        new Atom(new PhiSymbol(".b"))),
                List.of(AddExpr.OP_ADD, AddExpr.OP_ADD));
        PhiScope scope = createScope();
        PhiObject result = expr1.eval(scope);
        assertEquals(Type.SYMBOL, result.getType());
        assertEquals("ab", result.toString());

        AddExpr expr2 = new AddExpr(
                List.of(
                        new Atom(new PhiSymbol(".a")),
                        new Atom(new PhiSymbol(".b"))),
                List.of(AddExpr.OP_ADD, AddExpr.OP_SUB));
        assertThrows("Subtracting symbols succeeded", PhiException.class,
                ()->expr2.eval(scope)
        );
    }

    @Test
    public void testReturnThrough() throws PhiException{
        WhileExpr expr = new WhileExpr(new Atom(PhiInt.TRUE),
                new AddExpr(
                        List.of(
                                new BreakExpr(
                                        new Atom(new PhiInt(1))),
                                new Atom(new PhiInt(2))),
                        List.of(AddExpr.OP_ADD, AddExpr.OP_ADD)));
        PhiScope scope = createScope();
        PhiObject result = expr.eval(scope);
        assertEquals(1, result.longValue());
    }
}