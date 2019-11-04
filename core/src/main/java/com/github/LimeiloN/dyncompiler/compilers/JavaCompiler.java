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
 * The general compiler interface. All compilers implementing
 * this interface should read the resources from the reader
 * and store the java class files into the ResourceStore.
 * <p>
 * The actual compilation language does not matter. But the
 * contract is that the result of the compilation will be a
 * class file.
 * <p>
 * If possible the compiler should notify the optional
 * CompilationProblemHandler as soon as a problem is found.
 *
 * @author tcurdt
 */
public interface JavaCompiler {

    /**
     * Set the the handler that gets the notification of an error
     * or warning as soon as this information is available from
     * the compiler.
     * Note: Some compilers might not support this feature.
     *
     * @param pHandler
     */
    void setCompilationProblemHandler(final CompilationProblemHandler pHandler);

    /**
     * factory method to create the underlying default settings
     */
    JavaCompilerSettings createDefaultSettings();

    /**
     * uses the default compiler settings and the current classloader
     */
    CompilationResult compile(final String[] pResourcePaths, final ResourceReader pReader, final ResourceStore pStore);

    /**
     * uses the default compiler settings
     */
    CompilationResult compile(final String[] pResourcePaths, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader);

    /**
     * Compiles the java resources "some/path/to/MyJava.java"
     * read through the ResourceReader and then stores the resulting
     * classes in the ResourceStore under "some/path/to/MyJava.class".
     * Note: As these are resource path you always have to use "/"
     * <p>
     * The result of the compilation run including detailed error
     * information is returned as CompilationResult. If you need to
     * get notified already during the compilation process you can
     * register a CompilationProblemHandler.
     * Note: Not all compilers might support this notification mechanism.
     *
     * @param pResourcePaths
     * @param pReader
     * @param pStore
     * @param pClassLoader
     * @param pSettings
     * @return always a CompilationResult
     */
    CompilationResult compile(final String[] pResourcePaths, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader, final JavaCompilerSettings pSettings);

}
