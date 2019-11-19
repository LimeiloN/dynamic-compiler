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

import com.github.LimeiloN.dyncompiler.compilers.CompilationResult;
import com.github.LimeiloN.dyncompiler.compilers.JavaCompiler;
import com.github.LimeiloN.dyncompiler.readers.ResourceReader;
import com.github.LimeiloN.dyncompiler.stores.MemoryResourceStore;
import com.github.LimeiloN.dyncompiler.stores.TransactionalResourceStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A CompilingListener is an improved version of the ReloadingListener.
 * It even compiles the classes from source before doing the reloading.
 *
 * @author tcurdt
 */
public class CompilingListener extends ReloadingListener {

    private final Logger log = LogManager.getLogger(CompilingListener.class);

    private final JavaCompiler compiler;
    private final TransactionalResourceStore transactionalStore;
    private ResourceReader reader;
    private CompilationResult lastResult;

    public CompilingListener() {
        this(new JavaCompilerFactory().createCompiler("eclipse"));
    }

    public CompilingListener(final JavaCompiler pCompiler) {
        this(pCompiler, new TransactionalResourceStore(new MemoryResourceStore()));
    }

    public CompilingListener(final JavaCompiler compiler, final TransactionalResourceStore transactionalStore) {
        super(transactionalStore);
        compiler = compiler;
        transactionalStore = transactionalStore;
        lastResult = null;
    }

    public JavaCompiler getCompiler() {
        return compiler;
    }

    public String getSourceFileExtension() {
        return ".java";
    }

    public ResourceReader getReader(final FilesystemAlterationObserver pObserver) {
        return new FileResourceReader(pObserver.getRootDirectory());
    }

    public String getSourceNameFromFile(final FilesystemAlterationObserver pObserver, final File pFile) {
        return ConversionUtils.stripExtension(ConversionUtils.getResourceNameFromFileName(ConversionUtils.relative(pObserver.getRootDirectory(), pFile))) + getSourceFileExtension();
    }

    @Override
    public ResourceStore getStore() {
        return transactionalStore;
    }

    public synchronized CompilationResult getCompilationResult() {
        return lastResult;
    }

    @Override
    public void onStart(final FilesystemAlterationObserver pObserver) {
        super.onStart(pObserver);

        reader = getReader(pObserver);

        transactionalStore.onStart();
    }

    public String[] getResourcesToCompile(final FilesystemAlterationObserver pObserver) {
        final Collection<File> created = getCreatedFiles();
        final Collection<File> changed = getChangedFiles();

        final Collection<String> resourceNames = new ArrayList<String>();

        for (File createdFile : created) {
            if (createdFile.getName().endsWith(getSourceFileExtension())) {
                resourceNames.add(getSourceNameFromFile(pObserver, createdFile));
            }
        }

        for (File changedFile : changed) {
            if (changedFile.getName().endsWith(getSourceFileExtension())) {
                resourceNames.add(getSourceNameFromFile(pObserver, changedFile));
            }
        }

        final String[] result = new String[resourceNames.size()];
        resourceNames.toArray(result);
        return result;
    }

    @Override
    public boolean isReloadRequired(final FilesystemAlterationObserver pObserver) {
        boolean reload = false;

        final Collection<File> created = getCreatedFiles();
        final Collection<File> changed = getChangedFiles();
        final Collection<File> deleted = getDeletedFiles();

        log.debug("created:" + created.size() + " changed:" + changed.size() + " deleted:" + deleted.size() + " resources");

        if (deleted.size() > 0) {
            for (File deletedFile : deleted) {
                final String resourceName = ConversionUtils.getResourceNameFromFileName(ConversionUtils.relative(pObserver.getRootDirectory(), deletedFile));

                if (resourceName.endsWith(getSourceFileExtension())) {
                    // if source resource got removed delete the corresponding class 
                    transactionalStore.remove(ConversionUtils.stripExtension(resourceName) + ".class");
                } else {
                    // ordinary resource to be removed
                    transactionalStore.remove(resourceName);
                }

                // FIXME: does not remove nested classes

            }
            reload = true;
        }

        final String[] resourcesToCompile = getResourcesToCompile(pObserver);

        if (resourcesToCompile.length > 0) {

            log.debug(resourcesToCompile.length + " classes to compile");

            final CompilationResult result = compiler.compile(resourcesToCompile, reader, transactionalStore);

            synchronized (this) {
                lastResult = result;
            }

            final CompilationProblem[] errors = result.getErrors();
            final CompilationProblem[] warnings = result.getWarnings();

            log.debug(errors.length + " errors, " + warnings.length + " warnings");

            if (errors.length > 0) {
                // FIXME: they need to be marked for re-compilation
                // and then added as compileables again
                for (int j = 0; j < resourcesToCompile.length; j++) {
                    transactionalStore.remove(resourcesToCompile[j]);
                }
            }

            reload = true;
        }

        return reload;
    }
}
