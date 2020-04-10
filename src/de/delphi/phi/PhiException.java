package de.delphi.phi;

public class PhiException extends Exception {

    public PhiException(){

    }

    public PhiException(String message){
        super(message);
    }

    public PhiException(String message, int line, int col){
        super(String.format("Line %d, col %d: %s", line, col, message));
    }
}
