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

package com.github.LimeiloN.dyncompiler.impl.ecj;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

/**
 * Native Eclipse compiler settings
 * 
 * @author tcurdt
 */
public final class EclipseJavaCompilerSettings extends JavaCompilerSettings {

    final private Map<String, String> defaultEclipseSettings = new HashMap<String, String>();

    public EclipseJavaCompilerSettings() {
        defaultEclipseSettings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
    }
    
    public EclipseJavaCompilerSettings( final JavaCompilerSettings pSettings ) {
    	super(pSettings);
    	
    	if (pSettings instanceof EclipseJavaCompilerSettings) {
    		defaultEclipseSettings.putAll(((EclipseJavaCompilerSettings)pSettings).toNativeSettings());
    	}
    }
    
    public EclipseJavaCompilerSettings( final Map<String, String> pMap ) {
        defaultEclipseSettings.putAll(pMap);
    }

    private static Map<String, String> nativeVersions = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
	{
    	put("1.1", CompilerOptions.VERSION_1_1);
    	put("1.2", CompilerOptions.VERSION_1_2);
    	put("1.3", CompilerOptions.VERSION_1_3);
    	put("1.4", CompilerOptions.VERSION_1_4);
    	put("1.5", CompilerOptions.VERSION_1_5);
    	put("1.6", CompilerOptions.VERSION_1_6);
    	put("1.7", CompilerOptions.VERSION_1_7);
    }};
    
    private String toNativeVersion( final String pVersion ) {
    	final String nativeVersion = nativeVersions.get(pVersion);
    	
    	if (nativeVersion == null) {
    		throw new RuntimeException("unknown version " + pVersion);
    	}
    	
    	return nativeVersion;
    }
    
    Map<String, String> toNativeSettings() {
        final Map<String, String> map = new HashMap<String, String>(defaultEclipseSettings);

        map.put(CompilerOptions.OPTION_SuppressWarnings, isWarnings()?CompilerOptions.GENERATE:CompilerOptions.DO_NOT_GENERATE);
        map.put(CompilerOptions.OPTION_ReportDeprecation, isDeprecations()?CompilerOptions.GENERATE:CompilerOptions.DO_NOT_GENERATE);
        map.put(CompilerOptions.OPTION_TargetPlatform, toNativeVersion(getTargetVersion()));
        map.put(CompilerOptions.OPTION_Source, toNativeVersion(getSourceVersion()));
        map.put(CompilerOptions.OPTION_Encoding, getSourceEncoding());

        return map;
    }
    
    @Override
    public String toString() {
        return toNativeSettings().toString();
    }
}
