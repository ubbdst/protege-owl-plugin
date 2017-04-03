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

package edu.stanford.smi.protegex.owl.ui.repository.wizard.impl;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ExtensionFilter;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileBrowserPanel extends JPanel {

    private Action browseAction;

    private JTextField fileNameField;

    private boolean showOnlyFolders;

    private WizardPage wizardPage;

    private JCheckBox forceReadOnlyCheckBox;

	private JCheckBox recursiveCheckBox;


    public FileBrowserPanel(WizardPage wizardPage, String label, boolean showOnlyFolders, String helpText) {
        this.wizardPage = wizardPage;
        this.showOnlyFolders = showOnlyFolders;
        fileNameField = new JTextField();
        LabeledComponent lc = new LabeledComponent(label, fileNameField);
        browseAction = new AbstractAction("Browse...", Icons.getAddIcon()) {
            public void actionPerformed(ActionEvent e) {
                browse();
            }
        };
        wizardPage.setPageComplete(false);
        lc.addHeaderButton(browseAction);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel holder = new JPanel(new BorderLayout(7, 7));
        holder.add(lc, BorderLayout.NORTH);
        JPanel checkBoxHolder = new JPanel(new BorderLayout(3, 3));
	    holder.add(checkBoxHolder, BorderLayout.SOUTH);
        forceReadOnlyCheckBox = new JCheckBox("Force Read-Only", true);
        checkBoxHolder.add(forceReadOnlyCheckBox, BorderLayout.NORTH);
	    recursiveCheckBox = new JCheckBox("Include sub-folders");
	    if(showOnlyFolders) {
		    checkBoxHolder.add(recursiveCheckBox, BorderLayout.SOUTH);
	    }
	    setLayout(new BorderLayout(12, 12));
        add(holder, BorderLayout.NORTH);
        if (helpText != null) {
            add(OWLUI.createHelpPanel(helpText, null, OWLUI.WIZARD_HELP_HEIGHT),
                    BorderLayout.SOUTH);
        }
        fileNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validateFileName();
            }


            public void removeUpdate(DocumentEvent e) {
                validateFileName();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        });
    }


    public boolean isForceReadOnly() {
        return forceReadOnlyCheckBox.isSelected();
    }

	public boolean isRecursive() {
		return recursiveCheckBox.isSelected();
	}


    public void browse() {
    	 java.util.List<String> extensions = (java.util.List<String>) Arrays.asList((new String[]{"owl" , "rdfs", "rdf"}));
 	    ExtensionFilter extensionFilter = new ExtensionFilter(extensions.iterator(), "Select OWL or RDF(S) files");
    	JFileChooser chooser = ComponentFactory.createFileChooser("OWL or RDF(S) file", extensionFilter);
        if (showOnlyFolders) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
        	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileNameField.setText(getPathName(chooser.getSelectedFile()));
        }
    }


    public String getPathName(File file) {
        return file.getPath();
    }


    public File getFile() {
        return new File(fileNameField.getText());
    }


    public boolean validateFileName() {
        File f = new File(fileNameField.getText());
        if ((f.isDirectory() == showOnlyFolders) == false) {
            wizardPage.setPageComplete(false);
            return false;
        }
        else {
            wizardPage.setPageComplete(true);
            return true;
        }
    }


}

