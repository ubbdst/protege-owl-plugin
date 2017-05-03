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

package edu.stanford.smi.protegex.owl.model.triplestore;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceMap;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * An interface for low-level access to the single triples in an OWLModel.
 * All current implementations are better suited for querying than actually
 * writing triples, so better don't modify the triples in your own application
 * yet.  If you need to do that, make sure that you invoke the post-processor
 * as done in the ProtegeOWLParser.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleStore extends NamespaceMap, Disposable {

    NamespaceManager getNamespaceManager();

    OWLOntology  getOWLOntology();

    void add(Triple triple);


    void add(RDFResource subject, RDFProperty predicate, Object object);


    boolean contains(Triple triple);


    boolean contains(RDFResource subject, RDFProperty predicate, Object object);


    String getName();


    RDFResource getHomeResource(String name);


    /**
     * Provides access to the internal Protege storage for low-level access.
     * This method should only be used by experienced users.
     *
     * @return the NarrowFrameStore
     */
    NarrowFrameStore getNarrowFrameStore();


    /**
     * Gets all resources that have their "home" in this triple store.
     * The home is defined to be the TripleStore with the :NAME value of the resource.
     *
     * @return an Iterator of RDFResources
     */
    Iterator<RDFResource> listHomeResources();


    /**
     * Gets the values of a given subject/property combination.
     *
     * @param subject
     * @param property
     * @return a Collection of Objects (e.g. RDFResources)
     */
    Iterator listObjects(RDFResource subject, RDFProperty property);


    /**
     * The the subjects of all triples where a given property has any value.
     * The Iterator does not contain duplicates.
     *
     * @param property the property to look for
     * @return an Iterator of RDFResources
     */
    Iterator<RDFResource> listSubjects(RDFProperty property);


    /**
     * Gets the subjects of all triples with a given predicate and object.
     *
     * @param predicate the predicate to match
     * @param object    the object to match
     * @return an Iterator of RDFResources
     */
    Iterator listSubjects(RDFProperty predicate, Object object);


    Iterator<Triple> listTriples();

    /**
     * @return All the user defined classes (non-system) defined in this triplestore.
     */
    Set<RDFSNamedClass> getUserDefinedClasses();

    /**
     * @return All the user defined properties (non-system) defined in this triplestore.
     */
    Set<RDFProperty> getUserDefinedProperties();


    /**
     * Returns the direct class instances defined in this triplestore as objects of <code>X</code>.
     */
    <X extends RDFResource> Set<X> getUserDefinedDirectInstancesOf(RDFSClass rdfsClass, Class<? extends X> javaClass);

    /**
     * Returns the all (direct and indirect) class instances defined in this triplestore as objects of <code>X</code>.
     */
    <X extends RDFResource> Set<X> getUserDefinedInstancesOf(RDFSClass rdfsClass, Class<? extends X> javaClass);

    /**
     * Lists all Triples that have a given object.
     *
     * @param object the object to get the triples of
     * @return an Iterator of Triples
     */
    Iterator<Triple> listTriplesWithObject(RDFObject object);


    /**
     * Lists all Triples that have a given subject.
     * In other words, this returns all property-value pairs of a given resource.
     * Note that this operation is currently not efficiently implemented.
     *
     * @param subject the subject in the triples
     * @return an Iterator of Triples
     */
    Iterator<Triple> listTriplesWithSubject(RDFResource subject);


    void remove(Triple triple);


    void remove(RDFResource subject, RDFProperty predicate, Object object);


    void setName(String value);


    void sortPropertyValues(RDFResource resource, RDFProperty property, Comparator comparator);


    String getOriginalXMLBase();

    void setOriginalXMLBase(String xmlBase);


    /**
     * Debugging only.
     */
    void dump(Level level);


    /**
     * Disposes this triple store. Called by the triple store manager when an
     * OWL model is disposed.
     */
    void dispose();

    /**
     * Tracks the set of io names that have been used to retrieve this triple store.  This can be
     * different than the ontology name in the case of a broken import statement.
     */
    Collection<String> getIOAddresses();

    void addIOAddress(String uri);

    void removeIOAddress(String uri);

}
