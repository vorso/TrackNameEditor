package com.vorso.filehandling;

import org.junit.Test;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.Assert.*;

public class SourceFileFinderTest {

    @Test
    public void shouldFailToSelectAlsFile() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        new Thread(() -> {
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            robot.delay(1000);
            robot.keyPress(KeyEvent.VK_ESCAPE);
        }).start();
        Optional<Path> path = new SourceFileFinder().getAlsPath();
        assertFalse(path.isPresent());
    }
}