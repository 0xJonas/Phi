package de.delphi.phi.parser;

import de.delphi.phi.PhiSyntaxException;
import de.delphi.phi.data.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Turns text input into a stream of tokens.
 */
class Lexer {

    private enum State {
        //Starting state
        START,

        //Reserved words
        I, IF,
        T, TH, THE, THEN,
        E, EL, ELS, ELSE,
        W, WH, WHI, WHIL, WHILE,
        D, DO,
        F, FO, FOR,
        B, BR, BRE, BREA, BREAK,
        C, CO, CON, CONT, CONTI, CONTIN, CONTINU, CONTINUE,
        R, RE, RET, RETU, RETUR, RETURN,
        V, VA, VAR,
           FU, FUN, FUNC, FUNCT, FUNCTI, FUNCTIO, FUNCTION,
        L, LA, LAM, LAMB, LAMBD, LAMBDA,
        N, NE, NEW,
           TR, TRU, TRUE,
           FA, FAL, FALS, FALSE,
           NU, NUL, NULL,

        //Parentheses, brackets, braces and associated stuff
        LEFT_PARENTHESIS, RIGHT_PARENTHESIS, COMMA, SEMICOLON,
        LEFT_BRACKET, RIGHT_BRACKET,
        LEFT_BRACE, RIGHT_BRACE,
        PERIOD, QUOTE,

        //Operators
        ADD, ASSIGN_ADD,
        SUB, ASSIGN_SUB, ARROW,
        MUL, ASSIGN_MUL,
        DIV, ASSIGN_DIV,
        MOD, ASSIGN_MOD,
        AND, ASSIGN_AND,
        OR, ASSIGN_OR,
        XOR, ASSIGN_XOR,
        NOT, NOT_EQUALS,
        ASSIGN, EQUALS,
        LESS_THAN, LESS_EQUALS, SHIFT_LEFT, ASSIGN_SHIFT_LEFT,
        GREATER_THAN, GREATER_EQUALS, SHIFT_RIGHT, ASSIGN_SHIFT_RIGHT,

        //Literals and symbols
        ZERO, INT_LITERAL,
        FLOAT_LITERAL, FLOAT_EXPONENT_SIGN, FLOAT_EXPONENT,
        SYMBOL,
        STRING_START, STRING, STRING_ESCAPE, STRING_END,
        COMMENT, COMMENT_ASTERISK, LINE_COMMENT, COMMENT_END,
        EOI, ERROR, FILLER
    }

    private static final int BASE_BINARY = 2;
    private static final int BASE_OCTAL = 8;
    private static final int BASE_DECIMAL = 10;
    private static final int BASE_HEX = 16;

    /**
     * Buffer for current lexeme
     */
    private StringBuilder lexeme;

    /**
     * Information on the current position in the input
     */
    private int line, col, tokenStartCol;

    /**
     * The current code point
     */
    private int peek;

    /**
     * The current base for numerical literals
     */
    private int base = BASE_DECIMAL;

    /**
     * Current state of the lexer
     */
    private State state = State.START;

    /**
     * Input source for the lexer.
     */
    private Reader reader;

    /**
     * Sets the input for the lexer and resets it's state.
     * @param reader The input reader
     */
    public Lexer(Reader reader){
        this.reader = reader;
        lexeme = new StringBuilder();
    }

    public Lexer(String input){
        this.reader = new StringReader(input);
        lexeme = new StringBuilder();
    }

    /**
     * Checks whether the given code point is valid as the first character of a symbol.
     *
     * Valid characters are upper- and lowercase letters, the underscore '_', the dollar sign '$'
     * and the question mark '?'. A symbol can NOT start with a digit.
     * @param cp The code point to check.
     * @return true if the code point can start a symbol.
     */
    private boolean isValidSymbolStart(int cp){
        return Character.isLetter(cp)
                || cp == '_'
                || cp == '$'
                || cp == '?';
    }

    /**
     * Checks whether the given code point can be part of a symbol.
     *
     * Valid characters are all characters that can start a symbol as well as digits.
     * @param cp The code point to check.
     * @return true if the code point can be part of a symbol.
     */
    private boolean isValidSymbolPart(int cp){
        return isValidSymbolStart(cp) || Character.isDigit(cp);
    }

    /**
     * Converts a code point to it's corresponding escaped character.
     *
     * The possible characters are:
     * <ul>
     *     <li>a -> bell</li>
     *     <li>b -> backspace</li>
     *     <li>f -> form feed</li>
     *     <li>n -> newline</li>
     *     <li>r -> carriage return</li>
     *     <li>t -> tab</li>
     * </ul>
     *
     * All other characters get returned without change.
     * @param cp The code point to escape.
     * @return The escaped character or the character itself if there was no corresponding escape character.
     */
    private int escapeCodePoint(int cp){
        switch(cp){
            case 'a': return 7; //Bell
            case 'b': return '\b';
            case 'f': return '\f';
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
            default: return cp;
        }
    }

    /**
     * Moves the lexer into a given state if the current peek character matches the {@code cp} parameter.
     *
     * This method is used to match reserved words. Therefore, if a match fails the lexer is put into
     * the SYMBOL state if peek is a valid symbol character {@see isValidSymbolPart()}. Otherwise the incomplete
     * word is interpreted as a symbol and returned.
     * @param cp The character to match against.
     * @param success The state to go to when the match succeeds.
     * @return If the current peek character is does not match the input parameter and is also not a valid
     * symbol character, a token containing the incomplete reserved word is returned. Otherweise this method returns
     * null.
     */
    private Token matchReservedWord(int cp, State success){
        if(peek == cp){ //Successful match
            state = success;
            return null;
        }else if(isValidSymbolPart(peek)){  //failed match but valid symbol character
            state = State.SYMBOL;
            return null;
        }else{  //failed match and not a valid symbol character
            startNextToken();
            return makeToken(Tag.SYMBOL);
        }
    }

    /**
     * Signifies that the current peek character is the first character of a new token.
     */
    private void startNextToken(){
        state = State.START;
        processCharacter();
    }

    /**
     * Checks whether the current peek character can be part of a symbol.
     * Otherwise returns the supplied token. This is used to match reserved words. If a reserved has been completely
     * matched, the next character must not be a valid symbol character (i.e. white space, operators).
     * @param success The token to return if the current peek character is not a valid symbol character.
     * @return The input token or null if 'peek' is a valid symbol port.
     */
    private Token completeReservedWord(Token success){
        if(isValidSymbolPart(peek)){
            state = State.SYMBOL;
            return null;
        }else{
            startNextToken();
            return success;
        }
    }

    /**
     * Checks whether the current peek character is '='.
     *
     * This method is used for the various combined assignments (+= -= *= ...). If 'peek' is not '=' the
     * supplied token is returned, which should be an operator (+ - * ...). Otherwise the lexer is moved to
     * the state associated with the assignment operator.
     * @param success The state to go to when 'peek' is '='.
     * @param op The token to return when 'peek' is not '='.
     * @return The input token or null.
     */
    private Token matchAssignOperator(State success, Token op){
        if(peek == '='){
            state = success;
            return null;
        }else{
            startNextToken();
            return op;
        }
    }

    /**
     * Checks whether the input character is a valid digit in the given base.
     * @param digit The digit to check.
     * @param base The base to draw the digits from.
     * @return true if 'digit' is valid in the given base, false otherwise.
     */
    private boolean isValidDigitInBase(int digit, int base){
        switch(base){
            case BASE_BINARY: return digit >= '0' && digit <= '1';
            case BASE_OCTAL: return digit >= '0' && digit <= '7';
            case BASE_DECIMAL: return digit >= '0' && digit <= '9';
            case BASE_HEX: return (digit >= '0' && digit <= '9') || (digit >= 'a' && digit <= 'f');
        }
        return false;
    }

    /**
     * Checks whether the given character is a valid exponent indicator for the given base.
     *
     * Valid exponent indicators are 'p' and 'P' for hexadecimal and 'e' and 'E' for all other bases.
     * @param c The character to check.
     * @param base The base.
     * @return true if c is a valid exponent indicator.
     */
    private boolean isValidExponentIndicator(int c, int base){
        switch(base){
            case BASE_BINARY:
            case BASE_OCTAL:
            case BASE_DECIMAL:
                return c == 'e' || c == 'E';
            case BASE_HEX:
                return c == 'p' || c == 'P';
        }
        return false;
    }

    /**
     * Returns the length of the base indicator (the 0x in 0x7f) for a given base.
     * @param base The base.
     * @return The length of the indicator.
     */
    private int baseIndicatorLength(int base){
        switch(base){
            case BASE_BINARY:
            case BASE_HEX:
                return 2;
            case BASE_OCTAL: return 1;
            case BASE_DECIMAL: return 0;
        }
        return 0;
    }

    /**
     * Converts a lexeme containing a floating point literal to a double value.
     *
     * @param lexeme The String to convert.
     * @param base The base
     * @return the double value represented by the input string.
     */
    private double parseDoubleExtended(String lexeme, int base){
        switch(base){
            case BASE_BINARY:{
                //TODO
                return 0.0;
            } case BASE_OCTAL:{
                //TODO
                return 0.0;
            }
            case BASE_DECIMAL:
                return Double.parseDouble(lexeme);
            case BASE_HEX:{
                //Add exponent since it is mandatory for hex floats in Java
                if(lexeme.indexOf('p') < 0 && lexeme.indexOf('P') < 0)
                    lexeme += "p0";
                return Double.parseDouble(lexeme);
            }
        }
        return 0.0;
    }

    /**
     * Creates a token with the given tag and the current lexeme, line number, and column number.
     * @param tag The tag for the token
     * @return The new token
     */
    private Token makeToken(Tag tag){
        return new Token(tag, lexeme.toString(), line, tokenStartCol);
    }

    /**
     * Creates a Literal token containing the given value.
     * @param value The value for the Literal.
     * @return The new Literal.
     */
    private Literal makeLiteral(PhiObject value){
        return new Literal(lexeme.toString(), value, line, tokenStartCol);
    }

    /**
     * Processes a single character
     *
     * This method checks the current 'peek' character and modifies the lexer's state accordingly.
     * At the point this method is called the 'peek' character has not yet been added to the lexeme buffer.
     * If the current state and 'peek' character indicate that a token is complete, that token is returned. Otherwise
     * this method returns null.
     * @return If a token has been completed, returns the token, otherwise null.
     */
    private Token processCharacter(){
        switch(state) {
            case START: {
                switch (peek) {
                    case -1: state = State.EOI; return null;
                    case 'i': state = State.I; return null;
                    case 't': state = State.T; return null;
                    case 'e': state = State.E; return null;
                    case 'w': state = State.W; return null;
                    case 'd': state = State.D; return null;
                    case 'f': state = State.F; return null;
                    case 'b': state = State.B; return null;
                    case 'c': state = State.C; return null;
                    case 'r': state = State.R; return null;
                    case 'v': state = State.V; return null;
                    case 'l': state = State.L; return null;
                    case 'n': state = State.N; return null;
                    case '0': state = State.ZERO; return null;

                    case '(': state = State.LEFT_PARENTHESIS; return null;
                    case ')': state = State.RIGHT_PARENTHESIS; return null;
                    case ',': state = State.COMMA; return null;
                    case ';': state = State.SEMICOLON; return null;
                    case '[': state = State.LEFT_BRACKET; return null;
                    case ']': state = State.RIGHT_BRACKET; return null;
                    case '{': state = State.LEFT_BRACE; return null;
                    case '}': state = State.RIGHT_BRACE; return null;

                    case '.': state = State.PERIOD; return null;
                    case '\'': state = State.QUOTE; return null;
                    case '+': state = State.ADD; return null;
                    case '-': state = State.SUB; return null;
                    case '*': state = State.MUL; return null;
                    case '/': state = State.DIV; return null;
                    case '%': state = State.MOD; return null;
                    case '&': state = State.AND; return null;
                    case '|': state = State.OR; return null;
                    case '^': state = State.XOR; return null;
                    case '<': state = State.LESS_THAN; return null;
                    case '>': state = State.GREATER_THAN; return null;
                    case '=': state = State.ASSIGN; return null;
                    case '!': state = State.NOT; return null;
                    case '\"': state = State.STRING_START; return null;
                    default:
                        if (Character.isWhitespace(peek)) {
                            state = State.FILLER;
                        }else if (isValidSymbolStart(peek)) {
                            state = State.SYMBOL;
                        }else if (Character.isDigit(peek)) {
                            base = BASE_DECIMAL;
                            state = State.INT_LITERAL;
                        }else {
                            state = State.ERROR;
                        }
                        return null;
                }
            }
            case EOI: startNextToken(); return makeToken(Tag.EOI);
            case I: return matchReservedWord('f', State.IF);
            case IF: return completeReservedWord(makeToken(Tag.IF));

            case T: {
                switch(peek){
                    case 'h': state = State.TH; return null;
                    case 'r': state = State.TR; return null;
                    default: state = State.SYMBOL; return null;
                }
            }
            case TH: return matchReservedWord('e', State.THE);
            case THE: return matchReservedWord('n', State.THEN);
            case THEN: return completeReservedWord(makeToken(Tag.THEN));

            case E: return matchReservedWord('l', State.EL);
            case EL: return matchReservedWord('s', State.ELS);
            case ELS: return matchReservedWord('e', State.ELSE);
            case ELSE: return completeReservedWord(makeToken(Tag.ELSE));

            case W: return matchReservedWord('h', State.WH);
            case WH: return matchReservedWord('i', State.WHI);
            case WHI: return matchReservedWord('l', State.WHIL);
            case WHIL: return matchReservedWord('e', State.WHILE);
            case WHILE: return completeReservedWord(makeToken(Tag.WHILE));

            case D: return matchReservedWord('o', State.DO);
            case DO: return completeReservedWord(makeToken(Tag.DO));

            case F: {
                switch(peek){
                    case 'a': state = State.FA; return null;
                    case 'o': state = State.FO; return null;
                    case 'u': state = State.FU; return null;
                    default: state = State.SYMBOL; return null;
                }
            }
            case FO: return matchReservedWord('r', State.FOR);
            case FOR: return completeReservedWord(makeToken(Tag.FOR));

            case B: return matchReservedWord('r', State.BR);
            case BR: return matchReservedWord('e', State.BRE);
            case BRE: return matchReservedWord('a', State.BREA);
            case BREA: return matchReservedWord('k', State.BREAK);
            case BREAK: return completeReservedWord(makeToken(Tag.BREAK));

            case C: return matchReservedWord('o', State.CO);
            case CO: return matchReservedWord('n', State.CON);
            case CON: return matchReservedWord('t', State.CONT);
            case CONT: return matchReservedWord('i', State.CONTI);
            case CONTI: return matchReservedWord('n', State.CONTIN);
            case CONTIN: return matchReservedWord('u', State.CONTINU);
            case CONTINU: return matchReservedWord('e', State.CONTINUE);
            case CONTINUE: return completeReservedWord(makeToken(Tag.CONTINUE));

            case R: return matchReservedWord('e', State.RE);
            case RE: return matchReservedWord('t', State.RET);
            case RET: return matchReservedWord('u', State.RETU);
            case RETU: return matchReservedWord('r', State.RETUR);
            case RETUR: return matchReservedWord('n', State.RETURN);
            case RETURN: return completeReservedWord(makeToken(Tag.RETURN));

            case V: return matchReservedWord('a', State.VA);
            case VA: return matchReservedWord('r', State.VAR);
            case VAR: return completeReservedWord(makeToken(Tag.VAR));

            case FU: return matchReservedWord('n', State.FUN);
            case FUN: return matchReservedWord('c', State.FUNC);
            case FUNC: return matchReservedWord('t', State.FUNCT);
            case FUNCT: return matchReservedWord('i', State.FUNCTI);
            case FUNCTI: return matchReservedWord('o', State.FUNCTIO);
            case FUNCTIO: return matchReservedWord('n', State.FUNCTION);
            case FUNCTION: return completeReservedWord(makeToken(Tag.FUNCTION));

            case L: return matchReservedWord('a', State.LA);
            case LA: return matchReservedWord('m', State.LAM);
            case LAM: return matchReservedWord('b', State.LAMB);
            case LAMB: return matchReservedWord('d', State.LAMBD);
            case LAMBD: return matchReservedWord('a', State.LAMBDA);
            case LAMBDA: return completeReservedWord(makeToken(Tag.LAMBDA));

            case N:{
                switch(peek){
                    case 'e': state = State.NE; return null;
                    case 'u': state = State.NU; return null;
                    default: state = State.SYMBOL; return null;
                }
            }
            case NE: return matchReservedWord('w', State.NEW);
            case NEW: return completeReservedWord(makeToken(Tag.NEW));

            case TR: return matchReservedWord('u', State.TRU);
            case TRU: return matchReservedWord('e', State.TRUE);
            case TRUE: return completeReservedWord(makeLiteral( new PhiInt(1)));

            case FA: return matchReservedWord('l', State.FAL);
            case FAL: return matchReservedWord('s', State.FALS);
            case FALS: return matchReservedWord('e', State.FALSE);
            case FALSE: return completeReservedWord(makeLiteral(new PhiInt(0)));

            case NU: return matchReservedWord('l', State.NUL);
            case NUL: return matchReservedWord('l', State.NULL);
            case NULL: return completeReservedWord(makeLiteral(PhiNull.NULL));

            case ADD: return matchAssignOperator(State.ASSIGN_ADD, makeToken(Tag.ADD));
            case SUB: {
                switch(peek){
                    case '=': state = State.ASSIGN_SUB; return null;
                    case '>': state = State.ARROW; return null;
                    default: startNextToken(); return makeToken(Tag.SUB);
                }
            }
            case MUL: return matchAssignOperator(State.ASSIGN_MUL, makeToken(Tag.MUL));
            case DIV: {
                switch(peek){
                    case '/': state = State.LINE_COMMENT; return null;
                    case '*': state = State.COMMENT; return null;
                    case '=': state = State.ASSIGN_DIV; return null;
                    default: startNextToken(); return makeToken(Tag.DIV);
                }
            }
            case MOD: return matchAssignOperator(State.ASSIGN_MOD, makeToken(Tag.MOD));
            case AND: return matchAssignOperator(State.ASSIGN_AND, makeToken(Tag.AND));
            case OR: return matchAssignOperator(State.ASSIGN_OR, makeToken(Tag.OR));
            case XOR: return matchAssignOperator(State.ASSIGN_XOR, makeToken(Tag.XOR));
            case SHIFT_LEFT: return matchAssignOperator(State.ASSIGN_SHIFT_LEFT, makeToken(Tag.SHIFT_LEFT));
            case SHIFT_RIGHT: return matchAssignOperator(State.ASSIGN_SHIFT_RIGHT, makeToken(Tag.SHIFT_RIGHT));
            case NOT: return matchAssignOperator(State.NOT_EQUALS, makeToken(Tag.NOT));

            case GREATER_THAN:{
                switch(peek){
                    case '=': state = State.GREATER_EQUALS; return null;
                    case '>': state = State.SHIFT_RIGHT; return null;
                    default: startNextToken(); return makeToken(Tag.GREATER_THAN);
                }
            }
            case LESS_THAN:{
                switch(peek){
                    case '=': state = State.LESS_EQUALS; return null;
                    case '<': state = State.SHIFT_LEFT; return null;
                    default: startNextToken(); return makeToken(Tag.LESS_THAN);
                }
            }


            case ASSIGN: {
                if(peek == '='){
                    state = State.EQUALS;
                    return null;
                }else{
                    startNextToken();
                    return makeToken(Tag.ASSIGN);
                }
            }
            case PERIOD:{
                if(peek >= '0' && peek <= '9'){
                    state = State.FLOAT_LITERAL;
                    return null;
                }else{
                    startNextToken();
                    return makeToken(Tag.PERIOD);
                }
            }
            case ARROW: startNextToken(); return makeToken(Tag.ARROW);
            case ASSIGN_ADD: startNextToken(); return makeToken(Tag.ASSIGN_ADD);
            case ASSIGN_SUB: startNextToken(); return makeToken(Tag.ASSIGN_SUB);
            case ASSIGN_MUL: startNextToken(); return makeToken(Tag.ASSIGN_MUL);
            case ASSIGN_DIV: startNextToken(); return makeToken(Tag.ASSIGN_DIV);
            case ASSIGN_MOD: startNextToken(); return makeToken(Tag.ASSIGN_MOD);
            case ASSIGN_AND: startNextToken(); return makeToken(Tag.ASSIGN_AND);
            case ASSIGN_OR: startNextToken(); return makeToken(Tag.ASSIGN_OR);
            case ASSIGN_XOR: startNextToken(); return makeToken(Tag.ASSIGN_XOR);
            case ASSIGN_SHIFT_LEFT: startNextToken(); return makeToken(Tag.ASSIGN_SHIFT_LEFT);
            case ASSIGN_SHIFT_RIGHT: startNextToken(); return makeToken(Tag.ASSIGN_SHIFT_RIGHT);
            case EQUALS: startNextToken(); return makeToken(Tag.EQUALS);
            case NOT_EQUALS: startNextToken(); return makeToken(Tag.NOT_EQUALS);
            case GREATER_EQUALS: startNextToken(); return makeToken(Tag.GREATER_EQUALS);
            case LESS_EQUALS: startNextToken(); return makeToken(Tag.LESS_EQUALS);

            case QUOTE: startNextToken(); return makeToken(Tag.QUOTE);
            case LEFT_PARENTHESIS: startNextToken(); return makeToken(Tag.LEFT_PARENTHESIS);
            case RIGHT_PARENTHESIS: startNextToken(); return makeToken(Tag.RIGHT_PARENTHESIS);
            case COMMA: startNextToken(); return makeToken(Tag.COMMA);
            case SEMICOLON: startNextToken(); return makeToken(Tag.SEMICOLON);
            case LEFT_BRACKET: startNextToken(); return makeToken(Tag.LEFT_BRACKET);
            case RIGHT_BRACKET: startNextToken(); return makeToken(Tag.RIGHT_BRACKET);
            case LEFT_BRACE: startNextToken(); return makeToken(Tag.LEFT_BRACE);
            case RIGHT_BRACE: startNextToken(); return makeToken(Tag.RIGHT_BRACE);

            case COMMENT:{
                if(peek == '*')
                    state = State.COMMENT_ASTERISK;
                return null;
            }
            case COMMENT_ASTERISK:{
                if(peek == '/')
                    state = State.COMMENT_END;
                else
                    state = State.COMMENT;
                return null;
            }
            case LINE_COMMENT:{
                if(peek == '\n')
                    state = State.COMMENT_END;
                return null;
            }
            case COMMENT_END: startNextToken(); return makeToken(Tag.FILLER);

            //return FILLER so the starting \" does not get added to the lexeme
            case STRING_START: state = State.STRING; return makeToken(Tag.FILLER);
            case STRING:{
                switch(peek){
                    case '\\': state = State.STRING_ESCAPE; return null;
                    case '\"': state = State.STRING_END; return null;
                    default: return null;
                }
            }
            case STRING_ESCAPE:{
                //No need to test code point length, since the last character will always be '\\'
                lexeme.deleteCharAt(lexeme.length() - 1);   //delete \ from lexeme
                peek = escapeCodePoint(peek);
                state = State.STRING;
                return null;
            }
            case STRING_END:{
                //Last character will always be \"
                lexeme.deleteCharAt(lexeme.length() - 1);
                startNextToken();
                String str = lexeme.toString();
                return makeLiteral(new PhiString(str));
            }
            case SYMBOL:{
                if(!isValidSymbolPart(peek)) {
                    startNextToken();
                    return makeToken(Tag.SYMBOL);
                }else
                    return null;
            }
            case ZERO:{
                switch(peek){
                    case 'x': case 'X': base = BASE_HEX; state = State.INT_LITERAL; return null;
                    case 'b': case 'B': base = BASE_BINARY; state = State.INT_LITERAL; return null;
                    case '.': state = State.FLOAT_LITERAL; return null;
                    default: {
                        if(peek >= '0' && peek <= '9') {
                            base = BASE_OCTAL;
                            state = State.INT_LITERAL;
                            return null;
                        }else {
                            startNextToken();
                            return makeLiteral(new PhiInt(0));
                        }
                    }
                }
            }
            case INT_LITERAL: {
                if (isValidDigitInBase(peek, base)) {
                    return null;
                }else if(isValidExponentIndicator(peek, base)){
                    state = State.FLOAT_EXPONENT_SIGN;
                    return null;
                }else if(peek == '.') {
                    state = State.FLOAT_LITERAL;
                    return null;
                }else if(isValidSymbolPart(peek)){
                    state = State.ERROR;
                    return null;
                }else{
                    startNextToken();
                    PhiInt value = new PhiInt(Long.parseLong(lexeme.substring(baseIndicatorLength(base)), base));
                    return makeLiteral(value);
                }
            }
            case FLOAT_LITERAL:{
                if(isValidDigitInBase(peek, base)){
                    return null;
                }else if(isValidExponentIndicator(peek, base)){
                    state = State.FLOAT_EXPONENT_SIGN;
                    return null;
                }else if(isValidSymbolPart(peek)) {
                    state = State.ERROR;
                    return null;
                }else{
                    startNextToken();
                    return makeLiteral(new PhiFloat(parseDoubleExtended(lexeme.toString(), base)));
                }
            }
            case FLOAT_EXPONENT_SIGN:{
                if(peek == '-') {
                    state = State.FLOAT_EXPONENT;
                }else if(isValidDigitInBase(peek, base)){
                    state = State.FLOAT_EXPONENT;
                }else {
                    state = State.ERROR;
                }
                return null;
            }
            case FLOAT_EXPONENT:{
                if(isValidDigitInBase(peek, base)){
                    return null;
                }else if(isValidSymbolPart(peek)) {
                    state = State.ERROR;
                    return null;
                }else{
                    startNextToken();
                    return makeLiteral(new PhiFloat(parseDoubleExtended(lexeme.toString(), base)));
                }
            }
            case ERROR:{
                if(!isValidSymbolPart(peek)){
                    startNextToken();
                    return makeToken(Tag.ERROR);
                }else
                    return null;
            }
            case FILLER: {
                if (Character.isWhitespace(peek))
                    return null;
                else {
                    startNextToken();
                    return makeToken(Tag.FILLER);
                }
            }
            default:
                return null;
        }
    }

    /**
     * Returns the next token from the input stream.
     * @return The next token.
     * @throws IOException If something went wrong reading the input.
     */
    public Token nextToken() throws IOException, PhiSyntaxException {
        Token token;
        do{
            peek = reader.read();
            token = processCharacter();
            if(token != null) {
                lexeme = new StringBuilder();
                tokenStartCol = col;
            }
            if(peek > 0)
                lexeme.appendCodePoint(peek);

            col++;
            if(peek == '\n') {
                col = 0;
                line++;
            }
        }while(token == null || token.tag == Tag.FILLER);
        if(token.tag == Tag.ERROR)
            throw new PhiSyntaxException("Invalid token: " + token.lexeme, token.line, token.col);
        return token;
    }
}
