package bit.fileserver.io;

import lombok.Cleanup;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class NaiveIOWorker extends IOWorker {
    protected final Map<String, FileMeta> files = new HashMap<>();

    public NaiveIOWorker(boolean slow) throws IOException {
        super(slow);
        if (Files.exists(dbPath)) {
            val in = Files.newInputStream(dbPath, StandardOpenOption.READ);
            @Cleanup val dIn = new DataInputStream(new BufferedInputStream(in));
            val count = dIn.readInt();
            for (int i = 0; i < count; i++) {
                val key = readString(dIn);
                val name = readString(dIn);
                val mime = readString(dIn);
                files.put(key, new FileMeta(name, mime));
            }
        }
    }

    @Override
    public @NotNull String storeFile(@NotNull StoredFile file) throws IOException {
        //Very bad identifier generation to make collisions trivial
        //We want the race condition in the IO section so this is purposefully a local variable.
        String name = null;
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            val id = Long.toString(i);
            if (!files.containsKey(id)) {
                name = id;
                break;
            }
        }
        if (name == null) {
            throw new IOException("Could not find valid identifier");
        }

        try (val output = Files.newOutputStream(storePath.resolve(name))) {
            output.write(file.data());
            //Pretend that we're storing a huge file on a slow drive
            if (slow) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        files.put(name, file.meta());

        return name;
    }

    @Override
    public @Nullable StoredFile loadFile(@NotNull String identifier) throws IOException {
        if (!files.containsKey(identifier)) {
            return null;
        }
        val filePath = storePath.resolve(identifier);
        if (!Files.exists(filePath))
            return null;
        val data = Files.readAllBytes(filePath);
        return new StoredFile(data, files.get(identifier));
    }

    @Override
    public void close() throws Exception {
        val out = Files.newOutputStream(dbPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        @Cleanup val dOut = new DataOutputStream(new BufferedOutputStream(out));
        dOut.writeInt(files.size());
        for (val entry: files.entrySet()) {
            val key = entry.getKey();
            val value = entry.getValue();
            writeString(dOut, key);
            writeString(dOut, value.name());
            writeString(dOut, value.mime());
        }
    }
}
