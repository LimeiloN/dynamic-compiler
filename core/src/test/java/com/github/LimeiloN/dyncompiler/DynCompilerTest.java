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

import com.squareup.javapoet.ClassName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DynCompilerTest {

    @Test
    public void testSingleFile() {

        String someCode = "package somepackage;" +
                          "public class SomeClass { " +
                          "    public static String runScript() {" +
                          "        return \"Hello, world !\";" +
                          "    }" +
                          "}";

        try {

            DynCompiler compiler = new DynCompiler();
            Class<?> someClass = compiler.compileAndLoad(ClassName.bestGuess("somepackage.SomeClass"), someCode);

            Method m = someClass.getDeclaredMethod("runScript");
            assert m.invoke(null).equals("Hello, world !");

            // So much exceptions to handle ... rip
        } catch (CompilerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testMultipleSources() {

        String someCode1name = "somepackage.SomeClass";
        String someCode1 = "package somepackage;" +
                           "public class SomeClass { " +
                           "    public static String runScript() {" +
                           "        return OtherClass.data;" +
                           "    }" +
                           "}";

        String someCode2name = "somepackage.OtherClass";
        String someCode2 = "package somepackage;" +
                           "public class OtherClass {" +
                           "    public static String data = \"Some data !\";" +
                           "}";

        Map<ClassName, String> sources = new HashMap<>(2);
        sources.put(ClassName.bestGuess(someCode1name), someCode1);
        sources.put(ClassName.bestGuess(someCode2name), someCode2);

        try {

            DynCompiler compiler = new DynCompiler();
            Map<String, Class<?>> compiled = compiler.compileAndLoad(sources);
            Class<?> someClass = compiled.get("somepackage.SomeClass");

            Method m = someClass.getDeclaredMethod("runScript");
            assert m.invoke(null).equals("Some data !");

            // So much exceptions to handle ... rip
        } catch (CompilerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testError() {

        String someCode = "package somepackage;" +
                          "public class SomeClass { " +
                          "    public static String runScript() {" +
                          "        // Missing ';'" +
                          "        return \"Hello, world !\"" +
                          "    }" +
                          "}";

        try {
            DynCompiler compiler = new DynCompiler();

            Assertions.assertThrows(CompilerException.class,
                                    () -> compiler.compileAndLoad(ClassName.bestGuess("somepackage.SomeClass"), someCode));

        } catch (CompilerException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testSimpleEval() {

        String source = "2 + 2";

        try {
            DynCompiler compiler = new DynCompiler();

            Assertions.assertEquals((int) compiler.evalExp(source, int.class), 4);
        } catch (CompilerException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testComplexEval() {

        String source = "3 * x + b";

        try {
            DynCompiler compiler = new DynCompiler();

            Assertions.assertEquals((double) (3 * 2 + 2),
                                    compiler.evalExp(source,
                                                     new EvalContext<>(Double.TYPE)
                                                         .addParam("x", ClassName.DOUBLE)
                                                         .addParam("b", ClassName.DOUBLE)),
                                    2.0,
                                    2.0));
        } catch (CompilerException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

    /*
    @Test
    public void testCompilerception() {

        String source = "import com.limelion.dyncompiler.*;" +
                        "import java.lang.reflect.InvocationTargetException;" +
                        "public class MyClass {" +
                        "public static void runScript() {" +
                        "    try {" +
                        "        DynamicCompiler compiler = new DynamicCompiler();" +
                        "        assert (Boolean) compiler.evalExp(\"1 == 1\");" +
                        "    } catch (CompilerException | InvocationTargetException e) {" +
                        "        e.printStackTrace();" +
                        "    }" +
                        "}}";

        try {
            DynamicCompiler compiler = new DynamicCompiler();
            compiler.compileAndRun("MyClass", "runScript", source);
        } catch (CompilerException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Assertions.fail(e);
        }
    }

     */

    @Test
    public void testRun() {

        String source = "assert 1 == 0;";

        try {
            DynCompiler compiler = new DynCompiler();
            Assertions.assertThrows(InvocationTargetException.class, () -> compiler.runScript(source));
        } catch (CompilerException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testMultipleEval() {

        String source = "\"Why the fuck would I even do this ?\"";

        try {
            DynCompiler compiler = new DynCompiler();
            String s1 = compiler.evalExp(source, String.class);
            String s2 = compiler.evalExp(source, String.class);
            String s3 = compiler.evalExp(source, String.class);
            Assertions.assertEquals(s1, s2);
            Assertions.assertEquals(s1, s3);
        } catch (CompilerException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }
}
