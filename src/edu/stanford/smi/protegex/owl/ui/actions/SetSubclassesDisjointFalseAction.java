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

package edu.stanford.smi.protegex.owl.ui.actions;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.refactoring.RefactorResourceAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SetSubclassesDisjointFalseAction extends RefactorResourceAction {

    public SetSubclassesDisjointFalseAction() {
        super("Unset all subclasses disjoint",
                OWLIcons.getImageIcon(OWLIcons.PRIMITIVE_OWL_CLASS));
    }


    public void actionPerformed(ActionEvent e) {
        OWLNamedClass cls = (OWLNamedClass) getResource();
        OWLModel owlModel = cls.getOWLModel();
        try {
            owlModel.beginTransaction("" + getValue(Action.NAME) +
                    " at " + cls.getBrowserText(), cls.getName());
            cls.setSubclassesDisjoint(false);
            for (Iterator it = cls.getSubclasses(true).iterator(); it.hasNext();) {
                Cls subCls = (Cls) it.next();
                if (subCls instanceof OWLNamedClass && subCls.isEditable()) {
                    ((OWLNamedClass) subCls).setSubclassesDisjoint(false);
                }
            }
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource.isEditable() &&
                resource instanceof OWLNamedClass &&
                ((OWLNamedClass) resource).getSubclassesDisjoint();
    }
}
