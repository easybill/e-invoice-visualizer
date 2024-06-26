package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import java.net.HttpURLConnection;
import java.util.logging.Logger;

public abstract class XmlRequestExtractor {
    static final Logger logger = Logger.getGlobal();
    private static final String CII_VALIDATION_STRING = "<rsm:CrossIndustryInvoice";

    Optional<String> validate(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            logger.severe("Invalid request method: " + exchange.getRequestMethod());

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
            return Optional.empty();
        }

        String xml = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (xml.isBlank() || !xml.contains(CII_VALIDATION_STRING)) {
            logger.severe("Invalid XML content!");

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
            return Optional.empty();
        }

        return Optional.of(xml);
    }

    String getLanguage(HttpExchange exchange) {
        String acceptLanguage = exchange.getRequestHeaders().getFirst("Accept-Language");
        return acceptLanguage != null && acceptLanguage.toLowerCase().contains("en") ? "en" : "de";
    }

}
