package de.delphi.phi.parser;

import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.data.*;

import java.io.IOException;
import java.io.Reader;

class Lexer {

    private enum State {
        START,
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

        LEFT_PARENTHESIS, RIGHT_PARENTHESIS, COMMA, SEMICOLON,
        LEFT_BRACKET, RIGHT_BRACKET,
        LEFT_BRACE, RIGHT_BRACE,
        PERIOD, QUOTE,

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

    private StringBuilder lexeme;

    private int line, col, tokenStartCol;

    private int peek;

    private int base = BASE_DECIMAL;

    private State state = State.START;

    private Reader reader;

    public void setInput(Reader reader){
        this.reader = reader;
        lexeme = new StringBuilder();
        state = State.START;
    }

    private boolean isValidSymbolStart(int cp){
        return Character.isLetter(cp)
                || cp == '_'
                || cp == '$'
                || cp == '?';
    }

    private boolean isValidSymbolPart(int cp){
        return isValidSymbolStart(cp) || Character.isDigit(cp);
    }

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

    private Token matchReservedWord(int cp, State success){
        if(peek == cp){
            state = success;
            return null;
        }else if(isValidSymbolPart(peek)){
            state = State.SYMBOL;
            return null;
        }else{
            startNextToken();
            return makeToken(Tag.SYMBOL);
        }
    }

    private void startNextToken(){
        state = State.START;
        processCharacter();
    }

    private Token completeReservedWord(Token success){
        if(isValidSymbolPart(peek)){
            state = State.SYMBOL;
            return null;
        }else{
            startNextToken();
            return success;
        }
    }

    private Token matchAssignOperator(State success, Token op){
        if(peek == '='){
            state = success;
            return null;
        }else{
            startNextToken();
            return op;
        }
    }

    private boolean isValidDigitInBase(int digit, int base){
        switch(base){
            case BASE_BINARY: return digit >= '0' && digit <= '1';
            case BASE_OCTAL: return digit >= '0' && digit <= '7';
            case BASE_DECIMAL: return digit >= '0' && digit <= '9';
            case BASE_HEX: return (digit >= '0' && digit <= '9') || (digit >= 'a' && digit <= 'f');
        }
        return false;
    }

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

    private Token makeToken(Tag tag){
        return new Token(tag, lexeme.toString(), line, tokenStartCol);
    }

    private Literal makeLiteral(PhiObject value){
        return new Literal(lexeme.toString(), value, line, tokenStartCol);
    }

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

            case STRING_START: state = State.STRING; return makeToken(Tag.FILLER);   //Discard \" character
            case STRING:{
                switch(peek){
                    case '\\': state = State.STRING_ESCAPE; return null;
                    case '\"': state = State.STRING_END; return null;
                    default: return null;
                }
            }
            case STRING_ESCAPE:{
                //No need to test code point length, since the last character will always be '\\'
                lexeme.deleteCharAt(lexeme.length() - 1);
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
                throw new PhiRuntimeException("Unexpected state: " + state);
        }
    }

    public Token nextToken() throws IOException {
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
        return token;
    }
}
