/*
 * Copyright (c) 2020. Witalij Bukatkin
 * Github profile: https://github.com/witalijbukatkin
 */

package com.github.witalijbukatkin.textfinder.element;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Collectors;

public class FileTree extends JTree {
    private File rootFile;
    private TreeNode rootNode;

    public FileTree(){
        setModel(null);
    }

    public void setRootAndUpdate(File root){
        this.rootFile = root;

        update();
    }

    public void update(){
        if(rootFile.exists() && rootFile.canRead()) {
            TreeModel model = new DefaultTreeModel(createTreeNode(rootFile));
            this.rootNode = (DefaultMutableTreeNode) model.getRoot();
            setModel(model);
        }
    }

    private MutableTreeNode createTreeNode(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());

            if(files != null) {
                for (File item : files) {
                    if(item.exists() && item.canRead()) {
                        node.add(createTreeNode(item));
                    }
                }
            }

            return node;
        }
        else {
            return new DefaultMutableTreeNode(file.getName());
        }
    }

    public void addSearchResult(String name, int countSearch){
        addSearchResult((DefaultMutableTreeNode) rootNode, name, countSearch);
    }

    public void addSearchResult(DefaultMutableTreeNode node, String name, int countSearch){
        if(node != null) {
            if (node.getChildCount() > 0) {
                Enumeration<TreeNode> children = node.children();

                while (children.hasMoreElements()){
                    addSearchResult((DefaultMutableTreeNode) children.nextElement(), name, countSearch);
                }
            } else {
                if(name.equals(node.getUserObject())){
                    node.add(new DefaultMutableTreeNode("founded " + countSearch));
                }
            }
        }
    }

    public File getFile(TreePath treePath){
        String path = Arrays.stream(treePath.getPath()).parallel()
                .skip(1)
                .map(Object::toString)
                .collect(Collectors.joining("/"));

        return rootFile.toPath().resolve(path).toFile();
    }
}
