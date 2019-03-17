package com.limelion.dyncompiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private DynamicClassLoader classLoader;
    private Map<String, SourceObject> sources;

    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager
     *     delegate to this file manager
     */
    public FileManager(JavaFileManager fileManager, DynamicClassLoader cloader) {

        super(fileManager);
        this.classLoader = cloader;
    }

    @Override
    public JavaFileObject getFileForInput(Location location, String packageName, String relativeName) {

        System.out.println("[FileManager::getFileForInput] " + location + "; " + packageName + "; " + relativeName);
        return sources.get(packageName.replace("\\.", File.pathSeparator) + relativeName);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qname, JavaFileObject.Kind kind, FileObject outputFile) throws IOException {

        System.out.println("[FileManager::getFileForOutput] " + location + "; " + qname + "; " + kind + "; " + outputFile);
        if (kind == JavaFileObject.Kind.CLASS)
            return classLoader.getObject(qname);
        else
            return super.getJavaFileForOutput(location, qname, kind, outputFile);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {

        return classLoader;
    }
}
