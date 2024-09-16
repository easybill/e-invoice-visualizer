package io.github.easybill.xrviz;

public class XmlHelper {
    public static String removeBOM(String str) {
        // Check if XML contains BOM, if so, we will remove if due to the XslTransformer having issues with anything
        // before the XML prolog.
        if (str.startsWith("\uFEFF")) {
            return str.substring(1);
        }

        return str;
    }

}
