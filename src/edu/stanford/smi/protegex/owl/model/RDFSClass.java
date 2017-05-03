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

import edu.stanford.smi.protegex.owl.model.event.ClassListener;

import javax.swing.*;
import java.util.Collection;
import java.util.Set;

/**
 * The base interface of the RDFS/OWL classes used by the OWL Plugin.
 * All user-defined classes will be some instance of this interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFSClass extends ProtegeCls, RDFResource {


    /**
     * Adds a ClassListener to receive events about this RDFSClass.
     *
     * @param listener the ClassListener to add
     */
    void addClassListener(ClassListener listener);


    /**
     * Adds a class to the rdfs:subClassOfs of this.
     *
     * @param superclass
     */
    void addSuperclass(RDFSClass superclass);


    /**
     * Creates a copy of anonymous classes which contain a completely new expression tree.
     * Named classes are NOT copied.
     *
     * @return a clone of this or the object itself for named classes
     */
    RDFSClass createClone();


    /**
     * Creates a new instance of this class.
     *
     * @param name the name of the new instance or null for a default name
     * @return a new instance of this
     */
    RDFResource createInstance(String name);


    /**
     * Gets all OWLAnonymousClasses the life cycle of which depends on this.
     * These are deleted when this is deleted.
     * <p/>
     * From existing usage, it appears that only the direct anonymous
     * classes are required to be returned - these are then iterated over
     *
     * @return a Collection of OWLAnonymousClass instances
     */
    Collection getDependingClasses();


    /**
     * Gets a Collection of all directly equivalent Clses of this.
     * These are those direct superclasses that also have this as direct superclass.
     *
     * @return a Collection of Cls objects
     */
    Collection getEquivalentClasses();


    /**
     * Gets an ImageIcon displaying this.  This is a harder version of <CODE>getIcon()</CODE>
     * for use when an ImageIcon is required.
     *
     * @return the ImageIcon
     */
    ImageIcon getImageIcon();


    /**
     * Gets the number of direct inferred instances of this class.
     * This is equivalent to <CODE>getInferredInstances(false).size()</CODE> but
     * could be optimized internally for better performance.
     *
     * @return the number of inferred instances
     * @see #getInferredInstances
     */
    int getInferredInstanceCount();


    /**
     * Gets all resources that have this as their inferred type.
     * Optionally, it is possible to include subclasses of this into the search.
     *
     * @param includingSubclasses true to include instances of subclasses of this
     * @return the inferred instances of this
     */
    Collection getInferredInstances(boolean includingSubclasses);


    /**
     * Gets the number of instances of this, possibly including the instances of all subclasses.
     *
     * @param includingSubclasses true to also include the instances of the subclasses
     * @return the number of instances of this
     */
    int getInstanceCount(boolean includingSubclasses);


    /**
     * Gets the instances of this, possibly including the instances of all subclasses.
     *
     * @param includingSubclasses true to also get the instances of the subclasses
     * @return the instances of this
     */
    Collection getInstances(boolean includingSubclasses);


    /**
     * Gets a Collection of all direct subclasses that are not anonymous.
     * (This is a convenience method for getNamedSubclasses(false))
     *
     * @return a Collection of Cls objects
     */
    Collection getNamedSubclasses();


    /**
     * Gets the named subclasses of this class.
     *
     * @param transitive true to include the descendent classes
     *                   or false to only include the direct named subclasses
     * @return a Collection of RDFSNamedClass objects.
     */
    Collection getNamedSubclasses(boolean transitive);


    /**
     * Gets a Collection of all direct superclasses that are not anonymous.
     * (This is a convenience method for getNamedSuperclasses(false))
     *
     * @return a Collection of Cls objects
     */
    Collection getNamedSuperclasses();


    /**
     * Gets the superclasses of this class.
     *
     * @param transitive true to include the ancestor classes
     *                   or false to only include the direct named superclasses
     * @return A Collection of RDFSNamedClass objects.
     */
    Collection getNamedSuperclasses(boolean transitive);


    /**
     * Gets the browser text when this is embedded into a complex expression.
     * This usually returns the browser key enclosed with round brackets.
     *
     * @return the nested browser text
     */
    String getNestedBrowserText();


    /**
     * Gets all RDFSClses that are somewhere used in the expression below this.
     * If this is an anonymous class, this traverses the expression tree to
     * collect them.  If this is already an RDFSNamedClass, it will return itself.
     *
     * @param set an (initially empty) Set that will contain the nested named classes
     */
    void getNestedNamedClasses(Set set);


    /**
     * @deprecated not needed anymore
     */
    String getParsableExpression();


    /**
     * Gets the subset of those direct superclasses which are not at the same
     * time equivalent classes (or operands of equivalent intersection classes).
     * Note that equivalent classes are superclasses of each other, so that in
     * many cases you may want to use this method instead of
     * <CODE>getSuperclasses(false)</CODE>.
     *
     * @return the direct superclasses
     */
    Collection getPureSuperclasses();


    /**
     * Gets the number of (direct) subclasses of this.
     *
     * @return the number of classes with this as a rdfs:subClassOf value
     */
    int getSubclassCount();


    /**
     * Gets the subclasses of this, including the subclasses of the subclasses etc.
     *
     * @return the subclass values
     */
    Collection getSubclasses(boolean transitive);


    /**
     * Gets the number of (direct) superclasses of this.
     *
     * @return the number of rdfs:subClassOf values
     */
    int getSuperclassCount();


    /**
     * Gets the superclasses of this, optionally including the superclasses of the superclasses etc.
     *
     * @return the rdfs:subClassOf values
     */
    Collection getSuperclasses(boolean transitive);


    /**
     * Equivalent to <CODE>getUnionDomainProperties(false)</CODE>.
     *
     * @return the properties that have this in their (direct) domain
     */
    Collection getUnionDomainProperties();


    /**
     * Gets those properties that have this in their domain, with union semantics.
     * If the argument is true, then this will also consider those properties that have
     * superclasses of this in their domain.
     *
     * @param transitive true to include superclasses
     * @return a Collection of RDFProperty objects
     */
    Collection getUnionDomainProperties(boolean transitive);


    /**
     * Checks whether this is a (direct) equivalent class of a given other Cls.
     *
     * @param other the Cls to compare with
     * @return true if other is among the direct equivalent classes
     */
    boolean hasEquivalentClass(RDFSClass other);


    /**
     * For an object-valued property, checks whether one of the property values has a
     * given browser text.
     *
     * @param property    the property (must not have datatype literals as values)
     * @param browserText the browser text to compare to
     * @return true  if one of the values of the property matches
     */
    boolean hasPropertyValueWithBrowserText(RDFProperty property, String browserText);


    /**
     * Checks whether this is an anonymous class.  All classes except for instances
     * of OWLNamedClass are anonymous.
     *
     * @return true  if this is anonymous
     */
    boolean isAnonymous();


    /**
     * Checks whether this is a metaclass, i.e. whether it is a subclass of rdfs:Class.
     *
     * @return true  if this is a metaclass
     */
    boolean isMetaclass();


    /**
     * Checks whether this is a (direct) subclass of a given class.
     *
     * @param superclass the superclass in question
     * @return true  if this has superclass among its rdfs:subClassOf values
     */
    boolean isSubclassOf(RDFSClass superclass);


    /**
     * Removes a ClassListener to receive events about this RDFSClass.
     *
     * @param listener the ClassListener to remove
     */
    void removeClassListener(ClassListener listener);


    /**
     * Removes a given class from the superclasses of this.
     *
     * @param superclass the superclass to remove
     */
    void removeSuperclass(RDFSClass superclass);
}
