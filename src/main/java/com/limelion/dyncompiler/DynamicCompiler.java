package com.limelion.dyncompiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public Class<?> compile(String qname, String source) throws CompilerException {

        return compile(Collections.singletonMap(qname, source)).get(qname);
    }

    public <T> T eval(String source, EvalContext<T> ctx, Object... params) throws CompilerException, InvocationTargetException {

        StringBuilder dummy = new StringBuilder();

        for (Class<?> c : ctx.getImports()) {
            dummy.append("import ").append(c.getCanonicalName()).append(";");
        }

        dummy.append("public class Dummy { public static ").append(ctx.getReturnType().getCanonicalName()).append(" eval(");

        int cnt = 1;
        for (Map.Entry e : ctx.getParameters().entrySet()) {
            dummy.append(((Class<?>) e.getValue()).getCanonicalName()).append(" ").append(e.getKey());
            if (cnt != ctx.getParameters().size())
                dummy.append(", ");
            cnt++;
        }

        dummy.append(") { return ");
        dummy.append(source).append(";}}");

        System.out.println("[DynamicCompiler::eval] evaluating : " + dummy.toString());

        try {
            return (T) compile("Dummy", dummy.toString())
                .getDeclaredMethod("eval", ctx.getParameters().values().toArray(new Class<?>[0]))
                .invoke(null, params);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object eval(String source) throws CompilerException, InvocationTargetException {

        return eval(source, new EvalContext<>(Object.class));
    }

    public void run(String source) throws CompilerException, InvocationTargetException {

        StringBuilder dummy = new StringBuilder();
        dummy.append("public class Dummy { public static void run() { ");
        dummy.append(source).append("}}");

        try {
            compileAndRun("Dummy", "run", dummy.toString());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extremely unstable as we let the user write its own code. (ex: method could be private or not static)
     *
     * @param name
     *     the name of the enclosing class
     * @param methodName
     *     the name of the method to run
     * @param source
     *     the source code
     */
    public void compileAndRun(String name, String methodName, String source) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, CompilerException {

        compile(name, source).getDeclaredMethod(methodName).invoke(null);
    }
}
