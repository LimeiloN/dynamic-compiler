### dyncompiler
A java library that is able of compiling code at runtime.

#### What can it do
Compile, run and eval code on the fly. It is designed to be used in
parallel with [JavaPoet](https://github.com/square/javapoet). Note that
JavaPoet is exported to your classpath when you use this library.

#### Example
A quick example (taken from tests)
````java
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
````

#### Security Note
Letting the user enter code is always a bad idea.

#### Get it
Available on JitPack :
````groovy
implementation "com.github.LimeiloN:dyncompiler:2.0.0"
````

#### Credits
This project is heavily inspired of [https://github.com/raulgomis/dynamic-java-compiler] (© Raúl Gomis) and 
[https://github.com/michaelliao/compiler] (© Michael Liao).