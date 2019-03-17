package com.limelion.dyncompiler;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

public class CompiledObject extends SimpleJavaFileObject {

    final String name;
    final ByteArrayOutputStream baos;

    CompiledObject(String name) {

        super(URI.create("string:///" + name), Kind.CLASS);
        this.name = name;
        this.baos = new ByteArrayOutputStream();
    }

    @Override
    public OutputStream openOutputStream() {

        return baos;
    }

    public byte[] getBytes() {

        return baos.toByteArray();
    }
}
