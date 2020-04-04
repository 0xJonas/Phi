package de.delphi.phi.parser;

enum Tag {
    IF, THEN, ELSE, WHILE, DO, FOR, BREAK, CONTINUE, RETURN, VAR, FUNCTION, LAMBDA, NEW,    //keywords
    ARROW,
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS, COMMA, SEMICOLON,
    LEFT_BRACKET, RIGHT_BRACKET,
    LEFT_BRACE, RIGHT_BRACE,
    PERIOD, QUOTE,
    ADD, SUB, MUL, DIV, MOD,
    AND, OR, XOR, NOT,
    SHIFT_LEFT, SHIFT_RIGHT,
    ASSIGN, ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD,
    ASSIGN_AND, ASSIGN_OR, ASSIGN_XOR, ASSIGN_SHIFT_LEFT, ASSIGN_SHIFT_RIGHT,
    GREATER_THAN, GREATER_EQUALS, EQUALS, NOT_EQUALS, LESS_EQUALS, LESS_THAN,
    LITERAL, SYMBOL, EOI, ERROR, FILLER
}
