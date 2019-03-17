package com.limelion.dyncompiler;

import java.net.URI;

public final class CompilerUtils {

    public static URI getSource(String name) {

        return URI.create("string:///" + name + ".java");
    }

    public static String[] splitQName(String qname) {

        String[] splitted;

        if (qname.contains(".")) {

            splitted = new String[2];

            String[] tmp = qname.split("\\.");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < tmp.length - 1; i++) {
                sb.append(tmp[i]);
            }

            splitted[0] = sb.toString();
            splitted[1] = tmp[tmp.length - 1];

        } else {
            splitted = new String[] { qname };
        }

        return splitted;
    }
}
