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
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateClassUsingMetaclassAction extends ResourceAction {


    public CreateClassUsingMetaclassAction() {
        super("Create subclass using metaclass...", Icons.getBlankIcon(), ChangeMetaclassAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        OWLSubclassPane pane = (OWLSubclassPane) getComponent();
        OWLModel owlModel = getOWLModel();
        RDFSClass parent = (RDFSClass) getResource();
        RDFSClass rootMetaCls = ChangeMetaclassAction.getRootMetaCls(parent);
        Collection roots = CollectionUtilities.createCollection(rootMetaCls);
        Cls metaCls = ProtegeUI.getSelectionDialogFactory().selectClass(pane, owlModel, roots, "Select Metaclass");
        if (metaCls != null) {
            Cls cls = ((KnowledgeBase) owlModel).createCls(null, Collections.singleton(parent), metaCls);
            pane.extendSelection(cls);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource.isEditable() &&
                component instanceof OWLSubclassPane &&
                ChangeMetaclassAction.getRootMetaCls(resource).getSubclassCount() > 0;
    }
}
