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

public class AddExprTest {

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

    private PhiObject parseAndEval(String input, PhiScope scope) throws PhiException, IOException{
        Parser parser = new Parser(input);
        Expression expr = parser.nextExpression();
        return expr.eval(scope);
    }

    @Test
    public void testIntAdd() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result = parseAndEval("a + 3 - 2", scope);
        assertEquals(Type.INT, result.getType());
        assertEquals(6, result.longValue());
    }

    @Test
    public void testFloatAdd() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result = parseAndEval("a + 2.5 - 2", scope);
        assertEquals(Type.FLOAT, result.getType());
        assertEquals(5.5, result.doubleValue(), 1e-9);
    }

    @Test
    public void testStringConcat() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result = parseAndEval("\"a\" + \"b\"", scope);
        assertEquals(Type.STRING, result.getType());
        assertEquals("ab", result.toString());

        assertThrows("Subtracting strings succeeded", PhiTypeException.class,
                ()->parseAndEval("\"a\" - \"b\"", scope)
        );
    }

    @Test
    public void testSymbolConcat() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result = parseAndEval("qa + qb", scope);
        assertEquals(Type.SYMBOL, result.getType());
        assertEquals("ab", result.toString());

        assertThrows("Subtracting symbols succeeded", PhiTypeException.class,
                ()->parseAndEval("qa - qb", scope)
        );
    }

    @Test
    public void testReturnThrough() throws PhiException, IOException{
        PhiScope scope = createScope();
        PhiObject result = parseAndEval("while true do ((break(1)) + 2)", scope);
        assertEquals(1, result.longValue());
    }
}