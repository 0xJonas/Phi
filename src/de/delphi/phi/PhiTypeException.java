package de.delphi.phi;

public class PhiTypeException extends PhiRuntimeException {

    public PhiTypeException(){

    }

    public PhiTypeException(String message){
        super(message);
    }

    public PhiTypeException(String message, int line, int col){
        super("TypeException: " + message, line ,col);
    }
}
