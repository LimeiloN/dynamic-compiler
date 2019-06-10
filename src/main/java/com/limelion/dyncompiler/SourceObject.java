package com.limelion.dyncompiler;

import com.squareup.javapoet.ClassName;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Represent a java source file where the actual file content is stored in memory.
 */
public class SourceObject extends SimpleJavaFileObject {

    final String code;
    final long lastModified;
    final ClassName className;

    SourceObject(ClassName className, String code) {

        super(CompilerUtils.getSourceURI(className), Kind.SOURCE);
        this.code = code;
        this.lastModified = System.currentTimeMillis();
        this.className = className;
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {

        return code;
    }

    @Override
    public InputStream openInputStream() {

        return new ByteArrayInputStream(getCharContent(true).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public long getLastModified() {

        return lastModified;
    }

    public ClassName getClassName() {

        return className;
    }

    public String getCanonicalName() {

        return CompilerUtils.getCanonicalName(className);
    }
}
