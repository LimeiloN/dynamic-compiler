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

package com.github.LimeiloN.dyncompiler.internal;

import com.squareup.javapoet.ClassName;

import javax.tools.JavaFileObject;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

public final class Utils {

    /**
     * Please do not use - internal
     * org/my/Class.xxx -> org.my.Class
     */
    public static String convertResourceToClassName(final String pResourceName) {
        return Utils.stripExtension(pResourceName).replace('/', '.');
    }

    /**
     * Please do not use - internal
     * org.my.Class -> org/my/Class.class
     */
    public static String convertClassToResourcePath(final String pName) {
        return pName.replace('.', '/') + ".class";
    }

    /**
     * Please do not use - internal
     * org/my/Class.xxx -> org/my/Class
     */
    public static String stripExtension(final String pResourceName) {
        final int i = pResourceName.lastIndexOf('.');
        if (i < 0) {
            return pResourceName;
        }
        final String withoutExtension = pResourceName.substring(0, i);
        return withoutExtension;
    }

    public static String toJavaCasing(final String pName) {
        final char[] name = pName.toLowerCase(Locale.US).toCharArray();
        name[0] = Character.toUpperCase(name[0]);
        return new String(name);
    }

    /*
        public static String clazzName( final File base, final File file ) {
            final int rootLength = base.getAbsolutePath().length();
            final String absFileName = file.getAbsolutePath();
            final int p = absFileName.lastIndexOf('.');
            final String relFileName = absFileName.substring(rootLength + 1, p);
            final String clazzName = relFileName.replace(File.separatorChar, '.');
            return clazzName;
        }
    */
    public static String relative(final File base, final File file) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        final String relFileName = absFileName.substring(rootLength + 1);
        return relFileName;
    }

    /**
     * a/b/c.java -> a/b/c.java
     * a\b\c.java -> a/b/c.java
     *
     * @param pFileName
     * @return the converted name
     */
    public static String getResourceNameFromFileName(final String pFileName) {
        if ('/' == File.separatorChar) {
            return pFileName;
        }

        return pFileName.replace(File.separatorChar, '/');
    }

    public static void closeQuietly(Closeable is) {
        try {
            is.close();
        } catch (IOException ignored) { }
    }

    public static URI getSourceURI(String qname) {

        return URI.create("string:///" + qname.replace(".", "/") + JavaFileObject.Kind.SOURCE.extension);
    }

    public static URI getSourceURI(ClassName className) {

        return getSourceURI(getCanonicalName(className));
    }

    /**
     * Split a class qualified className into 2 bits. "org.example.Class" -> {"org.example", "Class"}
     *
     * @param qname the qualified className to split
     * @return an array containing the 2 bits
     */
    public static String[] splitQName(String qname) {

        String[] splitted = new String[2];

        if (qname.contains(".")) {

            String[] tmp = qname.split("\\.");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < tmp.length - 1; i++) {
                sb.append(tmp[i]);
                if (i != tmp.length - 2)
                    sb.append('.');
            }

            splitted[0] = sb.toString();
            splitted[1] = tmp[tmp.length - 1];

        } else {
            splitted[0] = "";
            splitted[1] = qname;
        }

        return splitted;
    }

    public static String getPackage(String cname) {

        return splitQName(cname)[0];
    }

    public static String getSimpleClassName(String cname) {

        return splitQName(cname)[1];
    }

    public static String getCanonicalName(ClassName className) {

        return className.enclosingClassName() != null
                ? (getCanonicalName(className.enclosingClassName()) + '.' + className.simpleName())
                : (className.packageName().isEmpty() ? className.simpleName() : className.packageName() + '.' + className.simpleName());
    }
}
