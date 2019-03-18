package com.limelion.dyncompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvalContext<T> {

    private List<Class<?>> imports;
    private Class<T> returnType;
    private Map<String, Class<?>> parameters;
    private List<Class<? extends Throwable>> throwables;

    public EvalContext(Class<T> returnType) {

        this.returnType = returnType;
        this.imports = new ArrayList<>();
        this.parameters = new HashMap<>();
        this.throwables = new ArrayList<>();
    }

    public EvalContext addImport(Class<?> clazz) {

        imports.add(clazz);
        return this;
    }

    public EvalContext addParameter(String name, Class<?> clazz) {

        parameters.put(name, clazz);
        return this;
    }

    public EvalContext addThrow(Class<? extends Throwable> clazz) {

        throwables.add(clazz);
        return this;
    }

    public List<Class<?>> getImports() {

        return imports;
    }

    public Map<String, Class<?>> getParameters() {

        return parameters;
    }

    public List<Class<? extends Throwable>> getThrowables() {

        return throwables;
    }

    public Class<T> getReturnType() {

        return returnType;
    }

    public EvalContext setReturnType(Class<T> returnType) {

        this.returnType = returnType;
        return this;
    }

}
