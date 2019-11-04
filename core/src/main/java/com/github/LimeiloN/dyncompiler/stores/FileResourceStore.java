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

package com.github.LimeiloN.dyncompiler.stores;

import com.github.LimeiloN.dyncompiler.internal.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Stores the results on disk
 *
 * @author tcurdt
 */
public final class FileResourceStore implements ResourceStore {

    private final File root;

    public FileResourceStore(final File root) {
        this.root = root;
    }

    @Override
    public byte[] read(final String pResourceName) {
        InputStream is = null;
        try {
            is = new FileInputStream(getFile(pResourceName));
            final byte[] data = is.readAllBytes();
            return data;
        } catch (Exception e) {
            return null;
        } finally {
            Utils.closeQuietly(is);
        }
    }

    @Override
    public void write(final String pResourceName, final byte[] pData) {
        OutputStream os = null;
        try {
            final File file = getFile(pResourceName);
            final File parent = file.getParentFile();
            if (!parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("could not create" + parent);
            }
            os = new FileOutputStream(file);
            os.write(pData);
        } catch (Exception e) {
            // FIXME: now what?
        } finally {
            Utils.closeQuietly(os);
        }
    }

    @Override
    public byte[] remove(final String pResourceName) {
        getFile(pResourceName).delete();
        return new byte[0];
    }

    private File getFile(final String pResourceName) {
        final String fileName = pResourceName.replace('/', File.separatorChar);
        return new File(root, fileName);
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

    @Override
    public String toString() {
        return this.getClass().getName() + root.toString();
    }

}
