package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.easybill.xrviz.Config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class StatusHandler extends XmlRequestExtractor implements HttpHandler {

    public static final String JSON_RESPONSE = """
        {"version":"%s","freeMemory":%d,"totalMemory":%d,"uptime":%d,"uptimeString":"%s"}""";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            logger.info("Health check requested");
            String version = Config.getVersion();
            Config.UptimePair uptime = Config.calcUptime();

            byte[] response = String.format(JSON_RESPONSE,
                version,
                Runtime.getRuntime().freeMemory(),
                Runtime.getRuntime().totalMemory(),
                uptime.upLong(),
                uptime.upStr()
            ).getBytes(StandardCharsets.UTF_8);

            sendResponse(exchange, response, HttpURLConnection.HTTP_OK, "application/json");
        } else {
            logger.warning("Wrong method request for health check: " + exchange.getRequestMethod());
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
        }

    }
}