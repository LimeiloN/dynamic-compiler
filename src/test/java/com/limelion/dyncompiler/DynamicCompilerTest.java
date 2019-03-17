package com.limelion.dyncompiler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

        Map<String, String> sources = new HashMap<>(1);
        sources.put("somepackage.SomeClass", someCode);

        try {

            DynamicCompiler compiler = new DynamicCompiler();
            Map<String, Class<?>> compiled = compiler.compile(sources);
            Class<?> someClass = compiled.get("somepackage.SomeClass");

            Method m = someClass.getDeclaredMethod("run");
            assert m.invoke(null).equals("Hello, world !");

            // So much exceptions to handle ... rip
        } catch (CompilerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleFiles() {

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
            Map<String, Class<?>> compiled = compiler.compile(sources);
            Class<?> someClass = compiled.get("somepackage.SomeClass");

            Method m = someClass.getDeclaredMethod("run");
            assert m.invoke(null).equals("Some data !");

            // So much exceptions to handle ... rip
        } catch (CompilerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
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

        Map<String, String> sources = new HashMap<>(1);
        sources.put("somepackage.SomeClass", someCode);

        try {
            DynamicCompiler compiler = new DynamicCompiler();

            Assertions.assertThrows(CompilerException.class, () -> compiler.compile(sources));

        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }

}
