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

import com.github.LimeiloN.dyncompiler.internal.Utils;
import com.squareup.javapoet.ClassName;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Represent a java source file where the actual file content is stored in memory.
 */
public class SourceObject extends SimpleJavaFileObject {

    final String code;
    final long lastModified;
    final ClassName className;

    SourceObject(ClassName className, String code) {

        super(Utils.getSourceURI(className), Kind.SOURCE);
        this.code = code;
        this.lastModified = System.currentTimeMillis();
        this.className = className;
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {

        return code;
    }

    @Override
    public InputStream openInputStream() {

        return new ByteArrayInputStream(getCharContent(true).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public long getLastModified() {

        return lastModified;
    }

    public ClassName getClassName() {

        return className;
    }

    public String getCanonicalName() {

        return Utils.getCanonicalName(className);
    }
}
