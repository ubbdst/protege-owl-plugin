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

package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FindUsage {

    public static Collection<FindUsageTableItem> getItems(RDFResource searchInstance) {
        OWLModel owlModel = searchInstance.getOWLModel();
        Set<RDFProperty> ignoreProperties = getIgnoreProperties(owlModel);
        Collection anons = searchInstance.getReferringAnonymousClasses();
        Collection<FindUsageTableItem> items = new ArrayList<FindUsageTableItem>();
        Slot superClsesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
        RDFProperty disjointWithProperty = owlModel.getRDFProperty(OWLNames.Slot.DISJOINT_WITH);
        Slot rangeSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.VALUE_TYPE);
        if (!owlModel.getOWLThingClass().equals(searchInstance)) {
            for (Iterator<Reference> it = ((KnowledgeBase) owlModel).getReferences(searchInstance, 10000).iterator(); it.hasNext();) {
                Reference ref = it.next();
                if (disjointWithProperty.equals(ref.getSlot())) {
                    items.add(new FindUsageTableItem(FindUsageTableItem.DISJOINT_CLASS,
                                                     (RDFResource) ref.getFrame(), searchInstance));
                }
                else if (rangeSlot.equals(ref.getSlot())) {
                    items.add(new FindUsageTableItem(FindUsageTableItem.RANGE,
                            (RDFResource) ref.getFrame(), searchInstance));
                }
            }
        }
        Set<OWLAnonymousClass> used = new HashSet<OWLAnonymousClass>();
        for (Iterator<OWLAnonymousClass> it = anons.iterator(); it.hasNext();) {
            OWLAnonymousClass cls = it.next();
            OWLAnonymousClass rootCls = cls.getExpressionRoot();
            if (searchInstance instanceof OWLNamedClass && ((OWLNamedClass) searchInstance).getEquivalentClasses().contains(rootCls)) {
                continue;
            }
            if (used.contains(rootCls)) {
                continue;
            }
            used.add(rootCls);
            Collection<Reference> refs = ((KnowledgeBase) owlModel).getReferences(rootCls, 100000);
            for (Iterator<Reference> rit = refs.iterator(); rit.hasNext();) {
                Reference reference = rit.next();
                if (reference.getFrame() instanceof RDFResource) {
                    RDFResource host = (RDFResource) reference.getFrame();
                    if (superClsesSlot.equals(reference.getSlot())) {
                        if (host instanceof OWLNamedClass && ((OWLNamedClass) host).getPureSuperclasses().contains(rootCls)) {
                            items.add(new FindUsageTableItem(FindUsageTableItem.SUPERCLASS,
                                    host, rootCls));
                        }
                        else {
                            items.add(new FindUsageTableItem(FindUsageTableItem.EQUIVALENT_CLASS,
                                    host, rootCls));
                        }
                    }
                    else if (disjointWithProperty.equals(reference.getSlot())) {
                        items.add(new FindUsageTableItem(FindUsageTableItem.DISJOINT_CLASS,
                                host, rootCls));
                    }
                    else if (rangeSlot.equals(reference.getSlot())) {
                        items.add(new FindUsageTableItem(FindUsageTableItem.RANGE,
                                host, rootCls));
                    }
                }
            }
        }

        Collection<Reference> refs = ((KnowledgeBase) owlModel).getReferences(searchInstance, 1000);
        for (Iterator<Reference> it = refs.iterator(); it.hasNext();) {
            Reference ref = it.next();
            if (ref.getSlot() instanceof RDFProperty && ref.getFrame() instanceof RDFResource) {
                RDFProperty property = (RDFProperty) ref.getSlot();
                if (!ignoreProperties.contains(property)) {
                    RDFResource host = (RDFResource) ref.getFrame();
                    if (!host.isAnonymous()) {
                        items.add(new FindUsageTableItem(FindUsageTableItem.VALUE,
                                                         host, property));
                   }
                }
            }
        }

        return items;
    }


    private static Set<RDFProperty> getIgnoreProperties(OWLModel owlModel) {
        Set<RDFProperty> result = new HashSet<RDFProperty>();
        result.add(owlModel.getRDFTypeProperty());
        result.add(owlModel.getRDFSRangeProperty());
        //result.add(owlModel.getRDFSDomainProperty());
        result.add(owlModel.getOWLDisjointWithProperty());
        result.add(owlModel.getRDFSSubClassOfProperty());
        result.add(owlModel.getOWLEquivalentClassProperty());
        return result;
    }
}
