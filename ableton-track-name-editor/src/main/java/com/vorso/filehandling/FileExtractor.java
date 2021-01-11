package com.vorso.filehandling;

import com.vorso.GZipper;
import org.apache.commons.compress.utils.FileNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileExtractor {

    static final Logger logger = LoggerFactory.getLogger(FileExtractor.class);

    public Path getXmlCopy(Path alsFilePath) throws IOException {
        Path xmlFileName = Paths.get(FileNameUtils.getBaseName(alsFilePath.getFileName().toString()).concat(".xml"));

        Path xmlFilePath = Files.createTempDirectory(null).resolve(xmlFileName);

        GZipper.decompressGZIP(alsFilePath, xmlFilePath);
        logger.info("File [{}] was unzipped to [{}]", alsFilePath.getFileName(), xmlFilePath);
        return xmlFilePath;
    }
}
