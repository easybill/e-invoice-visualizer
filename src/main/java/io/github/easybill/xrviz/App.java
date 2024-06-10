package io.github.easybill.xrviz;

public class App {
    public static void main(String[] args) {
        Config.initLogger();
        Config.initTransformerFactory();
        Config.showBanner();
        XslTransformer.validateFiles();
        HttpController.init();
        Config.startupTime();
    }
}
