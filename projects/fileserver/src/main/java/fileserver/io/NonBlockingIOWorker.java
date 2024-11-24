package fileserver.io;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class NonBlockingIOWorker extends NaiveIOWorker {
    private final AtomicInteger idPrefixGenerator = new AtomicInteger(0);
    public NonBlockingIOWorker(boolean slow) throws IOException {
        super(slow);
    }

    // Active conflict avoidance
    @Override
    public @NotNull String storeFile(@NotNull StoredFile file) throws IOException {
        String name = null;
        long i = 0;
        while (true) {
            for (; i < Long.MAX_VALUE; i++) {
                val id = Long.toString(i);
                if (!files.containsKey(id)) {
                    name = id;
                    break;
                }
            }
            if (name == null) {
                break;
            }
            // Atomic file creation
            try (val output = Files.newOutputStream(storePath.resolve(name), StandardOpenOption.CREATE_NEW)) {
                output.write(file.data());
                //Pretend that we're storing a huge file on a slow drive
                if (slow) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                }
            } catch (FileAlreadyExistsException e) {
                i++;
                continue;
            }

            files.put(name, file.meta());
            break;
        }
        if (name == null) {
            throw new IOException("Could not find valid identifier");
        }

        return name;
    }
}
