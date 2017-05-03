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

package edu.stanford.smi.protegex.owl.ui.jena;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.LabeledComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaSchemagenPanel extends JPanel {

    private JFileChooser fileChooser = new JFileChooser(".");

    private JTextField fileTextField;

    private JTextField packageTextField;


    public JenaSchemagenPanel() {
        packageTextField = new JTextField();
        fileTextField = new JTextField();

        fileChooser.setDialogTitle("Select output Java file");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        LabeledComponent lc = new LabeledComponent("Output Java file", fileTextField);
        lc.addHeaderButton(new AbstractAction("Select file...", Icons.getAddIcon()) {
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        add(lc);
        add(Box.createVerticalStrut(8));
        add(new LabeledComponent("Java package", packageTextField));
        //setMinimumSize(new Dimension(400, 150));
    }


    public String getFileName() {
        return fileTextField.getText();
    }


    public String getPackage() {
        return packageTextField.getText();
    }


    public void setFileName(String fileName) {
        fileTextField.setText(fileName);
    }


    public void setPackage(String packageName) {
        packageTextField.setText(packageName);
    }


    private void selectFile() {
        if (fileChooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileTextField.setText(file.toString());
        }
    }
}
