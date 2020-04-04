package de.delphi.phi.data;

public enum Type {
    INT,
    FLOAT,
    STRING,
    FUNCTION,
    SYMBOL,
    COLLECTION,
    NULL;

    public boolean isNumeric(){
       return this == INT || this == FLOAT;
    }
}
