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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

/**
 * A FrameStore with specific support for OWL ontologies.
 * While most calls simply forward to their default implementation, some calls are intercepted
 * to do some extra work:
 * <UL>
 * <LI>Deleting depending anonymous classes when their host is deleted</LI>
 * <LI>Synchronizing facet overrides and restriction superclasses (RestrictionUpdaters)</LI>
 * <LI>Ensuring that equivalent classes and superclasses are synchronized</LI>
 * </UL>
 * The goal of this architecture is to make sure that there a no unused anonymous classes
 * around.  This cannot be done through a garbage collection because then components such as
 * the Jena synchronizers could not be able to delete a class when it should be deleted.
 * <BR>
 * Normally, all anonymous classes are used only in one place (their host class).  The host
 * can be a restriction (as facet override in the all and some restrictions), a slot (e.g.
 * via domain and range), or a named class (e.g. as superclass or disjoint class).  Much
 * of the logic for this deleting of depending classes is encapsulated in the subtypes of
 * RDFSClass.  Some extra cases, however (especially restrictions) must be handled with care.
 * <BR>
 * With restrictions, the rule that anonymous classes are only attached to a single host is
 * no longer valid.  Restrictions store their filler as a facet override.  In the case of
 * all and some restrictions, these overrides can be (anonymous) classes.  The same classes
 * are mapped into facet overrides in the host class -- they are not cloned because there
 * is only one representation in the corresponding Jena/OWL model.
 * <BR>
 * These anonymous classes have the restriction as host, and they are deleted when the
 * restriction is deleted or changes its filler.  As a consequence, the following actions
 * are performed by this FrameStore:
 * <UL>
 * <LI>If the filler of a restriction changes, then the filler is also assigned to the
 * (named) subclass of the restriction as facet override</LI>
 * <LI>If the facet override changes in the named subclass, then the old restriction is
 * replaced with a new one that has the override value as filler (i.e. also as
 * override)</LI>
 * <LI>If the named class is deleted, then the depending restrictions are deleted</LI>
 * <LI>If the restriction is deleted, then the facet override is removed from the
 * named class</LI>
 * </UL>
 * <BR>
 * Finally, the OWLFrameStore also makes sure that if a named class has an intersection as
 * equivalent class, then all named operands of these intersections are also superclasses
 * of the host class.  Note that this is maintained in both direction, i.e. if someone
 * removes an intersection, then the named operands are also automatically removed, no
 * matter whether the user intended to also add the class as a superclass.
 * These side effects need to be considered by OWL API users.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFrameStore extends FrameStoreAdapter {
    private static final transient Logger log = Log.getLogger(OWLFrameStore.class);

    private AbstractOWLModel owlModel;

    public final static String IGNORE_PREFIXES_IN_SEARCH = "OWL-TOLERATE-PREFIXES-IN-SEARCH";

    private boolean deletingRDFSDatatype = false;

    /**
     * An ugly trick to prevent anonymous classes from being deleted as a side effect
     */
    public static boolean autoDeleteOfAnonymousClses = true;
    
    private Set<OWLAnonymousClass> deletionsInProgress = new HashSet<OWLAnonymousClass>();


    public OWLFrameStore(AbstractOWLModel owlModel) {
        this.owlModel = owlModel;
    }

    private void deleteAnonymousClass(OWLAnonymousClass cls) {
        if (deletionsInProgress.contains(cls)) {
            return;
        }
        try {
            deletionsInProgress.add(cls);
            //moved from AbstractOWLModel
            if (cls.getDirectSubclassCount() == 1) {
                Cls subCls = (Cls) cls.getDirectSubclasses().iterator().next();            
                subCls.removeDirectSuperclass(cls);
            }
            else {
                Collection<RDFSClass> refs = getReferringAnonymousClassesAndDeleteDependingListInstances(cls);

                if (refs.size() > 0) {
                    deleteAnonymousClses(refs);
                }
            }
            deleteAnonymousClses(Collections.singleton(cls));
        }
        finally {
            deletionsInProgress.remove(cls);
        }
    }

    
    /**
     * This method has been artificially introduce to avoid a double call to getReferences()
     * @param cls
     * @return
     */
    private Set<RDFSClass> getReferringAnonymousClassesAndDeleteDependingListInstances(OWLAnonymousClass cls) {
        final Slot directSubclassesSlot = ((Cls) cls).getKnowledgeBase().getSlot(Model.Slot.DIRECT_SUBCLASSES);
        final RDFProperty rdfFirstProperty = cls.getOWLModel().getRDFFirstProperty();
        final RDFList nil = owlModel.getRDFNil();
        
        Set<RDFSClass> result = new HashSet<RDFSClass>();        
        
        for (Iterator it = cls.getReferences().iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            Frame frame = reference.getFrame();
            Slot slot = reference.getSlot();
            
            if (frame instanceof OWLAnonymousClass) {
                if (!directSubclassesSlot.equals(slot)) {
                    result.add((RDFSClass) frame);
                }
            } else if (frame instanceof RDFList && rdfFirstProperty.equals(slot)) {
                RDFList list = (RDFList) frame;
                RDFList start = list.getStart();
                OWLUtil.getReferringLogicalClasses(start, result); //very expensive call
                
                // Delete RDFList nodes that have the value as rdf:first               
                if (!nil.equals(list)) {
                	deleteListInstance(list);
                }
            }
        }

        // Delete any RDFLists that are direct own slot values of the instance
        if (cls instanceof RDFResource) {
            deleteRDFListsThatArePropertyValues(cls);
        }
        
        return result;
    }
    

    private void deleteAnonymousClses(Collection clses) {
        deleteAnonymousClasses(clses, Collections.EMPTY_LIST);
    }


    private void deleteAnonymousClasses(Collection clses, Collection survivors) {
        if (!clses.isEmpty()) {
            Collection roots = new HashSet();
            for (Iterator it = clses.iterator(); it.hasNext();) {
                Object next = it.next();
                if (next instanceof OWLAnonymousClass) {
                    OWLAnonymousClass root = ((OWLAnonymousClass) next).getExpressionRoot();
                    if (!survivors.contains(root)) {
                        roots.add(root);
                    }
                }
                else if (next instanceof OWLDataRange) {
                    ((OWLDataRange) next).delete();
                }
            }

            for (Iterator it = roots.iterator(); it.hasNext();) {
                OWLAnonymousClass anonymousCls = (OWLAnonymousClass) it.next();
                deleteAnonymousTree(anonymousCls);
            }
        }
    }


    private void deleteAnonymousTree(OWLAnonymousClass anonymousClass) {

        if (anonymousClass instanceof OWLIntersectionClass) {
            OWLIntersectionClass intersectionClass = (OWLIntersectionClass) anonymousClass;
            ensureRDFSSubClassOfStatementsExistForNamedOperands(intersectionClass);
        }

        Collection dependants = anonymousClass.getDependingClasses();
        for (Iterator dit = dependants.iterator(); dit.hasNext();) {
            OWLAnonymousClass dependentCls = (OWLAnonymousClass) dit.next();
            deleteAnonymousTree(dependentCls);
        }
        deleteRDFListsThatArePropertyValues(anonymousClass);
        if (anonymousClass instanceof OWLQuantifierRestriction) {
            RDFResource filler = ((OWLQuantifierRestriction) anonymousClass).getFiller();
            if (filler instanceof RDFSDatatype && filler.isAnonymous()) {
                deletingRDFSDatatype = true;
                filler.delete();
                deletingRDFSDatatype = false;
            }
        }
        super.deleteCls(anonymousClass);
    }


    private void deleteDependingAnonymousClses(Cls cls) {
        Collection ds = getClsesToDelete(cls);
        for (Iterator it = ds.iterator(); it.hasNext();) {
            Cls dc = (Cls) it.next();
            if (dc instanceof RDFSNamedClass) {
                Collection refs = ((RDFSNamedClass) dc).getReferringAnonymousClasses();
                deleteAnonymousClses(refs);
                for (Iterator j = new ArrayList(dc.getDirectSuperclasses()).iterator(); j.hasNext();) {
                    Cls superCls = (Cls) j.next();
                    if (superCls instanceof OWLAnonymousClass) {
                        superCls.delete();
                    }
                }
            }
        }
    }


    private void deleteDependingAnonymousClses(Slot slot) {
        Collection ds = getSlotsToDelete(slot);
        for (Iterator it = ds.iterator(); it.hasNext();) {
            Slot dc = (Slot) it.next();
            if (dc instanceof RDFProperty) {
                Collection refs = ((RDFProperty) dc).getReferringAnonymousClasses();
                deleteAnonymousClses(refs);
            }
        }
    }


    private void deleteDependingListInstances(Instance instance) {

        final RDFList nil = owlModel.getRDFNil();

        // Delete RDFList nodes that have the value as rdf:first
        Collection refs = getReferences(instance);
        Slot firstSlot = owlModel.getRDFFirstProperty();
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (firstSlot.equals(reference.getSlot())) {
                RDFList li = (RDFList) reference.getFrame();
                if (!nil.equals(li)) {
                    deleteListInstance(li);
                }
            }
        }

        // Delete any RDFLists that are direct own slot values of the instance
        if (instance instanceof RDFResource) {
            deleteRDFListsThatArePropertyValues((RDFResource) instance);
        }
    }


    private void deleteRDFListsThatArePropertyValues(RDFResource resource) {
        RDFResource nil = owlModel.getRDFNil();
        for (Iterator it = (resource).getPossibleRDFProperties().iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            Collection values = new ArrayList(resource.getPropertyValues(property));
            for (Iterator vit = values.iterator(); vit.hasNext();) {
                Object o = vit.next();
                if (o instanceof RDFList && !nil.equals(o)) {
                    RDFList l = (RDFList) o;
                    resource.removePropertyValue(property, l);
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("Deleting " + l.getBrowserText() + " at " + l.getBrowserText() + " . " + property.getBrowserText());
                    }
                    l.delete();
                }
            }
        }
    }


    /**
     * Deletes a RDFList cleanly from its list.
     * This will update all references to it (e.g. if it is a direct own slot
     * value somewhere, then it will replace those with the rest of this.
     *
     * @param li the RDFList to delete
     */
    private void deleteListInstance(RDFList li) {
        RDFList rest = li.getRest();
        if (owlModel.getRDFNil().equals(rest)) {
            rest = null;
        }
        li.setRest(null);
        if (rest != null) {
            RDFProperty restProperty = owlModel.getRDFRestProperty();
            for (Iterator it = getReferences(li).iterator(); it.hasNext();) {
                Reference reference = (Reference) it.next();
                Frame frame = reference.getFrame();
                Slot slot = reference.getSlot();
                if (!slot.isSystem() ||
                    slot.equals(restProperty) ||
                    slot.equals(owlModel.getOWLIntersectionOfProperty()) ||
                    slot.equals(owlModel.getOWLUnionOfProperty()) ||
                    slot.equals(owlModel.getOWLOneOfProperty()) ||
                    slot.equals(owlModel.getOWLDistinctMembersProperty())) {
                    frame.addOwnSlotValue(slot, rest);
                }
            }
        }
        li.delete();
    }


    private void deleteNamedClass(RDFSNamedClass cls) {
        // Delete depending anonymous classes before they are handled further down
        deleteNamedClassFromDomainsAndRanges(cls);
        deleteDependingAnonymousClses(cls);
        deleteAnonymousClses(cls.getSuperclasses(false));
        deleteAnonymousClses(cls.getReferringAnonymousClasses());
        deleteDependingListInstances(cls);
        super.deleteCls(cls);
    }


    private void deleteNamedClassFromDomainsAndRanges(RDFSNamedClass cls) {
        Collection anons = cls.getReferringAnonymousClasses();
        for (Iterator it = anons.iterator(); it.hasNext();) {
            OWLAnonymousClass anon = (OWLAnonymousClass) it.next();
            if (anon instanceof OWLUnionClass) {
                Iterator refs = owlModel.getReferences(anon).iterator();
                while (refs.hasNext()) {
                    Reference reference = (Reference) refs.next();
                    if (reference.getFrame() instanceof RDFProperty) {
                        RDFProperty property = (RDFProperty) reference.getFrame();
                        if (reference.getSlot().equals(owlModel.getRDFSDomainProperty())) {
                            property.removeUnionDomainClass(cls);
                        }
                        else if (reference.getSlot().equals(owlModel.getRDFSRangeProperty())) {
                            Collection remainingClasses = new ArrayList(property.getUnionRangeClasses());
                            remainingClasses.remove(cls);
                            property.setUnionRangeClasses(remainingClasses);
                        }
                    }
                }
            }
        }

        // remove from the domain if not in a union
        Collection refs = owlModel.getReferences(cls);
        for (Iterator i=refs.iterator(); i.hasNext();){
            Reference ref = (Reference) i.next();
            if (ref.getSlot().equals(owlModel.getRDFSDomainProperty())) {
                RDFProperty prop = (RDFProperty) ref.getFrame();
                prop.removeUnionDomainClass(cls);
            }
        }
    }


    /**
     * Deletes all QuantifierRestrictions (and the expressions where they are used)
     * which restrict a given propertyerty.  This is needed when a property changes its type.
     *
     * @param property the restricted property
     */
    public void deleteQuantifierRestrictions(OWLProperty property) {
        Collection roots = new HashSet();
        for (Iterator it = owlModel.getReferences(property).iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof OWLQuantifierRestriction) {
                OWLQuantifierRestriction r = (OWLQuantifierRestriction) reference.getFrame();
                roots.add(r.getExpressionRoot());
            }
        }
        for (Iterator it = roots.iterator(); it.hasNext();) {
            OWLAnonymousClass cls = (OWLAnonymousClass) it.next();
            if (log.isLoggable(Level.FINE)) {
                log.fine("- Deleting quantifier restriction root " + cls.getBrowserText());
            }
            cls.delete();
        }
    }


    private void deleteListChain(RDFList list) {
        Object first = list.getFirst();
        if (first instanceof OWLAnonymousClass) {
            list.setFirst(null);
            ((OWLAnonymousClass) first).delete();
        }
        RDFList rest = list.getRest();
        if (rest != null && !owlModel.getRDFNil().equals(rest)) {
            rest.delete();
        }
    }




    private void ensureRDFSSubClassOfStatementsExistForNamedOperands(OWLIntersectionClass intersectionClass) {
        Collection superclasses = intersectionClass.getSuperclasses(false);
        if (superclasses.size() == 1) {
            RDFSNamedClass namedClass = (RDFSNamedClass) superclasses.iterator().next();
            RDFProperty scp = owlModel.getRDFSSubClassOfProperty();
            Iterator operands = intersectionClass.listOperands();
            while (operands.hasNext()) {
                RDFSClass operand = (RDFSClass) operands.next();
                if (operand instanceof RDFSNamedClass) {
                    if (!namedClass.getPropertyValues(scp).contains(operand)) {
                        namedClass.addPropertyValue(scp, operand);
                    }
                }
            }
        }
    }


    /**
     * A slighly adjusted version of DeleteSimplificationFrameStore
     * that ignores links through the anonymous root class.
     *
     * @param cls
     * @return see the super method
     */
    public Collection getClsesToDelete(Cls cls) {
        Collection subclasses = getSubclasses(cls);
        Collection clsesToBeDeleted = new HashSet(subclasses);
        clsesToBeDeleted.add(cls);

        Iterator it = subclasses.iterator();
        while (it.hasNext()) {
            Cls subclass = (Cls) it.next();
            if (!subclass.equals(cls) && isReachableByAnotherRoute(subclass, clsesToBeDeleted)) {
                clsesToBeDeleted.remove(subclass);
                Collection subsubclasses = new HashSet(getSubclasses(subclass));
                subsubclasses.remove(cls);
                clsesToBeDeleted.removeAll(subsubclasses);
            }
        }
        return clsesToBeDeleted;
    }


    private DeleteSimplificationFrameStore getDeleteSimplificationFrameStore() {
        for (Iterator it = owlModel.getFrameStores().iterator(); it.hasNext();) {
            FrameStore frameStore = (FrameStore) it.next();
            if (frameStore instanceof DeleteSimplificationFrameStore) {
                return (DeleteSimplificationFrameStore) frameStore;
            }
        }
        return null;
    }

 

    /**
     * @deprecated This method was moved into AbstractOWLModel
     */     
    @Deprecated
    public List getLiteralValues(final List values) {
        List result = new ArrayList();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSLiteral) {
                result.add(o);
            }
            else {
                result.add(owlModel.createRDFSLiteral(o));
            }
        }
        return result;
    }


    private Collection getSlotsToDelete(Slot slot) {
        DeleteSimplificationFrameStore fs = getDeleteSimplificationFrameStore();
        return fs.getSlotsToDelete(slot);
    }





    public static boolean isIgnorePrefixesInSearch(OWLModel owlModel) {
        return Boolean.TRUE.equals(owlModel.getOWLProject().getSettingsMap().getBoolean(IGNORE_PREFIXES_IN_SEARCH));
    }


    private boolean isReachableByAnotherRoute(Cls subclass, Collection classesToBeDeleted) {
        Collection superclasses = new ArrayList(getDirectSuperclasses(subclass));
        superclasses.remove(owlModel.getAnonymousRootCls());
        if (superclasses.size() > 1) {
            Iterator it = superclasses.iterator();
            while (it.hasNext()) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof RDFSNamedClass && !classesToBeDeleted.contains(superCls)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Frame Store implementation
     * 
     */
    
    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        if (frame instanceof RDFProperty && slot.equals(owlModel.getRDFSRangeProperty())) {
            List oldValues = frame.getDirectOwnSlotValues(slot);

            super.setDirectOwnSlotValues(frame, slot, values);
            deleteAnonymousClasses(oldValues, values);
        }
        else if (frame instanceof RDFProperty && slot.equals(owlModel.getRDFSDomainProperty())) {
            List oldValues = frame.getDirectOwnSlotValues(slot);
            super.setDirectOwnSlotValues(frame, slot, values);
            deleteAnonymousClasses(oldValues, values);
        }
        else {
            super.setDirectOwnSlotValues(frame, slot, values);
        }
    }
    
    @Override
    public Cls createCls(FrameID id, Collection directTypes, Collection directSuperclasses, boolean loadDefaults) {
        Cls cls = super.createCls(id, directTypes, directSuperclasses, loadDefaults);
        if (cls instanceof RDFSNamedClass) {
            ((RDFSNamedClass) cls).setPropertyValues(owlModel.getRDFSSubClassOfProperty(), directSuperclasses);
        }
        if (cls instanceof OWLNamedClass && cls.isEditable()) {
            for (Iterator it = directSuperclasses.iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof OWLNamedClass &&
                    ((OWLNamedClass) superCls).getSubclassesDisjoint()) {
                    OWLUtil.ensureSubclassesDisjoint((OWLNamedClass) superCls);
                }
            }
        }
        return cls;
    }
    

    @Override
    public void deleteCls(Cls cls) {
        if (cls instanceof OWLAnonymousClass) {
            deleteAnonymousClass((OWLAnonymousClass) cls);
        }
        else if (cls instanceof RDFSNamedClass) {
            deleteNamedClass((RDFSNamedClass) cls);
        }
        else {
            deleteDependingListInstances(cls);
            super.deleteCls(cls);  // Default handling for anything else
        }
    }


    /**
     * Deletes the depending classes (anonymous domains and ranges) of the slot
     * being deleted.
     *
     * @param slot the Slot being deleted
     */
    @Override
    public void deleteSlot(Slot slot) {
        deleteDependingListInstances(slot);
        if (slot instanceof RDFProperty) {
            RDFProperty rdfProperty = (RDFProperty) slot;
            deleteDependingAnonymousClses(rdfProperty);
            if (rdfProperty.hasObjectRange()) {
                deleteAnonymousClses(rdfProperty.getPropertyValues(owlModel.getRDFSRangeProperty()));
            }
            Collection domain = slot.getDirectDomain();
            deleteAnonymousClses(domain);
            RDFResource range = rdfProperty.getRange();
            if (range instanceof RDFSDatatype && range.isAnonymous()) {
                range.delete();
            }
        }
        super.deleteSlot(slot);
    }
    

    @Override
    public void deleteSimpleInstance(SimpleInstance simpleInstance) {
        if (simpleInstance instanceof RDFList) {
            deleteListChain((RDFList) simpleInstance);
        }
        deleteDependingListInstances(simpleInstance);
        if (simpleInstance instanceof RDFResource && !deletingRDFSDatatype) {
            RDFResource resource = (RDFResource) simpleInstance;
            deleteAnonymousClses(resource.getReferringAnonymousClasses());
        }
        super.deleteSimpleInstance(simpleInstance);
    }



    @Override
    public Set getClsesWithMatchingBrowserText(String value, Collection superclasses, int maxMatches) {
        Set results = new HashSet();
        results.addAll(super.getClsesWithMatchingBrowserText(value, superclasses, maxMatches));
        if (isIgnorePrefixesInSearch(owlModel)) {
            Iterator it = owlModel.getNamespaceManager().getPrefixes().iterator();
            while (it.hasNext()) {
                String prefix = (String) it.next();
                results.addAll(super.getClsesWithMatchingBrowserText(prefix + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + value, superclasses, maxMatches));
            }
        }
        return results;
    }

    @Override
    public Set getFramesWithMatchingDirectOwnSlotValue(Slot slot, String value, int maxMatches) {
        Set results = super.getFramesWithMatchingDirectOwnSlotValue(slot, value, maxMatches);
        return results;
    }


    @Override
    public Set getMatchingReferences(String value, int maxMatches) {
        Set results = new HashSet();
        results.addAll(super.getMatchingReferences(value, maxMatches));
        if (isIgnorePrefixesInSearch(owlModel)) {
            Iterator it = owlModel.getNamespaceManager().getPrefixes().iterator();
            while (it.hasNext()) {
                String prefix = (String) it.next();
                results.addAll(super.getMatchingReferences(prefix + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + value, maxMatches));
            }
        }
        return results;
    }


    @Override
    public void removeDirectSuperclass(Cls cls, Cls superCls) {
        super.removeDirectSuperclass(cls, superCls);
        if (superCls instanceof OWLAnonymousClass && autoDeleteOfAnonymousClses) {
            superCls.delete();
        }
    }
    
}
