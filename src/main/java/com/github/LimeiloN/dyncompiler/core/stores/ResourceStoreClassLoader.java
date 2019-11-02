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
package com.github.LimeiloN.dyncompiler.core.stores;

import com.github.LimeiloN.dyncompiler.internal.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A ClassLoader backed by an array of ResourceStores
 *
 * @author tcurdt
 */
public final class ResourceStoreClassLoader extends ClassLoader {

    private static final Logger log = LogManager.getLogger(ResourceStoreClassLoader.class);

    private final ResourceStore[] stores;

    public ResourceStoreClassLoader(final ClassLoader pParent, final ResourceStore[] pStores) {
        super(pParent);

        stores = new ResourceStore[pStores.length];
        System.arraycopy(pStores, 0, stores, 0, stores.length);
    }

    private Class<?> fastFindClass(final String name) {

        if (stores != null) {
            for (ResourceStore store : stores) {
                final byte[] clazzBytes = store.read(Utils.convertClassToResourcePath(name));
                if (clazzBytes != null) {
                    log.debug(getId() + " found class: " + name + " (" + clazzBytes.length + " bytes)");
                    return defineClass(name, clazzBytes, 0, clazzBytes.length);
                }
            }
        }

        return null;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // log.debug(getId() + " looking for: " + name);
        Class<?> clazz = findLoadedClass(name);

        if (clazz == null) {
            clazz = fastFindClass(name);

            if (clazz == null) {

                final ClassLoader parent = getParent();
                if (parent != null) {
                    clazz = parent.loadClass(name);
                    // log.debug(getId() + " delegating loading to parent: " + name);
                } else {
                    throw new ClassNotFoundException(name);
                }

            } else {
                log.debug(getId() + " loaded from store: " + name);
            }
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        final Class<?> clazz = fastFindClass(name);
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    private String getId() {
        return "" + this + "[" + this.getClass().getClassLoader() + "]";
    }
}
