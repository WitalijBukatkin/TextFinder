import element.Window;

import javax.swing.*;

public class Launcher {


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
        catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {

        }

        new Window();
    }
}
