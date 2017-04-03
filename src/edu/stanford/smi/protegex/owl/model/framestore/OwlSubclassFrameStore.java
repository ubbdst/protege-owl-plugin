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

package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

public class OwlSubclassFrameStore extends FrameStoreAdapter {
    private static transient Logger log = Log.getLogger(OwlSubclassFrameStore.class);
    
    private OWLModel owlModel;
    
    private Slot directSuperclassesSlot;
    private Slot directSubclassesSlot;
    private RDFProperty rdfsSubClassOfProperty;
    private RDFProperty owlEquivalentClassProperty;
    
    public OwlSubclassFrameStore(OWLModel owlModel) {
        this.owlModel = owlModel;
        directSuperclassesSlot =owlModel.getSystemFrames().getDirectSuperclassesSlot();
        directSubclassesSlot = owlModel.getSystemFrames().getDirectSubclassesSlot();
        rdfsSubClassOfProperty = owlModel.getRDFSSubClassOfProperty();
        owlEquivalentClassProperty = owlModel.getOWLEquivalentClassProperty();
    }


    private void addNamedOperandsToDirectSuperclasses(OWLNamedClass cls, OWLIntersectionClass superCls) {
        for (Iterator it = superCls.getOperands().iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            if (operand instanceof OWLNamedClass) {
                cls.addSuperclass(operand);
            }
        }
    }
    
    /**
     * Updates the values of rdfs:subClassOf (and owl:equivalentClass) in response
     * to changes in the :SLOT-DIRECT-SUPERCLASSES.
     *
     * @param cls the RDFSClass that has changed its superclasses
     */
    @SuppressWarnings("unchecked")
    private void updateRDFSSubClassOf(RDFSNamedClass cls) {
        Collection<RDFSClass> newSuperclasses = new ArrayList<RDFSClass>();
        Collection<RDFSClass> newEquivalentClasses = new ArrayList<RDFSClass>();

        for (Cls superClass : super.getDirectSuperclasses(cls)) {
            if (superClass instanceof RDFSClass) {
                if (super.getDirectSuperclasses(superClass).contains(cls)) {  // is equivalent class
                    newEquivalentClasses.add((RDFSClass) superClass);
                    if (superClass instanceof RDFSNamedClass) {
                        newSuperclasses.add((RDFSClass) superClass);
                    }
                }
                else {
                    newSuperclasses.add((RDFSClass) superClass);
                }
            }
        }
        super.setDirectOwnSlotValues(cls, rdfsSubClassOfProperty, newSuperclasses);
        super.setDirectOwnSlotValues(cls, owlEquivalentClassProperty, newEquivalentClasses);
    }
    
    @SuppressWarnings("unchecked")
    private void removeNamedOperandsFromDirectSuperclasses(OWLNamedClass cls,
                                                           OWLIntersectionClass intersectionCls) {
        Collection toRemove = intersectionCls.getNamedOperands();
        if (!toRemove.isEmpty()) {
        	for (Object o : super.getDirectSuperclasses(cls)) {
        		if (o  instanceof RDFSClass && super.getDirectSuperclasses((RDFSClass) o).contains(cls)) {
                    RDFSClass equivalentClass = (RDFSClass) o;
                    if (equivalentClass instanceof OWLIntersectionClass) {
                        toRemove.removeAll(((OWLIntersectionClass) equivalentClass).getNamedOperands());
                    }
        		}
        	}
            for (Iterator it = toRemove.iterator(); it.hasNext();) {
                RDFSNamedClass namedCls = (RDFSNamedClass) it.next();
                if (!namedCls.hasEquivalentClass(cls)) {
                    cls.removeSuperclass(namedCls);
                }
            }
        }
    }

    
    /*
     * Frame Store implementation
     */
    
    @Override
    public Cls createCls(FrameID id, Collection directTypes, Collection directSuperclasses, boolean loadDefaults) {
        
        Cls cls = super.createCls(id, directTypes, directSuperclasses, loadDefaults);
        if (cls instanceof RDFSNamedClass) {
            super.setDirectOwnSlotValues(cls, owlModel.getRDFSSubClassOfProperty(), directSuperclasses);
        }
        return cls;
    }

    @Override
    public void addDirectSuperclass(Cls cls, Cls superCls) {
        Collection<Cls> superClasses = super.getDirectSuperclasses(cls);
        if (!superClasses.contains(superCls)) {   // Disallow duplicates
            if (log.isLoggable(Level.FINE)) {
                log.fine("-> " +cls.getBrowserText() + " ADDED " + superCls.getBrowserText());
            }
            super.addDirectSuperclass(cls, superCls);
            if (superCls instanceof OWLIntersectionClass &&
                cls instanceof OWLNamedClass &&
                super.getDirectSuperclasses(superCls).contains(cls)) {
                addNamedOperandsToDirectSuperclasses((OWLNamedClass) cls, (OWLIntersectionClass) superCls);
            }
            else if (cls instanceof OWLIntersectionClass &&
                     superCls instanceof OWLNamedClass &&
                     super.getDirectSuperclasses(superCls).contains(cls)) {
                addNamedOperandsToDirectSuperclasses((OWLNamedClass) superCls, (OWLIntersectionClass) cls);
            }

            if (cls instanceof OWLNamedClass &&
                superCls instanceof OWLNamedClass &&
                cls.isEditable() &&
                ((OWLNamedClass) superCls).getSubclassesDisjoint()) {
                OWLUtil.ensureSubclassesDisjoint((OWLNamedClass) superCls);
            }

            if (cls instanceof RDFSNamedClass) {
                updateRDFSSubClassOf((RDFSNamedClass) cls);
            }
            if (superCls instanceof RDFSNamedClass) {
                updateRDFSSubClassOf((RDFSNamedClass) superCls);
            }
        }
    }
    
    @Override
    public void removeDirectSuperclass(Cls cls, Cls superCls) {

        boolean wasEquivalentCls = superCls.hasDirectSuperclass(cls);

        // log("-> " +cls.getBrowserText() + " REMOVED " + superCls.getBrowserText());
        super.removeDirectSuperclass(cls, superCls);

        if (cls instanceof OWLNamedClass && superCls instanceof OWLIntersectionClass && wasEquivalentCls) {
            removeNamedOperandsFromDirectSuperclasses((OWLNamedClass) cls,
                                                      (OWLIntersectionClass) superCls);
        }
        else if (superCls instanceof OWLNamedClass && cls instanceof OWLIntersectionClass && wasEquivalentCls) {
            removeNamedOperandsFromDirectSuperclasses((OWLNamedClass) superCls,
                                                      (OWLIntersectionClass) cls);
        }

        if (cls instanceof RDFSNamedClass) {
            updateRDFSSubClassOf((RDFSNamedClass) cls);
        }
        else if (superCls instanceof RDFSNamedClass) {
            updateRDFSSubClassOf((RDFSNamedClass) superCls);
        }
    }

}
