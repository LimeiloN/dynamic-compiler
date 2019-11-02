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

package com.github.LimeiloN.dyncompiler.core.compilers;


/**
 * Most common denominator for JavaCompiler settings.
 * <p>
 * If you need more specific settings you have to provide
 * the native compiler configurations to the compilers.
 * Writing of a custom factory is suggested.
 *
 * @author tcurdt
 */
public class JavaCompilerSettings {

    private String targetVersion = "1.4";
    private String sourceVersion = "1.4";
    private String sourceEncoding = "UTF-8";
    private boolean warnings = false;
    private boolean deprecations = false;
    private boolean debug = false;

    /**
     * @deprecated
     */
    @Deprecated
    private boolean verbose = false;

    public JavaCompilerSettings() {
    }

    public JavaCompilerSettings(final JavaCompilerSettings pSettings) {
        targetVersion = pSettings.targetVersion;
        sourceVersion = pSettings.sourceVersion;
        sourceEncoding = pSettings.sourceEncoding;
        warnings = pSettings.warnings;
        deprecations = pSettings.deprecations;
        debug = pSettings.debug;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(final String pTargetVersion) {
        targetVersion = pTargetVersion;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(final String pSourceVersion) {
        sourceVersion = pSourceVersion;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(final String pSourceEncoding) {
        sourceEncoding = pSourceEncoding;
    }

    public boolean isWarnings() {
        return warnings;
    }

    public void setWarnings(final boolean pWarnings) {
        warnings = pWarnings;
    }

    public boolean isDeprecations() {
        return deprecations;
    }

    public void setDeprecations(final boolean pDeprecations) {
        deprecations = pDeprecations;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean pDebug) {
        debug = pDebug;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setVerbose(final boolean pVerbose) {
        verbose = pVerbose;
    }

}
