module attacker {
    requires static lombok;
    requires static org.jetbrains.annotations;
    requires info.picocli;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    opens attacker.client to info.picocli;
    exports attacker;
}