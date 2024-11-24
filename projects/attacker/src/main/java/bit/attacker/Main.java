package bit.attacker;

import picocli.CommandLine;
import bit.attacker.client.Launch;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int exitCode = new CommandLine(new Launch()).execute(args);
        if (exitCode == -1) {
            // Stdin died, process will exit on sigint.
            return;
        }
        System.exit(exitCode);
    }
}
