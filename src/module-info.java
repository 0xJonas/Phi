module Phi {
    requires java.base;
    requires java.scripting;

    exports de.delphi.phi;
    exports de.delphi.phi.data;

    provides javax.script.ScriptEngineFactory with de.delphi.phi.PhiScriptEngineFactory;
}