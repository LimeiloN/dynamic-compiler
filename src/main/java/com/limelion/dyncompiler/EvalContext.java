package com.limelion.dyncompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvalContext<T> {

    private List<Class<?>> imports;
    private Map<String, Object> variables;
    private Class<T> evalType;

    public EvalContext(Class<T> evalType) {

        this.imports = new ArrayList<>();
        this.variables = new HashMap<>();
        this.evalType = evalType;
    }

    public EvalContext<T> addImport(Class<?> toImport) {

        this.imports.add(toImport);
        return this;
    }

    public List<Class<?>> getImports() {

        return imports;
    }

    public Map<String, Object> getVariables() {

        return variables;
    }

    public Class<T> getEvalType() {

        return evalType;
    }
}
