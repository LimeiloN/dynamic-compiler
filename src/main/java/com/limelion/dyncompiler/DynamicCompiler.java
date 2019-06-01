package com.limelion.dyncompiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Where everything happens. Compile, load and run code.
 */
public class DynamicCompiler {

    private JavaCompiler compiler;
    private FileManager fileManager;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private DynamicClassLoader cl;

    /**
     * Instantiate a new DynamicCompiler, since it uses heavy tools like javac, it may be a good idea to reuse this
     * object instead of creating another.
     *
     * @throws CompilerException thrown we're unable to reach the system default compiler.
     */
    public DynamicCompiler() throws CompilerException {

        this.compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            // Unable to get a compiler instance (maybe we're on a JRE)
            throw new CompilerException("Unable to instantiate default system compiler. (Maybe we're on a JRE ?)");
        }

        this.diagnostics = new DiagnosticCollector<>();
        this.cl = new DynamicClassLoader(getClass().getClassLoader());
        this.fileManager = new FileManager(compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8), cl);
    }

    /**
     * Compile the provided sources and load them into classpath.
     *
     * @param sources a map containing the qualified class names associated with their sources.
     *
     * @return a map containing the same qualified class names associated with the loaded classes in memory.
     *
     * @throws CompilerException if an error is thrown during compilation.
     */
    public Map<String, Class<?>> compileAndLoad(Map<String, String> sources) throws CompilerException {

        compile(sources);
        return cl.loadAll();
    }

    /**
     * Compile and load the specified class source.
     *
     * @param qname  the qualified name of the class to compile
     * @param source the source code
     *
     * @return the loaded Class
     *
     * @throws CompilerException if there was an exception compiling the source
     */
    public Class<?> compileAndLoad(String qname, String source) throws CompilerException {

        compile(qname, source);

        try {
            return cl.loadClass(qname);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Compile the specified sources.
     *
     * @param sources a Map containing the classes and their qualified name.
     *
     * @return a Map containing the compiled classes and their name.
     *
     * @throws CompilerException if an error is thrown during compilation
     */
    public synchronized Map<String, CompiledObject> compile(Map<String, String> sources) throws CompilerException {

        List<SourceObject> sourceObjs = new ArrayList<>(sources.size());

        for (Map.Entry<String, String> e : sources.entrySet())
            sourceObjs.add(new SourceObject(CompilerUtils.getName(e.getKey()), e.getValue()));

        for (String s : sources.keySet())
            cl.addClass(new CompiledObject(s));

        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, sourceObjs);

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }
        return cl.getObjects();
    }

    /**
     * Compile the specified class.
     *
     * @param qname  the class name
     * @param source the class source
     *
     * @return the compiled class
     *
     * @throws CompilerException
     */
    public synchronized CompiledObject compile(String qname, String source) throws CompilerException {

        cl.addClass(new CompiledObject(qname));
        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, Collections.singletonList(new SourceObject(CompilerUtils.getName(qname), source)));

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }

        return cl.getObject(qname);
    }

    public <T> T eval(String source, EvalContext<T> ctx, Object... params) throws CompilerException, InvocationTargetException {

        StringBuilder dummy = new StringBuilder();

        for (Class<?> c : ctx.getImports()) {
            dummy.append("import ").append(c.getCanonicalName()).append(";");
        }

        dummy.append("public class Dummy {\n public static ").append(ctx.getSignature().toString("eval"));

        dummy.append(" { return ");
        dummy.append(source).append(";}}");

        //System.out.println("[DynamicCompiler::eval] evaluating : " + dummy.toString());

        try {
            return (T) compileAndLoad("Dummy", dummy.toString())
                .getDeclaredMethod("eval", ctx.getSignature().getParameters().values().toArray(new Class<?>[0]))
                .invoke(null, params);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object eval(String source) throws CompilerException, InvocationTargetException {

        return eval(source, new EvalContext<>(new MethodSignature<>(Object.class)));
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
     * @param name       the name of the enclosing class
     * @param methodName the name of the method to run
     * @param source     the source code
     */
    public void compileAndRun(String name, String methodName, String source) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, CompilerException {

        compileAndLoad(name, source).getDeclaredMethod(methodName).invoke(null);
    }
}