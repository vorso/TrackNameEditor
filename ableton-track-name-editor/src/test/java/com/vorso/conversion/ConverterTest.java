package com.vorso.conversion;

import com.vorso.conversion.Converter.ConversionResult;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.vorso.TrackNameEditor.Format.*;
import static org.junit.Assert.*;

public class ConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Path tempDirectory;
    Path xmlFile;

    @Before
    public void getSourceFile() throws URISyntaxException, IOException {
        Path originalXmlFile = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("xml_files/output.xml")).toURI());
        //make a copy in a temp dir since we use the path for the copy
        tempDirectory = Files.createTempDirectory(null);
        FileUtils.copyFile(originalXmlFile.toFile(), tempDirectory.resolve(originalXmlFile.getFileName()).toFile());
        xmlFile = tempDirectory.resolve(originalXmlFile.getFileName());
    }

    @Test
    public void shouldConvertToLowerCase() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        assertTrue(findFileLine(xmlFile,"<EffectiveName Value=\"a midi TRACK\"/>"));

        ConversionResult conversionResult = new Converter(LOWERCASE, xmlFile).convert();
        assertEquals(66, conversionResult.getNumberOfChanges());

        assertFalse(findFileLine(xmlFile,"<EffectiveName Value=\"a midi TRACK\"/>"));
        assertTrue(findFileLine(xmlFile,"<EffectiveName Value=\"a midi track\"/>"));
    }

    @Test
    public void shouldConvertToUpperCase() throws ParserConfigurationException, IOException, SAXException, TransformerException  {
        assertTrue(findFileLine(xmlFile,"<EffectiveName Value=\"a midi TRACK\"/>"));

        ConversionResult conversionResult = new Converter(UPPERCASE, xmlFile).convert();
        assertEquals(66, conversionResult.getNumberOfChanges());

        assertFalse(findFileLine(xmlFile,"<EffectiveName Value=\"a midi TRACK\"/>"));
        assertTrue(findFileLine(xmlFile,"<EffectiveName Value=\"A MIDI TRACK\"/>"));
    }

    @Test
    public void shouldConvertToTitleCase() throws ParserConfigurationException, IOException, SAXException, TransformerException  {
        assertTrue(findFileLine(xmlFile,"<EffectiveName Value=\"a midi TRACK\"/>"));

        ConversionResult conversionResult = new Converter(TITLECASE, xmlFile).convert();
        assertEquals(66, conversionResult.getNumberOfChanges());

        assertFalse(findFileLine(xmlFile,"<EffectiveName Value=\"a midi TRACK\"/>"));
        assertTrue(findFileLine(xmlFile,"<EffectiveName Value=\"A Midi Track\"/>"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldHandleNullFormatInput() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        new Converter(null, xmlFile).convert();
    }

    @Test(expected = NullPointerException.class)
    public void shouldHandleNullFileInput() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        new Converter(TITLECASE, null).convert();
    }

    private boolean findFileLine(Path sourceFile, String targetString) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(sourceFile);
             LineNumberReader lineReader = new LineNumberReader(reader)) {
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (line.contains(targetString)) {
                    return true;
                }
            }
            return false;
        }
    }
}