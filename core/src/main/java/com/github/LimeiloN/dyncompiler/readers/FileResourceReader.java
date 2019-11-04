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

package com.github.LimeiloN.dyncompiler.readers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple file system based Reader implementation
 *
 * @author tcurdt
 */
public final class FileResourceReader implements ResourceReader {

    private final File root;

    public FileResourceReader(final File pRoot) {
        root = pRoot;
    }

    public boolean isAvailable(final String pResourceName) {
        return new File(root, pResourceName).exists();
    }

    public byte[] getBytes(final String pResourceName) {
        try {
            return Utils.readFileToString(new File(root, pResourceName), "UTF-8").getBytes();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String[] list() {
        final List<String> files = new ArrayList<String>();
        list(root, files);
        return files.toArray(new String[files.size()]);
    }

    /**
     * @deprecated
     */
    @Deprecated
    private void list(final File pFile, final List<String> pFiles) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add(pFile.getAbsolutePath().substring(root.getAbsolutePath().length() + 1));
        }
    }
}
