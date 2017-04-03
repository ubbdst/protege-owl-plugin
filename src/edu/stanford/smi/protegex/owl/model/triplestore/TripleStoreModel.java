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
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * The TripleStoreModel is an access layer on top of the normal OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleStoreModel extends Disposable{


    /**
     * A low level method that creates and adds a new TripleStore to implement imports.
     *
     * @param frameStore the FrameStore to be used with the TripleStore
     * @return the new TripleStore
     */
    TripleStore createActiveImportedTripleStore(NarrowFrameStore frameStore);


    /**
     * Deletes an imported TripleStore.
     *
     * @param tripleStore the TripleStore to delete (must be neither system nor top TripleStore)
     */
    void deleteTripleStore(TripleStore tripleStore);


    /**
     * Gets the currently "active" TripleStore, which all future set operations will be
     * operating on.
     *
     * @return the active TripleStore
     * @see #getTripleStores
     * @see #setActiveTripleStore
     */
    TripleStore getActiveTripleStore();


    /**
     * Gets the "home" triple store of a given resource.  This can be used to determine the
     * TripleStore where changes on the resource should be performed consistently.
     * In the default implementation, the home TripleStore is the one that contains a :NAME
     * slot value of the resource.  The intention is that this will be where the resource
     * is first defined (using the ordering defined by the imports tree with imported  ontologies
     * first).
     *
     * The reality is that this is not well defined.  It is easy to imagine that  there are
     * several triple stores that have  a :NAME slot value for the resource.   In  this case
     * this routine chooses one of these triple stores at random.
     *
     * @param resource the RDFResource to find the home TripleStore of
     * @return the home TripleStore
     */
    TripleStore getHomeTripleStore(RDFResource resource);


    /**
     * The home triple store of the triple defined by the method arguments.
     * @param subject - the subject of the triple
     * @param predicate - the predicate of the triple
     * @param object - the object of the triple
     * @return - the triplestore in which the triple <code>(subject, predicate, object)</code> is defined
     */
    TripleStore getHomeTripleStore(Instance subject, Slot predicate, Object object);


    /**
     * A low-level access method to access property values while the usual API methods are
     * in an invalid state.  This may be necessary during the direct manipulation of triples
     * on a TripleStore level.
     *
     * @param resource the resource to get the property values of
     * @param property the property to get the values of
     * @return a Collection of property values (may include RDFSLiterals)
     */
    Collection getPropertyValues(RDFResource resource, RDFProperty property);


    /**
     * Similar to getPropertyValues but for native Protege slots and instances.
     *
     * @param instance the instance to get the values of
     * @param slot     the slot to get the values of
     * @return a Collection of slot values (not including RDFSLiterals)
     */
    Collection getSlotValues(Instance instance, Slot slot);


    /**
     * Gets the TripleStore with a given name.
     * For additional TripleStores, the name corresponds to the import URI.
     *
     * @param name the name / import URI of the TripleStore
     * @return the TripleStore or null if none of this name exists
     */
    TripleStore getTripleStore(String name);


    /**
     * Gets the first TripleStore that uses a given default namespace.
     *
     * @param namespace the namespace
     * @return the TripleStore or null
     */
    TripleStore getTripleStoreByDefaultNamespace(String namespace);

    /**
     * Gets the System Triple Store.
     *
     * @return the system triple store
     */
    TripleStore getSystemTripleStore();


    /**
     * Gets the TripleStores which provide an RDF triple-based view onto this OWLModel.
     * <B>Warning: This is work in progress!</B>
     *
     * @return a collection of TripleStore objects
     */
    List<TripleStore> getTripleStores();


    /**
     * Gets the "top" TripleStore, which is the first user-editable TripleStore,
     * from which all imports start.
     *
     * @return the top TripleStore (never null)
     */
    TripleStore getTopTripleStore();


    /**
     * Checks if a given triple is stored in the currently active TripleStore.
     * This is a shortcut for <CODE>getActiveTripleStore().contains(...)</CODE>.
     *
     * @param subject   the subject of the triple
     * @param predicate the predicate of the triple
     * @param object    the object of the triple
     * @return true  if the value is found in the active TripleStore
     */
    boolean isActiveTriple(RDFResource subject, RDFProperty predicate, Object object);


    /**
     * Checks if a given triple is stored in an editable TripleStore.
     * Editable TripleStores are those that could be saved to a local file, using the
     * OWLModel's URIResolver.
     *
     * @param subject   the subject of the triple
     * @param predicate the predicate of the triple
     * @param object    the object of the triple
     * @return true  if the value is found in an editable TripleStore
     */
    boolean isEditableTriple(RDFResource subject, RDFProperty predicate, Object object);


    boolean isEditableTripleStore(TripleStore ts);


    /**
     * Combines the result of the corresponding method of each TripleStore.
     *
     * @param subject the subject to get all triples of
     * @return an Iterator of Triples
     */
    Iterator<Triple> listTriplesWithSubject(RDFResource subject);


    /**
     * Returns an iterator of all subjects in any triplestore
     * that have a value for property.
     */
    Iterator<RDFResource> listSubjects(RDFProperty property);


    /**
     * Provides an Iterator on all user TripleStores, i.e. all results of <CODE>getTripleStores()</CODE>
     * except for the first (system) TripleStore.
     *
     * @return an Iterator of TripleStore objects
     */
    Iterator<TripleStore> listUserTripleStores();


    void replaceJavaObject(RDFResource subject);


    /**
     * Specifies which TripleStore shall be the active one for future write operations.
     *
     * @param tripleStore one of the results of a recent call to <CODE>getTripleStores()</CODE>.
     */
    void setActiveTripleStore(TripleStore tripleStore);


    /**
     * Moves a given RDFResource into another "home" TripleStore.  This does not move any other
     * triples but only moves the low-level information about which TripleStore shall be regarded
     * as the "home" in the future.  In Protege, this is the graph where the :NAME slot value is
     * stored, i.e. moving the home means moving the :NAME value.
     *
     * @param resource    the RDFResource to move
     * @param tripleStore the new home TripleStoe
     */
    void setHomeTripleStore(RDFResource resource, TripleStore tripleStore);


    /**
     * Sets the top level triple store to the currently active triple store.
     *
     * This should only be called early on in the initialization sequence.
     * Use of this after an owl model is loaded will lead to unpredictable
     * behavior.
     */
    void setTopTripleStore(TripleStore tripleStore);

    void setViewActiveOnly(boolean viewActiveOnly);

    /**
     * Changes the <CODE>isIncluded()</CODE> value of all resources to reflect the
     * currently active TripleStore.  This should be called if the editable flag
     * is relevant (e.g. in a user interface) after changes of the active TripleStore.
     */
    void updateEditableResourceState();

    /**
     * Cleans up all the triple stores managed by this.
     * This is called when an OWLModel is disposed (e.g. at project close in the UI).
     */
    void dispose();

}
