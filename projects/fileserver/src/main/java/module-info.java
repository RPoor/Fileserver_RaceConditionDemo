module fileserver {
    requires jdk.httpserver;
    requires static lombok;
    requires static org.jetbrains.annotations;
    requires info.picocli;
    requires dev.mccue.jdk.httpserver.fileupload;
    requires java.sql;
    opens fileserver.server to info.picocli;
    exports fileserver;
}