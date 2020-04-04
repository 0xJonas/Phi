package de.delphi.phi;

import javax.script.*;
import java.io.Reader;

public class PhiScriptEngine extends AbstractScriptEngine {

    /*package*/ PhiScriptEngine(){

    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return null;
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return null;
    }

    @Override
    public Bindings createBindings() {
        return null;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return null;
    }
}
