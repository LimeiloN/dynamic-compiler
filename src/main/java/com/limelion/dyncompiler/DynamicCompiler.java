package com.limelion.dyncompiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DynamicCompiler {

    static Locale locale;
    private JavaCompiler compiler;
    private FileManager fileManager;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private DynamicClassLoader cl;

    public DynamicCompiler() throws CompilerException {

        this(Locale.US);
    }

    public DynamicCompiler(Locale locale) throws CompilerException {

        this.compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            // Unable to get a compiler instance (maybe we're on a JRE)
            throw new CompilerException("Unable to instantiate default system compiler. (Maybe we're on a JRE ?)");
        }

        this.diagnostics = new DiagnosticCollector<>();
        this.cl = new DynamicClassLoader(getClass().getClassLoader());
        this.fileManager = new FileManager(compiler.getStandardFileManager(diagnostics, locale, StandardCharsets.UTF_8), cl);
        DynamicCompiler.locale = locale;
    }

    public synchronized Map<String, Class<?>> compile(Map<String, String> sources) throws CompilerException {

        List<SourceObject> sourceObjs = new ArrayList<>(sources.size());

        for (Map.Entry<String, String> e : sources.entrySet())
            sourceObjs.add(new SourceObject(CompilerUtils.splitQName(e.getKey())[1], e.getValue()));

        for (String s : sources.keySet())
            cl.addClass(new CompiledObject(s));

        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, sourceObjs);

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }

        return cl.loadAll();
    }

}
