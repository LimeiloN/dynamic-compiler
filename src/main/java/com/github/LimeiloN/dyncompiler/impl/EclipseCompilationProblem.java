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

package com.github.LimeiloN.dyncompiler.impl;

import org.apache.commons.jci.problems.CompilationProblem;
import org.eclipse.jdt.core.compiler.IProblem;

/**
 * Wrapping an Eclipse compiler problem
 * 
 * @author tcurdt
 */
public final class EclipseCompilationProblem implements CompilationProblem {

    private final IProblem problem;

    public EclipseCompilationProblem(final IProblem pProblem) {
        problem = pProblem;
    }

    public boolean isError() {
        return problem.isError();
    }

    public String getFileName() {
        return new String(problem.getOriginatingFileName());
    }

    public int getStartLine() {
        return problem.getSourceLineNumber();
    }

    public int getStartColumn() {
        return problem.getSourceStart();
    }

    public int getEndLine() {
        return getStartLine();
    }

    public int getEndColumn() {
        return problem.getSourceEnd();
    }

    public String getMessage() {
        return problem.getMessage();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getFileName()).append(" (");
        sb.append(getStartLine());
        sb.append(":");
        sb.append(getStartColumn());
        sb.append(") : ");
        sb.append(getMessage());
        return sb.toString();
    }

    public int getId() {
        return problem.getID();
    }

}
