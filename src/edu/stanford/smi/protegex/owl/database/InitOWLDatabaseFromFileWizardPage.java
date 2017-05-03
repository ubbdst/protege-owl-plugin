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

package edu.stanford.smi.protegex.owl.database;

import java.awt.BorderLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.stanford.smi.protege.util.URIField;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InitOWLDatabaseFromFileWizardPage extends WizardPage {

    private static final long serialVersionUID = -2234031363650243817L;

    private CreateOWLDatabaseFromFileProjectPlugin plugin;

    private URIField uriField;


    public InitOWLDatabaseFromFileWizardPage(Wizard wizard, CreateOWLDatabaseFromFileProjectPlugin plugin) {
        super("Specify Ontology File", wizard);
        this.plugin = plugin;
        uriField = new URIField("URI of OWL file to populate the new database with",
                null, ".owl", "OWL Files");
        uriField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setPageComplete(true);
            }
        });
        add(BorderLayout.CENTER, uriField);
        add(BorderLayout.SOUTH,
                OWLUI.createHelpPanel("This allows you to use the Protege-OWL parser to load OWL/RDF files directly into a new database.  In theory, this parser should be able to handle files of arbitrary size, because it loads the file triple-by-triple.", "Create OWL Database from a File"));
        setPageComplete(false);
    }


    @Override
    public WizardPage getNextPage() {
        OWLDatabaseWizardPageExistingSources wizard = new OWLDatabaseWizardPageExistingSources(getWizard(), plugin);
        wizard.setFileToDatabase(true);
        return wizard;
    }


    @Override
    public void onFinish() {
        plugin.setOntologyInputSource(uriField.getAbsoluteURI());
    }
}
