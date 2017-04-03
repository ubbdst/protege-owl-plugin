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

package edu.stanford.smi.protegex.owl.model;

import java.util.Collection;

/**
 * A named OWL class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLNamedClass extends RDFSNamedClass, OWLClass {


    /**
     * Adds a class to the list of disjoint classes of this.
     *
     * @param disjointClass the disjoint class to add
     */
    void addDisjointClass(RDFSClass disjointClass);


    /**
     * Adds a given class to the list of equivalent classes of this.
     * This will establish a bidirectional superclass relationship between the two classes.
     *
     * @param equivalentClass the RDFSClass to add as equivalent class
     */
    void addEquivalentClass(RDFSClass equivalentClass);


    /**
     * Adds a computed superclass, as the result of a classification.
     * This will automatically add the inverse direction, too.
     *
     * @param superclass the computed superclass to add
     */
    void addInferredSuperclass(RDFSClass superclass);


    /**
     * Creates a new individual of this (assuming this is not a metaclass).
     *
     * @param name the name of the new instance or null for a default value
     * @return the new instance
     */
    OWLIndividual createOWLIndividual(String name);


    /**
     * Gets the allowed class for a given property.  This is the filler of an allValuesFrom restriction,
     * or the range of the property (if no restriction has been defined).
     */
    RDFResource getAllValuesFrom(RDFProperty property);


    /**
     * Gets the classification status (whether this class is marked as inconsistent or not).
     *
     * @return one of the OWLNames.CLASSIFICATION_STATUS constants
     */
    int getClassificationStatus();


    /**
     * Gets the first equivalent class of this, or null if none is equivalent.
     *
     * @return the first equivalent class
     */
    RDFSClass getDefinition();


    /**
     * @see #getRestrictions
     * @deprecated will be deleted shortly, replaced with getRestrictions(false)
     */
    Collection getDirectRestrictions();


    /**
     * Gets the value of an arbitrary owl:hasValue restriction defined on this class.
     * Note that this will also check for restrictions defined on superclasses.
     *
     * @param property the property to look for hasValue restrictions on
     * @return the restriction value or null if none is defined.
     */
    Object getHasValue(RDFProperty property);


    /**
     * Gets a collection of values for all owl:hasValue restrictions defined on this class.
     * Note that this will also check for restrictions defined on superclasses.
     *
     * @param property the property to look for hasValue restrictions on
     * @return a collection of fillers for the hasValue restrictions on this class and the property given as argument.
     */
    Collection getHasValues(RDFProperty property);
    

    Collection getInferredEquivalentClasses();


    /**
     * Gets the subclasses that were computed by the most recent call of
     * a classifier.
     *
     * @return a Collection of RDFSClass objects
     */
    Collection getInferredSubclasses();


    /**
     * Gets the superclasses that were computed by the most recent call of
     * a classifier.
     *
     * @return a Collection of RDFSClass objects
     */
    Collection getInferredSuperclasses();


    /**
     * Gets the maximum number of values that are allowed for a given property at this class.
     * This method essentially returns the filler of any owl:maxCardinality or owl:cardinality restrictions
     * that are defined on it, either directly, or in its superclasses.  If the property is functional, then
     * this method always returns 1.  If neither functionality nor restrictions are found, the method delivers -1.
     *
     * @param property the property to get the maximum cardinality of
     * @return the maximum cardinality or -1 if there is no limit
     */
    int getMaxCardinality(RDFProperty property);


    /**
     * Gets the minimum number of values that are allowed for a given property at this class.
     * This method essentially returns the filler of any owl:minCardinality or owl:cardinality restrictions
     * that are defined on it, either directly, or in its superclasses.
     *
     * @param property the property to get the minimum cardinality of
     * @return the minimum cardinality or 0 if there is no limit
     */
    int getMinCardinality(RDFProperty property);


    /**
     * Same as <CODE>getRestrictions(false)</CODE>.
     *
     * @return a Collection of (direct) OWLRestrictions objects
     */
    Collection getRestrictions();


    /**
     * Gets all Restrictions that are defined on this class or (optionally) its
     * superclasses.  In case superclasses are also considered, the method filters
     * out those restrictions that have been "overloaded" further down.  For example,
     * if a subclass defines an owl:minCardinality restriction that narrows another
     * owl:minCardinality on the same property at a higher class, then only the first
     * restriction is being returned.
     *
     * @param includingSuperclassRestrictions
     *         true to also include restrictions from named superclasses
     * @return a Collection of OWLRestriction objects
     * @see #getDirectRestrictions
     */
    Collection getRestrictions(boolean includingSuperclassRestrictions);


    /**
     * Gets all restrictions that are defined on a given property.
     * This is similar to <CODE>getRestrictions(includingSuperclassRestrictions)</CODE> but with
     * only those restrictions that restrict the selected property.
     *
     * @param property the property to get the restrictions about
     * @param includingSuperclassRestrictions
     *                 true to also include restrictions from named superclasses
     * @return the restrictions on property
     */
    Collection getRestrictions(RDFProperty property, boolean includingSuperclassRestrictions);


    /**
     * Gets the filler of any defined someValuesFrom restriction for a given property.
     */
    RDFResource getSomeValuesFrom(RDFProperty property);


    /**
     * Checks whether all subclasses of this are declared to be disjoint.
     *
     * @return true  if the all children disjoint flag is set
     */
    boolean getSubclassesDisjoint();


    /**
     * Checks whether this has at least one named direct superclass.
     * This invariant should always be true in a consistent model.
     *
     * @return true iff there is at least one named direct superclass
     */
    boolean hasNamedSuperclass();


    /**
     * Determines whether this class has been flagged as being
     * inconsistent or not.
     *
     * @return <code>true</code> if the class is consistent, <code>false</code>
     *         if the class is inconsistent.
     */
    boolean isConsistent();


    /**
     * Checks if this is a "defined" class, with necessary and sufficient conditions.
     * This is equivalent to <CODE>getDefinition() != null</CODE>.
     *
     * @return true  if this has a definition
     */
    boolean isDefinedClass();


    /**
     * Checks if this has been marked as a probe class, using the
     * protege:probeClass annotation property.
     *
     * @return true if this is a probe class
     */
    boolean isProbeClass();


    /**
     * Removes a disjoint class of this.  If the disjoint class is anonymous, it is completely
     * deleted from the knowledge base.
     *
     * @param disjointClass the disjoint class to remove
     */
    void removeDisjointClass(RDFSClass disjointClass);


    /**
     * Removes an equivalent class.  If the equivalent class is anonymous, it is completely
     * removed from the knowledge base.  Otherwise, this method removes the bidirectional
     * superclass relationship between the two classes.<BR>
     * <p/>
     * Note that an alternative to using this method is to remove either class from the
     * superclasses of the other (in Protege, two OWL classes are treated as equivalent iff
     * they are direct superclasses of each other).
     *
     * @param equivalentClass
     */
    void removeEquivalentClass(RDFSClass equivalentClass);


    /**
     * Removes a computed superclass, as the result of a classification.
     * This will automatically remove the inverse direction, too.
     *
     * @param superclass the computed superclass to remove
     */
    void removeInferredSuperclass(RDFSClass superclass);


    /**
     * Sets the classification status of this.
     *
     * @param value one of OWLNames.CLASSIFICATION_STATUS_xxx
     */
    void setClassificationStatus(int value);


    /**
     * Removes all equivalent classes and then adds the given class (as definition).
     * If after the call no other named direct superclasses would remain, the method
     * adds owl:Thing to the superclasses.
     *
     * @param definingClass the new equivalent class
     */
    void setDefinition(RDFSClass definingClass);


    /**
     * Sets the value of the protege:subclassesDisjoint flag at this class.
     * Note that this operation is only permitted if the protege metadata ontology
     * has been imported, i.e. the protege:subclassesDisjoint property must exist.
     *
     * @param value the new value
     */
    void setSubclassesDisjoint(boolean value);
}
