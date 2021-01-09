package com.vorso.filehandling;

import org.apache.commons.io.FileUtils;
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

import static com.vorso.TrackNameEditor.Format.TITLECASE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileWriterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Path sourceFile;
    Path tempDirectory;
    Path xmlFile;

    @Before
    public void getSourceFile() throws URISyntaxException, IOException {
        xmlFile = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("xml_files/output.xml")).toURI());
        Path originalSourceFile = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("als_files/TrackNameEditor Test Project.als")).toURI());
        //make a copy in a temp dir since we use the path for the copy
        tempDirectory = Files.createTempDirectory(null);
        FileUtils.copyFile(originalSourceFile.toFile(), tempDirectory.resolve(originalSourceFile.getFileName()).toFile());
        sourceFile = tempDirectory.resolve(originalSourceFile.getFileName());
    }

    @Test
    public void shouldWriteFile() throws IOException {
        new FileWriter().writeFileToProjectFolder(sourceFile, xmlFile, TITLECASE);
        List<Path> paths = Files.list(tempDirectory).collect(Collectors.toList());
        //We should have 2 files, the original and the updated version
        assertEquals(2, paths.size());
        //One should have the original filename
        assertTrue(paths.contains(sourceFile));
        paths.remove(sourceFile);
        //The remaining file should be the updated version
        assertTrue(paths.get(0).getFileName().toString().endsWith("TITLECASE.als"));
    }

    @After
    public void cleanUp() {
        FileUtils.deleteQuietly(tempDirectory.toFile());
    }

}