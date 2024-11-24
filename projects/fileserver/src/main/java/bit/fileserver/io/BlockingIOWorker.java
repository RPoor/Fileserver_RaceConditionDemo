package bit.fileserver.io;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A simple extension of the naive IO, guarding the upload logic with a lockfile.
 *
 */
public class BlockingIOWorker extends NaiveIOWorker {
    protected final Path lockPath = rootPath.resolve("upload.lock");
    public BlockingIOWorker(boolean slow) throws IOException {
        super(slow);
    }

    private void acquireLock() throws IOException {
        while (true) {
            //Wait if file is present
            if (Files.exists(lockPath)) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
                continue;
            }
            try {
                //Atomically create the lockfile
                Files.createFile(lockPath);
                break;
            } catch (FileAlreadyExistsException ignored) {
            }
        }
    }

    private void releaseLock() throws IOException {
        Files.delete(lockPath);
    }

    @Override
    public @NotNull String storeFile(@NotNull StoredFile file) throws IOException {
        acquireLock();
        try {
            return super.storeFile(file);
        } finally {
            releaseLock();
        }
    }
}
