module com.github.LimeiloN.dyncompiler {

    requires java.compiler;
    requires com.squareup.javapoet;
    requires janino;
    requires commons.compiler;
    requires ecj;

    requires org.apache.logging.log4j;

    exports com.github.LimeiloN.dyncompiler;
}