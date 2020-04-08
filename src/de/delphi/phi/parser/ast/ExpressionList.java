package de.delphi.phi.parser.ast;

import java.util.List;

public class ExpressionList {

    private Expression[] names, values;

    public ExpressionList(List<Expression> names, List<Expression> values){
        this.names = names.toArray(new Expression[0]);
        this.values = values.toArray(new Expression[0]);
    }

    public Expression getName(int index){
        return names[index];
    }

    public Expression getValue(int index){
        return values[index];
    }

    public int length(){
        return names.length;
    }
}
