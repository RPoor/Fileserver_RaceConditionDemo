package bit.fileserver.handlers;

import lombok.val;
import org.apache.commons.fileupload2.core.FileItemFactory;

import java.io.IOException;

public class MemoryFileItemFactory implements FileItemFactory<MemoryFileItem> {
    @Override
    public <B extends AbstractFileItemBuilder<MemoryFileItem, B>> AbstractFileItemBuilder<MemoryFileItem, B> fileItemBuilder() {
        return new AbstractFileItemBuilder<>() {
            @Override
            public MemoryFileItem get() throws IOException {
                val file = new MemoryFileItem(getContentType(), getFileName());
                file.setFieldName(getFieldName());
                file.setFormField(isFormField());
                file.setHeaders(getFileItemHeaders());
                return file;
            }
        };
    }
}
