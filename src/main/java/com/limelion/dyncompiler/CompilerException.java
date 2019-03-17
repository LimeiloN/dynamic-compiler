package com.limelion.dyncompiler;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;

public class CompilerException extends Exception {

    private List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompilerException(String message) {

        super(message);
    }

    public CompilerException(String message, List<Diagnostic<? extends JavaFileObject>> diagnostics) {

        super(message);
        this.diagnostics = diagnostics;
    }

    public CompilerException(Throwable e, List<Diagnostic<? extends JavaFileObject>> diagnostics) {

        super(e);
        this.diagnostics = diagnostics;
    }

    public String getDiagnosticsError() {

        StringBuilder sb = new StringBuilder();
        if (diagnostics != null) {
            diagnostics.forEach(diagnostic -> sb.append(String.format("Error on line %d: %s\n",
                                                                      diagnostic.getLineNumber(),
                                                                      diagnostic.getMessage(DynamicCompiler.locale))));
        }
        return sb.toString();
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {

        return diagnostics;
    }

    @Override
    public String toString() {

        return getDiagnosticsError();
    }

}
