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

package com.github.LimeiloN.dyncompiler.core.listeners;

import org.apache.commons.jci.listeners.AbstractFilesystemAlterationListener;
import org.apache.commons.jci.monitor.FilesystemAlterationObserver;

import java.io.File;

/**
 * The most simple implemenation of an FilesystemAlterationListener.
 *
 * @author tcurdt
 */
public class FileChangeListener extends AbstractFilesystemAlterationListener {

    private boolean changed;

    public boolean hasChanged() {
        return changed;
    }

    @Override
    public void onStart(final FilesystemAlterationObserver pObserver) {
        changed = false;
        super.onStart(pObserver);
    }

    @Override
    public void onStop(final FilesystemAlterationObserver pObserver) {
        super.onStop(pObserver);
    }


    @Override
    public void onFileChange(final File pFile) {
        changed = true;
    }


    @Override
    public void onFileCreate(final File pFile) {
        changed = true;
    }


    @Override
    public void onFileDelete(final File pFile) {
        changed = true;
    }


    @Override
    public void onDirectoryChange(final File pDir) {
    }

    @Override
    public void onDirectoryCreate(final File pDir) {
    }

    @Override
    public void onDirectoryDelete(final File pDir) {
    }

}
