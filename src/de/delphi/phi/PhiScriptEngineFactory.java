package de.delphi.phi;

import de.delphi.phi.data.PhiCollection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.List;

public class PhiScriptEngineFactory implements ScriptEngineFactory {

    public static String NAME = "Phi Embeddable Interpreter";
    private static final int VERSION_MAJOR = 0;
    private static final int VERSION_MINOR = 1;
    private static final String VERSION_NAME = "alpha";

    public static String VERSION = VERSION_NAME + " " + VERSION_MAJOR + "." + VERSION_MINOR;

    public static String PHI_VERSION = "1.0";

    private PhiCollection globalScope;

    public PhiScriptEngineFactory(){
        globalScope = new PhiCollection();
    }

    @Override
    public String getEngineName() {
        return NAME + " " + VERSION;
    }

    @Override
    public String getEngineVersion() {
        return VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return null;
    }

    @Override
    public List<String> getMimeTypes() {
        return null;
    }

    @Override
    public List<String> getNames() {
        return null;
    }

    @Override
    public String getLanguageName() {
        return "Phi";
    }

    @Override
    public String getLanguageVersion() {
        return PHI_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        return null;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return null;
    }

    @Override
    public String getProgram(String... statements) {
        return String.join(System.lineSeparator(), statements);
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new PhiScriptEngine();
    }
}
