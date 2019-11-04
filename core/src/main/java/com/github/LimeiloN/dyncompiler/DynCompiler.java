/*
 * MIT License
 *
 * Copyright (c) 2019 Guillaume "LimeiloN" Anthouard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.LimeiloN.dyncompiler;

import com.github.LimeiloN.dyncompiler.internal.Utils;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The main class of the library. Use it to compile, runScript or evalExp code at runtime.
 *
 * <b>Note :</b> The constructor will throw an error if the current platform doesn't provide a compiler.
 */
public class DynCompiler {

    private JavaCompiler compiler;
    private FileManager fileManager;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private DynamicClassLoader dcl;

    /**
     * Instantiate a new DynamicCompiler.
     *
     * @throws CompilerException thrown we're unable to reach the system default compiler.
     */
    public DynCompiler() throws CompilerException {


        this.compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            // Unable to get a compiler instance (maybe we're on a JRE)
            throw new CompilerException("Unable to instantiate default system compiler. (Maybe we're on a JRE ?)");
        }

        this.diagnostics = new DiagnosticCollector<>();
        this.dcl = new DynamicClassLoader(getClass().getClassLoader());
        this.fileManager = new FileManager(compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8), dcl);
    }

    /**
     * Compile the provided sources and load them into classpath.
     *
     * @param sources a map containing the qualified class names associated with their sources.
     * @return a map containing the same qualified class names associated with the loaded classes in memory.
     * @throws CompilerException if an error is thrown during compilation.
     */
    public Map<String, Class<?>> compileAndLoad(Map<ClassName, String> sources) throws CompilerException {

        return dcl.loadAll(compile(sources).keySet());
    }

    /**
     * Compile and load the specified class source.
     *
     * @param cname  the qualified className of the class to compile
     * @param source the source code
     * @return the loaded Class
     * @throws CompilerException if there was an exception compiling the source
     */
    public Class<?> compileAndLoad(ClassName cname, String source) throws CompilerException {

        compile(cname, source);

        try {
            return dcl.loadClass(Utils.getCanonicalName(cname));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Compile the specified sources.
     *
     * @param sources a Map containing the classes and their qualified className.
     * @return a Map containing the compiled classes and their className.
     * @throws CompilerException if an error is thrown during compilation
     */
    public synchronized Map<String, CompiledObject> compile(Map<ClassName, String> sources) throws CompilerException {

        List<SourceObject> sourceObjs = new ArrayList<>(sources.size());

        for (Map.Entry<ClassName, String> e : sources.entrySet())
            sourceObjs.add(new SourceObject(e.getKey(), e.getValue()));

        for (ClassName cname : sources.keySet())
            dcl.addClass(new CompiledObject(cname));

        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, sourceObjs);

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }
        return dcl.getObjects();
    }

    /**
     * Compile the specified class.
     *
     * @param name   the qualified (or canonical) class name
     * @param source the class source
     * @return the compiled class
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
     * @return
     * @throws CompilerException
     */
    public synchronized CompiledObject compile(ClassName name, JavaFileObject source) throws CompilerException {

        dcl.addClass(new CompiledObject(name));
        JavaCompiler.CompilationTask ctask = compiler.getTask(null, fileManager, diagnostics, null, null, Collections.singletonList(source));

        if (!ctask.call()) {
            throw new CompilerException("Compilation failed !", diagnostics.getDiagnostics());
        }

        return dcl.getObject(Utils.getCanonicalName(name));
    }

    public <T> T evalExp(String expr, Class<T> expectedType) throws CompilerException, InvocationTargetException {

        return evalExp(expr, new EvalContext<>(expectedType));
    }

    public <T> T evalExp(String expr, EvalContext<T> ctx) throws CompilerException, InvocationTargetException {

        return evalScript("return " + expr + ";", ctx);
    }

    public <T> T evalScript(String script, Class<T> returnType) throws CompilerException, InvocationTargetException {

        return evalScript(script, new EvalContext<>(returnType));
    }

    public <T> T evalScript(String script, EvalContext<T> ctx) throws CompilerException, InvocationTargetException {

        MethodSpec.Builder evalB = MethodSpec.methodBuilder("evalExp")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addStatement(script)
                .returns(ctx.getEvalType());
        for (Map.Entry<String, TypeName> param : ctx.getParams().entrySet()) {
            evalB.addParameter(param.getValue(), param.getKey());
        }

        MethodSpec eval = evalB.build();

        TypeSpec dummy = TypeSpec.classBuilder("Dummy")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(eval)
                .build();

        JavaFile clazz = JavaFile.builder("", dummy)
                .build();

        //System.out.println("[DynamicCompiler::evalExp] evaluating : " + dummy.toString());

        try {
            return (T) compileAndLoad(ClassName.bestGuess("Dummy"), clazz.toString())
                    .getDeclaredMethod("evalExp")
                    .invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void runScript(String source) throws CompilerException, InvocationTargetException {

        MethodSpec run = MethodSpec.methodBuilder("runScript")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addCode(source)
                .build();

        TypeSpec dummy = TypeSpec.classBuilder("Dummy")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(run)
                .build();

        JavaFile clazz = JavaFile.builder("", dummy).build();

        try {
            compileAndLoad(ClassName.bestGuess("Dummy"), clazz.toString()).getDeclaredMethod("runScript").invoke(null);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Method compileMethod(MethodSpec signature) throws CompilerException {

        TypeSpec dummy = TypeSpec.classBuilder("Dummy")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(signature)
                .build();

        JavaFile clazz = JavaFile.builder("", dummy).build();

        try {
            return compileAndLoad(ClassName.bestGuess("Dummy"), clazz.toString()).getDeclaredMethod(signature.name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}