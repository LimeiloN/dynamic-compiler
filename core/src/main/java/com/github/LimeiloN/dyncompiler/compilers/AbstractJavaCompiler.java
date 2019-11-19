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

import com.github.LimeiloN.dyncompiler.problems.CompilationProblemHandler;
import com.github.LimeiloN.dyncompiler.readers.ResourceReader;
import com.github.LimeiloN.dyncompiler.stores.ResourceStore;

/**
 * Base class for compiler implementations. Provides just a few
 * convenience methods.
 *
 * @author tcurdt
 */
public abstract class AbstractJavaCompiler implements JavaCompiler {

    protected CompilationProblemHandler problemHandler;

    public void setCompilationProblemHandler(final CompilationProblemHandler pHandler) {
        problemHandler = pHandler;
    }

    public CompilationResult compile(final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        return compile(pClazzNames, pReader, pStore, classLoader, createDefaultSettings());
    }

    public CompilationResult compile(final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader) {
        return compile(pClazzNames, pReader, pStore, pClassLoader, createDefaultSettings());
    }

}
