package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.easybill.xrviz.XslTransformer;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.HttpURLConnection;

public class PdfHandler extends XmlRequestExtractor implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("PDF conversion requested");
        try {
            var xml = validate(exchange);

            if (xml.isEmpty()) {
                return;
            }

            sendResponse(exchange,
                XslTransformer.transformToPdf(xml.get(), getLanguage(exchange)),
                HttpURLConnection.HTTP_OK, "application/pdf");

        } catch (TransformerException | SAXException e) {
            logger.severe("Error while transforming XML to PDF: " + e.getMessage());
            sendResponse(exchange, e.getMessage().getBytes(), HttpURLConnection.HTTP_BAD_REQUEST, "text/plain");
        }
    }
}