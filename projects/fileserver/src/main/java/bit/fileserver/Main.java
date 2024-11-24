package bit.fileserver;

import picocli.CommandLine;
import bit.fileserver.server.Launch;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Launch()).execute(args);
        if (exitCode == -1) {
            // Stdin died, process will exit on sigint.
            return;
        }
        System.exit(exitCode);
    }
}
