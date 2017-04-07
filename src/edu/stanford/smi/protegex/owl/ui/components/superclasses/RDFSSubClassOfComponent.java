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

package edu.stanford.smi.protegex.owl.ui.components.superclasses;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.components.multiresource.MultiResourceComponent;

import javax.swing.*;

/**
 * A MultiResourceComponent for editing values of the rdfs:subClassOf property.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFSSubClassOfComponent extends MultiResourceComponent {

    public RDFSSubClassOfComponent(OWLModel owlModel) {
        super(owlModel.getRDFSSubClassOfProperty(), false);
    }

    public RDFSSubClassOfComponent(OWLModel owlModel, boolean isReadOnly) {
        super(owlModel.getRDFSSubClassOfProperty(), false, null, isReadOnly );
    }
    

    public void addObject(RDFResource resource, boolean symmetric) {
        RDFSNamedClass cls = (RDFSNamedClass) getSubject();
        if (!cls.equals(resource) && resource instanceof RDFSNamedClass) {
            cls.addSuperclass((RDFSNamedClass) resource);
        }
    }


    protected Action createCreateAction() {
        return null;
    }


    protected void handleRemove() {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(this);
        Object[] sels = getSelectedObjects();
        RDFSNamedClass cls = (RDFSNamedClass) getSubject();
        if (cls.getSuperclasses(false).size() - sels.length <= 0) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(getOWLModel(),
                    "Each class must have at least one superclass.");
        }
        else {
            for (int i = 0; i < sels.length; i++) {
                RDFSClass superclass = (RDFSClass) sels[i];
                cls.removeSuperclass(superclass);
            }
            if (tab != null) {
                tab.ensureClsSelected(cls, -1);
            }
        }
    }
}