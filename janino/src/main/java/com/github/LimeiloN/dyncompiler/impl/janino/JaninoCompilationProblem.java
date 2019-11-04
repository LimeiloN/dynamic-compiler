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

package com.github.LimeiloN.dyncompiler.impl.janino;

import com.github.LimeiloN.dyncompiler.problems.CompilationProblem;

/**
 * Janino version of a CompilationProblem
 * 
 * @author tcurdt
 */
public final class JaninoCompilationProblem implements CompilationProblem {

    private final Location location;
    private final String fileName;
    private final String message;
    private final boolean error;

    public JaninoCompilationProblem(final LocatedException pLocatedException) {
        this(pLocatedException.getLocation(), pLocatedException.getMessage(), true);
    }

    public JaninoCompilationProblem(final Location pLocation, final String pMessage, final boolean pError) {
      this(pLocation.getFileName(), pLocation, pMessage, pError);
    }

    public JaninoCompilationProblem(final String pFilename, final String pMessage, final boolean pError) {
        this(pFilename, null, pMessage, pError);
    }

    public JaninoCompilationProblem(final String pFilename, final Location pLocation, final String pMessage, final boolean pError) {
        location = pLocation;
        fileName = pFilename;
        message = pMessage;
        error = pError;
    }

    public boolean isError() {
        return error;
    }

    public String getFileName() {
        return fileName;
    }

    public int getStartLine() {
        if (location == null) {
            return 0;
        }
        return location.getLineNumber();
    }

    public int getStartColumn() {
        if (location == null) {
            return 0;
        }
        return location.getColumnNumber();
    }

    public int getEndLine() {
        return getStartLine();
    }

    public int getEndColumn() {
        return getStartColumn();
    }

    public String getMessage() {
        return message;
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

}
