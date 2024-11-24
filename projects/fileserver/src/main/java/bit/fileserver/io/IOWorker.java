package bit.fileserver.io;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class IOWorker implements AutoCloseable {

    protected final Path rootPath = Path.of("files");
    protected final Path dbPath = rootPath.resolve("db");
    protected final Path storePath = rootPath.resolve("store");
    protected final boolean slow;

    public IOWorker(boolean slow) throws IOException {
        if (!Files.exists(storePath)) {
            Files.createDirectories(storePath);
        }
        this.slow = slow;
    }

    public abstract @NotNull String storeFile(@NotNull StoredFile file) throws IOException;

    public abstract @Nullable StoredFile loadFile(@NotNull String identifier) throws IOException;

    @Override
    public void close() throws Exception {
        Files.deleteIfExists(dbPath);
    }

    protected static void writeString(@NotNull DataOutput output, @NotNull String str) throws IOException {
        val buf = str.getBytes(StandardCharsets.UTF_8);
        output.writeShort(buf.length);
        output.write(buf);
    }

    protected static String readString(@NotNull DataInput input) throws IOException {
        val length = input.readUnsignedShort();
        val buf = new byte[length];
        input.readFully(buf);
        return new String(buf, StandardCharsets.UTF_8);
    }

    public record FileMeta(@NotNull String name, @NotNull String mime) {
    }

    public record StoredFile(byte @NotNull [] data, @NotNull FileMeta meta) {
    }
}
