/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2007.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.owl.javacode;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.LabeledComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JavaCodeGeneratorPanel extends JPanel {

    private static final long serialVersionUID = 3706024179518110486L;

    private JCheckBox abstractCheckBox;

    private JTextField factoryClassNameTextField;

    private JFileChooser fileChooser = new JFileChooser(".");

    private EditableJavaCodeGeneratorOptions options;

    private JTextField packageTextField;

    private JTextField rootFolderTextField;

    private JCheckBox setCheckBox;

    private JCheckBox prefixCheckBox;


    public JavaCodeGeneratorPanel(EditableJavaCodeGeneratorOptions options) {

        this.options = options;

        packageTextField = new JTextField();
        if (options.getPackage() != null) {
            packageTextField.setText(options.getPackage());
        }
        rootFolderTextField = new JTextField();
        if (options.getOutputFolder() != null) {
            rootFolderTextField.setText(options.getOutputFolder().getAbsolutePath());
        }

        fileChooser.setDialogTitle("Select output folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        factoryClassNameTextField = new JTextField();
        if (options.getFactoryClassName() != null) {
            factoryClassNameTextField.setText(options.getFactoryClassName());
        }

        abstractCheckBox = new JCheckBox("Create abstract base files (e.g., Person_)");
        abstractCheckBox.setSelected(options.getAbstractMode());

        setCheckBox = new JCheckBox("Return Set instead of Collection");
        setCheckBox.setSelected(options.getSetMode());

        prefixCheckBox = new JCheckBox("Include prefixes in generated Java names");
        prefixCheckBox.setSelected(options.getPrefixMode());
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        LabeledComponent lc = new LabeledComponent("Root output folder", rootFolderTextField);
        lc.addHeaderButton(new AbstractAction("Select folder...", Icons.getAddIcon()) {
            private static final long serialVersionUID = 2754013804198315963L;

            public void actionPerformed(ActionEvent e) {
                selectFolder();
            }
        });
        add(lc);
        add(Box.createVerticalStrut(8));
        add(new LabeledComponent("Java package", packageTextField));
        add(Box.createVerticalStrut(8));
        add(new LabeledComponent("Factory class name", factoryClassNameTextField));
        add(Box.createVerticalStrut(8));
        add(createCheckBoxPanel(abstractCheckBox));
        add(Box.createVerticalStrut(8));
        add(createCheckBoxPanel(setCheckBox));
        add(Box.createVerticalStrut(8));
        add(createCheckBoxPanel(prefixCheckBox));
        add(Box.createVerticalStrut(8));
    }


    private JPanel createCheckBoxPanel(Component comp) {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(BorderLayout.WEST, comp);
        southPanel.add(BorderLayout.CENTER, new JPanel());
        southPanel.setPreferredSize(new Dimension(300, 24));
        return southPanel;
    }


    public void ok() {
        File newFile = null;
        String rootFolder = rootFolderTextField.getText().trim();
        if (rootFolder.length() > 0) {
            newFile = new File(rootFolder);
        }
        options.setOutputFolder(newFile);

        options.setAbstractMode(abstractCheckBox.isSelected());
        options.setSetMode(setCheckBox.isSelected());
        options.setPrefixMode(prefixCheckBox.isSelected());
        options.setFactoryClassName(factoryClassNameTextField.getText());

        String pack = packageTextField.getText().trim();
        options.setPackage(pack.length() > 0 ? pack : null);
    }


    private void selectFolder() {
        if (fileChooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            rootFolderTextField.setText(file.toString());
        }
    }
}
