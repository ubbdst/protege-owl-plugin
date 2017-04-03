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

import edu.stanford.smi.protegex.owl.model.event.PropertyListener;

import javax.swing.*;
import java.util.Collection;

/**
 * An RDFResource representing an rdf:Property or an instance of a subclass of rdf:Property
 * such as owl:ObjectProperty and owl:FunctionalProperty.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFProperty extends ProtegeSlot, RDFResource, Deprecatable {


    /**
     * Adds a property to the list of equivalent properties of this.
     * This will add an owl:equivalentProperty statement.
     *
     * @param property the property to declare to be equivalent to this
     */
    void addEquivalentProperty(RDFProperty property);


    /**
     * Adds a PropertyListener to receive events on changes in the property.
     * Note that for the sake of backward compatibility this currenty needs
     * to take a PropertyAdapter as argument, but this may later change to
     * take a real PropertyListener.
     *
     * @param listener the PropertyListener to add (must be a PropertyAdapter)
     */
    void addPropertyListener(PropertyListener listener);


    /**
     * Adds a property to the super properties of this.
     * This will add an rdfs:subPropertyOf statement.
     *
     * @param superProperty the new super property
     */
    void addSuperproperty(RDFProperty superProperty);


    /**
     * Adss a class to the domain of this, applying union semantics.
     * This means that if there is already a class in the domain, then
     * the union of the existing class and the new class is taken as domain.
     * If the first entry of the current domain is a union class, then the new
     * class is added to the union.
     *
     * @param domainClass the new domain class
     */
    void addUnionDomainClass(RDFSClass domainClass);


    /**
     * Gets the first value of the rdfs:domain property.
     * Note that in most cases, this is only one value (possibly a union class).
     *
     * @param includingSuperproperties true to also consider superproperties
     * @return the first value of rdfs:domain or null
     */
    RDFSClass getDomain(boolean includingSuperproperties);


    /**
     * Gets the values of the rdfs:domain property.
     * Note that in most cases, this is only one value (possibly a union class).
     * If multiple domains are returned, these domains are to be interpreted in the
     * owl sense.  That is the intersection (not the union) of the returned classes is a 
     * domain of this  property.  Therefore each of the returned values is also a domain
     * of this property.
     *
     * @param includingSuperproperties true to also consider superproperties
     * @return the values of rdfs:domain
     */
    Collection getDomains(boolean includingSuperproperties);


    /**
     * Gets the equivalent slots of this.
     *
     * @return a Collection of OWLProperty objects
     */
    Collection getEquivalentProperties();


    /**
     * Gets the first superproperty of this (if any exists).
     *
     * @return the first superproperty or null
     */
    RDFProperty getFirstSuperproperty();


    Icon getInheritedIcon();


    /**
     * Gets the inverse property of this.
     *
     * @return the inverse property or null if none is defined
     */
    RDFProperty getInverseProperty();


    /**
     * Equivalent to <CODE>getRange(false)</CODE>.
     *
     * @return the (direct) range of this
     */
    RDFResource getRange();


    /**
     * Gets the range defined at this.  This will return the first of the direct
     * values of the rdfs:range property defined at this property, regardless of
     * other values (which would be interpreted as an intersection.
     *
     * @param includingSuperproperties true to also consider superproperties
     * @return the range of this
     * @see #setRange
     * @see #hasDatatypeRange
     * @see #hasObjectRange
     */
    RDFResource getRange(boolean includingSuperproperties);


    /**
     * Gets the direct or inherited RDFSDatatype of this.
     * If the range of this is an RDFSDatatype, this will return that type.
     * If the range of this is an OWLDataRange, this will return the type of
     * the first element in the datarange.  In all other cases, this will
     * return null.  If there is no local range defined at this property,
     * the method will recurse into superproperties.
     *
     * @return the RDFSDatatype
     */
    RDFSDatatype getRangeDatatype();


    /**
     * Gets all range definitions at this (or optionally its superproperties).
     *
     * @param includingSuperproperties true to also consider superproperties
     * @return the ranges of this
     */
    Collection getRanges(boolean includingSuperproperties);


    /**
     * Gets the subproperties of this, and possibly the subproperties of the subproperties etc.
     *
     * @param transitive true to get subproperties recursively
     * @return a collection of RDFProperty
     */
    Collection getSubproperties(boolean transitive);


    /**
     * Gets the number of properties that have this as rdfs:subPropertyOf.
     *
     * @return the number of (direct) subproperties
     */
    int getSubpropertyCount();


    /**
     * Gets the superproperties of this, and possibly the superproperties of the superproperties etc.
     *
     * @param transitive true to get superproperties recursively
     * @return a collection of RDFProperty
     */
    Collection getSuperproperties(boolean transitive);


    /**
     * Gets the number of values of the rdfs:subPropertyOf properties.
     *
     * @return the number of (direct) superproperties
     */
    int getSuperpropertyCount();


    /**
     * Equivalent to <CODE>getUnionDomain(false)</CODE>.
     *
     * @return the direct (union) domain
     */
    Collection getUnionDomain();


    /**
     * Gets the domain of this, with union semantics.
     * This means, that if the first entry of the domain is a union class,
     * then the operands of the union will be returned.
     * If the argument is true, then the method will recurse into the superproperties
     * if the local domain of this property is not defined, i.e. the domain is only inherited.
     *
     * @param includingSuperproperties true to walk up the superproperty tree
     * @return the union domain of this
     */
    Collection getUnionDomain(boolean includingSuperproperties);


    /**
     * Gets the range of this with union semantics.
     * This means that if the first entry of the range is a union class, then
     * the operands of the union will be returned.
     *
     * @return a Collection of RDFSClass
     */
    Collection getUnionRangeClasses();


    /**
     * Checks whether this can take only primitive, datatype values.
     * This is true if the range of this (or its superproperties) only consists
     * of RDFSDatatype or an OWLDataRange, or if this is an owl:DatatypeProperty.
     */
    boolean hasDatatypeRange();


    /**
     * Checks whether this can take only object/reference values.
     * This is true if the range of this (or its superproperties) only consists
     * of RDFSClasses (or if this is an owl:ObjectProperty).
     */
    boolean hasObjectRange();


    /**
     * Checks whether this has any concrete range definition.  Optionally this can be
     * extended to its superproperties.
     *
     * @param includingSuperproperties true to also look at the range of the superproperties
     * @return true  if this has an rdfs:range statement (or its superproperties)
     */
    boolean hasRange(boolean includingSuperproperties);


    /**
     * Checks whether this Slot is an OWL AnnotationProperty.
     * Annotation properties are used to assign arbitrary metadata (mostly strings) to
     * resources.  In contrast to normal properties, they are ignored by most algorithms.
     *
     * @return true if this is an annotation property
     */
    boolean isAnnotationProperty();
    
    /**
     * Checks whether this property is a real annotation property.  This means
     * that it is an annotation property that is not declaraed as a Datatype Property or 
     * an Object Property.
     */
    boolean isPureAnnotationProperty();


    /**
     * Checks whether the domain of this property has been explicitly defined.
     * This only checks for the direct domain, i.e. it does not check for
     * the domain definitions in possible superproperties.
     * This is comparable to <CODE>isDomainDefined(false)</CODE>.
     *
     * @return true  if the (direct) domain is defined
     */
    boolean isDomainDefined();


    /**
     * Checks whether the domain of this property has been explicitly defined.
     * A top-level property does not have a direct domain defined, iff its domain
     * contains owl:Thing.  A subproperty does not have a direct domain defined, if
     * it's value of the rdfs:domain property is null.
     *
     * @param transitive true to also consider the superproperties recursively
     * @return true  if the domain is defined
     */
    boolean isDomainDefined(boolean transitive);


    /**
     * Checks whether this is a functional property.  A property is functional if
     * it either has the rdf:type owl:FunctionalProperty or one of its superproperties
     * is functional.
     *
     * @return true  if this is functional
     */
    boolean isFunctional();


    /**
     * Checks whether this property defines its own range.  This is true for all top-level properties.
     * Subproperties have no range defined if they don't overload the inherited range from the
     * superproperties.
     *
     * @return true  if the range of this has been defined
     */
    boolean isRangeDefined();


    /**
     * Checks if the values of this slot should be treated as read-only.
     * This is typically true when the protege:readOnly annotation property
     * has the value true on this property.
     *
     * @return true  if this is read only
     */
    boolean isReadOnly();


    /**
     * Checks if this is a subproperty of a given superproperty.
     *
     * @param superproperty the potential superproperty
     * @param transitive    true to traverse the superproperty hierarchy recursively
     * @return true  if this is a subproperty of superproperty
     */
    boolean isSubpropertyOf(RDFProperty superproperty, boolean transitive);


    /**
     * Removes a property from the list of equivalent properties.
     *
     * @param property the equivalent property to remove
     */
    void removeEquivalentProperty(OWLProperty property);


    /**
     * Removes a PropertyListener to receive events on changes in the property.
     * Note that for the sake of backward compatibility this currenty needs
     * to take a PropertyAdapter as argument, but this may later change to
     * take a real PropertyListener.
     *
     * @param listener the PropertyListener to remove (must be a PropertyAdapter)
     */
    void removePropertyListener(PropertyListener listener);


    /**
     * Removes a superproperty.
     *
     * @param superproperty the property to remove from the superproperties
     */
    void removeSuperproperty(RDFProperty superproperty);


    /**
     * Removes a domain class, with union semantics.
     *
     * @param domainClass the domain class to remove from the domain
     */
    void removeUnionDomainClass(RDFSClass domainClass);


    /**
     * Sets the value of rdfs:domain to the given class.
     *
     * @param domainClass the new domain
     */
    void setDomain(RDFSClass domainClass);


    /**
     * Sets the values of the rdfs:domain property at this.
     *
     * @param domainClasses the new domains (RDFSClasses)
     */
    void setDomains(Collection domainClasses);


    /**
     * Specifies whether the domain of this property is defined or not.  This
     * will first empty the domain of this.  Then, if the new value is false,
     * it either adds owl:Thing to the domain (if this does not have superproperties),
     * or sets the domain to null (if this has superproperties).
     */
    void setDomainDefined(boolean value);


    /**
     * Sets the equivalent properties of this.
     *
     * @param properties a Collection of RDFProperty objects
     */
    void setEquivalentProperties(Collection properties);


    /**
     * Adds or removes owl:FunctionalProperty to/from the rdf:types of this.
     *
     * @param value true to make this a functional property, false to make this unfunctional
     */
    void setFunctional(boolean value);


    /**
     * Sets the inverse property of this.
     *
     * @param inverseProperty the new inverse property or null to clear the value
     */
    void setInverseProperty(RDFProperty inverseProperty);


    /**
     * Sets the rdfs:range of this.  The range of a property defines the types of
     * valid values that it can take anywhere.  The range can consist of RDFSClasses,
     * RDFDatatypes or RDFDataRanges.  If this is supposed to hold datatype values, then
     * a typical range would be <CODE>OWLModel.getXSDint</CODE>.  If this is supposed
     * to hold instances of multiple classes, you can pass in a union class.
     * Note that this call will completely replace any previous ranges of this.
     *
     * @param range the new range of this property or null to delete the range
     */
    void setRange(RDFResource range);


    /**
     * Sets all ranges of this (in the unlikely case that multiple are needed).
     *
     * @param ranges a Collection of RDFResources (possibly empty)
     * @see #setRange
     */
    void setRanges(Collection ranges);


    void setUnionRangeClasses(Collection classes);
}
