package com.vorso.filehandling;

import org.apache.commons.compress.utils.FileNameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileExtractorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Path sourceFile;

    @Before
    public void getSourceFile() throws URISyntaxException {
        sourceFile = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("als_files/TrackNameEditor Test Project.als")).toURI());
    }

    @Test
    public void shouldExtractFileAsXML() throws IOException, ParserConfigurationException, SAXException {
        Path generatedFile = new FileExtractor().getXmlCopy(sourceFile);

        assertEquals("xml", FileNameUtils.getExtension(generatedFile.getFileName().toString()));
        //Check the generated file is can be parsed
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(generatedFile.toFile());

        assertNotNull(document);
    }

    @Test(expected = IOException.class)
    public void shouldFailToExtractFile() throws IOException {
        new FileExtractor().getXmlCopy(Paths.get("potato"));
    }
}