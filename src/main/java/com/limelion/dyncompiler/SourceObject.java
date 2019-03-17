package com.limelion.dyncompiler;

import javax.tools.SimpleJavaFileObject;
import java.nio.CharBuffer;

public class SourceObject extends SimpleJavaFileObject {

    final String code;

    SourceObject(String name, String code) {

        super(CompilerUtils.getSource(name), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharBuffer getCharContent(boolean ignoreEncodingErrors) {

        return CharBuffer.wrap(code);
    }
}
