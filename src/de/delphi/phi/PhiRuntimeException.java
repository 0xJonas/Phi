package de.delphi.phi;

public class PhiRuntimeException extends PhiException {

    public PhiRuntimeException(){

    }

    public PhiRuntimeException(String message){
        super(message);
    }

    public PhiRuntimeException(String message, int line, int col){
        super(message, line ,col);
    }
}
