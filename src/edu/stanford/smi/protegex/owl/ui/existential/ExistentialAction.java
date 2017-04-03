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

package edu.stanford.smi.protegex.owl.ui.existential;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.HierarchyManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExistentialAction extends ResourceSelectionAction {

    private OWLNamedClass cls;

    private Component component;

    private HierarchyManager hierarchyManager;


    public ExistentialAction(Component component, HierarchyManager hierarchyManager, OWLNamedClass cls) {
        super("Explore existential relationships...", OWLIcons.getImageIcon("Transitivity"));
        this.cls = cls;
        this.component = component;
        this.hierarchyManager = hierarchyManager;
    }


    public void resourceSelected(RDFResource resource) {
        if (resource instanceof OWLObjectProperty) {
            OWLObjectProperty property = (OWLObjectProperty) resource;
            ExistentialTreePanel tp = new ExistentialTreePanel(cls,
                    ((KnowledgeBase) cls.getOWLModel()).getSlot(Model.Slot.DIRECT_SUPERCLASSES),
                    property);
            hierarchyManager.addHierarchy(tp);
        }
    }


    public Collection getSelectableResources() {
        OWLModel owlModel = cls.getOWLModel();
        Collection properties = new ArrayList(owlModel.getVisibleUserDefinedOWLProperties());
        List list = new ArrayList();
        for (Iterator it = properties.iterator(); it.hasNext();) {
            OWLProperty property = (OWLProperty) it.next();
            if (property instanceof OWLObjectProperty) {
                list.add(property);
            }
        }
        return list;
    }


    public RDFResource pickResource() {
        Collection list = getSelectableResources();
        return (OWLObjectProperty) ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(
                component, cls.getOWLModel(), list, "Select a property to explore");
    }


    public void setCls(OWLNamedClass cls) {
        this.cls = cls;
    }
}
