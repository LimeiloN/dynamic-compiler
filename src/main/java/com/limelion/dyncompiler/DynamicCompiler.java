package com.limelion.dyncompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
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
 * The main class of the library. Use it to compile, run or eval code at runtime.
 *
 * <b>Note :</b> The constructor will throw an error if the current platform doesn't provide a compiler.
 */
public class DynamicCompiler {

    private JavaCompiler compiler;
    private FileManager fileManager;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private DynamicClassLoader cl;

    /**
     * Instantiate a new DynamicCompiler.
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
    public Map<String, Class<?>> compileAndLoad(Map<ClassName, String> sources) throws CompilerException {

        return cl.loadAll(compile(sources).keySet());
    }

    /**
     * Compile and load the specified class source.
     *
     * @param cname  the qualified className of the class to compile
     * @param source the source code
     *
     * @return the loaded Class
     *
     * @throws CompilerException if there was an exception compiling the source
     */
    public Class<?> compileAndLoad(ClassName cname, String source) throws CompilerException {

        compile(cname, source);

        try {
            return cl.loadClass(CompilerUtils.getCanonicalName(cname));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Compile the specified sources.
     *
     * @param sources a Map containing the classes and their qualified className.
     *
     * @return a Map containing the compiled classes and their className.
     *
     * @throws CompilerException if an error is thrown during compilation
     */
    public synchronized Map<String, CompiledObject> compile(Map<ClassName, String> sources) throws CompilerException {

        List<SourceObject> sourceObjs = new ArrayList<>(sources.size());

        for (Map.Entry<ClassName, String> e : sources.entrySet())
            sourceObjs.add(new SourceObject(e.getKey(), e.getValue()));

        for (ClassName cname : sources.keySet())
            cl.addClass(new CompiledObject(cname));

        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, sourceObjs);

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }
        return cl.getObjects();
    }

    /**
     * Compile the specified class.
     *
     * @param name   the qualified (or canonical) class name
     * @param source the class source
     *
     * @return the compiled class
     *
     * @throws CompilerException
     */
    public CompiledObject compile(ClassName name, String source) throws CompilerException {

        return compile(name, new SourceObject(name, source));
    }

    /**
     * Compiles a JavaFileObject to a compiled object
     *
     * @param name
     * @param source
     *
     * @return
     *
     * @throws CompilerException
     */
    public synchronized CompiledObject compile(ClassName name, JavaFileObject source) throws CompilerException {

        cl.addClass(new CompiledObject(name));
        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, Collections.singletonList(source));

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }

        return cl.getObject(CompilerUtils.getCanonicalName(name));
    }

    public <T> T eval(String source, EvalContext<T> ctx) throws CompilerException, InvocationTargetException {

        MethodSpec eval = MethodSpec.methodBuilder("eval")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                    .addCode(source)
                                    .returns(ctx.getEvalType())
                                    .build();

        TypeSpec dummy = TypeSpec.classBuilder("Dummy")
                                 .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                 .addMethod(eval)
                                 .build();

        JavaFile clazz = JavaFile.builder("", dummy)
                                 .build();

        //System.out.println("[DynamicCompiler::eval] evaluating : " + dummy.toString());

        try {
            return (T) compileAndLoad(ClassName.bestGuess("Dummy"), clazz.toString())
                .getDeclaredMethod("eval")
                .invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T eval(String source, Class<T> expectedType) throws CompilerException, InvocationTargetException {

        return eval(source, new EvalContext<>(expectedType));
    }

    public void run(String source) throws CompilerException, InvocationTargetException {

        MethodSpec run = MethodSpec.methodBuilder("run")
                                   .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                   .addCode(source)
                                   .build();

        TypeSpec dummy = TypeSpec.classBuilder("Dummy")
                                 .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                 .addMethod(run)
                                 .build();

        JavaFile clazz = JavaFile.builder("", dummy).build();

        try {
            compileAndLoad(ClassName.bestGuess("Dummy"), clazz.toString()).getDeclaredMethod("run").invoke(null);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}