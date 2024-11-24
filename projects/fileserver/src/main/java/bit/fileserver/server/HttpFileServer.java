package bit.fileserver.server;

import com.sun.net.httpserver.HttpServer;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import bit.fileserver.handlers.DownloadHandler;
import bit.fileserver.handlers.MainPageHandler;
import bit.fileserver.handlers.UploadHandler;
import bit.fileserver.io.BlockingIOWorker;
import bit.fileserver.io.IOWorker;
import bit.fileserver.io.NaiveIOWorker;
import bit.fileserver.io.NonBlockingIOWorker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple file server with some basic responsibility isolation into subclasses.
 */
public class HttpFileServer implements AutoCloseable {
    private final HttpServer server;
    private final IOWorker worker;
    private final ExecutorService executor;

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("help -- Prints this help");
        System.out.println("exit -- Shuts down the server (sending an interrupt also works)");
    }

    public static int launch(@NotNull InetSocketAddress address, @NotNull IOType ioType, boolean service, boolean slow) {
        try {
            val server = new HttpFileServer(address, ioType, slow);
            val hook = new Thread() {
                @Override
                public void run() {
                    try {
                        server.close();
                    } catch (Throwable ignored) {}
                }
            };
            Runtime.getRuntime().addShutdownHook(hook);
            if (!service) {
                val scanner = new Scanner(System.in);
                System.out.println("Type help for more info");
                while (scanner.hasNextLine()) {
                    val line = scanner.nextLine();
                    switch (line) {
                        case "help" -> printHelp();
                        case "exit" -> {
                            server.close();
                            Runtime.getRuntime().removeShutdownHook(hook);
                            return 0;
                        }
                        default -> {
                            System.out.printf("Unknown command \"%s\"\n", line);
                        }
                    }
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private HttpFileServer(@NotNull InetSocketAddress address, @NotNull IOType ioType, boolean slow) throws IOException {
        server = HttpServer.create(address, 0);
        worker = switch (ioType) {
            case Naive -> new NaiveIOWorker(slow);
            case Blocking -> new BlockingIOWorker(slow);
            case NonBlocking -> new NonBlockingIOWorker(slow);
        };
        server.createContext("/", new MainPageHandler());
        server.createContext("/upload", new UploadHandler(worker));
        server.createContext("/download", new DownloadHandler(worker));

        // Using virtual threads so that we can have a lot of possible race conditions from thousands of requests in parallel :D
        executor = Executors.newVirtualThreadPerTaskExecutor();
        server.setExecutor(executor);
        server.start();

        System.out.printf("Server is running on %s:%s, IO worker: %s\n", address.getHostString(), address.getPort(), ioType.name());
    }

    @Override
    public void close() throws Exception {
        Exception ex = null;
        System.out.println("Shutting down server");
        try {
            server.stop(0);
        } catch (Exception e) {
            ex = e;
        }
        System.out.println("Shutting down IO worker");
        try {
            worker.close();
        } catch (Exception e) {
            if (ex == null) {
                ex = e;
            } else {
                ex.addSuppressed(e);
            }
        }
        System.out.println("Shutting down thread pool");
        try {
            executor.shutdown();
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                Thread.yield();
            }
        } catch (Exception e) {
            if (ex == null) {
                ex = e;
            } else {
                ex.addSuppressed(e);
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}
