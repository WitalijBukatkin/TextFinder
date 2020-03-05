/*
 * <!--
 *   ~ Copyright (c) 2019. Witalij Bukatkin
 *   ~ Github profile: https://github.com/witalijbukatkin
 *   -->
 */

package com.github.witalijbukatkin.textfinder.element;

import com.github.witalijbukatkin.textfinder.search.SearchEngine;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import static java.awt.BorderLayout.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.FILES_AND_DIRECTORIES;
import static javax.swing.JOptionPane.showMessageDialog;

public class Window extends JFrame {
    private FileTree fileTree;
    private FileTextPane textPane;

    private JTextField searchPattern;
    private JTextField extensionPattern;

    private SearchEngine searchEngine;

    private final int sizeBlock = 10000;

    public Window() {
        setTitle("Finder");
        setSize(900, 700);
        setLayout(new BorderLayout());

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(createTopMenu(), NORTH);

        add(new JScrollPane(createFileTree()), BorderLayout.WEST);
        add(createTextArea(), CENTER);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                try {
                    if(searchEngine != null) {
                        searchEngine.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                System.exit(0);
            }
        });

        setVisible(true);

        openAction();
    }

    public JPanel createSearchPanel(){
        JPanel panel = new JPanel(new FlowLayout());

        extensionPattern = new JTextField(5);
        extensionPattern.setText("txt");

        searchPattern = new JTextField(12);

        JButton search = new JButton("Search");
        search.addActionListener((l) -> searchAction());

        JButton back = new JButton("<");
        back.addActionListener((l) -> textPane.markBack());
        JButton next = new JButton(">");
        next.addActionListener((l) -> textPane.markNext());

        panel.add(new JLabel("Search pattern "));
        panel.add(searchPattern);
        panel.add(new JLabel(" in files with extension "));
        panel.add(extensionPattern);
        panel.add(search);
        panel.add(back);
        panel.add(next);

        return panel;
    }

    public JPanel createTopMenu(){
        JPanel panel = new JPanel(new BorderLayout());

        JButton open = new JButton("Open");
        open.addActionListener((l) -> openAction());

        panel.add(open, WEST);
        panel.add(createSearchPanel(), EAST);

        return panel;
    }

    public FileTree createFileTree(){
        fileTree = new FileTree();

        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fileTreeAction(e);
            }
        });

        return fileTree;
    }

    public FileTextPane createTextArea(){
        textPane = new FileTextPane(sizeBlock);

        return textPane;
    }

    public void searchAction(){
        String text = searchPattern.getText();

        String extension = extensionPattern.getText()
                .replace(" ", "")
                .replace(".", "").toLowerCase();

        if(!text.trim().equals("") && searchEngine != null){

            searchEngine.searchAsync(() -> {
                try{
                    fileTree.update();

                    searchEngine.search(text, extension, ((fileName, countFound) -> {
                        fileTree.addSearchResult(fileName, countFound);
                    }));

                    showMessageDialog(null, "Matches find count: " + searchEngine.getCount());

                    textPane.update();

                    searchEngine.threadAsyncReset();
                } catch (IOException e) {
                    searchEngine.threadAsyncReset();
                }
            });
        }
    }

    public void openAction(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(FILES_AND_DIRECTORIES);

        if(chooser.showOpenDialog(this) == APPROVE_OPTION){
            File file = chooser.getSelectedFile();

            fileTree.setRootAndUpdate(file);

            searchEngine = new SearchEngine(file, sizeBlock);
            textPane.setEngine(searchEngine);

            revalidate();
        }
    }

    public void fileTreeAction(MouseEvent e){
        TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());

        if(path != null) {
            File file = fileTree.getFile(path);

            if(file.exists() && file.isFile()) {
                textPane.update(file);
            }
        }
    }
}
