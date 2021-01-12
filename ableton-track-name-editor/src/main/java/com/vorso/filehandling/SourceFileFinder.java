package com.vorso.filehandling;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Path;
import java.util.Optional;

import static com.vorso.TrackNameEditor.*;

public class SourceFileFinder {

    //optional starting dir?
    public Optional<Path> getAlsPath() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        System.out.println(ANSI_YELLOW + "Open an Ableton .als file:" + ANSI_RESET);

        JFileChooser projectFileChooser = new JFileChooser();
        projectFileChooser.setDialogTitle("Select an Ableton .als file:");
        projectFileChooser.setMultiSelectionEnabled(false);
        projectFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        projectFileChooser.setAcceptAllFileFilterUsed(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("als files", "als");
        projectFileChooser.addChoosableFileFilter(filter);
        int returnValue = projectFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return Optional.of(projectFileChooser.getSelectedFile().toPath());
        }
        System.out.println(ANSI_RED + "No file selected, operation cancelled" + ANSI_RESET);
        return Optional.empty();
    }
}
