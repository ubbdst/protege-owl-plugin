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

package edu.stanford.smi.protegex.owl.ui.menu;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.tidy.Checker;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionConstants;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * An Action that allows to validate the OWL sublanguage of the current Jena ontology.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class ValidatorAction extends AbstractOWLModelAction {
		
    public String getMenubarPath() {
        return TOOLS_MENU;
    }


    public String getName() {
        return "Determine OWL Sublanguage...";
    }


    public void run(OWLModel owlModel) {
        Set<String> imports = owlModel.getAllImports();
        Map<URI, String> map = new HashMap<URI, String>();
        for (Iterator<String> it = imports.iterator(); it.hasNext();) {
            String uriString = it.next();
            try {
                URI uri = new URI(uriString);
                Repository rep = owlModel.getRepositoryManager().getRepository(uri);
                if (rep != null) {
                    map.put(uri, rep.getOntologyLocationDescription(uri));
                }
            }
            catch (URISyntaxException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }

        if (!map.isEmpty()) {
            String str = "Your project uses redirected imports.  " +
                    "The species validation does not use these imports\n" +
                    "and therefore the following result may be wrong.  The following\n" +
                    "URI aliases are used:\n";
            for (Iterator<URI> it = map.keySet().iterator(); it.hasNext();) {
                URI key = it.next();
                String alias = map.get(key);
                str += "- " + key + "\n   has alias " + alias + "\n";
            }
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, str, "Warning");
        }

        if (!OWLUI.isConfirmationNeeded(owlModel) ||
                OWLUI.isConfirmed(owlModel, owlModel.getRDFResourceCount() > OWLUI.getConfirmationThreshold(owlModel))) {
            performAction(owlModel);
        }
    }


    private void performAction(OWLModel owlModel) {
        OntModelProvider ontModelProvider = (OntModelProvider) owlModel;
        String msg = Jena.getOWLSpeciesString(ontModelProvider.getOWLSpecies());
        ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                "The OWL sublanguage of this ontology is OWL " + msg,
                "OWL Sublanguage");
    }


    public static String getSubLanguage(OntModel ontModel) {
        Checker checker = new Checker(false);
        checker.addGraphAndImports(ontModel.getGraph());
        String sublanguage = checker.getSubLanguage();
        return sublanguage;
    }
}
