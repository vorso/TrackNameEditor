package com.vorso;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class GZipper {

	public static void compressGZIP(Path input, Path output) throws IOException {
		try (GzipCompressorOutputStream out = new GzipCompressorOutputStream(new FileOutputStream(output.toFile()))){
			IOUtils.copy(new FileInputStream(input.toFile()), out);
		}
	}

	public static void decompressGZIP(Path input, Path output) throws IOException {
		try (GzipCompressorInputStream in = new GzipCompressorInputStream(new FileInputStream(input.toFile()))){
			IOUtils.copy(in, new FileOutputStream(output.toFile()));
		}
	}
}
