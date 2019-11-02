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

package com.github.LimeiloN.dyncompiler.core.problems;

/**
 * An abstract definition of a compilation problem
 *
 * @author tcurdt
 */
public interface CompilationProblem {

    /**
     * is the problem an error and compilation cannot continue
     * or just a warning and compilation can proceed
     *
     * @return true if the problem is an error
     */
    boolean isError();

    /**
     * name of the file where the problem occurred
     *
     * @return name of the file where the problem occurred
     */
    String getFileName();

    /**
     * position of where the problem starts in the source code
     *
     * @return position of where the problem starts in the source code
     */
    int getStartLine();

    int getStartColumn();

    /**
     * position of where the problem stops in the source code
     *
     * @return position of where the problem stops in the source code
     */
    int getEndLine();

    int getEndColumn();

    /**
     * the description of the problem
     *
     * @return the description of the problem
     */
    String getMessage();

}