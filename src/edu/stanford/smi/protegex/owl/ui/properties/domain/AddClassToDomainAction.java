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

package edu.stanford.smi.protegex.owl.ui.properties.domain;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddClassToDomainAction extends ResourceSelectionAction {

    private OWLDomainWidget widget;


    public AddClassToDomainAction(OWLDomainWidget widget) {
        super("Specialise Domain",
                OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS), true);
        this.widget = widget;
    }


    public static boolean canAdd(RDFProperty property) {
        if (!(property instanceof OWLProperty)) {
            OWLModel owlModel = property.getOWLModel();
            if (!ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Union_Classes)) {
                Collection dd = new ArrayList(property.getUnionDomain());
                dd.remove(owlModel.getOWLThingClass());
                if (dd.size() > 0) {
                    ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                            "In pure RDF, rdf:Properties can only have one class in their\n" +
                                    "(union) domain.  You need to select a different language profile.");
                    return false;
                }
            }
        }
        return true;
    }


    private String checkCls(Cls cls) {
        if (cls instanceof RDFSNamedClass) {
            RDFProperty property = (RDFProperty) widget.getEditedResource();
            if (property.getUnionDomain().contains(cls)) {
                return "The class " + cls.getBrowserText() + " is already in the domain.";
            }
//            else if (property.getSuperpropertyCount() > 0) {
//                for (Iterator supers = property.getSuperproperties(true).iterator(); supers.hasNext();) {
//                    Slot superSlot = (Slot) supers.next();
//                    for (Iterator it = superSlot.getDirectDomain().iterator(); it.hasNext();) {
//                        Cls domainCls = (Cls) it.next();
//                        if (cls.equals(domainCls) || cls.hasSuperclass(domainCls)) {
//                            return null;
//                        }
//                    }
//                }
//                return "Sub-properties can only narrow the domain of their super-properties.\n" +
//                        "The class " + cls.getBrowserText() + " is not a subclass of any class in the\n" +
//                        "domain of its super-properties.";
//            }
        }
        return null;
    }


    public void resourceSelected(RDFResource resource) {
        RDFSClass cls = (RDFSClass) resource;
        RDFProperty property = (RDFProperty) widget.getEditedResource();
        final OWLModel owlModel = cls.getOWLModel();
        if (!canAdd(property)) {
            return;
        }
        String msg = checkCls(cls);
        if (msg == null) {
            try {
                owlModel.beginTransaction("Add " + resource.getBrowserText() + " to the domain of " + property.getBrowserText(), property.getName());
                if (((Slot) property).getDirectOwnSlotValue(((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_DOMAIN)) == null) {
                    property.addUnionDomainClass(cls);
                    for (Iterator it = property.getSuperproperties(true).iterator(); it.hasNext();) {
                        RDFProperty superSlot = (RDFProperty) it.next();
                        for (Iterator sit = superSlot.getUnionDomain().iterator(); sit.hasNext();) {
                            RDFSClass domainCls = (RDFSClass) sit.next();
                            if (!cls.getSuperclasses(true).contains(domainCls) && !property.getUnionDomain().contains(domainCls)) {
                                property.addUnionDomainClass(domainCls);
                            }
                        }
                    }
                }
                else {
                    if (owlModel.getOWLThingClass().getUnionDomainProperties().contains(property)) {
                        property.removeUnionDomainClass(owlModel.getOWLThingClass());
                    }
                    property.addUnionDomainClass(cls);
                }
                owlModel.commitTransaction();
            }
            catch (Exception ex) {
            	owlModel.rollbackTransaction();
                OWLUI.handleError(owlModel, ex);
            }
        }
        else {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, msg, "Invalid domain");
        }
    }


    public Collection getSelectableResources() {
        OWLModel owlModel = (OWLModel) widget.getKnowledgeBase();
        RDFProperty property = (RDFProperty) widget.getEditedResource();
        Collection clses = null;
        if (property.getSuperpropertyCount() > 0) {
            clses = new HashSet();
            for (Iterator it = property.getSuperproperties(true).iterator(); it.hasNext();) {
                Slot superSlot = (Slot) it.next();
                for (Iterator jt = superSlot.getDirectDomain().iterator(); jt.hasNext();) {
                    Cls domainCls = (Cls) jt.next();
                    if (domainCls instanceof RDFSNamedClass) {
                        clses.add(domainCls);
                        for (Iterator sit = domainCls.getSubclasses().iterator(); sit.hasNext();) {
                            Cls subCls = (Cls) sit.next();
                            if (subCls instanceof RDFSNamedClass) {
                                clses.add(subCls);
                            }
                        }
                    }
                }
            }
            clses.removeAll(property.getUnionDomain());
        }
        else {
            clses = owlModel.getUserDefinedRDFSNamedClasses();
            clses.add(owlModel.getOWLThingClass());
            clses.removeAll(property.getUnionDomain());
        }
        return clses;
    }


    public Collection pickResources() {
        OWLModel owlModel = (OWLModel) widget.getKnowledgeBase();
        RDFProperty property = (RDFProperty) widget.getEditedResource();

        Collection rootClasses = new HashSet();
        rootClasses.add(owlModel.getOWLThingClass());
        if (property.getSuperpropertyCount() > 0) {
                    for (Iterator it = property.getSuperproperties(true).iterator(); it.hasNext();) {
                        RDFProperty superProp = (RDFProperty) it.next();
                        rootClasses.addAll(superProp.getUnionDomain());
                    }
            if (rootClasses.size() > 1){
                rootClasses.remove(owlModel.getOWLThingClass());
            }
        }

        return ProtegeUI.getSelectionDialogFactory().selectClasses(widget, owlModel,
                rootClasses,
                "Select named class(es) to add");
    }
}
