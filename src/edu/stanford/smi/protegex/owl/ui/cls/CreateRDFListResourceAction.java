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

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFListResourceAction extends ResourceAction {

    public CreateRDFListResourceAction() {
        super("Create restricted subclass of rdf:List...", Icons.getBlankIcon());
    }


    public void actionPerformed(ActionEvent e) {
        OWLModel owlModel = getOWLModel();
        RDFSClass typeClass = ProtegeUI.getSelectionDialogFactory().selectClass(getComponent(), owlModel,
                owlModel.getOWLThingClass(), "Select type of list elements");
        if (typeClass != null) {
            RDFSNamedClass listClass = owlModel.createOWLNamedClass(null);
            listClass.addSuperclass(owlModel.getRDFListClass());
            listClass.removeSuperclass(owlModel.getOWLThingClass());
            listClass.addSuperclass(owlModel.createOWLCardinality(owlModel.getRDFFirstProperty(), 1));
            listClass.addSuperclass(owlModel.createOWLAllValuesFrom(owlModel.getRDFFirstProperty(), typeClass));
            listClass.addSuperclass(owlModel.createOWLCardinality(owlModel.getRDFRestProperty(), 1));
            listClass.addSuperclass(owlModel.createOWLAllValuesFrom(owlModel.getRDFRestProperty(), listClass));
            owlModel.getRDFNil().addProtegeType(listClass);
            OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(getComponent());
            if (tab != null) {
                tab.setSelectedCls(listClass);
            }
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource.getName().equals(RDFNames.Cls.LIST);
    }
}
