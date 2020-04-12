package de.delphi.phi.parser;

import de.delphi.phi.PhiSyntaxException;
import de.delphi.phi.data.PhiSymbol;
import de.delphi.phi.parser.ast.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Lexer lexer;

    private Token peek;

    private StringBuilder errorLog;

    public Parser(Reader in){
        lexer = new Lexer();
        lexer.setInput(in);
    }

    private void consume() throws IOException{
        try {
            peek = lexer.nextToken();
        }catch(PhiSyntaxException e){
            errorLog.append(e.getMessage());
            errorLog.append('\n');
        }
    }

    private void expect(Tag tag, String message) throws IOException {
        if(peek.tag == tag)
            consume();
        else {
            errorLog.append(new PhiSyntaxException(message, peek.line, peek.col).getMessage());
            errorLog.append('\n');
        }
    }

    private Expression expression() throws IOException{
        Expression expr;
        switch(peek.tag){
            case LAMBDA: expr = lambdaExpr(); break;
            case IF: expr = ifExpr(); break;
            case WHILE: expr = whileExpr(); break;
            case FOR: expr = forExpr(); break;
            case LEFT_BRACE: expr = compoundExpr(); break;
            case BREAK: expr = breakExpr(); break;
            case CONTINUE: expr = continueExpr(); break;
            case RETURN: expr = returnExpr(); break;
            case VAR: expr = varExpr(); break;
            case FUNCTION: expr = functionExpr(); break;
            case LEFT_BRACKET: expr = collectionExpr(); break;
            default: expr = assignExpr(); break;
        }

        while(peek.tag == Tag.SEMICOLON)
            consume();
        return expr;
    }

    private Expression lambdaExpr() throws IOException{
        consume();
        expect(Tag.LEFT_PARENTHESIS, "( expected.");
        ExpressionList params = declList(Tag.RIGHT_PARENTHESIS);
        expect(Tag.RIGHT_PARENTHESIS, ") expected.");
        expect(Tag.ARROW, "-> expected.");  //Maybe not?
        Expression body = expression();
        return new FunctionDefinitionExpr(params, new FunctionBody(body));
    }

    private Expression ifExpr() throws IOException{
        consume();
        Expression condition = expression();
        if(peek.tag == Tag.THEN)
            consume();
        Expression trueExpr = expression(), falseExpr = null;
        if(peek.tag == Tag.ELSE){
            consume();
            falseExpr = expression();
        }
        return new IfExpr(condition, trueExpr, falseExpr);
    }

    private Expression whileExpr() throws IOException{
        consume();
        Expression condition = expression();
        if(peek.tag == Tag.DO)
            consume();
        Expression body = expression();

        return new WhileExpr(condition, body);
    }

    private Expression forExpr() throws IOException{
        consume();
        Expression init = expression();
        Expression condition = expression();
        Expression iteration = expression();
        Expression body = expression();
        return new ForExpr(init, condition, iteration, body);
    }

    private Expression compoundExpr() throws IOException{
        consume();
        ArrayList<Expression> expressions = new ArrayList<>();
        do{
            expressions.add(expression());
        }while(peek.tag != Tag.RIGHT_BRACE);
        return new CompoundExpr(expressions);
    }

    private Expression breakExpr() throws IOException{
        consume();
        Expression returnExpr = null;
        if(peek.tag == Tag.LEFT_PARENTHESIS){
            consume();
            if(peek.tag == Tag.RIGHT_PARENTHESIS){
                consume();
            }else{
                returnExpr = expression();
                expect(Tag.RIGHT_PARENTHESIS, ") expected.");
            }
        }
        return new BreakExpr(returnExpr);
    }

    private Expression continueExpr() throws IOException{
        consume();
        Expression returnExpr = null;
        if(peek.tag == Tag.LEFT_PARENTHESIS){
            consume();
            if(peek.tag == Tag.RIGHT_PARENTHESIS){
                consume();
            }else{
                returnExpr = expression();
                expect(Tag.RIGHT_PARENTHESIS, ") expected.");
            }
        }
        return new ContinueExpr(returnExpr);
    }

    private Expression returnExpr() throws IOException{
        consume();
        Expression retVal = expression();
        return new ReturnExpr(retVal);
    }

    private Expression varExpr() throws IOException{
        consume();
        ArrayList<Expression> names = new ArrayList<>();
        ArrayList<Expression> values = new ArrayList<>();

        do{
            varDecl(names, values);
            if(peek.tag == Tag.COMMA)
                consume();
            else
                break;
        }while(true);

        return new VariableDeclarationExpr(new ExpressionList(names, values));
    }

    private Expression functionExpr() throws IOException{
        ArrayList<Expression> names = new ArrayList<>();
        ArrayList<Expression> values = new ArrayList<>();
        functionDecl(names, values);
        return new VariableDeclarationExpr(new ExpressionList(names, values));
    }

    private Expression collectionExpr() throws IOException{
        consume();
        ExpressionList content = declList(Tag.RIGHT_BRACKET);
        expect(Tag.RIGHT_BRACKET, "] expected.");
        return new CollectionDefinitionExpr(content);
    }

    private Expression assignExpr() throws IOException{
        Expression left = orExpr();
        //TODO implement
        return null;
    }

    private ExpressionList declList(Tag closingTag) throws IOException{
        ArrayList<Expression> names = new ArrayList<>();
        ArrayList<Expression> values = new ArrayList<>();
        while(peek.tag != closingTag){
            if(peek.tag == Tag.FUNCTION)
                functionDecl(names, values);
            else
                varDecl(names, values);
        }
        consume();
        return new ExpressionList(names, values);
    }

    private void varDecl(List<Expression> names, List<Expression> values) throws IOException{
        names.add(orExpr());
        if(peek.tag == Tag.ASSIGN){
            consume();
            values.add(orExpr());
        }else{
            values.add(null);
        }
    }

    private void functionDecl(List<Expression> names, List<Expression> values) throws IOException {
        consume();
        names.add(expression());
        expect(Tag.LEFT_PARENTHESIS, "( expected.");
        ExpressionList params = declList(Tag.RIGHT_PARENTHESIS);
        expect(Tag.RIGHT_PARENTHESIS, ") expected");
        Expression body = expression();
        values.add(new FunctionDefinitionExpr(params, new FunctionBody(body)));
    }

    private Expression orExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        do {
            children.add(xorExpr());
            if(peek.tag != Tag.OR)
                break;
            consume();
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new OrExpr(children);
    }

    private Expression xorExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        do {
            children.add(andExpr());
            if(peek.tag != Tag.XOR)
                break;
            consume();
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new XorExpr(children);
    }

    public Expression andExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        do {
            children.add(relExpr());
            if(peek.tag != Tag.AND)
                break;
            consume();
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new AndExpr(children);
    }

    public Expression relExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        ArrayList<Integer> operators = new ArrayList<>();
        operators.add(RelationalExpr.OP_EQUALS);

        outer: do {
            children.add(shiftExpr());
            switch(peek.tag){
                case LESS_THAN: operators.add(RelationalExpr.OP_LESS_THAN); consume(); break;
                case LESS_EQUALS: operators.add(RelationalExpr.OP_LESS_EQUALS); consume(); break;
                case EQUALS: operators.add(RelationalExpr.OP_EQUALS); consume(); break;
                case NOT_EQUALS: operators.add(RelationalExpr.OP_NOT_EQUALS); consume(); break;
                case GREATER_EQUALS: operators.add(RelationalExpr.OP_GREATER_EQUALS); consume(); break;
                case GREATER_THAN: operators.add(RelationalExpr.OP_GREATER_THAN); consume(); break;
                default: break outer;
            }
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new RelationalExpr(children, operators);
    }

    public Expression shiftExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        ArrayList<Integer> operators = new ArrayList<>();
        operators.add(ShiftExpr.OP_SHIFT_LEFT);

        outer: do {
            children.add(addExpr());
            switch(peek.tag){
                case SHIFT_LEFT: operators.add(ShiftExpr.OP_SHIFT_LEFT); consume(); break;
                case SHIFT_RIGHT: operators.add(ShiftExpr.OP_SHIFT_RIGHT); consume(); break;
                default: break outer;
            }
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new ShiftExpr(children, operators);
    }

    public Expression addExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        ArrayList<Integer> operators = new ArrayList<>();
        operators.add(AddExpr.OP_ADD);

        outer: do {
            children.add(mulExpr());
            switch(peek.tag){
                case ADD: operators.add(AddExpr.OP_ADD); consume(); break;
                case SUB: operators.add(AddExpr.OP_SUB); consume(); break;
                default: break outer;
            }
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new AddExpr(children, operators);
    }

    public Expression mulExpr() throws IOException{
        ArrayList<Expression> children = new ArrayList<>();
        ArrayList<Integer> operators = new ArrayList<>();
        operators.add(MulExpr.OP_MUL);

        outer: do {
            children.add(unaryExpr());
            switch(peek.tag){
                case MUL: operators.add(MulExpr.OP_MUL); consume(); break;
                case DIV: operators.add(MulExpr.OP_DIV); consume(); break;
                case MOD: operators.add(MulExpr.OP_MOD); consume(); break;
                default: break outer;
            }
        }while(true);
        if(children.size() == 1)
            return children.get(0);
        else
            return new MulExpr(children, operators);
    }

    public Expression unaryExpr() throws IOException{
        switch(peek.tag){
            case SUB: consume(); return new NegationExpr(postfixExpr());
            case NOT: consume(); return new NotExpr(postfixExpr());
            case NEW: consume(); return new NewExpr(postfixExpr());
            default: return postfixExpr();
        }
    }

    public Expression postfixExpr() throws IOException{
        Expression name = quoteExpr();
        outer: do {
            switch (peek.tag) {
                case LEFT_BRACKET:
                    consume();
                    Expression index = expression();
                    expect(Tag.RIGHT_BRACKET, "] expected.");
                    name = new SubscriptExpr(name, index);
                    break;
                case PERIOD:
                    consume();
                    Expression member = atom();
                    name = new MemberAccessExpr(name, member);
                    break;
                case LEFT_PARENTHESIS:
                    consume();
                    ExpressionList params = declList(Tag.RIGHT_PARENTHESIS);
                    name = new FunctionCallExpr(name, params);
                default: break outer;
            }
        }while(true);
        return name;
    }

    public Expression quoteExpr() throws IOException{
        if(peek.tag == Tag.QUOTE){
            consume();
            return new QuoteExpr(atom());
        }else
            return atom();
    }

    public Expression atom() throws IOException{
        do{
            switch (peek.tag) {
                case LITERAL:
                    Atom atom = new Atom(((Literal) peek).content);
                    consume();
                    return atom;
                case SYMBOL:
                    PhiSymbol symbol = new PhiSymbol(peek.lexeme);
                    consume();
                    return new Atom(symbol);
                case LEFT_PARENTHESIS:
                    Expression subExpr = expression();
                    expect(Tag.RIGHT_PARENTHESIS, ") expected.");
                    return subExpr;
                default:
                    errorLog.append(new PhiSyntaxException("Unexpected token " + peek.lexeme, peek.line, peek.col));
                    consume();
                    break;
            }
        }while(true);
    }

    public boolean eoi(){
        return peek.tag == Tag.EOI;
    }

    public Expression nextExpression() throws PhiSyntaxException, IOException {
        errorLog = new StringBuilder();
        Expression expr = expression();
        if(errorLog.length() > 0)
            throw new PhiSyntaxException(errorLog.toString());
        return expr;
    }
}
