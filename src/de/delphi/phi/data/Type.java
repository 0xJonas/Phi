package de.delphi.phi.data;

public enum Type {
    INT(0),
    FLOAT(1),
    STRING(2),
    FUNCTION(4),
    SYMBOL(3),
    COLLECTION(4),
    NULL(5);

    private int level;

    Type(int level){
        this.level = level;
    }

    public int compare(Type other){
        if(this == FUNCTION || this == COLLECTION || other == FUNCTION || other == COLLECTION)
            return 0;
        else
            return other.level - this.level;
    }

    public boolean isNumeric(){
       return this == INT || this == FLOAT;
    }

    public static Type coerceTypes(Type t1, Type t2){
        if(t1 == FUNCTION || t1 == COLLECTION || t2 == FUNCTION || t2 == COLLECTION)
            return NULL;
        return t1.compare(t2) > 0 ? t2 : t1;
    }
}
