package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class XmlRequestExtractor {
    static final Logger logger = Logger.getGlobal();
    static final Pattern REGEX = Pattern.compile("[<:](CrossIndustryInvoice|Invoice|CreditNote)");

    Optional<String> validate(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            logger.severe("Invalid request method: " + exchange.getRequestMethod());

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
            return Optional.empty();
        }

        String xml = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (!isXMLValid(xml)) {
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

    private boolean isXMLValid(String xml) {
        return !xml.isBlank() && (REGEX.matcher(xml).find());
    }
}
