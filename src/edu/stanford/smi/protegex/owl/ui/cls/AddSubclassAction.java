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

package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddSubclassAction extends ResourceAction {

    public final static String GROUP = "Edit";


    public AddSubclassAction() {
        super("Add subclass...",
                OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS), GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        RDFSClass cls = ProtegeUI.getSelectionDialogFactory().selectClass(getComponent(), getOWLModel());
        if (cls != null && cls.isEditable()) {
            RDFSNamedClass superClass = (RDFSNamedClass) getResource();
            if (!superClass.equals(cls)) {
                if (!cls.getSuperclasses(true).contains(superClass)) {
                    cls.addSuperclass(superClass);
                }
                else {
                    ProtegeUI.getModalDialogFactory().showMessageDialog(getComponent(),
                            cls.getBrowserText() + " is already a subclass of " + cls.getBrowserText() + ".");
                }
            }
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return false; // component instanceof OWLSubclassPane && resource instanceof RDFSNamedClass;
    }
}
