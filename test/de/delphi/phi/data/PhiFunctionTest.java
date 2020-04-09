package de.delphi.phi.data;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.parser.ast.AddExpr;
import de.delphi.phi.parser.ast.Atom;
import de.delphi.phi.parser.ast.FunctionBody;
import de.delphi.phi.parser.ast.MulExpr;
import org.junit.Test;

import java.util.List;

import static de.delphi.phi.Polyfill.assertThrows;
import static org.junit.Assert.assertEquals;

public class PhiFunctionTest {

    private PhiFunction createFunction() {
        //lambda(a, b) -> a - b
        ParameterList paramList = new ParameterList(List.of("a", "b"), new PhiScope());
        FunctionBody body = new FunctionBody(
                new AddExpr(List.of(
                        new Atom(new PhiSymbol("a")),
                        new Atom(new PhiSymbol("b"))
                ), List.of(AddExpr.OP_ADD, AddExpr.OP_SUB))
        );
        return new PhiFunction(new PhiCollection(), paramList, body);
    }

    private PhiFunction createFunctionWithDefaults() throws PhiException{
        //lambda(a, b = 10) -> a % b
        PhiScope defaults = new PhiScope();
        defaults.createMember(new PhiSymbol("b"));
        defaults.setNamed("b", new PhiInt(10));

        ParameterList paramList = new ParameterList(List.of("a", "b"), defaults);
        FunctionBody body = new FunctionBody(
                new MulExpr(List.of(
                        new Atom(new PhiSymbol("a")),
                        new Atom(new PhiSymbol("b"))
                ), List.of(MulExpr.OP_MUL, MulExpr.OP_MOD))
        );
        return new PhiFunction(new PhiCollection(), paramList, body);
    }

    private PhiCollection createParameters(int... unnamed) throws PhiException {
        return createParameters(new String[0], new int[0], unnamed);
    }

    private PhiCollection createParameters(String[] names, int[] values, int... unnamed) throws PhiException{
        PhiCollection paramCollection = new PhiCollection();

        for(int i = 0; i < names.length; i++){
            paramCollection.createMember(new PhiSymbol(names[i]));
            paramCollection.setNamed(names[i], new PhiInt(values[i]));
        }

        if(unnamed.length > 0) {
            paramCollection.createMember(new PhiInt(unnamed.length - 1));
            for (int i = 0; i < unnamed.length; i++) {
                paramCollection.setUnnamed(i, new PhiInt(unnamed[i]));
            }
        }
        return paramCollection;
    }

    @Test
    public void testUnnamedParams() throws PhiException {
        PhiFunction func = createFunction();

        assertEquals(1, func.call(createParameters(3, 2)).longValue());
        assertThrows("Call with too few parameters succeeded.", PhiException.class,
                ()->func.call(createParameters(2))
        );
        assertThrows("Call with too many parameters succeeded.", PhiException.class,
                ()->func.call(createParameters(2, 3, 4))
        );
    }

    @Test
    public void testNamedParams() throws PhiException {
        PhiFunction func = createFunction();
        assertEquals("Named parameters did not get interpreted correctly",
                1, func.call(createParameters(new String[]{"b", "a"}, new int[]{2, 3})).longValue());
        assertEquals("Named parameters did not override unnamed parameters correctly",
                2, func.call(createParameters(new String[]{"b", "a"}, new int[]{4, 6}, 4)).longValue());

        assertThrows("Supplying nonexistent parameter succeeded.", PhiException.class,
                ()->func.call(createParameters(new String[]{"b", "a", "c"}, new int[]{2, 3, 4}))
        );
    }

    @Test
    public void testDefaultValues() throws PhiException {
        PhiFunction func = createFunctionWithDefaults();
        assertEquals("Function does not work.", 5, func.call(createParameters(11, 6)).longValue());
        assertEquals("Default value is not used.", 1, func.call(createParameters(11)).longValue());
    }
}