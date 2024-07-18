package io.github.easybill.xrviz;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.fonts.FontManager;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XslTransformer {
    static final Logger logger = Logger.getGlobal();
    static final String BASE_PATH = Config.getValue(Config.Keys.DATA_PATH);
    public static final String CII_VALIDATION_STRING = "<rsm:CrossIndustryInvoice";
    public static final String UBL_I_VALIDATION_STRING = "<Invoice";
    public static final String UBL_C_VALIDATION_STRING = "<CreditNote";

    enum DocumentType {
        CII("cii-xr.xsl"),
        UBL_I("ubl-invoice-xr.xsl"),
        UBL_C("ubl-creditnote-xr.xsl");

        private final String xslName;

        DocumentType(String xslName) {
            this.xslName = xslName;
        }

        public String getXslName() {
            return xslName;
        }

        public static Optional<DocumentType> detectDocumentType(String xmlContent) {
            if (xmlContent == null || xmlContent.isEmpty()) {
                return Optional.empty();
            }

            if (xmlContent.contains(CII_VALIDATION_STRING)) {
                return Optional.of(CII);
            } else if (xmlContent.contains(UBL_I_VALIDATION_STRING)) {
                return Optional.of(UBL_I);
            } else if (xmlContent.contains(UBL_C_VALIDATION_STRING)) {
                return Optional.of(UBL_C);
            }

            return Optional.empty();
        }
    }

    public static void validateFiles() {
        String[] files = {
            "xsl/cii-xr.xsl",
            "xsl/ubl-invoice-xr.xsl",
            "xsl/xrechnung-html.xsl",
            "xsl/xr-pdf.xsl",
            "fop/fop.xconf"
        };

        if (!Arrays.stream(files)
            .map(file -> Paths.get(BASE_PATH, file).toFile())
            .peek(f -> {
                if (!f.exists()) {
                    logger.log(Level.SEVERE, "Missing resource file: " + f.getAbsolutePath());
                }
            })
            .allMatch(File::exists)) {
            System.exit(1);
        }
    }

    private static DOMSource transformXmlToXr(String inputXml, DocumentType type) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource source = new StreamSource("data/xsl/" + type.getXslName());
        Transformer transformer = factory.newTransformer(source);
        Source xml = new StreamSource(new StringReader(inputXml));
        DOMResult domResult = new DOMResult();
        transformer.transform(xml, domResult);
        return new DOMSource(domResult.getNode());
    }

    public static String transformToHtml(String inputXml, String language) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslXrHtml = new StreamSource("data/xsl/xrechnung-html.xsl");

        Transformer xslXrTransformer = factory.newTransformer(xslXrHtml);
        xslXrTransformer.setParameter("lang", language);

        DOMSource transformedSource = transformXmlToXr(inputXml, DocumentType.detectDocumentType(inputXml).orElseThrow());
        StringWriter outputString = new StringWriter();
        xslXrTransformer.transform(transformedSource, new StreamResult(outputString));

        return outputString.toString();
    }

    public static byte[] transformToPdf(String inputXml, String language) throws TransformerException, FOPException, IOException {
        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslXrPdf = new StreamSource("data/xsl/xr-pdf.xsl");
        Transformer xslXrTransformer = factory.newTransformer(xslXrPdf);
        xslXrTransformer.setParameter("lang", language);

        DOMSource transformedSource = transformXmlToXr(inputXml, DocumentType.detectDocumentType(inputXml).orElseThrow());
        StringWriter outputString = new StringWriter();
        xslXrTransformer.transform(transformedSource, new StreamResult(outputString));
        String foXmlString = outputString.toString();

        // Apache FOP PDF generation

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        FopFactory fopFactory = FopFactory.newInstance(new File("data/fop/fop.xconf").toURI());
        FontManager fontManager = fopFactory.getFontManager();
        logger.info(fontManager.getResourceResolver().getBaseURI().toString());
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

        InputSource src = new InputSource(new java.io.StringReader(foXmlString));
        SAXSource saxSource = new SAXSource(src);

        Transformer transformer = factory.newTransformer();
        transformer.transform(saxSource, new SAXResult(fop.getDefaultHandler()));
        out.close();

        return out.toByteArray();
    }
}
