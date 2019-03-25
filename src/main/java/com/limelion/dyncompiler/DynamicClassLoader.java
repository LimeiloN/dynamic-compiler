package com.limelion.dyncompiler;

import java.util.HashMap;
import java.util.Map;

public class DynamicClassLoader extends ClassLoader {

    private Map<String, CompiledObject> compiledObjs;

    public DynamicClassLoader(ClassLoader classLoader) {

        super(classLoader);
        this.compiledObjs = new HashMap<>();
    }

    public void addClass(CompiledObject cdata) {

        this.compiledObjs.put(cdata.name, cdata);
    }

    public CompiledObject getObject(String qname) {

        return compiledObjs.get(qname);
    }

    public Map<String, CompiledObject> getObjects() {

        return new HashMap<>(compiledObjs);
    }

    public Map<String, Class<?>> loadAll() {

        Map<String, Class<?>> classes = new HashMap<>(compiledObjs.size());

        try {
            for (CompiledObject co : getObjects().values()) {

                classes.put(co.name, loadClass(co.name));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return classes;
    }

    @Override
    public Class<?> findClass(String qname) throws ClassNotFoundException {

        //System.out.println("[DynamicClassLoader::findClass] " + qname);
        CompiledObject compiled = compiledObjs.get(qname);

        if (compiled == null) {
            return super.findClass(qname);
        }

        compiledObjs.remove(qname);
        byte[] data = compiled.getBytes();
        return defineClass(qname, data, 0, data.length);
    }
}
