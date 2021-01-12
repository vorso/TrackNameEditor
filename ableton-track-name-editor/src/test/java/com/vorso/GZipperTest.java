package com.vorso;

import com.vorso.filehandling.FileExtractorTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.zip.GZIPInputStream.GZIP_MAGIC;
import static org.junit.Assert.*;

public class GZipperTest {

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

    /**
     * unZip is tested in
     * @see FileExtractorTest#shouldExtractFileAsXML()
     */
    @Test
    public void shouldZip() throws IOException {
        GZipper.compressGZIP(xmlFile, tempDirectory.resolve("output.als"));
        List<Path> pathList = Files.list(tempDirectory)
                .filter(path -> FilenameUtils.getExtension(path.getFileName().toString()).equals("als"))
                .collect(Collectors.toList());
        assertEquals(1, pathList.size());
        //test gzip by reading the file and checking the header
        assertTrue(gZipCheck(Files.readAllBytes(pathList.get(0))));
    }

    public boolean gZipCheck(byte[] bytes) {
        if ((bytes == null) || (bytes.length < 2)) {
            return false;
        } else {
            return ((bytes[0] == (byte) (GZIP_MAGIC)) && (bytes[1] == (byte) (GZIP_MAGIC >> 8)));
        }
    }

    @After
    public void cleanUp() {
        FileUtils.deleteQuietly(tempDirectory.toFile());
    }
}