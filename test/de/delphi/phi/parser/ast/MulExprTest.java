package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.data.PhiInt;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.data.Type;
import de.delphi.phi.parser.Parser;
import org.junit.Test;

import java.io.IOException;

import static de.delphi.phi.Polyfill.assertThrows;
import static org.junit.Assert.assertEquals;

public class MulExprTest {

    private PhiScope createScope() throws PhiRuntimeException {
        PhiScope scope = new PhiScope();
        scope.createMember(new PhiSymbol("a"));
        scope.createMember(new PhiSymbol("qa"));
        scope.createMember(new PhiSymbol("qb"));
        scope.setNamed("a", new PhiInt(5));
        scope.setNamed("qa", new PhiSymbol("a"));
        scope.setNamed("qb", new PhiSymbol("b"));
        return scope;
    }

    private PhiObject parseAndEval(String input, PhiScope scope) throws PhiException, IOException {
        Parser parser = new Parser(input);
        Expression expr = parser.nextExpression();
        return expr.eval(scope);
    }

    @Test
    public void testIntMul() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result1 = parseAndEval("a * 2", scope);
        assertEquals(result1.getType(), Type.INT);
        assertEquals(result1.longValue(), 10);

        PhiObject result2 = parseAndEval("a / 2", scope);
        assertEquals(result2.getType(), Type.INT);
        assertEquals(result2.longValue(), 2);

        PhiObject result3 = parseAndEval("a % 3", scope);
        assertEquals(result3.getType(), Type.INT);
        assertEquals(result3.longValue(), 2);
    }

    @Test
    public void testFloatMul() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result1 = parseAndEval("a * 2.5", scope);
        assertEquals(result1.getType(), Type.FLOAT);
        assertEquals(result1.doubleValue(), 12.5, 1e-9);

        PhiObject result2 = parseAndEval("a / 2.0", scope);
        assertEquals(result2.getType(), Type.FLOAT);
        assertEquals(result2.doubleValue(), 2.5, 1e-9);

        PhiObject result3 = parseAndEval("a % 4.5", scope);
        assertEquals(result3.getType(), Type.FLOAT);
        assertEquals(result3.doubleValue(), 0.5, 1e-9);
    }

    @Test
    public void testErrors() throws PhiException {
        PhiScope scope = createScope();
        assertThrows("Multiplying strings succeeded", PhiTypeException.class,
                ()->parseAndEval("\"a\" * \"b\"", scope)
        );
        assertThrows("Multiplying symbols succeeded", PhiTypeException.class,
                ()->parseAndEval("'a * 'b", scope)
        );
        assertThrows("Division by 0 succeeded", PhiRuntimeException.class,
                ()->parseAndEval("5 / 0", scope)
        );
        assertThrows("Division by 0 succeeded", PhiRuntimeException.class,
                ()->parseAndEval("5 % 0", scope)
        );
    }

    @Test
    public void testReturnThrough() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result = parseAndEval("while true do ((break(1)) * 2)", scope);
        assertEquals(1, result.longValue());
    }
}