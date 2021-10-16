package com.aghajari.compiler;

import com.aghajari.source.Parser;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.util.Arrays;

// COPYRIGHT : https://github.com/raulgomis/dynamic-java-compiler

public final class DynamicCompiler<T> {

    private JavaCompiler compiler;
    private DynamicExtendedFileManager dynamicExtendedFileManager;
    private DynamicClassLoader classLoader;

    private DiagnosticCollector<JavaFileObject> diagnostics;

    public DynamicCompiler() throws DynamicCompilerException {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new DynamicCompilerException("Compiler not found");
        }

        classLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        dynamicExtendedFileManager = new DynamicExtendedFileManager(standardFileManager, classLoader);
    }

    @SuppressWarnings("unchecked")
    public synchronized Class<T> compile(String packageName, String className, String javaSource, Parser.Output output) throws DynamicCompilerException {
        try {

            String qualifiedClassName = DynamicCompilerUtils.getQualifiedClassName(packageName, className);
            DynamicStringObject sourceObj = new DynamicStringObject(className, javaSource);

            dynamicExtendedFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName,
                                                       className + JavaFileObject.Kind.SOURCE.extension, sourceObj);

            CompilationTask task = compiler.getTask(null, dynamicExtendedFileManager, diagnostics, null, null, Arrays.asList(sourceObj));
            boolean result = task.call();

            if (!result) {
                DynamicCompilerException e = new DynamicCompilerException("Compilation failure", diagnostics.getDiagnostics());
                e.output = output;
                throw e;
            }

            dynamicExtendedFileManager.close();

            return (Class<T>) classLoader.loadClass(qualifiedClassName);

        } catch (Exception exception) {
            DynamicCompilerException e = new DynamicCompilerException(exception, diagnostics.getDiagnostics());
            e.output = output;
            throw e;
        }
    }
}
