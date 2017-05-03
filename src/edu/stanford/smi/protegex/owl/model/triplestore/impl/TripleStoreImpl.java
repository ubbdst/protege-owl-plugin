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

package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.Record;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

/**
 * A TripleStore that acts as a view on an existing NarrowFrameStore.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TripleStoreImpl implements TripleStore {
    private static transient final Logger log = Log.getLogger(TripleStoreImpl.class);

    private NamespaceManager namespaceManager;

    protected NarrowFrameStore narrowFrameStore;

    protected Slot nameSlot;

    protected OWLModel owlModel;

    protected TripleStoreModel tripleStoreModel;

    protected String originalXMLBase;

    private Collection<String> ioAddresses = new ArrayList<String>();

    private String name;

    public TripleStoreImpl(OWLModel owlModel, NarrowFrameStore frameStore, TripleStoreModel tripleStoreModel) {
        this(owlModel, frameStore, tripleStoreModel, new OWLNamespaceManager(), null);
    }

    public TripleStoreImpl(OWLModel owlModel, NarrowFrameStore narrowFrameStore, TripleStoreModel tripleStoreModel,
                           NamespaceManager namespaceManager, String name) {
        this.narrowFrameStore = narrowFrameStore;
        this.owlModel = owlModel;
        this.tripleStoreModel = tripleStoreModel;
        this.namespaceManager = namespaceManager;

        this.name = name;

        KnowledgeBase kb = owlModel;
        nameSlot = kb.getSystemFrames().getNameSlot();
    }

    public NamespaceManager getNamespaceManager() {
        return namespaceManager;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TripleStoreImpl) {
            return narrowFrameStore.getName().equals(((TripleStoreImpl) obj).narrowFrameStore.getName());
        }
        else {
            return false;
        }
    }


    public Iterator<Triple> listTriples() {
	    Collection<Slot> ignoreProperties = new HashSet<Slot>();
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        ignoreProperties.add(systemFrames.getOwlOntologyPrefixesProperty());
        ignoreProperties.add(systemFrames.getOwlOntologyPointerProperty());
        ignoreProperties.add(systemFrames.getDirectInstancesSlot());
        ignoreProperties.add(systemFrames.getDirectTypesSlot());

        ignoreProperties.add(systemFrames.getProtegeClassificationStatusProperty());
        ignoreProperties.add(systemFrames.getProtegeInferredSubclassesProperty());
        ignoreProperties.add(systemFrames.getProtegeInferredSuperclassesProperty());
        ignoreProperties.add(systemFrames.getProtegeInferredTypeProperty());


        List<Triple> triples = new ArrayList<Triple>();
        for (Record record : ((InMemoryFrameDb) narrowFrameStore).getRecords()) {
            Frame subject = record.getFrame();
            if (subject instanceof RDFResource) {
                Slot predicate = record.getSlot();
                if (log.isLoggable(Level.FINE)) {
                    log.fine("listTriples -- " + subject.getName() + " . " + predicate.getName());
                }
                if (predicate instanceof RDFProperty) {
                    if (record.getFacet() == null && !record.isTemplate() && !ignoreProperties.contains(predicate)) {
                        for (Object object : record.getValues()) {
                            if (log.isLoggable(Level.FINER)) {
                                log.finer("\tObject = " + object);
                            }
                            if (object instanceof String && DefaultRDFSLiteral.isRawValue((String) object)) {
                                object = new DefaultRDFSLiteral(owlModel, (String) object);
                            }
                            Triple triple = new DefaultTriple((RDFResource) subject, (RDFProperty) predicate, object);
                            triples.add(triple);
                        }
                    }
                }
            }
        }
        return triples.iterator();
    }

    @Override
    public int hashCode() {
    	return 0;
    }


    public void add(Triple triple) {
        add(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public void add(final RDFResource subject, final RDFProperty predicate, final Object object) {
        doChangesOnThisTripleStore(new Runnable() {
            public void run() {
                subject.addPropertyValue(predicate, object);
            }
        });
    }


    public boolean addValue(final Instance subject, final Slot slot, final Object object) {
        final Collection values = getValues(subject, slot);
        if (!values.contains(object)) {
            doChangesOnThisTripleStore(new Runnable() {
                public void run() {
                    subject.addOwnSlotValue(slot, object);
                }
            });
            return true;
        }
        else {
            return false;
        }
    }



    public boolean contains(Triple triple) {
        return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public boolean contains(RDFResource subject, RDFProperty predicate, Object object) {
    	Collection values = getValues(subject, predicate);
    	if (values == null) { return false;}

    	if (object instanceof RDFSLiteral) {
    		OWLModel owlModel = subject.getOWLModel();
    		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
    			Object value = iterator.next();
    			try {
    				RDFSLiteral lit = owlModel.asRDFSLiteral(value);
    				if (lit.equals(object)) {
    					return true;
    				}
    			} catch (IllegalArgumentException e) {
    				//convert to RDFLiteral throws IllegalArgumentException if it is a 
    				//OWLClass, etc.
    				Log.emptyCatchBlock(e); 
    			}
    		}
    		return false;
    	} else {
    		return values.contains(object);
    	}
    }


    public String getDefaultNamespace() {
        return getNamespaceForPrefix("");
    }

    public RDFResource getHomeResource(String name) {
        Collection values = narrowFrameStore.getFrames(nameSlot, null, false, name);
        if (values.isEmpty()) {
            return null;
        }
        else {
            return (RDFResource) values.iterator().next();
        }
    }

    public OWLOntology getOWLOntology() {
        String ontologyName = getName();
        if (ontologyName != null) {
            return (OWLOntology) ((KnowledgeBase) owlModel).getFrame(ontologyName);
        }
        else {
            return null;
        }
    }

    public String getName() {
        return name;
    }


    public String getNamespaceForPrefix(String prefix) {
    	return getNamespaceManager().getNamespaceForPrefix(prefix);
    }

    public NarrowFrameStore getNarrowFrameStore() {
        return narrowFrameStore;
    }


    public Collection getSlotValues(Instance instance, Slot slot) {
        return tripleStoreModel.getSlotValues(instance, slot);
    }


    protected Collection getValues(Instance instance, Slot slot) {
        return narrowFrameStore.getValues(instance, slot, null, false);
    }


    public String getPrefix(String namespace) {
    	return getNamespaceManager().getPrefix(namespace);
    }


    public Collection<String> getPrefixes() {
    	return getNamespaceManager().getPrefixes();
    }


    protected Collection<Reference> getReferences(Object search) {
        return narrowFrameStore.getReferences(search);
    }


    public Iterator<RDFResource> listHomeResources() {
        Collection frames = narrowFrameStore.getFramesWithAnyValue(nameSlot, null, false);
        Collection<RDFResource> results = AbstractOWLModel.getRDFResources(owlModel, frames);
        return results.iterator();
    }


    public Iterator listObjects(RDFResource subject, RDFProperty property) {
    	return new ArrayList(getValues(subject, property)).iterator();
    }


    public Iterator listSubjects(RDFProperty property) {
        Collection frames = narrowFrameStore.getFramesWithAnyValue(property, null, false);
        return frames.iterator();
    }


    public Iterator listSubjects(RDFProperty predicate, Object object) {
        if (object instanceof DefaultRDFSLiteral) {
            object = DefaultRDFSLiteral.getPlainValueIfPossible(object);
            if (object instanceof DefaultRDFSLiteral) {
                object = ((DefaultRDFSLiteral) object).getRawValue();
            }
        }
        return narrowFrameStore.getFrames(predicate, null, false, object).iterator();
    }


    public Iterator<Triple> listTriplesWithObject(RDFObject object) {
        Cls rdfproperty = ((KnowledgeBase) owlModel).getCls(RDFNames.Cls.PROPERTY);
        OWLJavaFactory factory = owlModel.getOWLJavaFactory();
        Object search = object;
        if (object instanceof DefaultRDFSLiteral) {
            Object plain = ((DefaultRDFSLiteral) object).getPlainValue();
            if (plain == null) {
                search = ((DefaultRDFSLiteral) object).getRawValue();
            }
            else {
                search = plain;
            }
        }
        Collection<Triple> triples = new ArrayList<Triple>();
        Collection<Reference> refs = getReferences(search);
        for (Object element : refs) {
            Reference reference = (Reference) element;
            if (reference.getFrame() instanceof RDFResource &&
                    reference.getSlot().hasType(rdfproperty) &&
                    !Model.SlotID.DIRECT_INSTANCES.equals(reference.getSlot().getFrameID())) {
                RDFProperty property = (RDFProperty) factory.createSlot(reference.getSlot().getFrameID(),
                                                                        reference.getSlot().getDirectTypes());
                Triple triple = new DefaultTriple((RDFResource) reference.getFrame(), property, object);
                triples.add(triple);
            }
        }
        return triples.iterator();
    }


    public Iterator listTriplesWithSubject(RDFResource subject) {
        Collection triples = new ArrayList();
        Iterator properties = subject.getOWLModel().listRDFProperties();
        while (properties.hasNext()) {
            RDFProperty property = (RDFProperty) properties.next();
            Iterator it = listObjects(subject, property);
            while (it.hasNext()) {
                Object object = it.next();
                Triple triple = new DefaultTriple(subject, property, object);
                triples.add(triple);
            }
        }
        return triples.iterator();
    }


    @SuppressWarnings("unchecked")
    public Set<RDFProperty> getUserDefinedProperties() {
        Collection possibleTypesForUserDefinedClasses = new HashSet(owlModel.getRDFPropertyClass().getSubclasses(true));
        possibleTypesForUserDefinedClasses.add(owlModel.getRDFPropertyClass());
        Set<RDFProperty> userDefinedClasses = new HashSet<RDFProperty>();
        for (Object type : possibleTypesForUserDefinedClasses) {
        	if (type instanceof RDFSClass) {
        		userDefinedClasses.addAll(getUserDefinedDirectInstancesOf((RDFSClass) type, RDFProperty.class));
        	}
        }
        return userDefinedClasses;
    }



    @SuppressWarnings("unchecked")
    public Set<RDFSNamedClass> getUserDefinedClasses() {
        Collection possibleTypesForUserDefinedClasses = new HashSet(owlModel.getRDFSNamedClassClass().getSubclasses(true));
        possibleTypesForUserDefinedClasses.add(owlModel.getRDFSNamedClassClass());
        Set<RDFSNamedClass> userDefinedClasses = new HashSet<RDFSNamedClass>();
        for (Object type : possibleTypesForUserDefinedClasses) {
        	if (type instanceof RDFSClass) {
        		userDefinedClasses.addAll(getUserDefinedDirectInstancesOf((RDFSClass) type, RDFSNamedClass.class));
        	}
        }
        return userDefinedClasses;
    }

    public <X extends RDFResource> Set<X> getUserDefinedDirectInstancesOf(RDFSClass rdfsClass, Class<? extends X> javaClass) {
        Collection<?> allInstances = getNarrowFrameStore().getValues(rdfsClass, owlModel.getSystemFrames().getDirectInstancesSlot(), null, false);
        Set<X> userDefinedInstances = new HashSet<X>();
        for (Object o : allInstances) {
            if (javaClass.isInstance(o)) {
                X resource = javaClass.cast(o);
                if (!resource.isSystem()) {
                    userDefinedInstances.add(resource);
                }
            }
        }
        return userDefinedInstances;
    }

    public <X extends RDFResource> Set<X> getUserDefinedInstancesOf(RDFSClass rdfsClass, Class<? extends X> javaClass) {
        Set<X> userDefinedInstances = new HashSet<X>(getUserDefinedDirectInstancesOf(rdfsClass, javaClass));
        for (Iterator iterator = rdfsClass.getSubclasses(true).iterator(); iterator.hasNext();) {
			RDFSClass subcls = (RDFSClass) iterator.next();
			if (!subcls.isSystem()) {
				userDefinedInstances.addAll(getUserDefinedInstancesOf(subcls, javaClass));
			}
		}
        return userDefinedInstances;
    }


    public void remove(Triple triple) {
        remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public void remove(final RDFResource subject, final RDFProperty predicate, final Object object) {
        doChangesOnThisTripleStore(new Runnable() {
            public void run() {
                subject.removePropertyValue(predicate, object);
            }
        });
    }


    public void setName(String value) {
        name = value;
        if (name != null) {
            getNarrowFrameStore().setName(value);
        }
    }


    public void removePrefix(String prefix) {
        namespaceManager.removePrefix(prefix);
    }


    public void setDefaultNamespace(String value) {
        setPrefix(value, "");
    }


    public void setDefaultNamespace(URI uri) {
        setDefaultNamespace(uri.toString());
    }


    public void setPrefix(String namespace, String prefix) {
    	getNamespaceManager().setPrefix(namespace, prefix);
    }


    public void setPrefix(URI namespace, String prefix) {
        setPrefix(namespace.toString(), prefix);
    }


    public void sortPropertyValues(RDFResource resource, RDFProperty property, Comparator comparator) {
        TripleStoreUtil.sortSlotValues(getNarrowFrameStore(), resource, property, comparator);
    }


    public String getOriginalXMLBase() {
        return originalXMLBase;
    }

    public void setOriginalXMLBase(String xmlBase) {
        originalXMLBase = xmlBase;
    }


    @Override
    public String toString() {
        return "TripleStore(" + getName() + ")";
    }


    public void dump(Level level) {
        if (log.isLoggable(level)) {
            log.log(level, "Triples in " + getName());
            Iterator it = listTriples();
            while (it.hasNext()) {
                log.log(level, " - " + it.next());
            }
        }
    }

    public void dispose() {

    }

    public void addIOAddress(String uri) {
        if (!ioAddresses.contains(uri)) {
            ioAddresses.add(uri);
        }
    }

    public Collection<String> getIOAddresses() {
        return Collections.unmodifiableCollection(ioAddresses);
    }

    public void removeIOAddress(String uri) {
        ioAddresses.remove(uri);
    }

    private void doChangesOnThisTripleStore(Runnable changes) {
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        try {
            tripleStoreModel.setActiveTripleStore(this);
            changes.run();
        }
        finally {
            tripleStoreModel.setActiveTripleStore(activeTripleStore);
        }
    }

}
