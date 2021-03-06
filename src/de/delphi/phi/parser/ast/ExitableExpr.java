package de.delphi.phi.parser.ast;

import de.delphi.phi.data.PhiNull;
import de.delphi.phi.data.PhiObject;

public abstract class ExitableExpr extends Expression{

    protected boolean shouldExit = false;

    protected PhiObject returnValue = PhiNull.NULL;

    void exit(PhiObject value){
        shouldExit = true;
        returnValue = value;
    }
}
