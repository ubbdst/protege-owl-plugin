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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 1, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class URLImportPage extends AbstractImportStartWizardPage {

    private JTextField urlField;


    public URLImportPage(ImportWizard wizard) {
        super("URL Import Page", wizard);
        createUI();
    }


    private void createUI() {
        setHelpText("Importing an ontology from the web", HELP_TEXT);
        urlField = new JTextField();
        urlField.setColumns(40);
        urlField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                setPageComplete(validateData());
            }


            public void removeUpdate(DocumentEvent e) {
                setPageComplete(validateData());
            }


            public void changedUpdate(DocumentEvent e) {
                setPageComplete(validateData());
            }
        });
        LabeledComponent lc = new LabeledComponent("Ontology URL (http://...)", urlField);
        getContentComponent().add(lc, BorderLayout.NORTH);
        setPageComplete(validateData());
    }


	public void nextPressed() {
		try {
			URL url = new URL(urlField.getText().trim());
			getImportWizard().getImportData().addImportEntry(new URLImportEntry(url));
		}
		catch(MalformedURLException e) {
                  Log.getLogger().log(Level.SEVERE, "Exception caught", e);
		}
	}


    private boolean validateData() {
        if (urlField.getText().trim().length() == 0) {
            return false;
        }
        try {
	        // The URL represents the URL of the document, not the ontology!
            URL url = new URL(urlField.getText().trim());
            return url.getHost().length() > 0 && url.getProtocol() != null && url.getProtocol().equals("http");
        }
        catch (MalformedURLException e) {
            return false;
        }


    }


    public WizardPage getNextPage() {
        return new ImportVerificationPage(getImportWizard());
    }


    private static final String HELP_TEXT = "<p>Please specify the URL that points to the document " +
            "containing the ontology to be imported. The URL can " +
            "be specified by typing it into the above text field, " +
            "or it could be copied and pasted from a web browser " +
            "for example.</p>";
}

