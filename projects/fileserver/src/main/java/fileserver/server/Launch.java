package fileserver.server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

@Command(name = "launch",
         mixinStandardHelpOptions = true,
         description = "Runs the file server")
public class Launch implements Callable<Integer> {
    @Option(names = {"-a", "--address"},
            description = "The IP address to bind the server to.",
            defaultValue = "localhost")
    public String ip;

    @Option(names = {"-p", "--port"},
            description = "The port to listen on.",
            defaultValue = "8080")
    public int port;

    @Option(names = {"-t", "--io-type"},
            description = {"The mode of the IO backend. Possible values:",
                           "Naive -- unsafe backend",
                           "Blocking -- blocking IO",
                           "NonBlocking -- Non-blocking IO"},
            required = true)
    public IOType type;

    @Option(names = {"-s", "--service"},
            description = "Run in service mode, disables the CLI")
    public boolean service;

    @Option(names = {"-S", "--slow"},
            description = "Adds artificial slowness into the file upload logic to exaggerate race conditions.")
    public boolean slow;

    @Override
    public Integer call() {
        return HttpFileServer.launch(new InetSocketAddress(ip, port), type, service, slow);
    }
}
