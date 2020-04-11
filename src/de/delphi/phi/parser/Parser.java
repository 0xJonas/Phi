package de.delphi.phi.parser;

import de.delphi.phi.PhiSyntaxException;
import de.delphi.phi.parser.ast.Expression;
import de.delphi.phi.parser.ast.ExpressionList;

import java.io.IOException;
import java.io.Reader;
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
        Expression expr = null;
        switch(peek.tag){
            case LAMBDA: expr = lambdaExpr(); break;
            case IF: expr = ifExpr(); break;
        }

        while(peek.tag == Tag.SEMICOLON)
            consume();
        return expr;
    }

    private Expression lambdaExpr(){

        return null;
    }

    private Expression ifExpr(){

        return null;
    }

    private Expression whileExpr(){

        return null;
    }

    private Expression forExpr(){

        return null;
    }

    private Expression compoundExpr(){

        return null;
    }

    private Expression breakExpr(){

        return null;
    }

    private Expression continueExpr(){

        return null;
    }

    private Expression returnExpr(){

        return null;
    }

    private Expression varExpr(){

        return null;
    }

    private Expression functionExpr(){

        return null;
    }

    private Expression collectionExpr(){

        return null;
    }

    private Expression assignExpr(){

        return null;
    }

    private ExpressionList declList(){

        return null;
    }

    private void declaration(List<Expression> names, List<Expression> values){

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
