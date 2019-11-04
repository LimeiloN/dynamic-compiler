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

package com.github.LimeiloN.dyncompiler.listeners;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jci.ReloadingClassLoader;
import org.apache.commons.jci.listeners.AbstractFilesystemAlterationListener;
import org.apache.commons.jci.monitor.FilesystemAlterationObserver;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.Transactional;
import org.apache.commons.jci.utils.ConversionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This Listener waits for FAM events to trigger a reload of classes
 * or resources.
 *
 * @author tcurdt
 */
public class ReloadingListener extends AbstractFilesystemAlterationListener {

    private final Log log = LogFactory.getLog(ReloadingListener.class);

    private final Set<ReloadNotificationListener> notificationListeners = new HashSet<ReloadNotificationListener>();
    private final ResourceStore store;

    public ReloadingListener() {
        this(new MemoryResourceStore());
    }

    public ReloadingListener(final ResourceStore pStore) {
        store = pStore;
    }

    public ResourceStore getStore() {
        return store;
    }

    public void addReloadNotificationListener(final ReloadNotificationListener pNotificationListener) {
        notificationListeners.add(pNotificationListener);

        if (pNotificationListener instanceof ReloadingClassLoader) {
            ((ReloadingClassLoader) pNotificationListener).addResourceStore(store);
        }

    }

    public boolean isReloadRequired(final FilesystemAlterationObserver pObserver) {
        boolean reload = false;

        final Collection<File> created = getCreatedFiles();
        final Collection<File> changed = getChangedFiles();
        final Collection<File> deleted = getDeletedFiles();

        log.debug("created:" + created.size() + " changed:" + changed.size() + " deleted:" + deleted.size() + " resources");

        if (deleted.size() > 0) {
            for (File file : deleted) {
                final String resourceName = ConversionUtils.getResourceNameFromFileName(ConversionUtils.relative(pObserver.getRootDirectory(), file));
                store.remove(resourceName);
            }
            reload = true;
        }

        if (created.size() > 0) {
            for (File file : created) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(file);
                    final byte[] bytes = IOUtils.toByteArray(is);
                    final String resourceName = ConversionUtils.getResourceNameFromFileName(ConversionUtils.relative(pObserver.getRootDirectory(), file));
                    store.write(resourceName, bytes);
                } catch (final Exception e) {
                    log.error("could not load " + file, e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        if (changed.size() > 0) {
            for (File file : changed) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(file);
                    final byte[] bytes = IOUtils.toByteArray(is);
                    final String resourceName = ConversionUtils.getResourceNameFromFileName(ConversionUtils.relative(pObserver.getRootDirectory(), file));
                    store.write(resourceName, bytes);
                } catch (final Exception e) {
                    log.error("could not load " + file, e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
            reload = true;
        }

        return reload;
    }

    @Override
    public void onStop(final FilesystemAlterationObserver pObserver) {


        if (store instanceof Transactional) {
            ((Transactional) store).onStart();
        }

        final boolean reload = isReloadRequired(pObserver);

        if (store instanceof Transactional) {
            ((Transactional) store).onStop();
        }

        if (reload) {
            notifyReloadNotificationListeners();
        }

        super.onStop(pObserver);
    }

    void notifyReloadNotificationListeners() {
        for (ReloadNotificationListener listener : notificationListeners) {
            log.debug("notifying listener " + listener);

            listener.handleNotification();
        }
    }

    @Override
    public void onDirectoryCreate(final File pDir) {
    }

    @Override
    public void onDirectoryChange(final File pDir) {
    }

    @Override
    public void onDirectoryDelete(final File pDir) {
    }
}
