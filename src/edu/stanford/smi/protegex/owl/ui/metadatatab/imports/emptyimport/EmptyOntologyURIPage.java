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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports.emptyimport;

import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.metadatatab.OntologyURIPanel;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizard;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizardPage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URI;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EmptyOntologyURIPage extends OWLWizardPage {

    private OntologyURIPanel ontologyURIPanel;

    private OWLModel owlModel;


    public EmptyOntologyURIPage(OWLWizard wizard, OWLModel owlModel) {
        super("Specify Ontology URI", wizard);
        this.owlModel = owlModel;
        createUI();
        setPageComplete(validateData());
    }


    private void createUI() {
        ontologyURIPanel = new OntologyURIPanel(false, false);
        ontologyURIPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setPageComplete(validateData());
            }
        });
        getContentComponent().add(ontologyURIPanel, BorderLayout.NORTH);
        setHelpText("Specifying the imported ontology URI", HELP_TEXT);
    }


    private boolean validateData() {
        if (ontologyURIPanel.getOntologyURI() != null) {
            URI uri = ontologyURIPanel.getOntologyURI();
            return uri != null;
        }
        else {
            return false;
        }
    }


    public WizardPage getNextPage() {
        return new LocalFileWizardPage(getOWLWizard(), owlModel);
    }


    public URI getOntologyURI() {
        return ontologyURIPanel.getOntologyURI();
    }


    public void onFinish() {
        super.onFinish();
        ((EmptyImportWizard) getWizard()).setOntologyURI(getOntologyURI());
    }


    private static final String HELP_TEXT = "<p>Please specify the URI of the new empty " +
            "ontology.</p>" +
            "<p>In general, the URI " +
            "should be an http URL that points to the location on the " +
            "web where the new ontology will eventually be made available.</p>";
}

