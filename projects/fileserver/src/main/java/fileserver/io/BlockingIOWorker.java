package fileserver.io;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A simple extension of the naive IO, guarding the upload/download logic with locks.
 *
 */
public class BlockingIOWorker extends NaiveIOWorker {
    private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    public BlockingIOWorker(boolean slow) throws IOException {
        super(slow);
    }

    @Override
    public @NotNull String storeFile(@NotNull StoredFile file) throws IOException {
        val lock = LOCK.writeLock();
        lock.lock();
        try {
            return super.storeFile(file);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public @Nullable StoredFile loadFile(@NotNull String identifier) throws IOException {
        val lock = LOCK.readLock();
        lock.lock();
        try {
            return super.loadFile(identifier);
        } finally {
            lock.unlock();
        }
    }
}
