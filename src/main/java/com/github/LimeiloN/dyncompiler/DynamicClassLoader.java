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
