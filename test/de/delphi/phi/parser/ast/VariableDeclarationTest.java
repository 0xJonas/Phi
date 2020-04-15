package de.delphi.phi.parser.ast;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;
import de.delphi.phi.data.Type;
import de.delphi.phi.parser.Parser;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VariableDeclarationTest {

    private PhiObject parseAndEval(String input, PhiScope scope) throws PhiException, IOException {
        Parser parser = new Parser(input);
        Expression expr = parser.nextExpression();
        return expr.eval(scope);
    }

    @Test
    public void testVarDecl() throws IOException, PhiException {
        PhiScope scope = new PhiScope();
        assertEquals(parseAndEval("var a, b = 5, c = b", scope).longValue(), 5);
        assertEquals(scope.getNamed("a"), PhiNull.NULL);
        assertEquals(scope.getNamed("b").longValue(), 5);
        assertEquals(scope.getNamed("c").longValue(), 5);
    }

    @Test
    public void testFunctionDecl() throws IOException, PhiException {
        PhiScope scope = new PhiScope();
        parseAndEval("function f(a, b) a + b", scope);
        assertEquals(scope.getNamed("f").getType(), Type.FUNCTION);
    }

}