package com.limelion.dyncompiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DynamicClassLoader extends ClassLoader {

    // Objects are indexed on their qualified (or canonical) name
    private Map<String, CompiledObject> compiledObjs;

    public DynamicClassLoader(ClassLoader classLoader) {

        super(classLoader);
        this.compiledObjs = new HashMap<>();
    }

    public void addClass(CompiledObject co) {

        this.compiledObjs.put(co.getCanonicalName(), co);
    }

    public CompiledObject getObject(String qname) {

        return compiledObjs.get(qname);
    }

    public Map<String, CompiledObject> getObjects() {

        return new HashMap<>(compiledObjs);
    }

    public Map<String, Class<?>> loadAll(Collection<String> names) {

        Map<String, Class<?>> classes = new HashMap<>(compiledObjs.size());
        CompiledObject co;

        try {
            for (String name : names) {

                if (compiledObjs.containsKey(name)) {
                    co = compiledObjs.get(name);
                    classes.put(co.getCanonicalName(), loadClass(co.getCanonicalName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return classes;
    }

    @Override
    public Class<?> findClass(String qname) throws ClassNotFoundException {

        System.out.println("[DynamicClassLoader::findClass] " + qname);
        CompiledObject compiled = compiledObjs.get(qname);

        if (compiled == null) {
            return super.findClass(qname);
        }

        compiledObjs.remove(qname);
        byte[] data = compiled.getBytes();
        return defineClass(qname, data, 0, data.length);
    }
}
