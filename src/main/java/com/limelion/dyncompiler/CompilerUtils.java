package com.limelion.dyncompiler;

import java.net.URI;
import java.util.Map.Entry;

public final class CompilerUtils {

    public static URI getSource(String name) {

        return URI.create("string:///" + name + ".java");
    }

    /**
     * Split a class qualified name into 2 bits. "org.example.Class" -> {"org.example", "Class"}
     *
     * @param qname
     *     the qualified name to split
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

    public static String getPackage(String qname) {

        return splitQName(qname)[0];
    }

    public static String getName(String qname) {

        return splitQName(qname)[1];
    }
}
