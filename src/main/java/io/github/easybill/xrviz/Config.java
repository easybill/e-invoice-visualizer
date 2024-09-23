package io.github.easybill.xrviz;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    static final Logger logger = Logger.getGlobal();
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
        }
    }
    public enum Keys {
        HTTP_PORT,
        LOG_LEVEL,
        DATA_PATH;
    }

    public static String getValue(Keys key) {
        String value = System.getenv(key.name());
        return value != null ? value : properties.getProperty(key.name(), "");
    }

    public static int getIntValue(Keys key) {
        try {
            return Integer.parseInt(getValue(key));
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    public static void initTransformerFactory() {
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    }

    public static void initLogger() {
//        LogManager.getLogManager().reset();
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        final Logger log = Logger.getGlobal();
        log.setUseParentHandlers(false);

        try {
            log.setLevel(Level.parse(getValue(Keys.LOG_LEVEL)));
        } catch (IllegalArgumentException e) {
            log.setLevel(Level.ALL);
        }

        PrintStream err = System.err;
        System.setErr(System.out);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        System.setErr(err);
        consoleHandler.setLevel(log.getLevel());
        log.addHandler(consoleHandler);

        System.setProperty("log4j.logger.org.apache.fop", "DEBUG");
//        Logger logger = Logger.getLogger("org.apache.commons.logging");
//        logger.setLevel(Level.OFF);
    }

    public static String getVersion() {
        try (InputStream manifestStream = Config.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (manifestStream != null) {
                String version = new Manifest(manifestStream).getMainAttributes().getValue("Implementation-Version");
                if (version != null) {
                    return version;
                }
            }
        } catch (IOException ignored) {
        }
        return "0.DEV";
    }

    public static void showBanner() {
        try (InputStream inputStream = Config.class.getResourceAsStream("/banner.txt")) {
            if (inputStream != null) {
                logger.info("\n" + new String(inputStream.readAllBytes()));
            }
        } catch (IOException ignored) {
        }
        logger.info("XRechnung Visualizer v" + getVersion());
    }

    public static void startupTime() {
        logger.info(String.format(Locale.ENGLISH, "App started in %.2fs",
            (System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000d));
    }

    public record UptimePair(String upStr, Long upLong) {
    }

    public static UptimePair calcUptime() {
        long diffMillis = System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime();
        Duration duration = Duration.ofMillis(diffMillis);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append("d ");
        if (hours > 0) result.append(hours).append("h ");
        if (minutes > 0) result.append(minutes).append("m ");
        result.append(seconds).append("s");

        return new UptimePair(result.toString().trim(), diffMillis);
    }
}
