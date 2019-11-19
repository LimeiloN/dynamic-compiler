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

import com.github.LimeiloN.dyncompiler.problems.CompilationProblem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A CompilationResult represents the result of a compilation.
 * It includes errors (which failed the compilation) or warnings
 * (that can be ignored and do not affect the creation of the
 * class files)
 *
 * @author tcurdt
 */
public final class CompilationResult {

    private final CompilationProblem[] errors;
    private final CompilationProblem[] warnings;

    public CompilationResult(final CompilationProblem[] problems) {
        final Collection<CompilationProblem> errorsColl = new ArrayList<>();
        final Collection<CompilationProblem> warningsColl = new ArrayList<>();

        for (CompilationProblem problem : problems) {
            if (problem.isError()) {
                errorsColl.add(problem);
            } else {
                warningsColl.add(problem);
            }
        }

        errors = new CompilationProblem[errorsColl.size()];
        errorsColl.toArray(errors);

        warnings = new CompilationProblem[warningsColl.size()];
        warningsColl.toArray(warnings);
    }

    public CompilationProblem[] getErrors() {
        final CompilationProblem[] res = new CompilationProblem[errors.length];
        System.arraycopy(errors, 0, res, 0, res.length);
        return res;
    }

    public CompilationProblem[] getWarnings() {
        final CompilationProblem[] res = new CompilationProblem[warnings.length];
        System.arraycopy(warnings, 0, res, 0, res.length);
        return res;
    }
}
