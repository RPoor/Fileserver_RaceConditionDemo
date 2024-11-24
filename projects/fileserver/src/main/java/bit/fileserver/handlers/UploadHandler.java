package bit.fileserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.mccue.jdk.httpserver.Body;
import dev.mccue.jdk.httpserver.HttpExchangeUtils;
import dev.mccue.jdk.httpserver.fileupload.HttpExchangeFileUpload;
import lombok.RequiredArgsConstructor;
import lombok.val;
import bit.fileserver.io.IOWorker;

import java.io.IOException;

@RequiredArgsConstructor
public class UploadHandler implements HttpHandler {
    private final IOWorker io;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HttpExchangeUtils.sendResponse(exchange, 405, Body.of("Invalid method"));
            return;
        }
        val headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");
        val upload = new HttpExchangeFileUpload<>(new MemoryFileItemFactory());
        val files = upload.parseRequest(exchange);
        val reply = new StringBuilder("<!DOCTYPE html><html><body><h1>Download links:</h1><br>");
        for (val file: files) {
            val meta = new IOWorker.FileMeta(file.getName(), file.getContentType());
            val path = io.storeFile(new IOWorker.StoredFile(file.get(), meta));
            reply.append(String.format("<a href='/download?file=%s'>%s</a><br>", path, file.getName()));
        }
        reply.append("</body></html>");
        HttpExchangeUtils.sendResponse(exchange, 200, Body.of(reply.toString()));
    }
}
