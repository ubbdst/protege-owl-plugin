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

import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.model.visitor.Visitable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * The base interface for various OWL classes, slots and instances.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFResource extends ProtegeInstance, RDFObject, Visitable {

    /**
     * Adds an rdfs:comment to this resource.
     *
     * @param comment the comment to add
     * @see #getComments
     * @see #removeComment
     */
    void addComment(String comment);


    /**
     * Adds an owl:differentFrom to this resource.
     *
     * @param resource the resource to add
     * @see #getDifferentFrom
     * @see #removeDifferentFrom
     */
    void addDifferentFrom(RDFResource resource);


    /**
     * Adds an rdfs:isDefinedBy to this resource.
     *
     * @param resource the resource to add
     * @see #getIsDefinedBy
     * @see #removeIsDefinedBy
     */
    void addIsDefinedBy(RDFResource resource);


    /**
     * Adds a label to this.
     *
     * @param label    the text of the label to add
     * @param language the language, or null for no language
     */
    void addLabel(String label, String language);


    /**
     * Adds a value for a given property to this resource.
     * The property should have the type of this resource in its domain.
     *
     * @param property the property to add a value for
     * @param value    the value to add (may be an RDFSLiteral, a primitive value or another Resource)
     * @see #getPropertyValue
     * @see #getPropertyValues
     * @see #listPropertyValues
     * @see #removePropertyValue
     */
    void addPropertyValue(RDFProperty property, Object value);


    /**
     * Adds a PropertyValueListener to receive events when property values have changed.
     *
     * @param listener the listener to add
     * @see #removePropertyValueListener
     */
    void addPropertyValueListener(PropertyValueListener listener);


    /**
     * Adds an type to this resource.
     *
     * @param type the type to add
     * @see #getProtegeTypes
     * @see #removeProtegeType
     */
    void addProtegeType(RDFSClass type);


    /**
     * Adds an rdf:type to this resource.
     *
     * @param type the rdf:type to add
     * @see #getProtegeTypes
     * @see #removeProtegeType
     */
    void addRDFType(RDFSClass type);


    /**
     * Adds a ResourceListener to receive events when the type of this has changed.
     *
     * @param listener the listener to add
     * @see #removeResourceListener
     */
    void addResourceListener(ResourceListener listener);


    /**
     * Adds an owl:sameAs to this resource.
     *
     * @param resource the resource to add
     * @see #getSameAs
     * @see #removeSameAs
     */
    void addSameAs(RDFResource resource);


    /**
     * Adds an owl:versionInfo to this resource.
     *
     * @param versionInfo the version info to add
     * @see #getVersionInfo
     * @see #removeVersionInfo
     */
    void addVersionInfo(String versionInfo);


    /**
     * Converts this dynamically into an instance of a given Java interface.
     * The interface must inherit from RDFResource and must be accompanied by a
     * public class DefaultXY in the impl subpackage below the interface's package.
     * <B>Note: Work in Progress!<B>
     *
     * @param javaInterface the Java interface to convert to
     * @return an instance of the Java interface
     */
    RDFResource as(Class javaInterface);


    /**
     * Checks whether this can be dynamically morphed into an instance of a given
     * Java interface.  The interface must inherit from RDFResource and must be accompanied
     * by an implementation in the subpackage "impl" below the interface package.
     * <B>Note: Work in Progress!<B>
     *
     * @param javaInterface the Java interface to test
     * @return true  if this can be morphed into the given Java class
     */
    boolean canAs(Class javaInterface);


    /**
     * Deletes this resource from the model.  This will first remove all references of the
     * resource to other resources and then destroy the object.
     */
    void delete();


    /**
     * Checks for rdfs:range or owl:allValuesFrom restrictions on the types of this resource.
     * This method can be used to check the "valid" value types for a given property.
     * The implementation will check all rdf:types of this.
     *
     * @param property the property to check for restrictions
     * @return the range or null if no restriction was found
     */
    RDFResource getAllValuesFromOnTypes(RDFProperty property);


    /**
     * Gets a human-readable visual representation of this resource.
     * This is typically just the name of the resource, but for example, with anonymous class expressions
     * this will return the full expression including special symbols.
     *
     * @return the browser text
     */
    String getBrowserText();


    /**
     * Gets all rdfs:comments for this resource.
     *
     * @return a Collection of Strings or RDFSLiterals
     * @see #addComment
     * @see #removeComment
     */
    Collection getComments();


    /**
     * Gets all owl:differentFroms for this resource.
     *
     * @return a Collection of RDFResources
     * @see #addDifferentFrom
     * @see #removeDifferentFrom
     */
    Collection getDifferentFrom();


    /**
     * Checks if there are any owl:hasValue restrictions defined on any rdf:type of this
     * and returns the fillers of the restrictions (if found).
     *
     * @param property the property to look for restrictions
     * @return the fillers of all owl:hasValue restriction defined on the types of this
     */
    Collection getHasValuesOnTypes(RDFProperty property);


    /**
     * Gets a class that serves as a location of the icon resource.
     * This method is used in conjunction with <CODE>getIconName()</CODE>.
     * @return the icon location class (e.g., OWLIcons.class)
     * @see #getIconName
     */
    Class getIconLocation();


    /**
     * Gets the local name of an icon for this type of resource.
     * This method is used in conjunction with <CODE>getIconLocation()</CODE>.
     * @return an icon name (without suffix, e.g. "RDFIndividual" for RDFIndividual.gif)
     * @see #getIconLocation
     */
    String getIconName();


    /**
     * Gets the direct types that were inferred during the most recent execution of
     * a reasoner.
     *
     * @return the collection of inferred direct types (RDFSClasses)
     */
    Collection getInferredTypes();


    /**
     * Gets all rdfs:isDefinedBy for this resource.
     *
     * @return a Collection of RDFResources
     * @see #addIsDefinedBy
     * @see #removeIsDefinedBy
     */
    Collection getIsDefinedBy();


    /**
     * Gets the rdfs:labels that are currenly defined for this.
     *
     * @return a Collection of strings or RDFSLiterals
     */
    Collection getLabels();


    /**
     * Gets the local part of the full URI (name) of this resource.
     * For example, for "owl:Class" this would return "Class".
     *
     * @return the part of the name after #
     */
    String getLocalName();
    
    /**
     * Gets the qualified name as a namespace, local name pair (e.g. "owl:Class").
     * 
     * @return the qualified name of the resource.
     */

    String getPrefixedName();

    /**
     * Gets the full name for the resource.,
     *
     * @return the name
     */
    String getName();


    /**
     * Gets the namespace of this resource.  This is the URI up to before the #
     *
     * @return the namespace
     */
    String getNamespace();


    /**
     * Gets the prefix of the namespace, i.e. the abbreviated name of the
     * namespace.
     *
     * @return the prefix, e.g. "owl" or "protege"
     */
    String getNamespacePrefix();


    /**
     * Gets the OWLModel this resource is defined in.
     * The OWLModel is the top-level container that allows programmers to create, delete
     * and query all resources in the ontology.
     *
     * @return the OWLModel
     */
    OWLModel getOWLModel();


    /**
     * Gets a Collection of all RDFProperties that this resource could take values for.
     * This includes all properties with this in their domain, and their subproperties.
     * The result of this method is a superset of <CODE>getRDFProperties()</CODE> which
     * only returns those properties that also have a value at this resource.
     *
     * @return a Collection of RDFProperties
     * @see #getRDFProperties
     */
    Collection getPossibleRDFProperties();


    /**
     * Gets all (direct) values of a given property.
     * This is an abbreviation of <CODE>getPropertyValue(property, true)</CODE>.
     *
     * @param property the property to get the values for
     * @return the property values
     */
    Object getPropertyValue(RDFProperty property);


    RDFResource getPropertyValueAs(RDFProperty property, Class javaInterface);


    /**
     * Gets the first value of a given property as an RDFSLiteral.
     * The caller must make sure that the property only has datatype values
     * (e.g. if the property is an OWLDatatypeProperty).
     *
     * @param property the property to get the value from
     * @return the property value as RDFSLiteral
     */
    RDFSLiteral getPropertyValueLiteral(RDFProperty property);


    /**
     * Gets all values of a given property, and optionally its subproperties.
     *
     * @param property               the property to get the values for
     * @param includingSubproperties true to also return the subproperty values
     * @return the property values
     */
    Object getPropertyValue(RDFProperty property, boolean includingSubproperties);


    /**
     * Gets the number of all (direct) values of a given property.
     *
     * @param property the property
     * @return the number of direct values of property
     */
    int getPropertyValueCount(RDFProperty property);


    /**
     * Gets all (direct) values of a given property at this resource.
     * This call is an abbreviation of <CODE>getPropertyValues(property, false)</CODE>.
     *
     * @param property the property to get the values from
     * @return the property values
     * @see #listPropertyValues
     */
    Collection getPropertyValues(RDFProperty property);


    Collection getPropertyValuesAs(RDFProperty property, Class javaInterface);


    /**
     * Gets all (direct) values of a given property at this as RDFSLiterals.
     * This is often more convenient than calling <CODE>getPropertyValues</CODE>,
     * which may return values as primitive values (without the wrapping in an RDFSLiteral).
     * The caller must make sure that the property only holds datatype values
     * prior to the call.
     *
     * @param property the property to get the values from
     * @return the property values as RDFSLiterals
     */
    Collection getPropertyValueLiterals(RDFProperty property);


    /**
     * Gets all values of a given property, optionally including the values of
     * the subproperties of the property.
     *
     * @param property               the property to get the values from
     * @param includingSubproperties true to also return the subproperty values
     * @return the property values
     */
    Collection getPropertyValues(RDFProperty property, boolean includingSubproperties);


    /**
     * Gets the first asserted type of this resource.
     * This is basically a convenience method for those cases where it is known in advance
     * that the resource only has a single type.  If the resource has multiple types, then
     * the result of this method is undefined.
     *
     * @return the first type of this
     * @see #getProtegeTypes
     */
    RDFSClass getProtegeType();


    /**
     * Gets the asserted types of this resource.
     * Note that in the case of anonymous classes, this result is different from the sibling
     * method <CODE>getRDFTypes()</CODE>: Anonymous classes are internally stored using some special
     * Protege metaclasses (e.g. OWLNames.Cls.COMPLEMENT_CLASS instead of owl:Class).
     *
     * @return the types of this
     * @see #getProtegeType
     */
    Collection getProtegeTypes();


    /**
     * Gets all RDFProperties that have at least one value at this.
     *
     * @return a collection of RDFProperty objects
     */
    Collection getRDFProperties();


    /**
     * Gets the first asserted rdf:type of this resource.
     * This is basically a convenience method for those cases where it is known in advance
     * that the resource only has a single type.  If the resource has multiple types, then
     * the result of this method is undefined.
     *
     * @return the first type of this
     * @see #getRDFTypes
     */
    RDFSClass getRDFType();


    /**
     * Gets the asserted rdf:types of this resource.
     *
     * @return the rdf:types of this
     * @see #getRDFType
     */
    Collection getRDFTypes();


    /**
     * Gets a Collection of all OWLAnonymousClasses that have a reference to this.
     * This includes
     * <UL>
     * <LI>all OWLLogicalClasses that have this as Operand</LI>
     * <LI>all OWLRestrictions that have this as restricted property or filler</LI>
     * <LI>all OWLEnumeratedClasses that have this as owl:oneOf</LI>
     * </UL>
     *
     * @return Set of OWLAnonymousClasses
     */
    Set getReferringAnonymousClasses();


    /**
     * Gets the values of the owl:sameAs property.
     *
     * @return a Collection of RDFResources
     */
    Collection getSameAs();


    /**
     * Gets the full URI of this resource.  This is typically of the form
     * <CODE>http://www.mydomain.org/myontology#Name</CODE>
     *
     * @return the URI
     */
    String getURI();


    /**
     * Gets the values of the owl:versionInfo property.
     *
     * @return the version info values
     */
    Collection getVersionInfo();


    /**
     * Checks whether this has any (direct) property value.
     *
     * @param property the property
     * @return true  if this has any values for property
     */
    boolean hasPropertyValue(RDFProperty property);


    /**
     * Checks whether this has any property value.
     *
     * @param property               the property
     * @param includingSubproperties true to also include subproperties of property
     * @return true  if this has any values for property
     */
    boolean hasPropertyValue(RDFProperty property, boolean includingSubproperties);


    /**
     * Checks whether this has a certain (direct) property value.
     *
     * @param property the property
     * @param value    the value (possibly an RDFSLiteral)
     * @return true  if one of the values of the property equals value
     */
    boolean hasPropertyValue(RDFProperty property, Object value);


    /**
     * Checks whether this has a certain property value.
     * This is a shortcut for <CODE>getPropertyValues(property).contains(value);</CODE>.
     *
     * @param property               the property
     * @param value                  the value (possibly an RDFSLiteral)
     * @param includingSubproperties true to also include subproperties of property
     * @return true  if one of the values of the property equals value
     */
    boolean hasPropertyValue(RDFProperty property, Object value, boolean includingSubproperties);


    /**
     * Checks whether this resource has a given type.
     * This is equivalent to <CODE>hasProtegeType(type, false)</CODE>.
     *
     * @param type the type in question
     * @return true if one of the rdf:types of this is type
     * @see #addProtegeType
     * @see #getProtegeTypes
     * @see #removeProtegeType
     */
    boolean hasProtegeType(RDFSClass type);


    boolean hasProtegeType(RDFSClass type, boolean includingSuperclasses);


    /**
     * Checks whether this resource has a given rdf:type.
     * This is equivalent to <CODE>hasProtegeType(type, false)</CODE>.
     *
     * @param type the type in question
     * @return true if one of the rdf:types of this is type
     * @see #addProtegeType
     * @see #getProtegeTypes
     * @see #removeProtegeType
     */
    boolean hasRDFType(RDFSClass type);


    boolean hasRDFType(RDFSClass type, boolean includingSuperclasses);


    /**
     * Checks whether this represents an anonymous resource / bnode.
     * In Protege-OWL, anonymous resources can be recognized by their name,
     * using the method <CODE>OWLModel.isAnonymousResourceName()</CODE>.
     *
     * @return true  if this is an anonymous resource
     * @see OWLModel#isAnonymousResourceName
     */
    boolean isAnonymous();


    /**
     * A convenience method to access the corresponding method in OWLModel.
     *
     * @param property the property to check against
     * @param object   the potential property value
     * @return true if object is a valid value for property
     * @see OWLModel#isValidPropertyValue
     */
    boolean isValidPropertyValue(RDFProperty property, Object object);


    /**
     * Checks whether this resource has been declared to be invisible in the user interface.
     *
     * @return true  if this is visible (default)
     * @see #setVisible
     */
    boolean isVisible();


    /**
     * Gets all direct values of a certain property as an Iterator.
     *
     * @param property the property to get the values from
     * @return an Iterator on property values
     * @see #getPropertyValues
     */
    Iterator listPropertyValues(RDFProperty property);


    Iterator listPropertyValuesAs(RDFProperty property, Class javaInterface);


    /**
     * Gets all values of a certain property as an Iterator.
     *
     * @param property               the property to get the values from
     * @param includingSubproperties true to also return values of subproperties
     * @return an Iterator on property values
     * @see #getPropertyValues
     */
    Iterator listPropertyValues(RDFProperty property, boolean includingSubproperties);


    /**
     * Gets an Iterator on the rdf:types of this.
     *
     * @return an Iterator of the types
     */
    Iterator listRDFTypes();


    /**
     * Removes an rdfs:comment from this resource.
     *
     * @param value the comment to remove
     * @see #addComment
     * @see #getComments
     */
    void removeComment(String value);


    void removeDifferentFrom(RDFResource resource);


    void removeIsDefinedBy(RDFResource resource);


    /**
     * Removes a label that has been previously added.
     *
     * @param label    the text of the label to remove
     * @param language the language of the label or null
     */
    void removeLabel(String label, String language);


    void removePropertyValue(RDFProperty property, Object value);


    void removePropertyValueListener(PropertyValueListener listener);


    /**
     * Removes a type from this resource.
     * Note that the resource must have at least one type at any time.
     *
     * @param type the type to remove
     * @see #addProtegeType
     * @see #getProtegeTypes
     */
    void removeProtegeType(RDFSClass type);


    /**
     * Removes an rdf:type from this resource.
     * Note that the resource must have at least one rdf:type at any time.
     *
     * @param type the type to remove
     * @see #addRDFType
     * @see #getRDFTypes
     */
    void removeRDFType(RDFSClass type);


    void removeResourceListener(ResourceListener listener);


    void removeSameAs(RDFResource resource);


    void removeVersionInfo(String versionInfo);


    void setComment(String comment);


    void setComments(Collection comments);


    void setInferredTypes(Collection types);


    void setPropertyValue(RDFProperty property, Object value);


    void setPropertyValues(RDFProperty property, Collection values);


    void setProtegeType(RDFSClass type);


    void setProtegeTypes(Collection types);


    void setRDFType(RDFSClass type);


    void setRDFTypes(Collection types);


    /**
     * Sets this resource visible or invisible.
     *
     * @param b true to make this visible
     * @see #isVisible
     */
    void setVisible(boolean b);
}
