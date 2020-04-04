package de.delphi.phi.parser;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class LexerTest {

    private static final double EPSILON = 0.000000000001;

    @Test
    public void testReservedWords() throws IOException {
        String input = "if then else while do for break continue return var function lambda new true false null";
        Lexer lexer = new Lexer();
        lexer.setInput(new StringReader(input));

        assertEquals(new Token(Tag.IF, "if", 0, 0), lexer.nextToken());
        assertEquals(new Token(Tag.THEN, "then", 0, 3), lexer.nextToken());
        assertEquals(new Token(Tag.ELSE, "else", 0, 8), lexer.nextToken());
        assertEquals(new Token(Tag.WHILE, "while", 0, 13), lexer.nextToken());
        assertEquals(new Token(Tag.DO, "do", 0, 19), lexer.nextToken());
        assertEquals(new Token(Tag.FOR, "for", 0, 22), lexer.nextToken());
        assertEquals(new Token(Tag.BREAK, "break", 0, 26), lexer.nextToken());
        assertEquals(new Token(Tag.CONTINUE, "continue", 0, 32), lexer.nextToken());
        assertEquals(new Token(Tag.RETURN, "return", 0, 41), lexer.nextToken());
        assertEquals(new Token(Tag.VAR, "var", 0, 48), lexer.nextToken());
        assertEquals(new Token(Tag.FUNCTION, "function", 0, 52), lexer.nextToken());
        assertEquals(new Token(Tag.LAMBDA, "lambda", 0, 61), lexer.nextToken());
        assertEquals(new Token(Tag.NEW, "new", 0, 68), lexer.nextToken());
        assertEquals(new Token(Tag.LITERAL, "true", 0, 72), lexer.nextToken());
        assertEquals(new Token(Tag.LITERAL, "false", 0, 77), lexer.nextToken());
        assertEquals(new Token(Tag.LITERAL, "null", 0, 83), lexer.nextToken());
        assertEquals(new Token(Tag.EOI, "", 0, 87), lexer.nextToken());
    }

    @Test
    public void testOperators() throws IOException {
        String input = "+ - * / % & | ^ ! << >> = == += -= *= /= %= &= |= ^= != <<= >>= < > <= >= -> , . ' ;";
        Lexer lexer = new Lexer();
        lexer.setInput(new StringReader(input));

        assertEquals(new Token(Tag.ADD, "+", 0, 0), lexer.nextToken());
        assertEquals(new Token(Tag.SUB, "-", 0, 2), lexer.nextToken());
        assertEquals(new Token(Tag.MUL, "*", 0, 4), lexer.nextToken());
        assertEquals(new Token(Tag.DIV, "/", 0, 6), lexer.nextToken());
        assertEquals(new Token(Tag.MOD, "%", 0, 8), lexer.nextToken());
        assertEquals(new Token(Tag.AND, "&", 0, 10), lexer.nextToken());
        assertEquals(new Token(Tag.OR, "|", 0, 12), lexer.nextToken());
        assertEquals(new Token(Tag.XOR, "^", 0, 14), lexer.nextToken());
        assertEquals(new Token(Tag.NOT, "!", 0, 16), lexer.nextToken());
        assertEquals(new Token(Tag.SHIFT_LEFT, "<<", 0, 18), lexer.nextToken());
        assertEquals(new Token(Tag.SHIFT_RIGHT, ">>", 0, 21), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN, "=", 0, 24), lexer.nextToken());
        assertEquals(new Token(Tag.EQUALS, "==", 0, 26), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_ADD, "+=", 0, 29), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_SUB, "-=", 0, 32), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_MUL, "*=", 0, 35), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_DIV, "/=", 0, 38), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_MOD, "%=", 0, 41), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_AND, "&=", 0, 44), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_OR, "|=", 0, 47), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_XOR, "^=", 0, 50), lexer.nextToken());
        assertEquals(new Token(Tag.NOT_EQUALS, "!=", 0, 53), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_SHIFT_LEFT, "<<=", 0, 56), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_SHIFT_RIGHT, ">>=", 0, 60), lexer.nextToken());
        assertEquals(new Token(Tag.LESS_THAN, "<", 0, 64), lexer.nextToken());
        assertEquals(new Token(Tag.GREATER_THAN, ">", 0, 66), lexer.nextToken());
        assertEquals(new Token(Tag.LESS_EQUALS, "<=", 0, 68), lexer.nextToken());
        assertEquals(new Token(Tag.GREATER_EQUALS, ">=", 0, 71), lexer.nextToken());
        assertEquals(new Token(Tag.ARROW, "->", 0, 74), lexer.nextToken());
        assertEquals(new Token(Tag.COMMA, ",", 0, 77), lexer.nextToken());
        assertEquals(new Token(Tag.PERIOD, ".", 0, 79), lexer.nextToken());
        assertEquals(new Token(Tag.QUOTE, "'", 0, 81), lexer.nextToken());
        assertEquals(new Token(Tag.SEMICOLON, ";", 0, 83), lexer.nextToken());
        assertEquals(new Token(Tag.EOI, "", 0, 84), lexer.nextToken());
    }

    @Test
    public void testComments() throws IOException {
        String input = "abc /*a comment*/ def //line comment \nghi /*\n*/test";
        Lexer lexer = new Lexer();
        lexer.setInput(new StringReader(input));

        assertEquals(new Token(Tag.SYMBOL, "abc", 0, 0), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "def", 0, 18), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "ghi", 1, 0), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "test", 2, 2), lexer.nextToken());
    }

    @Test
    public void testParentheses() throws IOException {
        String input = "()[]{}";
        Lexer lexer = new Lexer();
        lexer.setInput(new StringReader(input));

        assertEquals(new Token(Tag.LEFT_PARENTHESIS, "(", 0 ,0), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_PARENTHESIS, ")", 0 ,1), lexer.nextToken());
        assertEquals(new Token(Tag.LEFT_BRACKET, "[", 0 ,2), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_BRACKET, "]", 0 ,3), lexer.nextToken());
        assertEquals(new Token(Tag.LEFT_BRACE, "{", 0 ,4), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_BRACE, "}", 0 ,5), lexer.nextToken());
        assertEquals(new Token(Tag.EOI, "", 0 ,6), lexer.nextToken());
    }

    private void floatEquals(double expected, double actual){
        assertTrue(Math.abs(actual - expected) < EPSILON);
    }

    @Test
    public void testLiterals() throws IOException {
        String input = "\"hello \\\"world\\\"\" 123 0x1f3 0107 0b101011 1.23 .0 1. 5.5e2 200e-2  0xa.b 0x0.1p2 0b10.01 0b10.01e-11";
        Lexer lexer = new Lexer();
        lexer.setInput(new StringReader(input));

        assertEquals("hello \"world\"", ((Literal) lexer.nextToken()).content.toString());
        assertEquals(123, ((Literal) lexer.nextToken()).content.longValue());
        assertEquals(0x1f3, ((Literal) lexer.nextToken()).content.longValue());
        assertEquals(0107, ((Literal) lexer.nextToken()).content.longValue());
        assertEquals(0b101011, ((Literal) lexer.nextToken()).content.longValue());
        floatEquals(1.23, ((Literal) lexer.nextToken()).content.doubleValue());
        floatEquals(0.0, ((Literal) lexer.nextToken()).content.doubleValue());
        floatEquals(1.0, ((Literal) lexer.nextToken()).content.doubleValue());
        floatEquals(5.5e2, ((Literal) lexer.nextToken()).content.doubleValue());
        floatEquals(200e-2, ((Literal) lexer.nextToken()).content.doubleValue());
        floatEquals(0xa.bp0, ((Literal) lexer.nextToken()).content.doubleValue());
        floatEquals(0x0.1p2, ((Literal) lexer.nextToken()).content.doubleValue());

        //TODO float literals in different bases
        //floatEquals(2.25, ((Literal) lexer.nextToken()).content.doubleValue());
    }

    @Test
    public void testProgram1() throws IOException {
        String input = "function min(list)\n" +
                "    for var i = 0, min = 1000000;\n" +
                "        i < list.length;\n" +
                "        {i += 1\n" +
                "        min = if list[i] < min\n" +
                "            then list[i]\n" +
                "            else min}\n" +
                "            min";
        Lexer lexer = new Lexer();
        lexer.setInput(new StringReader(input));

        assertEquals(new Token(Tag.FUNCTION, "function", 0 ,0), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "min", 0 ,9), lexer.nextToken());
        assertEquals(new Token(Tag.LEFT_PARENTHESIS, "(", 0 ,12), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "list", 0 ,13), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_PARENTHESIS, ")", 0 ,17), lexer.nextToken());

        assertEquals(new Token(Tag.FOR, "for", 1 ,4), lexer.nextToken());
        assertEquals(new Token(Tag.VAR, "var", 1 ,8), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "i", 1 ,12), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN, "=", 1 ,14), lexer.nextToken());
        assertEquals(new Token(Tag.LITERAL, "0", 1 ,16), lexer.nextToken());
        assertEquals(new Token(Tag.COMMA, ",", 1 ,17), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "min", 1 ,19), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN, "=", 1 ,23), lexer.nextToken());
        assertEquals(new Token(Tag.LITERAL, "1000000", 1 ,25), lexer.nextToken());
        assertEquals(new Token(Tag.SEMICOLON, ";", 1 ,32), lexer.nextToken());

        assertEquals(new Token(Tag.SYMBOL, "i", 2 ,8), lexer.nextToken());
        assertEquals(new Token(Tag.LESS_THAN, "<", 2 ,10), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "list", 2 ,12), lexer.nextToken());
        assertEquals(new Token(Tag.PERIOD, ".", 2 ,16), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "length", 2 ,17), lexer.nextToken());
        assertEquals(new Token(Tag.SEMICOLON, ";", 2 ,23), lexer.nextToken());

        assertEquals(new Token(Tag.LEFT_BRACE, "{", 3 ,8), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "i", 3 ,9), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN_ADD, "+=", 3 ,11), lexer.nextToken());
        assertEquals(new Token(Tag.LITERAL, "1", 3 ,14), lexer.nextToken());

        assertEquals(new Token(Tag.SYMBOL, "min", 4 ,8), lexer.nextToken());
        assertEquals(new Token(Tag.ASSIGN, "=", 4 ,12), lexer.nextToken());
        assertEquals(new Token(Tag.IF, "if", 4 ,14), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "list", 4 ,17), lexer.nextToken());
        assertEquals(new Token(Tag.LEFT_BRACKET, "[", 4 ,21), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "i", 4 ,22), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_BRACKET, "]", 4 ,23), lexer.nextToken());
        assertEquals(new Token(Tag.LESS_THAN, "<", 4 ,25), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "min", 4 ,27), lexer.nextToken());

        assertEquals(new Token(Tag.THEN, "then", 5 ,12), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "list", 5 ,17), lexer.nextToken());
        assertEquals(new Token(Tag.LEFT_BRACKET, "[", 5 ,21), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "i", 5 ,22), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_BRACKET, "]", 5 ,23), lexer.nextToken());

        assertEquals(new Token(Tag.ELSE, "else", 6 ,12), lexer.nextToken());
        assertEquals(new Token(Tag.SYMBOL, "min", 6 ,17), lexer.nextToken());
        assertEquals(new Token(Tag.RIGHT_BRACE, "}", 6 ,20), lexer.nextToken());

        assertEquals(new Token(Tag.SYMBOL, "min", 7 ,12), lexer.nextToken());
    }
}