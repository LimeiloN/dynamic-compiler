package com.limelion.dyncompiler;

import java.util.ArrayList;
import java.util.List;

public class EvalContext<T> {

    private List<Class<?>> imports;
    private MethodSignature<T> signature;

    public EvalContext(MethodSignature<T> signature) {

        this.signature = signature;
        this.imports = new ArrayList<>();
    }

    public EvalContext<T> addImport(Class<?> toImport) {

        this.imports.add(toImport);
        return this;
    }

    public List<Class<?>> getImports() {

        return imports;
    }

    public MethodSignature<T> getSignature() {

        return signature;
    }
}
