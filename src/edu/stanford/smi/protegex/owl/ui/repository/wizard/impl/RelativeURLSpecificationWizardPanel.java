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

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.RelativeFolderRepository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryCreatorWizardPanel;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 28, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class  RelativeURLSpecificationWizardPanel extends RepositoryCreatorWizardPanel {

    private JTextField textField;

    private JCheckBox forceReadOnlyCheckBox;

    private OWLModel model;

    private WizardPage wizardPage;


    public RelativeURLSpecificationWizardPanel(WizardPage wizardPage, OWLModel model) {
        this.model = model;
        this.wizardPage = wizardPage;
        setLayout(new BorderLayout(7, 7));
        textField = new JTextField();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateWizardPageState();
            }


            public void removeUpdate(DocumentEvent e) {
                updateWizardPageState();
            }


            public void changedUpdate(DocumentEvent e) {
                updateWizardPageState();
            }
        });
        JPanel holderPanel = new JPanel(new BorderLayout(3, 3));
        LabeledComponent lc = new LabeledComponent("Relative URL", textField);
        holderPanel.add(lc, BorderLayout.NORTH);
        holderPanel.add(forceReadOnlyCheckBox = new JCheckBox("Force Read-Only"), BorderLayout.SOUTH);
        add(holderPanel, BorderLayout.NORTH);
        add(OWLUI.createHelpPanel(getDocumentation(), null, OWLUI.WIZARD_HELP_HEIGHT), BorderLayout.SOUTH);
        updateWizardPageState();
    }

	private void updateWizardPageState() {
		wizardPage.setPageComplete(validateData());
	}

    public Repository createRepository() {
        try {
            return new RelativeFolderRepository(getBaseURL(),
                    getRelativePath(),
                    isForcedReadOnlySelected());
        }
        catch (Exception e) {
            return null;
        }
    }


	protected URL getBaseURL() {
        try {
            URI projectURI = model.getProject().getProjectURI();
            File f = new File(projectURI).getParentFile();
            // Need to use toURI().toURL() as a Java bug work around
	        // (URL is not escaped correctly using the to URL method).
	        return f.toURI().toURL();
        }
        catch (MalformedURLException e) {
            return null;
        }
    }


    public boolean validateData() {
	    // Force the user to enter something - even if they want
	    // the project directory they should enter a '.'
	    if(textField.getText().trim().length() == 0) {
		    return false;
	    }
        try {
	        URL baseURL = getBaseURL();
            URL url = new URL(baseURL, textField.getText().trim());
	        File f = new File(new URI(url.toString()));
            boolean exists = f.exists();
            return exists;
        }
        catch (MalformedURLException e) {
            return false;
        }
	    catch(URISyntaxException e) {
	        return false;
        }
    }

    protected boolean isForcedReadOnlySelected() {
    	 return forceReadOnlyCheckBox.isSelected();
    }
    
    protected String getRelativePath() {
    	return textField.getText().trim();
    }
    
    protected OWLModel getOWLModel() {
    	return model;
    }
    
    protected String getDocumentation(){
    	return HELP_TEXT;
    }
    
    private static final String HELP_TEXT = "<p>Please specify a relative <b>URL</b> that points " +
            "to a folder containing ontologies.</p>" +
            "<p>The URL should be relative to the folder containing " +
            "the pprj/owl file.  For example if the pprj/owl file " +
            "is located at /Users/mrowl/documents/ontologies/myOnt.owl, the relative " +
            "URL ./../ontlibrary would specify the ontlibrary folder in " +
            "the parent folder of /Users/mrowl/documents/ontologies i.e." +
            " /Users/mrowl/documents/ontlibrary</p>" +
            "<p>Note that the path separator for URLs is the forward " +
            "slash '/', and spaces must be replaced with \"%20\".</p>";
}

