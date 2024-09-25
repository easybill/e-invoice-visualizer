package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.easybill.xrviz.XslTransformer;

import javax.xml.transform.TransformerException;
import java.io.IOException;
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

            byte[] response = XslTransformer.transformToHtml(xml.get(), getLanguage(exchange)).getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();

        } catch (TransformerException e) {
            exchange.sendResponseHeaders(500, -1);

            logger.severe("Error while transforming XML to HTML: " + e.getMessage());
        }
    }
}
