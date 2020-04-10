package de.delphi.phi;

public class PhiArgumentException extends PhiRuntimeException {

    public PhiArgumentException(){

    }

    public PhiArgumentException(String message){
        super(message);
    }

    public PhiArgumentException(String message, int line, int col){
        super("ArgumentException: " + message, line ,col);
    }
}
