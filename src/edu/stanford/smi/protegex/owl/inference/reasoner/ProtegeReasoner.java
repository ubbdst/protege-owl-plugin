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

package edu.stanford.smi.protegex.owl.inference.reasoner;

import java.util.Collection;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

public interface ProtegeReasoner extends Disposable {
	
	///////////////////////////////////////////////////////////
	//
	// General ontology management
	//
	///////////////////////////////////////////////////////////


	/**
	 * Returns the OWL model to which this reasoner is attached to.
	 */
	public OWLModel getOWLModel();
	
	
	/**
	 * Sets the OWL model that will be attached to this reasoner.
	 * The reasoner may do in this method a clean-up of the 
	 * previously attached OWL model.
	 * @param owlModel
	 */
	public void setOWLModel(OWLModel owlModel);

	/**
	 * This is an optional method that can be called by applications
	 * and that gives the reasoner a chance to do some pre-computations.
	 */
	public void initialize();

	/**
	 * Detaches the OWL model from this reasoner, clears internal caches,
	 * and releases the resources allocated by this reasoner for the previously
	 * attached OWL model. 
	 * resources allocated 
	 */
	public void reset();
	
	/**
	 * Resynchronizes the reasoner with the OWL model. This usually implies a 
	 * reset and a reload of the entire OWL model and can be an expensive operation.
	 * This is a useful method if the OWL model has changed, but the reasoner is not 
	 * aware of the changes.
	 */
	public void rebind();
	

	///////////////////////////////////////////////////////////
	//
	// General inferences over the ontology with updating 
	//   Protege-OWL structures
	//
	///////////////////////////////////////////////////////////


	/**
	 * For each named class in the ontology, this method queries the reasoner
	 * to obtain its inferred superclasses.  This information is then pieced together
	 * to form the inferred hierarchy.  The method updates the protege-owl structures
	 * that hold information about inferred superclasses, which means that methods
	 * such as <code>getInferredSuperClasses</code> on <code>RDFSClass</code> will
	 * return meaningful results.
	 */
	public void computeInferredHierarchy() throws ProtegeReasonerException;

	/**
	 * This method queries the reasoner for the consistency of all classes in
	 * the ontology, and updates protege-owl with the information.     
	 */
	public void computeInconsistentConcepts() throws ProtegeReasonerException;



	/**
	 * This method queries the reasoner for the types of all individuals in the
	 * ontology, and updates protege-owl with the information. 
	 */
	public void computeInferredIndividualTypes() throws ProtegeReasonerException;


	/**
	 * This method queries the reasoner the equivalent classes for each class in the
	 * ontology, and updates protege-owl with the information. 
	 */
	public void computeEquivalentConcepts() throws ProtegeReasonerException;


	/**
	 * For each named class in the ontology, this method queries the reasoner for the
	 * consistency of the class, its inferred super classes and its inferred equivalent
	 * classes.  The method updates the protege-owl structures that hold this information,
	 * meaning that methods on <code>RDFSClass</code> such as <code>getInferredSuperClasses</code>
	 * will return meaningful results.     
	 */
	public void classifyTaxonomy() throws ProtegeReasonerException;

	
	
    ///////////////////////////////////////////////////////////
    //
    // Concept Satisfiability
    //
    ///////////////////////////////////////////////////////////
	
	
    /**
     * This method queries the reasoner to determine if the specified concept is satisfiable.
     *
     * @param aClass       The <code>OWLClass</code> whose satisfiablity is to be determined.  
     * @return <code>true</code> if the specified class is satisfiable (consistent)
     *         , or <code>false</code> if the specified class is not satisfiable (not consistent).
     */
    public boolean isSatisfiable(OWLClass aClass) throws ProtegeReasonerException;


    /**
     * Determines if the intersection of the list of specified classes is satisfiable.
     *
     * @param clses        An array of <code>OWLClass</code>es, of which the satisfiablity of the
     *                     intersection will be determined.
     * @return <code>true</code> if the intersection of the list of classes is satisfiable (consistent)
     *         or <code>false</code> if the intersection of the list of classes is not satisfiable (not consistent)
     */
    public boolean isIntersectionSatisfiable(OWLClass[] clses) throws ProtegeReasonerException;


    /**
     * Determines if one class (cls1) is subsumed by another class (cls2).  In other words,
     * determines if cls2 is a superclass of cls1.
     *
     * @param cls1         The subsumee (the expected subclass)
     * @param cls2         The subsumer (the expected superclass)    
     * @return <code>true</code> if cls1 is subsumed by cls2, or <code>false</code> if cls1
     *         is not subsumed by cls2.
     */
    public boolean isSubsumedBy(OWLClass cls1,
                                OWLClass cls2) throws ProtegeReasonerException;


    /**
     * Determines if the specified classes are disjoint from each other.  Note that
     * because disjointedness is a symmetric property, the order of the specified
     * classes does not matter.
     *
     * @param cls1         An <code>OWLClass</code>
     * @param cls2         An <code>OWLClass</code>    
     * @return <code>true</code> if cls1 is disjoint with cls2.
     */
    public boolean isDisjointTo(OWLClass cls1,
                                OWLClass cls2) throws ProtegeReasonerException;


    ///////////////////////////////////////////////////////////
    //
    // Concept Hierarchy
    //
    ///////////////////////////////////////////////////////////
    

    /**
     * Gets the (direct) inferred superclasses of the specified class.
     *
     * @param aClass       The class whose inferred superclasses are to be retrieved.
     * @return A <code>Collection</code> containing the classes that are the
     *         inferred superclasses of the specified class.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLClass> getSuperclasses(OWLClass aClass) throws ProtegeReasonerException;


    /**
     * Gets the (direct) inferred subclasses of the specified class.
     *
     * @param aClass       The class whose inferred subclasses are to be retrieved.
     * @return A <code>Collection</code> containing the inferred subclasses of the
     *         specified class.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLClass> getSubclasses(OWLClass aClass) throws ProtegeReasonerException;


    /**
     * Gets the inferred ancestor classes of the specified class. The inferred
     * ancestor classes is equivalent to the transitive closure of the inferred
     * superclasses.
     *
     * @param aClass       The class whose inferred ancestor classes are to be retrieved.
     * @return A <code>Collection</code> containing the inferred ancestor classes
     *         of the specified class.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLClass> getAncestorClasses(OWLClass aClass) throws ProtegeReasonerException;


    /**
     * Gets the inferred descendant classes of the specified class. The inferred
     * descendant classes is equivalent to the transitive closure of the inferred
     * subclasses.
     *
     * @param aClass       The class whose descendent classes are to be retrieved. 
     * @return A <code>Collection</code> containing the inferred descendent classes.
     */
    public Collection<OWLClass> getDescendantClasses(OWLClass aClass) throws ProtegeReasonerException;


    /**
     * Gets the equivalent classes of the specified class.
     *
     * @param aClass       The class whose equivalent classes are to be retrieved.
     * @return A <code>Collection</code> containing the inferred equivalent classes
     *         of the specified class.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLClass> getEquivalentClasses(OWLClass aClass) throws ProtegeReasonerException;
    


    ///////////////////////////////////////////////////////////
    //
    // Properties - to implement later
    //
    ///////////////////////////////////////////////////////////

       
    Collection<OWLProperty> getSuperProperties(OWLProperty property) throws ProtegeReasonerException;;
    
	Collection<OWLProperty> getAncestorProperties(OWLProperty property) throws ProtegeReasonerException;;

	Collection<OWLProperty> getSubProperties(OWLProperty property) throws ProtegeReasonerException;;
	
	Collection<OWLProperty> getDescendantProperties(OWLProperty property) throws ProtegeReasonerException;;
	

    ///////////////////////////////////////////////////////////
    //
    // Individuals
    //
    ///////////////////////////////////////////////////////////


    /**
     * Gets the individuals that are inferred to be members of the specified class.
     *
     * @param aClass       The class whose members are to be retrieved.
     * @return A <code>Collection</code> containing the <code>OWLIndividuals</code>
     *         that are members of the specified class.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLIndividual> getIndividualsBelongingToClass(OWLClass aClass) throws ProtegeReasonerException;


    /**
     * Get the inferred types (direct and indirect) for the specified individual.
     *
     * @param individual   The individual whose inferred types are to be retrieved.
     * @return A <code>Collection</code> of <code>OWLClasses</code> that represent the
     *         inferred types for the specified individual.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLClass> getIndividualTypes(OWLIndividual individual) throws ProtegeReasonerException;

    
    /**
     * Get the direct inferred types for the specified individual.
     *
     * @param individual   The individual whose inferred direct types are to be retrieved.
     * @return A <code>Collection</code> of <code>OWLClasses</code> that represent the
     *         inferred direct types for the specified individual.
     * @throws ProtegeReasonerException
     */
    public Collection<OWLClass> getIndividualDirectTypes(OWLIndividual individual) throws ProtegeReasonerException;
	

	/**
	 * Get the individuals that are related to the <code>subject</code> individual 
	 * through the object property <code>objectProperty</code>.
	 * 
	 * @param subject - An <code>OWLIndividual</code>
	 * @param objectProperty - A <code>OWLObjectProperty</code>
	 * @return A <code>Collection</code> of <code>OWLIndividuals</code> that are related
	 * 			to the <code>subject</code> individual through <code>objectProperty</code> 
	 * @throws ProtegeReasonerException
	 */
	public Collection<OWLIndividual> getRelatedIndividuals(OWLIndividual subject, OWLObjectProperty objectProperty) throws ProtegeReasonerException;

	
	/**
	 * Get the values that are related to the <code>subject</code> individual 
	 * through the datatype property <code>datatypeProperty</code>.
	 * 
	 * @param subject - An <code>OWLIndividual</code>
	 * @param datatypeProperty - An <code>OWLDatatypeProperty</code>
	 * @return A <code>Collection</code> of values that are related
	 * 			to the <code>subject</code> individual through <code>datatypeProperty</code> 
	 * @throws ProtegeReasonerException
	 */
	public Collection getRelatedValues(OWLIndividual subject, OWLDatatypeProperty datatypeProperty) throws ProtegeReasonerException;

	

    ///////////////////////////////////////////////////////////
    //
    // Synchronization
    //
    ///////////////////////////////////////////////////////////

	/**
	 * Depending on the implementation of the ProtegeGenericReasoner,
	 * some kind of caching may be used.  This method will force
	 * the ProtegeOWLReasoner to synchronize the contents of the
	 * reasoner prior to the next query. (Note that
	 * this will not cause the reasoner to be resynchronized
	 * immediately - synchronisation will take place the next time
	 * the reasoner is queried).
	 */
	public void forceReasonerReSynchronization();


	/**
	 * Depending on the implementation of the ProtegeOWLReasoner,
	 * the DIG reasoner may automatically be sunchronized with changes
	 * in the Protege-OWL knowledgebase.  This method will enable/disable this
	 * autosynchronization (if it is used).  Note that if reasoner synchronisation
	 * is disabled, and then it is subsequently necessary to resynchronise the
	 * reasoner, the <code>forceReasonerReSynchronization</code> method should
	 * be used.
	 *
	 * @param b <code>true</code> to enable autosync, or <code>false</code>
	 *          to disable autosync.
	 */
	public void setAutoSynchronizationEnabled(boolean b);


	public boolean isAutoSynchronizationEnabled();

	
	
	///////////////////////////////////////////////////////////
    //
    // Reasoner task listener methods
    //
    ///////////////////////////////////////////////////////////
	

	/**
	 *  Set the listener which will be informed of the progress of
     *  the reasoning task. May be <code>null</code> if the progress of the task does not
     *  need to be monitored.
	 * @param reasonerTaskListener
	 */
	public void setReasonerTaskListener(ReasonerTaskListener reasonerTaskListener);

	public ReasonerTaskListener getReasonerTaskListener();
	
	
}
