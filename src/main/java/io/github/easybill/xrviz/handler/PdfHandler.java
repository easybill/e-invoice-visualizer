package io.github.easybill.xrviz.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.easybill.xrviz.XmlHelper;
import io.github.easybill.xrviz.XslTransformer;
import org.apache.fop.apps.FOPException;
import org.apache.fop.render.txt.Helper;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class PdfHandler extends XmlRequestExtractor implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("PDF conversion requested");
        try {
            var xml = validate(exchange);

            if (xml.isEmpty()) {
                return;
            }

            var xmlContent = XmlHelper.removeBOM(xml.get());

            byte[] response = XslTransformer.transformToPdf(xmlContent, getLanguage(exchange));

            exchange.getResponseHeaders().set("Content-Type", "application/pdf");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();

        } catch (TransformerException | FOPException e) {
            exchange.sendResponseHeaders(500, -1);

            logger.severe("Error while transforming XML to PDF: " + e.getMessage());
        }
    }
}