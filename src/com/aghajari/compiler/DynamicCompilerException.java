package com.aghajari.compiler;

import com.aghajari.source.Pair;
import com.aghajari.source.Parser;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;

public final class DynamicCompilerException extends Exception {

    private List<Diagnostic<? extends JavaFileObject>> diagnostics;
    Parser.Output output;

    public DynamicCompilerException(String message) {
        super(message);
    }

    public DynamicCompilerException(String message, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super(message);
        this.diagnostics = diagnostics;
    }

    public DynamicCompilerException(Throwable e, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super(e);
        this.diagnostics = diagnostics;
    }


    public String getDiagnosticsError() {
        StringBuilder sb = new StringBuilder();
        if (diagnostics != null) {
            if (output != null) {
                for (Diagnostic<?> diagnostic : diagnostics) {
                    if (output.lines.containsKey((int) diagnostic.getLineNumber() - output.lineDelta)) {
                        Pair<Integer, String> pair = output.lines.get((int) diagnostic.getLineNumber() - output.lineDelta);
                        sb.append(String.format("Error on line %d:\n>  %s\n%s\n",
                                pair.a, pair.b,
                                diagnostic.getMessage(null)));
                    } else {
                        sb.append(String.format("Error on line %d: %s\n",
                                diagnostic.getLineNumber(),
                                diagnostic.getMessage(null)));
                    }
                }
                return sb.toString();
            }
            diagnostics.forEach(diagnostic -> sb.append(String.format("Error on line %d: %s\n",
                    diagnostic.getLineNumber(),
                    diagnostic.getMessage(null))));
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
