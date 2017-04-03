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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A ResourceAction for named classes, to create a subclass of the currently selected class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateSubclassAction extends ResourceAction {

    public final static String TEXT = "Create subclass";


    public CreateSubclassAction() {
        super(TEXT, OWLIcons.getCreateIcon(OWLIcons.SUB_CLASS, 4));
    }


    public void actionPerformed(ActionEvent e) {
        ClassTreePanel classTreePanel = (ClassTreePanel) getComponent();
        RDFSNamedClass sibling = (RDFSNamedClass) getResource();
        performAction(Collections.singleton(sibling), classTreePanel);
    }


    @Override
	public int getPriority() {
        return 1;  // So that it shows up before "create sibling class"
    }


    @Override
	public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof ClassTreePanel &&
                resource instanceof RDFSNamedClass;
    }


    public static void performAction(Collection superclasses, ClassTreePanel classTreePanel) {
    	RDFSNamedClass cls = null;
        RDFSNamedClass superclass = (RDFSNamedClass) superclasses.iterator().next();
        OWLModel owlModel = superclass.getOWLModel();
        RDFSClass type = superclass.getProtegeType();
        if (superclasses.contains(owlModel.getOWLThingClass())) {
            if (!ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Create_OWLClass) &&
                    ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.CreateRDFSClass)) {
                type = owlModel.getRDFSNamedClassClass();
            }
        }
        
        String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_CLASS_NAME);
        try {
            owlModel.beginTransaction("Create class " + NamespaceUtil.getLocalName(name) + " as subclass of " + (superclass == null ? "(unknown)" : superclass.getBrowserText()), name);            
            cls = owlModel.createRDFSNamedClass(name, superclasses, type);
            if (cls instanceof OWLNamedClass) {
                for (Iterator it = superclasses.iterator(); it.hasNext();) {
                    RDFSNamedClass s = (RDFSNamedClass) it.next();
                    ((OWLNamedClass) cls).addInferredSuperclass(s);
                }
            }
            owlModel.commitTransaction();
		} catch (Exception e) {
			owlModel.rollbackTransaction();
			OWLUI.handleError(owlModel, e);
		}
        
        if (cls != null) 
        	classTreePanel.setSelectedClass(cls);
    }
}
