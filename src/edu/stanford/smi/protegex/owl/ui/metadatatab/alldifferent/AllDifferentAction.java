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

package edu.stanford.smi.protegex.owl.ui.metadatatab.alldifferent;

import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionConstants;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * An OWLModelAction to open a dialog to edit the owl:AllDifferents of a given OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AllDifferentAction extends AbstractOWLModelAction {
			
	public String getMenubarPath() {
        return OWL_MENU + PATH_SEPARATOR + OWLModelActionConstants.MODEL_OPERATIONS_GROUP;
    }


    public String getName() {
        return "Edit owl:AllDifferents...";
    }


    public void run(OWLModel owlModel) {
        ProjectView projectView = ProtegeUI.getProjectView(owlModel);
        AllDifferentPanel panel = new AllDifferentPanel(owlModel);
        ProtegeUI.getModalDialogFactory().showDialog(projectView, panel, getName(), ModalDialogFactory.MODE_CLOSE);
    }
}
