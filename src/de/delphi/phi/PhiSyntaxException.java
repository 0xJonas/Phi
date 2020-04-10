package de.delphi.phi;

public class PhiSyntaxException extends PhiException {

    public PhiSyntaxException(){

    }

    public PhiSyntaxException(String message){
        super(message);
    }

    public PhiSyntaxException(String message, int line, int col){
        super(message, line ,col);
    }
}
