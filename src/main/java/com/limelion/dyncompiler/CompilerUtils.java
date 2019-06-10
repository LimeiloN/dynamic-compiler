package com.limelion.dyncompiler;

import com.squareup.javapoet.ClassName;

import javax.tools.JavaFileObject;
import java.net.URI;

public final class CompilerUtils {

    public static URI getSourceURI(String qname) {

        return URI.create("string:///" + qname.replace(".", "/") + JavaFileObject.Kind.SOURCE.extension);
    }

    public static URI getSourceURI(ClassName className) {

        return getSourceURI(getCanonicalName(className));
    }

    /**
     * Split a class qualified className into 2 bits. "org.example.Class" -> {"org.example", "Class"}
     *
     * @param qname the qualified className to split
     *
     * @return an array containing the 2 bits
     */
    public static String[] splitQName(String qname) {

        String[] splitted = new String[2];

        if (qname.contains(".")) {

            String[] tmp = qname.split("\\.");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < tmp.length - 1; i++) {
                sb.append(tmp[i]);
                if (i != tmp.length - 2)
                    sb.append('.');
            }

            splitted[0] = sb.toString();
            splitted[1] = tmp[tmp.length - 1];

        } else {
            splitted[0] = "";
            splitted[1] = qname;
        }

        return splitted;
    }

    public static String getPackage(String cname) {

        return splitQName(cname)[0];
    }

    public static String getSimpleClassName(String cname) {

        return splitQName(cname)[1];
    }

    public static String getCanonicalName(ClassName className) {

        return className.enclosingClassName() != null
            ? (getCanonicalName(className.enclosingClassName()) + '.' + className.simpleName())
            : (className.packageName().isEmpty() ? className.simpleName() : className.packageName() + '.' + className.simpleName());
    }
}
