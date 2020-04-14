package de.delphi.phi.parser;

import de.delphi.phi.PhiSyntaxException;
import de.delphi.phi.parser.ast.*;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ParserTest {

    @Test
    public void testBasicParses() throws IOException, PhiSyntaxException {
        String input = "1 + 1\n 1 - 1\n 1 * 1\n 1 / 1\n 1 % 1\n 1 & 1\n 1 | 1\n 1 ^ 1\n 1 << 1\n 1 >> 1\n" +
                       "1 == 1\n 1 != 1\n 1 < 1\n 1 <= 1\n 1 >= 1\n 1 > 1;\n" +
                       "-1\n 'a\n new a\n !a\n" +
                       "a = 1\n a += 1\n a -= 1\n a *= 1\n a /= 1\n a %= 1\n a &= 1\n a |= 1\n a ^= 1\n a <<= 1\n a >>= 1\n" +
                       "a[1]\n a.b\n a(1)\n" +
                       "lambda(a) -> a\n if a then b\n if a then b else c\n while a do b\n for a b c do d\n" +
                       "{a b}\n break\n break(1)\n continue\n continue(1)\n return 1\n" +
                       "var a\n var a = 1\n function a(b) b;\n [1]";
        Parser parser = new Parser(input);

        assertTrue(parser.nextExpression() instanceof AddExpr);
        assertTrue(parser.nextExpression() instanceof AddExpr);
        assertTrue(parser.nextExpression() instanceof MulExpr);
        assertTrue(parser.nextExpression() instanceof MulExpr);
        assertTrue(parser.nextExpression() instanceof MulExpr);
        assertTrue(parser.nextExpression() instanceof AndExpr);
        assertTrue(parser.nextExpression() instanceof OrExpr);
        assertTrue(parser.nextExpression() instanceof XorExpr);
        assertTrue(parser.nextExpression() instanceof ShiftExpr);
        assertTrue(parser.nextExpression() instanceof ShiftExpr);
        assertTrue(parser.nextExpression() instanceof RelationalExpr);
        assertTrue(parser.nextExpression() instanceof RelationalExpr);
        assertTrue(parser.nextExpression() instanceof RelationalExpr);
        assertTrue(parser.nextExpression() instanceof RelationalExpr);
        assertTrue(parser.nextExpression() instanceof RelationalExpr);
        assertTrue(parser.nextExpression() instanceof RelationalExpr);
        assertTrue(parser.nextExpression() instanceof NegationExpr);
        assertTrue(parser.nextExpression() instanceof QuoteExpr);
        assertTrue(parser.nextExpression() instanceof NewExpr);
        assertTrue(parser.nextExpression() instanceof NotExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof AssignExpr);
        assertTrue(parser.nextExpression() instanceof SubscriptExpr);
        assertTrue(parser.nextExpression() instanceof MemberAccessExpr);
        assertTrue(parser.nextExpression() instanceof FunctionCallExpr);
        assertTrue(parser.nextExpression() instanceof FunctionDefinitionExpr);
        assertTrue(parser.nextExpression() instanceof IfExpr);
        assertTrue(parser.nextExpression() instanceof IfExpr);
        assertTrue(parser.nextExpression() instanceof WhileExpr);
        assertTrue(parser.nextExpression() instanceof ForExpr);
        assertTrue(parser.nextExpression() instanceof CompoundExpr);
        assertTrue(parser.nextExpression() instanceof BreakExpr);
        assertTrue(parser.nextExpression() instanceof BreakExpr);
        assertTrue(parser.nextExpression() instanceof ContinueExpr);
        assertTrue(parser.nextExpression() instanceof ContinueExpr);
        assertTrue(parser.nextExpression() instanceof ReturnExpr);
        assertTrue(parser.nextExpression() instanceof VariableDeclarationExpr);
        assertTrue(parser.nextExpression() instanceof VariableDeclarationExpr);
        assertTrue(parser.nextExpression() instanceof VariableDeclarationExpr);
        assertTrue(parser.nextExpression() instanceof CollectionDefinitionExpr);
        assertTrue(parser.eoi());
    }
}