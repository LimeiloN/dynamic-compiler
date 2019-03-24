package com.limelion.dyncompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a function.
 *
 * @param <T>
 *     the return type of the function
 */
public class MethodSignature<T> {

    private Class<T> returnType;
    private Map<String, Class<?>> parameters;
    private List<Class<? extends Throwable>> throwables;
    private String name = null;

    public MethodSignature(String name, Class<T> returnType) {

        this(returnType);
        this.name = name;
    }

    public MethodSignature(Class<T> returnType) {

        this.returnType = returnType;
        this.parameters = new HashMap<>();
        this.throwables = new ArrayList<>();
    }

    public MethodSignature addParameter(String name, Class<?> clazz) {

        parameters.put(name, clazz);
        return this;
    }

    public MethodSignature addThrow(Class<? extends Throwable> clazz) {

        throwables.add(clazz);
        return this;
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

    public MethodSignature setReturnType(Class<T> returnType) {

        this.returnType = returnType;
        return this;
    }

    public String toString(String name) {

        StringBuilder sb = new StringBuilder();
        sb.append(returnType == Void.class ? "void" : returnType.getCanonicalName()).append(" ").append(name == null ? this.name : name).append("(");

        int cnt = 1;
        for (Map.Entry<String, Class<?>> e : parameters.entrySet()) {
            sb.append(e.getValue().getCanonicalName()).append(" ").append(e.getKey());
            if (cnt != parameters.size())
                sb.append(", ");
            cnt++;
        }

        return sb.append(")").toString();
    }
}
