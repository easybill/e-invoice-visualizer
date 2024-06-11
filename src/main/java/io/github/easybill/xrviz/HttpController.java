package io.github.easybill.xrviz;

import com.sun.net.httpserver.HttpServer;
import io.github.easybill.xrviz.handler.HtmlHandler;
import io.github.easybill.xrviz.handler.PdfHandler;
import io.github.easybill.xrviz.handler.StatusHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpController {

    static HttpServer httpServer = null;
    static final int HTTP_PORT = Config.getIntValue(Config.Keys.HTTP_PORT);
    static final Logger logger = Logger.getGlobal();

    static void init() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(HttpController.HTTP_PORT), 0);
            httpServer.setExecutor(Executors.newFixedThreadPool(10));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> httpServer.stop(0)));

            httpServer.createContext("/convert.html", new HtmlHandler());
            httpServer.createContext("/convert.pdf", new PdfHandler());
            httpServer.createContext("/health", new StatusHandler());

            httpServer.start();
            logger.config("Web server started on port " + HttpController.HTTP_PORT);
        } catch (Exception e) {
            logger.severe("Can't start web server: " + e.getMessage());
            System.exit(1);
        }
    }
}
