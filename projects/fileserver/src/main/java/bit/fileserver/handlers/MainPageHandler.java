package bit.fileserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.mccue.jdk.httpserver.Body;
import dev.mccue.jdk.httpserver.HttpExchangeUtils;
import lombok.Cleanup;
import lombok.val;

import java.io.IOException;

public class MainPageHandler implements HttpHandler {
    private final byte[] data;

    {
        try {
            @Cleanup val res = MainPageHandler.class.getModule().getResourceAsStream("/main.html");
            if (res == null) {
                throw new IllegalStateException("main.html missing!");
            }
            data = res.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpExchangeUtils.sendResponse(exchange, 405, Body.of("Invalid method"));
            return;
        }
        val headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");
        HttpExchangeUtils.sendResponse(exchange, 200, Body.of(data));
    }
}
