package com.limelion.dyncompiler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamicCompilerTest {

    @Test
    public void testSingleFile() {

        String someCode = "package somepackage;" +
                          "public class SomeClass { " +
                          "    public static String run() {" +
                          "        return \"Hello, world !\";" +
                          "    }" +
                          "}";

        try {

            DynamicCompiler compiler = new DynamicCompiler();
            Map<String, Class<?>> compiled = compiler.compileAndLoad(Collections.singletonMap("somepackage.SomeClass", someCode));
            Class<?> someClass = compiled.get("somepackage.SomeClass");

            Method m = someClass.getDeclaredMethod("run");
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
                           "    public static String run() {" +
                           "        return OtherClass.data;" +
                           "    }" +
                           "}";

        String someCode2name = "somepackage.OtherClass";
        String someCode2 = "package somepackage;" +
                           "public class OtherClass {" +
                           "    public static String data = \"Some data !\";" +
                           "}";

        Map<String, String> sources = new HashMap<>(2);
        sources.put(someCode1name, someCode1);
        sources.put(someCode2name, someCode2);

        try {

            DynamicCompiler compiler = new DynamicCompiler();
            Map<String, Class<?>> compiled = compiler.compileAndLoad(sources);
            Class<?> someClass = compiled.get("somepackage.SomeClass");

            Method m = someClass.getDeclaredMethod("run");
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
                          "    public static String run() {" +
                          "        // Missing ';'" +
                          "        return \"Hello, world !\"" +
                          "    }" +
                          "}";

        try {
            DynamicCompiler compiler = new DynamicCompiler();

            Assertions.assertThrows(CompilerException.class, () -> compiler.compileAndLoad(Collections.singletonMap("somepackage.SomeClass", someCode)));

        } catch (CompilerException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testSimpleEval() {

        String source = "2 + 2";

        try {
            DynamicCompiler compiler = new DynamicCompiler();

            Assertions.assertEquals(compiler.eval(source), 4);
        } catch (CompilerException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testComplexEval() {

        String source = "3 * x + b";

        try {
            DynamicCompiler compiler = new DynamicCompiler();

            Assertions.assertEquals((double) (3 * 2 + 2),
                                    compiler.eval(source,
                                                  new EvalContext<>(new MethodSignature<>(Double.TYPE)
                                                                        .addParameter("x", Double.TYPE)
                                                                        .addParameter("b", Double.TYPE)),
                                                  2.0,
                                                  2.0));
        } catch (CompilerException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testCompilerception() {

        String source = "import com.limelion.dyncompiler.*;" +
                        "import java.lang.reflect.InvocationTargetException;" +
                        "public class MyClass {" +
                        "public static void run() {" +
                        "    try {" +
                        "        DynamicCompiler compiler = new DynamicCompiler();" +
                        "        assert (Boolean) compiler.eval(\"1 == 1\");" +
                        "    } catch (CompilerException | InvocationTargetException e) {" +
                        "        e.printStackTrace();" +
                        "    }" +
                        "}}";

        try {
            DynamicCompiler compiler = new DynamicCompiler();
            compiler.compileAndRun("MyClass", "run", source);
        } catch (CompilerException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testRun() {

        String source = "assert 1 == 0;";

        try {
            DynamicCompiler compiler = new DynamicCompiler();
            Assertions.assertThrows(InvocationTargetException.class, () -> compiler.run(source));
        } catch (CompilerException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testMultipleEval() {

        String source = "\"Why the fuck would I even do this ?\"";

        try {
            DynamicCompiler compiler = new DynamicCompiler();
            String s1 = (String) compiler.eval(source);
            String s2 = (String) compiler.eval(source);
            String s3 = (String) compiler.eval(source);
            Assertions.assertEquals(s1, s2);
            Assertions.assertEquals(s1, s3);
        } catch (CompilerException | InvocationTargetException e) {
            Assertions.fail(e);
        }
    }

}
