package de.hshannover.dqgui.core;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javafx.application.Application;

public final class Starter {

    private Starter() {}

    public static void main(String[] args) {
        try {
            Application.launch(App.class);
        } catch(Error e) {
            e.printStackTrace();
            if(e instanceof java.lang.NoClassDefFoundError) {
                String msg = "Failed to load DQGUI due to ClassNotFoundException.\nThis is very likely because the JavaFX runtime is missing from your system.\nDue to DQGUI only supporting Java 8 at the moment you'll either need to use the Oracle JDK or install the javafx runtime yourself (OpenJDK 8 openjfx package).";
                System.err.println(msg);
                JFrame j = new JFrame();
                j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JOptionPane.showMessageDialog(j, 
                        msg, 
                        "Failed to load DQGUI", 
                        JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }
    }
}
