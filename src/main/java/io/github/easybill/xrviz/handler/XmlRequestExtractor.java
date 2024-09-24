package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
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

        byte[] requestBody = exchange.getRequestBody().readAllBytes();

        // detect encoding
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(requestBody, 0, requestBody.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();

        // Fallback to UTF-8 if no encoding was detected
        Charset charset = (encoding != null) ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        String xml = new String(requestBody, charset);

        // Remove BOM if present
        if (xml.startsWith("\uFEFF") || xml.startsWith("\uFFFE")) {
            xml = xml.substring(1);
        }

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
