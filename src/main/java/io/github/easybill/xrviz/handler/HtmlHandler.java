package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.easybill.xrviz.XslTransformer;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HtmlHandler extends XmlRequestExtractor implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("HTML conversion requested");
        try {
            var xml = validate(exchange);

            if (xml.isEmpty()) {
                return;
            }

            sendResponse(exchange,
                XslTransformer.transformToHtml(xml.get(), getLanguage(exchange)).getBytes(StandardCharsets.UTF_8),
                HttpURLConnection.HTTP_OK, "text/html");

        } catch (TransformerException e) {
            sendResponse(exchange, e.getMessage().getBytes(), HttpURLConnection.HTTP_BAD_REQUEST, "text/plain");
            logger.severe("Error while transforming XML to HTML: " + e.getMessage());
        }
    }
}
