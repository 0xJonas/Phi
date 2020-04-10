package de.delphi.phi;

public class PhiAccessException extends PhiRuntimeException {

    public PhiAccessException(){

    }

    public PhiAccessException(String message){
        super(message);
    }

    public PhiAccessException(String message, int line, int col){
        super("AccessException: " + message, line ,col);
    }
}
