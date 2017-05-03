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

package edu.stanford.smi.protegex.owl.ui.repository;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.parser.UnresolvedImportHandler;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryWizard;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UnresolvedImportUIHandler implements UnresolvedImportHandler {

    public static final String ADD_OPTION = "Add Repository";

    public static final String CANCEL_OPTION = "Cancel";

    public static final ArrayList OPTIONS = new ArrayList();


    public UnresolvedImportUIHandler() {
        OPTIONS.add(ADD_OPTION);
        OPTIONS.add(CANCEL_OPTION);
    }


    public Repository handleUnresolvableImport(OWLModel model,
                                               TripleStore tripleStore,
                                               URI ontologyName) {
        try {
        	//FIXME: Check this with UI and without!!
        	if (!OWLUtil.runsWithGUI(model)) {
        		Log.getLogger().warning("The system cannot find the ontology " + ontologyName + " in any of the repositories. This import will be ignored.");
        		return null;
        	}
        	
            Repository rep = null;
            while (rep == null) {
                if (showMessage(ontologyName) == OPTIONS.indexOf(CANCEL_OPTION)) {
                    return null;
                }
                RepositoryWizard wizard = new RepositoryWizard(null, model);
                if (wizard.execute() == Wizard.RESULT_CANCEL) {
                    return null;
                }
                rep = wizard.getRepository();
                if (rep != null) {
                    if (rep.contains(ontologyName)) {
                        return rep;
                    }
                    else {
                        rep = null;
                    }
                }
                wizard.dispose();
            }
            return null;
        }
        catch (HeadlessException he) {
            return null;
        }
    }


    private int showMessage(URI ontologyName) {
        return JOptionPane.showOptionDialog(null, "The system cannot find the ontology:\n" + ontologyName + "\n\n" + "Select '" + ADD_OPTION + "' to add a repository that contains\n" + "this ontology, or select '" + CANCEL_OPTION + "' to stop " + "loading and exit.", "Unresolved import",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, OPTIONS.toArray(),
                ADD_OPTION);
    }
}

