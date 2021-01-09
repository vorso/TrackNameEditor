package com.vorso.filehandling;

import com.vorso.GZipper;
import com.vorso.TrackNameEditor.Format;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriter {

    static final Logger logger = LogManager.getLogger(FileExtractor.class);

    public Path writeFileToProjectFolder(Path originalProjectFile, Path temporaryXmlFile, Format format) throws IOException {
        Path targetFile = Paths.get(originalProjectFile.toFile().getAbsolutePath().replace(".als", " - " + format.name() + ".als"));
        GZipper.compressGZIP(temporaryXmlFile, targetFile);

        logger.info("File [{}] was zipped to [{}]", temporaryXmlFile.getFileName(), targetFile.getFileName());
        return targetFile;
    }
}
