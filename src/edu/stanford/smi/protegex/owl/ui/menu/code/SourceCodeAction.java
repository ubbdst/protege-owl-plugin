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

package edu.stanford.smi.protegex.owl.ui.menu.code;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionConstants;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.awt.*;

/**
 * An Action to display a SourceCodeDialog.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SourceCodeAction extends AbstractOWLModelAction {


    public String getIconFileName() {
        return OWLIcons.SOURCE_CODE;
    }


    public String getMenubarPath() {
        return CODE_MENU + PATH_SEPARATOR + OWLModelActionConstants.ONT_LANGUAGE_GROUP;
    }


    public String getName() {
        return "Show RDF/XML source code...";
    }


    public boolean isSuitable(OWLModel owlModel) {
        return owlModel instanceof JenaOWLModel;
    }


    public void run(OWLModel owlModel) {
        Component parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        ProtegeUI.getModalDialogFactory().showDialog(parent, new SourceCodePanel(owlModel),
                "RDF/XML Source Code", ModalDialogFactory.MODE_CLOSE);
    }
}
