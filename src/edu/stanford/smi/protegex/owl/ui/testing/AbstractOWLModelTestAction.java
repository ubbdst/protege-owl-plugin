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

package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.testing.OWLTestManager;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionConstants;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLModelTestAction extends AbstractOWLTestAction implements OWLModelAction {

    public AbstractOWLModelTestAction() {
    }


    public void dispose() {
    }


    public Class getIconResourceClass() {
        return OWLIcons.class;
    }


    public String getMenubarPath() {
    	return AbstractOWLModelAction.OWL_MENU + PATH_SEPARATOR + OWLModelActionConstants.OWL_TESTS_GROUP;
    }


	public String getToolbarPath() {
		return null;
	}


    public boolean isSuitable(OWLModel owlModel) {
        return true;
    }


    public void notifyPropertyChangeListeners(String propertyName, Object oldValue, Object newValue) {
        // TODO (theoretically)
    }


    public void run(OWLModel owlModel) {
        this.owlModel = owlModel;
        this.testManager = (OWLTestManager) owlModel;
        actionPerformed(null);
    }
}
