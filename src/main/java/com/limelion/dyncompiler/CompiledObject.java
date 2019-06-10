package com.limelion.dyncompiler;

import com.squareup.javapoet.ClassName;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

public class CompiledObject extends SimpleJavaFileObject {

    final ClassName className;
    private final ByteArrayOutputStream baos;

    CompiledObject(ClassName className) {

        super(URI.create(CompilerUtils.getCanonicalName(className)), Kind.CLASS);
        this.className = className;
        this.baos = new ByteArrayOutputStream();
    }

    @Override
    public OutputStream openOutputStream() {

        return baos;
    }

    public byte[] getBytes() {

        return baos.toByteArray();
    }

    public ClassName getClassName() {

        return className;
    }

    public String getCanonicalName() {

        return CompilerUtils.getCanonicalName(className);
    }
}
