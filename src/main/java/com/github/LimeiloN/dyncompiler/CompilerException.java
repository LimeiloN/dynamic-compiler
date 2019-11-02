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

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;

public class CompilerException extends Exception {

    private List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompilerException(String message) {

        super(message);
    }

    public CompilerException(String message, List<Diagnostic<? extends JavaFileObject>> diagnostics) {

        super(message);
        this.diagnostics = diagnostics;
    }

    public CompilerException(Throwable e, List<Diagnostic<? extends JavaFileObject>> diagnostics) {

        super(e);
        this.diagnostics = diagnostics;
    }

    public String getDiagnosticsError() {

        StringBuilder sb = new StringBuilder(getLocalizedMessage()).append(" : \n");
        if (diagnostics != null) {
            diagnostics.forEach(diagnostic -> sb.append(String.format("Error on line %d: %s\n",
                    diagnostic.getLineNumber(),
                    diagnostic.getMessage(null))));
        }
        return sb.toString();
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {

        return diagnostics;
    }

    @Override
    public String toString() {

        return getDiagnosticsError();
    }

}
