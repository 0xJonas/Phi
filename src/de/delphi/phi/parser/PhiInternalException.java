package de.delphi.phi.parser;

public class PhiInternalException extends RuntimeException {

    public PhiInternalException(){

    }

    public PhiInternalException(String message){
        super(message);
    }

    public PhiInternalException(Throwable cause){
        super(cause);
    }

    public PhiInternalException(String message, Throwable cause){
        super(message, cause);
    }
}
