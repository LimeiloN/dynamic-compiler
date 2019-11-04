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

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private DynamicClassLoader classLoader;
    //private Map<String, SourceObject> sources;

    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager delegate to this file manager
     */
    public FileManager(JavaFileManager fileManager, DynamicClassLoader cloader) {

        super(fileManager);
        this.classLoader = cloader;
    }

    /*
    @Override
    public JavaFileObject getFileForInput(Location location, String packageName, String relativeName) {

        System.out.println("[FileManager::getFileForInput] " + location + "; " + packageName + "; " + relativeName);
        return sources.get(packageName.replace("\\.", File.pathSeparator) + relativeName);
    }
    */

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qname, JavaFileObject.Kind kind, FileObject outputFile) throws IOException {

        //System.out.println("[FileManager::getFileForOutput] " + location + "; " + className + "; " + kind + "; " + outputFile);
        if (kind == JavaFileObject.Kind.CLASS)
            return classLoader.getObject(qname);
        else
            return super.getJavaFileForOutput(location, qname, kind, outputFile);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {

        return classLoader;
    }
}
