package element;

import util.FileReader;
import bean.Part;
import search.SearchEngine;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.awt.BorderLayout.*;

public class FileTextPane extends JPanel {
    private JTextPane textPane;
    private JLabel pageIndexLabel;

    private FileReader reader;
    private SearchEngine engine;

    private List<Part> parts;
    private int partsIndex;

    private final int sizeBlock;

    private File file;

    private final Highlighter highlighter;
    private final DefaultHighlightPainter painterYellow;
    private final DefaultHighlightPainter painterRed;

    public FileTextPane(int sizeBlock) {
        super(new BorderLayout());
        this.sizeBlock = sizeBlock;
        this.painterYellow = new DefaultHighlightPainter(Color.yellow);
        this.painterRed = new DefaultHighlightPainter(Color.red);

        add(createScrollPanel(), CENTER);
        add(createBottomPanel(), SOUTH);

        this.highlighter = textPane.getHighlighter();
    }

    private JScrollPane createScrollPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        textPane = new JTextPane();
        textPane.setEditable(false);

        panel.add(textPane);

        return new JScrollPane(panel);
    }

    private JPanel createBottomPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        JButton next = new JButton(">");
        pageIndexLabel = new JLabel();
        JButton back = new JButton("<");

        next.addActionListener((l) -> nextAction());
        back.addActionListener((l) -> backAction());

        panel.add(next, EAST);
        panel.add(pageIndexLabel, CENTER);
        panel.add(back, WEST);

        return panel;
    }

    public void setEngine(SearchEngine engine) {
        this.engine = engine;
    }

    public void update(File file){
        this.file = file;

        try {
            reader = new FileReader(file, sizeBlock);

            engine.newCurrent(reader);

            nextAction();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if(file != null) {
            update(file);
        }
    }

    public void search(boolean isNext) {
        if(engine != null && engine.ready()){
            try {
                parts = isNext ? engine.next() : engine.prev();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatePage(String text) {
        partsIndex = 0;

        pageIndexLabel.setText(" " + reader.getPageId()+ "/" + reader.getPageCount());

        textPane.setText(text);
        textPane.moveCaretPosition(0);

        mark();
    }

    public void nextAction() {
        if(reader != null && reader.getPageId() < reader.getPageCount()) {
            search(true);
            updatePage(reader.next());
        }
    }

    public void backAction() {
        if(reader != null && reader.getPageId() > 1) {
            search(false);
            updatePage(reader.prev());
        }
    }

    public void markNext(){
        if(!textPane.getText().isEmpty()) {
            if (partsIndex < parts.size() - 1) {
                partsIndex++;
            } else {
                partsIndex = 0;
            }

            mark();
        }
    }

    public void markBack(){
        if(!textPane.getText().isEmpty()) {
            if (partsIndex == 0) {
                partsIndex = parts.size() - 1;
            } else {
                partsIndex--;
            }

            mark();
        }
    }

    public void mark(){
        highlighter.removeAllHighlights();

        if(parts != null){

            for (int i = 0; i < parts.size(); i++) {
                Part part = parts.get(i);

                if(part != null) {

                    int partStart = part.getIndexStart();
                    int partEnd = part.getIndexEnd() + 1;

                    if (part.getPageStart() != reader.getPageId() - 1) {
                        partStart = 0;
                    }

                    if (part.getPageEnd() != reader.getPageId() -1) {
                        partEnd = textPane.getText().length();
                    }

                    var painter = painterYellow;

                    if(partsIndex == i){
                        painter = painterRed;
                        textPane.moveCaretPosition(partStart);
                    }

                    try {
                        highlighter.addHighlight(partStart, partEnd, painter);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
