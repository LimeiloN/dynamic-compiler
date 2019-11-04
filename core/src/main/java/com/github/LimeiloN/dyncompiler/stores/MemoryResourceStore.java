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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Store resources in memory
 *
 * @author tcurdt, LimeiloN
 */
public final class MemoryResourceStore implements ResourceStore {

    private final Logger log = LogManager.getLogger(MemoryResourceStore.class);

    private final Map<String, byte[]> store = new HashMap<>();

    @Override
    public byte[] read(final String pResourceName) {
        log.debug("Reading resource " + pResourceName);
        return store.get(pResourceName);
    }

    @Override
    public void write(final String pResourceName, final byte[] pData) {
        log.debug("Writing resource " + pResourceName + "(" + pData.length + ")");
        store.put(pResourceName, pData);
    }

    @Override
    public byte[] remove(final String pResourceName) {
        log.debug("Removing resource " + pResourceName);
        return store.remove(pResourceName);
    }

    public Set<String> listResources() {
        return store.keySet();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + store.toString();
    }
}
