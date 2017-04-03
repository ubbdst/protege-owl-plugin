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

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangeMetaclassOfSubclassesAction extends ResourceAction {

    /**
     * @deprecated use ChangeMetaclassAction.GROUP
     */
    public static String GROUP = "Metaclasses";


    public ChangeMetaclassOfSubclassesAction() {
        super("Change metaclass of subclasses...", Icons.getBlankIcon(), ChangeMetaclassAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        Cls cls = (Cls) getResource();
        Cls metaCls = cls.getDirectType();
        String text = "Change metaclass of all subclasses of ";
        text += cls.getBrowserText();
        text += " to " + metaCls.getBrowserText();
        OWLModel owlModel = (OWLModel) cls.getKnowledgeBase();
        if (ProtegeUI.getModalDialogFactory().showConfirmDialog(getComponent(), text, "Confirm")) {
            WaitCursor waitCursor = new WaitCursor(getComponent());
            try {
                Iterator i = cls.getSubclasses().iterator();
                while (i.hasNext()) {
                    Cls subclass = (Cls) i.next();
                    if (subclass.isEditable() && subclass instanceof RDFSNamedClass) {
                        subclass.setDirectType(metaCls);
                    }
                }
            }
            finally {
                waitCursor.hide();
            }
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        if (component instanceof OWLSubclassPane && ((Cls) resource).getDirectSubclassCount() > 0) {
            Cls cls = (Cls) resource;
            Cls type = cls.getDirectType();
            for (Iterator it = cls.getDirectSubclasses().iterator(); it.hasNext();) {
                Cls subCls = (Cls) it.next();
                if (subCls.isVisible() && !type.equals(subCls.getDirectType())) {
                    return true;
                }
            }
        }
        return false;
    }
}
