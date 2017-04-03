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
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizard;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizardPage;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EmptyImportExplanationPage extends OWLWizardPage {

    private OWLModel owlModel;


    public EmptyImportExplanationPage(OWLWizard wizard, OWLModel owlModel) {
        super("Explanation page", wizard);
        this.owlModel = owlModel;
        createUI();
    }


    private void createUI() {
	    setHelpText("Creating and importing an empty ontology.", HELP_TEXT);
    }


    public WizardPage getNextPage() {
        return new EmptyOntologyURIPage(getOWLWizard(), owlModel);
    }


    private static final String HELP_TEXT = "<p>This wizard will create an empty ontology and then " +
            "import it into this ontology.</p>" +
            "<p>The empty ontology will be " +
            "saved in a local file which can be later uploaded to a location " +
            "on the web that corresponds to the ontology URI that is specified " +
            "on the next page.</p>" +
            "<p>Any classes, properties or individuals that " +
            "are created in the new empty ontology will be available to this " +
            "ontology.</p>";


}

