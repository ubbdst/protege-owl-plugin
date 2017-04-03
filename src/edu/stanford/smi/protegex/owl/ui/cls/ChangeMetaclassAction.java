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
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangeMetaclassAction extends ResourceAction {

    public static String GROUP = "Metaclasses/";


    public ChangeMetaclassAction() {
        super("Change metaclass...", Icons.getBlankIcon(), GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        final OWLModel owlModel = getOWLModel();
        OWLNamedClass owlClassMetaCls = owlModel.getOWLNamedClassClass();
        RDFSNamedClass rdfsClassMetaCls = owlModel.getRDFSNamedClassClass();
        boolean rdfsMetaClsWasVisible = rdfsClassMetaCls.isVisible();
        boolean owlMetaClsWasVisible = owlClassMetaCls.isVisible();
        if (OWLUtil.hasRDFProfile(owlModel)) {
            rdfsClassMetaCls.setVisible(true);
            owlClassMetaCls.setVisible(true);
        }
        Cls rootMetaClass = rdfsClassMetaCls.isVisible() ? rdfsClassMetaCls :
                owlModel.getOWLNamedClassClass();
        Collection roots = CollectionUtilities.createCollection(rootMetaClass);
        RDFSNamedClass metaClass = ProtegeUI.getSelectionDialogFactory().selectClass(getComponent(), owlModel, roots, "Select Metaclass");
        rdfsClassMetaCls.setVisible(rdfsMetaClsWasVisible);
        owlClassMetaCls.setVisible(owlMetaClsWasVisible);
        Cls cls = (Cls) getResource();
        if (metaClass != null && !metaClass.equals(cls.getDirectType())) {
            cls.setDirectType(metaClass);
        }
    }


    public static RDFSNamedClass getRootMetaCls(Frame frame) {
        final OWLModel owlModel = (OWLModel) frame.getKnowledgeBase();
        RDFSNamedClass rdfsClassMetaCls = owlModel.getRDFSNamedClassClass();
        return rdfsClassMetaCls.isVisible() ? rdfsClassMetaCls :
                (RDFSNamedClass) owlModel.getOWLNamedClassClass();
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        OWLModel owlModel = resource.getOWLModel();
        return resource.isEditable() &&
                component instanceof OWLSubclassPane &&
                (getRootMetaCls(resource).getSubclassCount() > 0 ||
                        (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Create_OWLClass) &&
                                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.CreateRDFSClass)));
    }
}
