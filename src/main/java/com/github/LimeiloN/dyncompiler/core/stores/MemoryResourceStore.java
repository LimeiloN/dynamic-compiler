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

package com.github.LimeiloN.dyncompiler.core.stores;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Store just in memory
 *
 * @author tcurdt
 */
public final class MemoryResourceStore implements ResourceStore {

    private final Log log = LogFactory.getLog(MemoryResourceStore.class);

    private final Map<String, byte[]> store = new HashMap<String, byte[]>();

    public byte[] read(final String pResourceName) {
        log.debug("reading resource " + pResourceName);
        return store.get(pResourceName);
    }

    public void write(final String pResourceName, final byte[] pData) {
        log.debug("writing resource " + pResourceName + "(" + pData.length + ")");
        store.put(pResourceName, pData);
    }

    public void remove(final String pResourceName) {
        log.debug("removing resource " + pResourceName);
        store.remove(pResourceName);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String[] list() {
        if (store == null) {
            return new String[0];
        }
        final List<String> names = new ArrayList<String>();

        for (String name : store.keySet()) {
            names.add(name);
        }

        return names.toArray(new String[store.size()]);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + store.toString();
    }
}
