package attacker.client;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "launch",
         mixinStandardHelpOptions = true,
         description = "Runs the attack program")
public class Launch implements Callable<Integer> {
    @Option(names = {"-t", "--target"},
            description = "The target URL to attack",
            defaultValue = "http://localhost:8080/upload")
    public String target;

    @Option(names = {"-r", "--rate"},
            description = "How many malicious uploads per second should be attempted?",
            defaultValue = "5")
    public int rate;

    @Override
    public Integer call() throws Exception {
        Attack.startFlooder(target, rate);
        return -1;
    }
}
