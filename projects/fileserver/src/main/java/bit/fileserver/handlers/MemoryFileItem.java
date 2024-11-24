package bit.fileserver.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileItemHeaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

@Accessors(chain = true)
@RequiredArgsConstructor
public class MemoryFileItem implements FileItem<MemoryFileItem> {
    @Getter
    private final String contentType;
    @Getter
    private final String name;
    @Getter
    @Setter
    private boolean formField;
    @Getter
    @Setter
    private String fieldName;
    @Getter
    @Setter
    private FileItemHeaders headers;
    private byte[] data;
    @Override
    public MemoryFileItem delete() {
        data = null;
        return this;
    }

    @Override
    public byte[] get() throws UncheckedIOException {
        try {
            return getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (data == null)
            throw new IOException("No data");
        return new ByteArrayInputStream(data);
    }

    @Override
    public OutputStream getOutputStream() {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                data = this.toByteArray();
            }
        };
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public String getString() {
        return new String(data);
    }

    @Override
    public String getString(Charset toCharset) {
        return new String(data, toCharset);
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public MemoryFileItem write(Path file) throws IOException {
        Files.write(file, data);
        return this;
    }
}
