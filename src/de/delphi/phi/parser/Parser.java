package de.delphi.phi.parser;

import de.delphi.phi.PhiSyntaxException;
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

    private Expression assignExpr(){
        Expression left = orExpr();

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

    private Expression orExpr(){

        return null;
    }

    private Expression xorExpr(){

        return null;
    }

    public Expression andExpr(){

        return null;
    }

    public Expression relExpr(){

        return null;
    }

    public Expression shiftExpr(){

        return null;
    }

    public Expression addExpr(){

        return null;
    }

    public Expression mulExpr(){

        return null;
    }

    public Expression unaryExpr(){

        return null;
    }

    public Expression postfixExpr(){

        return null;
    }

    public Expression quoteExpr(){

        return null;
    }

    public Expression atom(){

        return null;
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
