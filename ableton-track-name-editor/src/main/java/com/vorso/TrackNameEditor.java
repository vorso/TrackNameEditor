package com.vorso;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.vorso.conversion.Converter;
import com.vorso.conversion.Converter.ConversionResult;
import com.vorso.filehandling.FileExtractor;
import com.vorso.filehandling.FileWriter;
import com.vorso.filehandling.SourceFileFinder;
import org.xml.sax.SAXException;

/**
 * Copyright 2021 Vorso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *  \\   //
 *   \\ //
 *    \//
 *    /\\
 *   // \\
 *  //   \\
 *  \\   //
 *   \\ //
 *    \//
 *    //\
 *   // \\
 *  //   \\
 *  \\
 *   \\
 *    \\
 *    // __
 *   // //\\
 *  // // //  
 *  \\// //
 *      //
 *     // 
 *    //
 *   //  \\
 *  //    \\
 *  \\    //
 *   \\  //
 *    \\//
 * 
 * Created by Vorso
 *  
 * Soundcloud:  https://soundcloud.com/vorso
 * Spotify:     https://open.spotify.com/artist/5Og6MsfuDPnFYd1asgHXdH
 * Twitter:     https://twitter.com/vorsomusic
 * Facebook:    https://facebook.com/vorsomusic
 * Youtube:     https://www.youtube.com/c/VorsoMusic
 * Bandcamp:    https://vorso.bandcamp.com/releases
 * 
 * Hope this helps! :^)
 */
public class TrackNameEditor {
    public static OS USER_OS;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static File Ableton;

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            USER_OS = checkOS();

            Path updatedAlsFile = new TrackNameEditor().convertFiles();

            if(checkPropertiesFileExists() && parseProperties()) {
                userInputOpenAbleton(updatedAlsFile);
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Failed to convert [" + e + "]" + ANSI_RESET);
        }
    }

    public enum OS {
        WINDOWS,
        MAC
    }

    public enum Format {
        UPPERCASE,
        LOWERCASE,
        TITLECASE
    }

    private Path convertFiles() throws IOException, ParserConfigurationException, SAXException, TransformerException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        Path alsFile = new SourceFileFinder().getAlsPath().orElseThrow(() -> new RuntimeException("No file selected, Operation cancelled"));
        Path xmlFile = new FileExtractor().getXmlCopy(alsFile);

        Format format = userInputSelectMode().orElseThrow(() -> new RuntimeException("No format selected, Operation cancelled"));

        ConversionResult conversionResult = new Converter(format, xmlFile).convert();

        Path updatedAlsFile = new FileWriter().writeFileToProjectFolder(alsFile, conversionResult.getXmlFileConvertedOutput(), format);

        System.out.println("New .als project file " + ANSI_GREEN + updatedAlsFile.getFileName() + ANSI_RESET + " created in Ableton Live Project Folder.");
        System.out.println("Changed " + ANSI_GREEN + conversionResult.getNumberOfChanges() + ANSI_RESET + " entries in the project XML.");
        System.out.println("Thank you for using :)");
        

        return updatedAlsFile;
    }

    public static void userInputOpenAbleton(Path updatedAlsFile) throws IOException {
        boolean yn = true;
        boolean open = false;
            
        System.out.println(ANSI_YELLOW + "Open the project file? " + ANSI_PURPLE + "(y/n)" + ANSI_RESET);

        while(yn) {
            switch(scanner.nextLine()) {
            case "y":
                open = true;
                yn = false;
                break;
            case "n":
                open = false;
                yn = false;
                break;
            default:
                System.out.println(ANSI_YELLOW + "Please enter " + ANSI_PURPLE + "y" 
                + ANSI_YELLOW + " or " + ANSI_PURPLE + "n" + ANSI_YELLOW 
                + ":" + ANSI_RESET);
                break;
            }
        }
        scanner.close();
        if(open) {
            Process openAbleton = OpenAbleton(updatedAlsFile);
        }
    }

    public Optional<Format> userInputSelectMode() {
        System.out.println(ANSI_YELLOW + "Please select a mode: " + ANSI_PURPLE + "(1/2/3)" + ANSI_RESET);
        System.out.println("    " + ANSI_PURPLE + "1." + ANSI_RESET + " UPPER CASE");
        System.out.println("    " + ANSI_PURPLE + "2." + ANSI_RESET + " lower case");
        System.out.println("    " + ANSI_PURPLE + "3." + ANSI_RESET + " Title Case");

        switch (scanner.nextLine()) {
            case "1":
                return Optional.of(Format.UPPERCASE);
            case "2":
                return Optional.of(Format.LOWERCASE);
            case "3":
                return Optional.of(Format.TITLECASE);
            default:
                return Optional.empty();
        }
    }

    public static Process OpenAbleton(Path projectFile) throws IOException {
        String[] params = {Ableton.getAbsolutePath(), projectFile.toFile().getAbsolutePath()};
        return Runtime.getRuntime().exec(params);
    }

    public static OS checkOS(){
        String OSString = System.getProperty("os.name").toLowerCase();
        switch(OSString) {
            case "mac os x": return OS.MAC;
            case "windows": return OS.WINDOWS;
        }
        return OS.WINDOWS;
    }

    public static Boolean checkPropertiesFileExists() {
        File properties = new File("paths.properties");
        if(!properties.isFile()) {
            System.out.println(ANSI_RED + "Properties file not found. Ableton not launching" + ANSI_RESET);
            return false;
        }
        return true;
    }

     /** parseProperties
     * 
     * Sets the Ableton Path
     *
     * 
     * @return TRUE if set correctly, FALSE if not
     */
    private static Boolean parseProperties() throws IOException {
           
        List<String> lines = Files.readAllLines(Paths.get("paths.properties"), StandardCharsets.UTF_8);
        String abletonPath = "";

        for (String line : lines) {
            if (line.contains("ABLETON_PATH")) {
                abletonPath = line.replace("ABLETON_PATH:{", "").replace("}", "").replace(",", "");
            }
        }

        if("".equals(abletonPath)) {
            System.out.println("Ableton Live 10 path not set correctly.");
            return false;
        }

        Ableton = new File(parsePath(abletonPath));

        if(!Ableton.isFile()) {
            System.out.println(ANSI_RED + "Ableton application not found in supplied Ableton Folder" + ANSI_RESET);
            return false;
        }

        return true;
    }

    public static String parsePath(String path) {
        switch(USER_OS) {
            case MAC: return path;
            case WINDOWS: return path.replace("/", "\\");
            default: return path;
        }
    }

}

