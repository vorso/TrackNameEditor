package com.vorso.conversion;

import com.vorso.TrackNameEditor.Format;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class Converter {

    static final Logger logger = LogManager.getLogger(Converter.class);

    private final Format format;
    private final Path xmlFileCopy;
    private int numberOfChanges = 0;

    public Converter(Format format, Path xmlFileCopy) {
        this.format = requireNonNull(format);
        this.xmlFileCopy = requireNonNull(xmlFileCopy);
    }

    public ConversionResult convert() throws TransformerException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFileCopy.toFile());
        document.getDocumentElement().normalize();

        NodeList nameList = document.getElementsByTagName("UserName");
        processText(nameList);

        NodeList effectiveNameList = document.getElementsByTagName("EffectiveName");
        processText(effectiveNameList);

        NodeList clipList = document.getElementsByTagName("Name");
        processTextForOnlyNodesWithGivenParent(clipList, "AudioClip");
        processTextForOnlyNodesWithGivenParent(clipList, "MidiClip");
        processTextForOnlyNodesWithGivenParent(clipList, "MidiClip");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);

        StreamResult streamResult = new StreamResult(xmlFileCopy.toFile());

        transformer.transform(domSource, streamResult);

        logger.info("File [{}] was converted to [{}] with [{}] changes", xmlFileCopy.getFileName(), format.name(), numberOfChanges);
        ConversionResult conversionResult = new ConversionResult(xmlFileCopy, numberOfChanges);
        numberOfChanges = 0;
        return conversionResult;
    }

    private void processText(NodeList nodes) {
        for(int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element)nodes.item(i);

            if(e.getParentNode().getParentNode().getNodeName().equals("MasterTrack")) {
                continue;
            }
            numberOfChanges++;
            e.setAttribute("Value", processText(e.getAttribute("Value")));
        }
    }

    private void processTextForOnlyNodesWithGivenParent(NodeList nodes, String parentName) {
        for(int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element)nodes.item(i);

            if(!e.getParentNode().getNodeName().equals(parentName)) {
                continue;
            }
            numberOfChanges++;
            e.setAttribute("Value", processText(e.getAttribute("Value")));
        }
    }

    private String processText(String text) {
        switch (format) {
            case UPPERCASE: return text.toUpperCase();
            case LOWERCASE: return text.toLowerCase();
            case TITLECASE: return WordUtils.capitalizeFully(text);
            default: return text;
        }
    }

    public static class ConversionResult {
        private final Path xmlFileConvertedOutput;
        private final int numberOfChanges;

        public ConversionResult(Path xmlFileConvertedOutput, int numberOfChanges) {
            this.xmlFileConvertedOutput = xmlFileConvertedOutput;
            this.numberOfChanges = numberOfChanges;
        }

        public Path getXmlFileConvertedOutput() {
            return xmlFileConvertedOutput;
        }

        public int getNumberOfChanges() {
            return numberOfChanges;
        }
    }
}
