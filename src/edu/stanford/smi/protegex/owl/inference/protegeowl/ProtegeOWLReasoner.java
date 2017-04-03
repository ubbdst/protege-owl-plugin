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

package edu.stanford.smi.protegex.owl.inference.protegeowl;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasoner;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Collection;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The ProtegeOWLReasoner is an interface to an external DIG
 * compliant reasoner.  DIG (Description Logic Implementation Group) is
 * a standard language for talking to a description logic reasoner over
 * http.
 * 
 * @deprecated - Use {@link ProtegeReasoner}
 */
@Deprecated
public interface ProtegeOWLReasoner {

    public static final int NO_SUBSUMPTION_RELATIONSHIP = 0;

    public static final int CLS1_SUBSUMES_CLS2 = 1;

    public static final int CLS1_SUBSUMED_BY_CLS2 = 2;

    public static final int CLS1_EQUIVALENT_TO_CLS2 = 3;


    /**
     * Sets the URL of the external DIG reasoner
     */
    public void setURL(String url);


    /**
     * Gets the URL of the external DIG reasoner.
     *
     * @return A <code>String</code> representing the URL.
     */
    public String getURL();


    /**
     * Determines if the ProtegeOWLReasoner
     * can connect to an external DIG compliant
     * reasoner to provide reasoning services.
     * If this method returns <code>false</code> then
     * none of the reaoning methods will work.
     */
    public boolean isConnected();


    /**
     * Gets the identity of the DIG reasoner.  The
     * <code>DIGReasonerIdentity</code> describes the
     * name, version and capabilities of an external DIG
     * reasoner.
     */
    public DIGReasonerIdentity getIdentity();


    /**
     * Gets the knowledge base (OWLModel) that this reasoner
     * reasons over.
     */
    public OWLModel getKnowledgeBase();


    /**
     * Gets the DIGReasoner that this reasoner uses
     * to talk to the external DIG Reasoner Process
     */
    public DIGReasoner getDIGReasoner();


    /**
     * The external DIG Reasoner that provides the reasoning services
     * assigns each knowledgebase that it knows about a URI in order
     * to identify the knowledgebase.  This method returns the URI
     * corresponding to the Protege-OWL knowledgebase that this reasoner
     * reasons about.
     *
     * @return A <code>String</code> representing the URI.
     */
    public String getReasonerKnowledgeBaseURI();


    /**
     * Depending on the implementation of the ProtegeOWLReasoner,
     * some kind of caching may be used.  This method will force
     * the ProtegeOWLReasoner to synchronize the contents of the
     * external DIG reasoner prior to the next query. (Note that
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
    // General inferences over the ontology
    //
    ///////////////////////////////////////////////////////////


    /**
     * For each named class in the ontology, this method queries the reasoner
     * to obtain its inferred superclasses.  This information is then pieced together
     * to form the inferred hierarchy.  The method updates the protege-owl structures
     * that hold information about inferred superclasses, which means that methods
     * such as <code>getInferredSuperClasses</code> on <code>RDFSClass</code> will
     * return meaningful results.
     *
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     */
    public void computeInferredHierarchy(ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * This method queries the reasoner for the consistency of all classes in
     * the ontology, and updates protege-owl with the information.
     *
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     */
    public void computeInconsistentConcepts(ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     */
    public void computeInferredIndividualTypes(ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     */
    public void computeEquivalentConcepts(ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * For each named class in the ontology, this method queries the reasoner for the
     * consistency of the class, its inferred super classes and its inferred equivalent
     * classes.  The method updates the protege-owl structures that hold this information,
     * meaning that methods on <code>RDFSClass</code> such as <code>getInferredSuperClasses</code>
     * will return meaningful results.
     *
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     */
    public void classifyTaxonomy(ReasonerTaskListener taskListener) throws DIGReasonerException;

    ///////////////////////////////////////////////////////////
    //
    // Concept Satisfiability
    //
    ///////////////////////////////////////////////////////////


    /**
     * This method queries the reasoner to determine if the specified concept is satisfiable.
     *
     * @param aClass       The <code>OWLClass</code> whose satisfiablity is to be determined.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return <code>true</code> if the specified class is satisfiable (consistent)
     *         , or <code>false</code> if the specified class is not satisfiable (not consistent).
     */
    public boolean isSatisfiable(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Determines if the intersection of the list of specified classes is satisfiable.
     *
     * @param clses        An array of <code>OWLClass</code>es, of which the satisfiablity of the
     *                     intersection will be determined.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return <code>true</code> if the intersection of the list of classes is satisfiable (consistent)
     *         or <code>false</code> if the intersection of the list of classes is not satisfiable (not consistent)
     */
    public boolean isIntersectionSatisfiable(OWLClass[] clses, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Determines if one class (cls1) is subsumed by another class (cls2).  In other words,
     * determines if cls2 is a superclass of cls1.
     *
     * @param cls1         The subsumee (the expected subclass)
     * @param cls2         The subsumer (the expected superclass)
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return <code>true</code> if cls1 is subsumed by cls2, or <code>false</code> if cls1
     *         is not subsumed by cls2.
     */
    public boolean isSubsumedBy(OWLClass cls1,
                                OWLClass cls2,
                                ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Determines if the specified classes are disjoint from each other.  Note that
     * because disjointedness is a symmetric property, the order of the specified
     * classes does not matter.
     *
     * @param cls1         An <code>OWLClass</code>
     * @param cls2         An <code>OWLClass</code>
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return <code>true</code> if cls1 is disjoint with cls2.
     */
    public boolean isDisjointTo(OWLClass cls1,
                                OWLClass cls2,
                                ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Gets the subsumption relationship between two classes - the subsumption relationship
     * of cls1 to cls2.  The ordering of arguments is important.
     *
     * @param cls1         An <code>OWLClass</code>
     * @param cls2         An <code>OWLClass</code>
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return An integer that indicates the subsumption relationship of cls1 in relation to cls2. See
     *         the constants defined in <code>ProtegeOWLReasoner</code> for result values
     * @throws DIGReasonerException
     */
    public int getSubsumptionRelationship(OWLClass cls1,
                                          OWLClass cls2,
                                          ReasonerTaskListener taskListener) throws DIGReasonerException;

    ///////////////////////////////////////////////////////////
    //
    // Concept Hierarchy
    //
    ///////////////////////////////////////////////////////////


    /**
     * Gets the (direct) inferred superclasses of the specified class.
     *
     * @param aClass       The class whose inferred superclasses are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> containing the classes that are the
     *         inferred superclasses of the specified class.
     * @throws DIGReasonerException
     */
    public Collection getSuperclasses(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Gets the inferred superclasses of the intersection of the list of
     * specified classes.
     *
     * @param clses        An array of <code>OWLClass</code>es, the intersection of
     *                     which will be obtained and then the inferred superclasses of this intersection
     *                     class will be retrieved from the reasoner.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> of classes.
     * @throws DIGReasonerException
     */
    public Collection getSuperclassesOfIntersection(OWLClass[] clses, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Gets the (direct) inferred subclasses of the specified class.
     *
     * @param aClass       The class whose inferred subclasses are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> containing the inferred subclasses of the
     *         specified class.
     * @throws DIGReasonerException
     */
    public Collection getSubclasses(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Gets the inferred ancestor classes of the specified class. The inferred
     * ancestor classes is equivalent to the transitive closure of the inferred
     * superclasses.
     *
     * @param aClass       The class whose inferred ancestor classes are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> containing the inferred ancestor classes
     *         of the specified class.
     * @throws DIGReasonerException
     */
    public Collection getAncestorClasses(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Gets the inferred descendant classes of the specified class. The inferred
     * descendant classes is equivalent to the transitive closure of the inferred
     * subclasses.
     *
     * @param aClass       The class whose descendent classes are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> containing the inferred descendent classes.
     */
    public Collection getDescendantClasses(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Gets the equivalent classes of the specified class.
     *
     * @param aClass       The class whose equivalent classes are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> containing the inferred equivalent classes
     *         of the specified class.
     * @throws DIGReasonerException
     */
    public Collection getEquivalentClasses(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;

    ///////////////////////////////////////////////////////////
    //
    // Individuals
    //
    ///////////////////////////////////////////////////////////


    /**
     * Gets the individuals that are inferred to be members of the specified class.
     *
     * @param aClass       The class whose members are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> containing the <code>OWLIndividuals</code>
     *         that are members of the specified class.
     * @throws DIGReasonerException
     */
    public Collection getIndividualsBelongingToClass(OWLClass aClass, ReasonerTaskListener taskListener) throws DIGReasonerException;


    /**
     * Get the inferred types for the specified individual.
     *
     * @param individual   The individual whose inferred types are to be retrieved.
     * @param taskListener - A listener which will be informed of the progress of
     *                     the task.  May be <code>null</code> if the progress of the task does not
     *                     need to be monitored.
     * @return A <code>Collection</code> of <code>OWLClasses</code> that represent the
     *         inferred types for the specified individual.
     * @throws DIGReasonerException
     */
    public Collection getIndividualTypes(OWLIndividual individual, ReasonerTaskListener taskListener) throws DIGReasonerException;
}

