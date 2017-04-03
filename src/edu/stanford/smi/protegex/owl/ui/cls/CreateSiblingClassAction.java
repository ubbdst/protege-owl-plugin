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
import java.util.HashSet;
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
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A ResourceAction for named classes, to create a sibling of the currently selected class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateSiblingClassAction extends ResourceAction {

    public final static String TEXT = "Create sibling class";


    public CreateSiblingClassAction() {
        super(TEXT, OWLIcons.getCreateIcon(OWLIcons.SIBLING_CLASS));
    }


    public void actionPerformed(ActionEvent e) {
        ClassTreePanel classTreePanel = (ClassTreePanel) getComponent();
        RDFSNamedClass sibling = (RDFSNamedClass) getResource();
        performAction(sibling, classTreePanel);
    }


    @Override
	public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof OWLSubclassPane &&
                resource instanceof RDFSNamedClass &&
                !(resource.equals(resource.getOWLModel().getOWLThingClass()));
    }


    public static void performAction(RDFSNamedClass sibling, ClassTreePanel classTreePanel) {
    	RDFSNamedClass cls = null;
        Collection parents = sibling.getNamedSuperclasses();
        if (!parents.isEmpty()) {
            OWLModel owlModel = sibling.getOWLModel();
            String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_CLASS_NAME);            
            owlModel.beginTransaction("Create class " + NamespaceUtil.getLocalName(name) + 
            		" as sibling of class " + sibling.getBrowserText(), name);
            
            try {                
                RDFSClass siblingType = sibling.getRDFType();
                if(siblingType == null) {
                    siblingType = sibling.getProtegeType();
                }
                /*
                 * The set is necessary to treat duplicates
                 * for the case that a class is defined in 
                 * multiple imports.
                 */
                HashSet parentsSet = new HashSet(parents);
                cls = owlModel.createRDFSNamedClass(name, parentsSet, siblingType);
                if (cls instanceof OWLNamedClass) {
                    for (Iterator it = parentsSet.iterator(); it.hasNext();) {
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
}
