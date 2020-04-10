package de.delphi.phi;

public class PhiStructureException extends PhiRuntimeException {

    public PhiStructureException(){

    }

    public PhiStructureException(String message){
        super(message);
    }

    public PhiStructureException(String message, int line, int col){
        super("StructureException: " + message, line ,col);
    }
}
