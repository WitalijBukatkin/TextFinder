/*
 * <!--
 *   ~ Copyright (c) 2019. Witalij Bukatkin
 *   ~ Github profile: https://github.com/witalijbukatkin
 *   -->
 */

package com.github.witalijbukatkin.textfinder;

import com.github.witalijbukatkin.textfinder.element.Window;

import javax.swing.*;

public class Launcher {


    public static void main(String[] args) {
        applyLookAndFeel();
        new Window();
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException ex) {
            }
        }
    }
}
