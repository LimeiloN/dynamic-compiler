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

package com.github.LimeiloN.dyncompiler.compilers;

import com.github.LimeiloN.dyncompiler.internal.Utils;

import java.util.HashMap;
import java.util.Map;


/**
 * Creates JavaCompilers
 * <p>
 * TODO use META-INF discovery mechanism
 *
 * @author tcurdt
 */
public final class JavaCompilerFactory {

    /**
     * @deprecated will be remove after the next release, please create an instance yourself
     */
    @Deprecated
    private static final JavaCompilerFactory INSTANCE = new JavaCompilerFactory();

    private final Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

    /**
     * @deprecated will be remove after the next release, please create an instance yourself
     */
    @Deprecated
    public static JavaCompilerFactory getInstance() {
        return JavaCompilerFactory.INSTANCE;
    }

    /**
     * Tries to guess the class name by convention. So for compilers
     * following the naming convention
     * <p>
     * org.apache.commons.jci.compilers.SomeJavaCompiler
     * <p>
     * you can use the short-hands "some"/"Some"/"SOME". Otherwise
     * you have to provide the full class name. The compiler is
     * getting instanciated via (cached) reflection.
     *
     * @param pHint
     * @return JavaCompiler or null
     */
    public JavaCompiler createCompiler(final String pHint) {

        final String className;
        if (pHint.indexOf('.') < 0) {
            className = "org.apache.commons.jci.compilers." + Utils.toJavaCasing(pHint) + "JavaCompiler";
        } else {
            className = pHint;
        }

        Class<?> clazz = classCache.get(className);

        if (clazz == null) {
            try {
                clazz = Class.forName(className);
                classCache.put(className, clazz);
            } catch (ClassNotFoundException e) {
                clazz = null;
            }
        }

        if (clazz == null) {
            return null;
        }

        try {
            return (JavaCompiler) clazz.newInstance();
        } catch (Throwable t) {
            return null;
        }
    }

}
