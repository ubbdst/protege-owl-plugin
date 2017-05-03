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

package edu.stanford.smi.protegex.owl.ui.refactoring;

import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RenameAcrossFilesPanel extends JPanel {

    private JFileChooser fileChooser;

    private SelectableList filesList;

    private DefaultListModel filesListModel;

    private JTextField newNameField;


    public RenameAcrossFilesPanel(RDFResource instance, String[] files) {

        filesList = new SelectableList();
        filesListModel = new DefaultListModel();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            filesListModel.addElement(new File(fileName));
        }
        filesList.setModel(filesListModel);

        newNameField = new JTextField(instance.getName());

        OWLLabeledComponent lc = new OWLLabeledComponent("Files to update references", new JScrollPane(filesList));
        lc.addHeaderButton(new AbstractAction("Add file...", OWLIcons.getAddIcon("File")) {
            public void actionPerformed(ActionEvent e) {
                addFile();
            }
        });
        lc.addHeaderButton(new AllowableAction("Remove selected file", OWLIcons.getRemoveIcon("File"), filesList) {
            public void actionPerformed(ActionEvent e) {
                removeFile();
            }
        });

        setLayout(new BorderLayout(8, 8));
        add(BorderLayout.NORTH, new LabeledComponent("New name", newNameField));
        add(BorderLayout.CENTER, lc);

        setPreferredSize(new Dimension(400, 300));
    }


    private void addFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filesListModel.addElement(file);
        }
    }


    public String getNewName() {
        return newNameField.getText();
    }


    public Iterator getSelectedFiles() {
        ArrayList result = new ArrayList();
        for (int i = 0; i < filesList.getModel().getSize(); i++) {
            result.add(filesList.getModel().getElementAt(i));
        }
        return result.iterator();
    }


    private void removeFile() {
        File file = (File) filesList.getSelectedValue();
        filesListModel.removeElement(file);
    }
}
