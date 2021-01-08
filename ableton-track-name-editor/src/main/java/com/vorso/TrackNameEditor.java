package com.vorso;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
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
    public static File Project_File;
    public static File alteredProjectFile;
    public static Function function;
    public static boolean gotAbleton = false;
    public static Scanner scanner = new Scanner(System.in);
    public static int numberOfChanges = 0;

public static void main(String[] args) throws IOException, InterruptedException {

            try {
                USER_OS = checkOS();

                gotAbleton = checkPropertiesFileExists() && parseProperties();

                System.out.println(ANSI_YELLOW + "Open an Ableton .als file:" + ANSI_RESET);

                JFileChooser projectFileChooser = new JFileChooser();
                int returnValue = projectFileChooser.showOpenDialog(null);
                
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    Project_File = projectFileChooser.getSelectedFile();
                    String extension = Project_File.getAbsolutePath().split("\\.")[1];

                    if (extension.equals("als")) {
                        System.out.println("Opening file...");

                        userInputSelectMode();

                        String ALSPath = Project_File.getAbsolutePath();
                        String XMLPath = ALSPath.split("\\.")[0].concat(".xml");
                        GZipper.decompressGzipFile(ALSPath, XMLPath);
                        File XMLFile = new File(XMLPath);
                        
                        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();  
                        Document document = documentBuilder.parse(XMLFile);  
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

                        //File xml = new File()
                        //compressGzipFile

                        StreamResult streamResult = new StreamResult(XMLFile);
            
                        transformer.transform(domSource, streamResult);

                        GZipper.compressGzipFile(XMLFile.getAbsolutePath(), Project_File.getAbsolutePath().replace(".als", " - " + function.name() + ".als"));

                        alteredProjectFile = new File(Project_File.getAbsolutePath().replace(".als", " - " + function.name() + ".als"));
            
                        System.out.println("New .als project file " + ANSI_GREEN + alteredProjectFile.getName() + ANSI_RESET + " created in Ableton Live Project Folder.");

                        System.out.println("Changed " + ANSI_GREEN + numberOfChanges + ANSI_RESET + " entries in the project XML.");

                        XMLFile.delete();

                    } else {
                        System.out.println(ANSI_RED + "Selected file was not a .als file, operation cancelled" + ANSI_RESET);
                        return;
                    }
                } else {
                    System.out.println("Operation Cancelled");
                    return;
                }   
                if(gotAbleton) {
                    userInputOpenAbleton();
                }
                System.out.println("Thank you for using :)");
                scanner.close();
            }

            catch (Exception e) {
                System.out.println(ANSI_RED + "Encountered an error." + ANSI_RESET);
                scanner.close();
                e.printStackTrace();
            } 
        }
        
    public static void userInputOpenAbleton() throws IOException {
        Boolean yn = true;
        Boolean open = false;
            
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

        if(open) {
            Process openAbleton = OpenAbleton(alteredProjectFile);
        }
    }

    public static void userInputSelectMode() {
        Boolean waiting = true;

        System.out.println(ANSI_YELLOW + "Please select a mode: " + ANSI_PURPLE + "(1/2/3)" + ANSI_RESET);
        System.out.println("    " + ANSI_PURPLE + "1." + ANSI_RESET + " UPPER CASE");
        System.out.println("    " + ANSI_PURPLE + "2." + ANSI_RESET + " lower case");
        System.out.println("    " + ANSI_PURPLE + "3." + ANSI_RESET + " Title Case");
        
        while(waiting) {
            switch(scanner.nextLine()) {
            case "1":
                function = Function.UPPERCASE;
                waiting = false;
                break;
            case "2":
                function = Function.LOWERCASE;
                waiting = false;
                break;
            case "3":
                function = Function.TITLECASE;
                waiting = false;
                break;
            default:
                System.out.println(ANSI_YELLOW + "Please enter " + ANSI_PURPLE + "1/2/3" + ANSI_RESET); 
                break;
            }
        }

    }

    public static Process OpenAbleton(File projectFile) throws IOException {
        String[] params = {Ableton.getAbsolutePath(), projectFile.getAbsolutePath()};
        return Runtime.getRuntime().exec(params);
    }

    public static void processText(NodeList nodes) {
        for(int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element)nodes.item(i);

            if(e.getParentNode().getParentNode().getNodeName() == "MasterTrack") {
                continue;
            }
            numberOfChanges++;
            e.setAttribute("Value", procText(e.getAttribute("Value")));
        }
    }

    public static void processTextForOnlyNodesWithGivenParent(NodeList nodes, String parentName) {
        for(int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element)nodes.item(i);

            if(!e.getParentNode().getNodeName().equals(parentName)) {
                continue;
            }
            numberOfChanges++;
            e.setAttribute("Value", procText(e.getAttribute("Value")));
        }
    }

    public static String procText(String text) {
        switch (function) {
            case UPPERCASE: return text.toUpperCase();
            case LOWERCASE: return text.toLowerCase();
            case TITLECASE: return convertToTitleCase(text);
            default: return text;
        }
    }
              
    static String convertToTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return UCharacter.toTitleCase(input, BreakIterator.getTitleInstance());
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
            System.out.println(ANSI_RED + "Properties file not found." + ANSI_RESET);
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
     * @throws IOException
     */
    private static Boolean parseProperties() throws IOException {
           
        List<String> lines = Files.readAllLines(Paths.get("paths.properties"), StandardCharsets.UTF_8);
        String abletonPath = "";

        for(int i = 0; i < lines.size(); i++) {
            if(lines.get(i).contains("ABLETON_PATH")) {
                abletonPath = lines.get(i).replace("ABLETON_PATH:{", "").replace("}", "").replace(",", "");
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

