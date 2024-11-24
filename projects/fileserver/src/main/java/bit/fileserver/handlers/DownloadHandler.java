package bit.fileserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.mccue.jdk.httpserver.Body;
import dev.mccue.jdk.httpserver.HttpExchangeUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import bit.fileserver.io.IOWorker;

import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
public class DownloadHandler implements HttpHandler {
    private final IOWorker io;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpExchangeUtils.sendResponse(exchange, 405, Body.of("Invalid method"));
            return;
        }
        val query = exchange.getRequestURI().getQuery();
        val queryMap = new HashMap<String, String>();
        val parts = query.split("&");
        for (val part: parts) {
            val split = part.split("=", 2);
            if (split.length == 2) {
                queryMap.put(split[0], split[1]);
            }
        }
        if (!queryMap.containsKey("file")) {
            HttpExchangeUtils.sendResponse(exchange, 400, Body.of("Missing file query"));
            return;
        }
        val file = queryMap.get("file");
        val fileData = io.loadFile(file);
        if (fileData == null) {
            HttpExchangeUtils.sendResponse(exchange, 404, Body.of("Unknown file"));
            return;
        }
        val headers = exchange.getResponseHeaders();
        headers.add("Content-Type", fileData.meta().mime());
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileData.meta().name()));
        HttpExchangeUtils.sendResponse(exchange, 200, Body.of(fileData.data()));
    }
}
