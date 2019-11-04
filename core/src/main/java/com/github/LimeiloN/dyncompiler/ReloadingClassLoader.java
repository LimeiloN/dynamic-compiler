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

import com.github.LimeiloN.dyncompiler.listeners.ReloadNotificationListener;
import com.github.LimeiloN.dyncompiler.stores.ResourceStore;
import com.github.LimeiloN.dyncompiler.stores.ResourceStoreClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.net.URL;

/**
 * The ReloadingClassLoader uses a delegation mechanism to allow
 * classes to be reloaded. That means that loadClass calls may
 * return different results if the class was changed in the underlying
 * ResourceStore.
 *
 * @author tcurdt
 */
public class ReloadingClassLoader extends ClassLoader implements ReloadNotificationListener {

    private static final Logger log = LogManager.getLogger(ReloadingClassLoader.class);

    private final ClassLoader parent;
    private ResourceStore[] stores = new ResourceStore[0];
    private ClassLoader delegate;

    public ReloadingClassLoader(final ClassLoader pParent) {
        super(pParent);
        parent = pParent;

        delegate = new ResourceStoreClassLoader(parent, stores);
    }

    public boolean addResourceStore(final ResourceStore pStore) {
        try {
            final int n = stores.length;
            final ResourceStore[] newStores = new ResourceStore[n + 1];
            System.arraycopy(stores, 0, newStores, 1, n);
            newStores[0] = pStore;
            stores = newStores;
            delegate = new ResourceStoreClassLoader(parent, stores);
            return true;
        } catch (final RuntimeException e) {
            log.error("could not add resource store " + pStore);
        }
        return false;
    }

    public boolean removeResourceStore(final ResourceStore pStore) {

        final int n = stores.length;
        int i = 0;

        // FIXME: this should be improved with a Map
        // find the pStore and index position with var i
        while ((i < n) && (stores[i] != pStore)) {
            i++;
        }

        // pStore was not found
        if (i == n) {
            return false;
        }

        // if stores length > 1 then array copy old values, else create new empty store 
        final ResourceStore[] newStores = new ResourceStore[n - 1];
        if (i > 0) {
            System.arraycopy(stores, 0, newStores, 0, i);
        }
        if (i < n - 1) {
            System.arraycopy(stores, i + 1, newStores, i, (n - i - 1));
        }

        stores = newStores;
        delegate = new ResourceStoreClassLoader(parent, stores);
        return true;
    }

    public void handleNotification() {
        log.debug("reloading");
        delegate = new ResourceStoreClassLoader(parent, stores);
    }

    @Override
    public void clearAssertionStatus() {
        delegate.clearAssertionStatus();
    }

    @Override
    public URL getResource(String name) {
        return delegate.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return delegate.getResourceAsStream(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return delegate.loadClass(name);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        delegate.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        delegate.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        delegate.setPackageAssertionStatus(packageName, enabled);
    }
}
