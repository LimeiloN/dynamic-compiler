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


/**
 * A TransactionalResourceStore get signals of the compilation process as a whole.
 * When it started and when the compiler finished.
 *
 * @author tcurdt
 */
public class TransactionalResourceStore implements ResourceStore, Transactional {

    private final ResourceStore store;

    public TransactionalResourceStore(final ResourceStore pStore) {
        store = pStore;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public byte[] read(final String pResourceName) {
        return store.read(pResourceName);
    }

    @Override
    public byte[] remove(final String pResourceName) {
        return store.remove(pResourceName);
    }

    @Override
    public void write(final String pResourceName, final byte[] pResourceData) {
        store.write(pResourceName, pResourceData);
    }

    @Override
    public String toString() {
        return store.toString();
    }
}
