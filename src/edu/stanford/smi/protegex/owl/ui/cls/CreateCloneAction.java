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

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.util.CloneFactory;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateCloneAction extends ResourceAction {

    public CreateCloneAction() {
        super("Create clone", Icons.getBlankIcon(), AddSubclassAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        OWLNamedClass namedCls = (OWLNamedClass) getResource();
        OWLNamedClass c = null;
        OWLModel owlModel = namedCls.getOWLModel();
        //TT: This is may be dangerous.. Needs some testing
        String cloneName = CloneFactory.getNextAvailableCloneName(namedCls);
        try {
            owlModel.beginTransaction("Create " + NamespaceUtil.getLocalName(cloneName) + 
            		" as clone of " + namedCls.getBrowserText(), cloneName);
            c = createClone(namedCls, cloneName);
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();        
            OWLUI.handleError(owlModel, ex);
        }
        ((ClassTreePanel) getComponent()).setSelectedClass(c);
    }

    public static OWLNamedClass createClone(OWLNamedClass oldCls) {
        return CloneFactory.cloneOWLNamedClass(oldCls);
    }
    
    public static OWLNamedClass createClone(OWLNamedClass oldCls, String cloneName) {
        return CloneFactory.cloneOWLNamedClass(oldCls, cloneName);
    }

//    public static OWLNamedClass createClone(OWLNamedClass oldCls) {
//        OWLModel owlModel = oldCls.getOWLModel();
//        String newName = null;
//        int i = 2;
//        do {
//            newName = oldCls.getName() + "_" + i;
//            i++;
//        }
//        while (owlModel.getRDFResource(newName) != null);
//        OWLNamedClass newCls = owlModel.createOWLNamedClass(newName);
//        boolean hasThing = false;
//        for (Iterator it = oldCls.getSuperclasses(false).iterator(); it.hasNext();) {
//            Cls superCls = (Cls) it.next();
//            if (superCls instanceof RDFSClass) {
//                RDFSClass cloneClass = ((RDFSClass) superCls).createClone();
//                newCls.addSuperclass(cloneClass);
//                if (newCls.equals(owlModel.getOWLThingClass())) {
//                    hasThing = true;
//                }
//                if (superCls.getDirectSuperclasses().contains(oldCls)) {
//                    cloneClass.addSuperclass(newCls);
//                }
//            }
//        }
//        if (!hasThing) {
//            newCls.removeSuperclass(owlModel.getOWLThingClass());
//        }
//        Iterator infs = oldCls.getInferredSuperclasses().iterator();
//        while (infs.hasNext()) {
//            RDFSClass inferredSuperclass = (RDFSClass) infs.next();
//            newCls.addInferredSuperclass(inferredSuperclass);
//        }
//        for (Iterator it = oldCls.getDisjointClasses().iterator(); it.hasNext();) {
//            RDFSClass disjointClass = (RDFSClass) it.next();
//            final RDFSClass c = disjointClass.createClone();
//            if (!newCls.getDisjointClasses().contains(c)) {
//                newCls.addDisjointClass(c);
//            }
//        }
//        for (Iterator it = oldCls.getUnionDomainProperties().iterator(); it.hasNext();) {
//            RDFProperty property = (RDFProperty) it.next();
//            property.addUnionDomainClass(newCls);
//        }
//        return newCls;
//    }


    @Override
	public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof ClassTreePanel &&
               resource instanceof OWLNamedClass &&
               !(resource.equals(resource.getOWLModel().getOWLThingClass()));
    }
}
