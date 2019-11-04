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

package com.github.LimeiloN.dyncompiler.impl.janino;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.LocatedException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.janino.ClassLoaderIClassLoader;
import org.codehaus.janino.Compiler;
import org.codehaus.janino.FilterWarningHandler;
import org.codehaus.janino.UnitCompiler.ErrorHandler;
import org.codehaus.janino.WarningHandler;
import org.codehaus.janino.util.StringPattern;
import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceCreator;
import org.codehaus.janino.util.resource.ResourceFinder;

/**
 * @author tcurdt
 */
public final class JaninoJavaCompiler extends AbstractJavaCompiler {

    private final Log log = LogFactory.getLog(JaninoJavaCompiler.class);

    private final JaninoJavaCompilerSettings defaultSettings;

    public JaninoJavaCompiler() {
    	this(new JaninoJavaCompilerSettings());
    }
    
    public JaninoJavaCompiler( final JaninoJavaCompilerSettings pSettings ) {
    	defaultSettings = pSettings;
    }
    
    private final static class JciResource implements Resource {

    	private final String name;
    	private final byte[] bytes;
    	
    	public JciResource( final String pName, final byte[] pBytes ) {
    		name = pName;
    		bytes = pBytes;
    	}
    	
		public String getFileName() {
			return name;
		}

		public long lastModified() {
			return 0;
		}

		public InputStream open() throws IOException {
			return new ByteArrayInputStream(bytes);
		}
    }

    private final static class JciOutputStream extends ByteArrayOutputStream {

    	private final String name;
    	private final ResourceStore store;

    	public JciOutputStream( final String pName, final ResourceStore pStore ) {
    		name = pName;
    		store = pStore;
    	}

		@Override
        public void close() throws IOException {
			super.close();

			final byte[] bytes = toByteArray();
			
			store.write(name, bytes);
		}
    }
    
    public CompilationResult compile( final String[] pSourceNames, final ResourceReader pResourceReader, final ResourceStore pStore, final ClassLoader pClassLoader, final JavaCompilerSettings pSettings ) {

    	final Collection<CompilationProblem> problems = new ArrayList<CompilationProblem>();
    	
    	final StringPattern[] pattern = StringPattern.PATTERNS_NONE;

    	final Compiler compiler = new Compiler(
    			new ResourceFinder() {
					@Override
                    public Resource findResource( final String pSourceName ) {
						final byte[] bytes = pResourceReader.getBytes(pSourceName);
						
						if (bytes == null) {
							log.debug("failed to find source " + pSourceName);
							return null;
						}
						
						log.debug("reading " + pSourceName + " (" + bytes.length + ")");
						
						return new JciResource(pSourceName, bytes);
					}    		
    			},
    			new ClassLoaderIClassLoader(pClassLoader),
    			new ResourceFinder() {
					@Override
                    public Resource findResource( final String pResourceName ) {
						final byte[] bytes = pStore.read(pResourceName);
						
						if (bytes == null) {
							log.debug("failed to find " + pResourceName);
							return null;
						}

						log.debug("reading " + pResourceName + " (" + bytes.length + ")");
						
						return new JciResource(pResourceName, bytes);
					}    		
    			},
    			new ResourceCreator() {
					public OutputStream createResource( final String pResourceName ) throws IOException {
						return new JciOutputStream(pResourceName, pStore);
					}

					public boolean deleteResource( final String pResourceName ) {
						log.debug("removing " + pResourceName);

						pStore.remove(pResourceName);
						return true;
					}    				
    			},
    			pSettings.getSourceEncoding(),
    			false,
    			pSettings.isDebug(),
                pSettings.isDebug(),
                pSettings.isDebug(),
    			new FilterWarningHandler(pattern, new WarningHandler() {
						public void handleWarning( final String pHandle, final String pMessage, final Location pLocation ) {
							final CompilationProblem problem = new JaninoCompilationProblem(pLocation.getFileName(), pLocation, pMessage, false);
							if (problemHandler != null) {
								problemHandler.handle(problem);
							}
							problems.add(problem);
						}    		
			    	})    			
    			);
    	
    	
    	compiler.setCompileErrorHandler(new ErrorHandler() {
			public void handleError( final String pMessage, final Location pLocation ) throws CompileException {
				final CompilationProblem problem = new JaninoCompilationProblem(pLocation.getFileName(), pLocation, pMessage, true);
				if (problemHandler != null) {
					problemHandler.handle(problem);
				}
				problems.add(problem);
			}
    	});
    	

    	final Resource[] resources = new Resource[pSourceNames.length];
        for (int i = 0; i < pSourceNames.length; i++) {
            log.debug("compiling " + pSourceNames[i]);
            final byte[] source = pResourceReader.getBytes(pSourceNames[i]);
            resources[i] = new JciResource(pSourceNames[i], source);
        }
        
        try {
            compiler.compile(resources);
        } catch ( LocatedException e ) {
            problems.add(new JaninoCompilationProblem(e));
        } catch ( IOException e ) {
            // low level problems reading or writing bytes
        	log.error("this error should have been cought before", e);
        }        
        final CompilationProblem[] result = new CompilationProblem[problems.size()];
        problems.toArray(result);
        return new CompilationResult(result);
    }

    public JavaCompilerSettings createDefaultSettings() {
        return new JaninoJavaCompilerSettings(defaultSettings);
    }
    
}
