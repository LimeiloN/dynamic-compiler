module com.github.LimeiloN.dyncompiler {

    requires java.compiler;
    requires com.squareup.javapoet;
    requires org.apache.logging.log4j;

    exports com.github.LimeiloN.dyncompiler;

    opens com.github.LimeiloN.dyncompiler.internal to
            com.github.LimeiloN.dyncompiler.ecj,
            com.github.LimeiloN.dyncompiler.janino;
    exports com.github.LimeiloN.dyncompiler.internal;
}