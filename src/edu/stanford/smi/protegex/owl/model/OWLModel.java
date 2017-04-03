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

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protegex.owl.jena.writersettings.WriterSettings;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStoreManager;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.validator.PropertyValueValidator;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.swrl.SWRLSystemFrames;
import edu.stanford.smi.protegex.owl.testing.OWLTestManager;

/**
 * A KnowledgeBase with a number of convenience methods to handle anonymous classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLModel extends ProtegeKnowledgeBase, OWLTestManager {


    /**
     * Adds a ClassListener to receive events about any class in this OWLModel.
     *
     * @param listener the ClassListener to add (currently must be a ClassAdapter)
     * @see #removeClassListener
     */
    void addClassListener(ClassListener listener);

    /**
     * Adds a ModelListener to receive notifications when resources have been created, renamed
     * or deleted.
     *
     * @param listener the ModelListener to add (currently must be a ModelAdapter)
     * @see #removeModelListener
     */
    void addModelListener(ModelListener listener);


    /**
     * Adds a PropertyListener to receive events about any property in this OWLModel.
     *
     * @param listener the PropertyListener to add (currently must be a PropertyAdapter)
     * @see #removePropertyListener
     */
    void addPropertyListener(PropertyListener listener);


    /**
     * Adds a PropertyValueListener to receive events about any property value change in this
     * OWLModel.  Note that the number of events is most likely very large and installing such
     * a global listener may slow down the system significantly.
     *
     * @param listener the PropertyValueListener to add
     * @see #removePropertyValueListener
     */
    void addPropertyValueListener(PropertyValueListener listener);


    /**
     * Gets an RDFSLiteral for a given value.  If the value is already an RDFSLiteral, then
     * the method will return it.  If the value is a String, Float, Integer or Boolean, then
     * the method will wrap it into a corresponding RDFSLiteral.  If the object is null, then
     * the method return null.  Objects of other types are not permitted for this method.
     * This is a convenience method to post-process those calls that return either an Object
     * or an RDFSLiteral, if only an RDFSLiteral is desired.
     *
     * @param value a primitive value or an RDFSLiteral
     * @return an RDFSLiteral of the same value or null if value is null
     */
    RDFSLiteral asRDFSLiteral(Object value);


    /**
     * Gets an <code>RDFObject</code> for a given value.  If the value is already an RDFObject
     * then the method will simply return it. If the value is already an RDFSLiteral,
     * then the method will return it.  If the value is a String, Float, Integer or Boolean, then
     * the method will wrap it up into a corresponding RDFSLiteral which will be returned as an
     * RDFObject.  If the object is an RDFResourece then the method will return it.  If the object
     * is <code>null</code> the the method will return <code>null</code>.
     *
     * @param object the object to convert
     * @return an RDFObject with the same value
     */
    RDFObject asRDFObject(Object object);


    List asRDFSLiterals(Collection values);


    /**
     * Adds a ResourceListener to receive events about any RDFResource in this OWLModel.
     *
     * @param listener the ResourceListener to add (currently must be ResourceAdapter)
     * @see #removeResourceListener
     */
    void addResourceListener(ResourceListener listener);
    
    
    /**
     * Creates a simple owl:AnnotationProperty.
     * @param name - the name of the annotation property. If name is null, a new name will be generated 
     * @return the owl:AnnotationProperty
     */
    RDFProperty createAnnotationProperty(String name);
  
    OWLDatatypeProperty createAnnotationOWLDatatypeProperty(String name);

    OWLObjectProperty createAnnotationOWLObjectProperty(String name);


    /**
     * Creates a unique name from a given local name prefix.  For example, if you pass in
     * "Class", then the method will return "Class_42" or "travel:Class_42" depending on
     * the default namespace of the currently active TripleStore.
     * This method can be used to create new names for classes, properties etc.
     *
     * @param partialLocalName the partial name
     * @return a unique name for a new resource
     */
    String createNewResourceName(String partialLocalName);

    /**
     * Generates a unique URI for a Frame given a base URI. For example, if you pass in
     * "http://a.com/MyOntology#Class" the method will generate the unique URI
     * "http://a.com/MyOntology#Class_42" or the like.
     * 
     * @param baseURI a partial URI
     * @return a unique URI for a new resource
     */
    String getUniqueFrameName(String baseURI);

    OWLAllDifferent createOWLAllDifferent();


    OWLAllValuesFrom createOWLAllValuesFrom();


    OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFResource filler);


    OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues);


    OWLCardinality createOWLCardinality();


    OWLCardinality createOWLCardinality(RDFProperty property, int value);


    OWLCardinality createOWLCardinality(RDFProperty property, int value, RDFSClass qualifier);


    OWLComplementClass createOWLComplementClass();


    OWLComplementClass createOWLComplementClass(RDFSClass complement);


    /**
     * Creates an empty owl:DataRange.
     *
     * @return a new empty owl:DataRange
     */
    OWLDataRange createOWLDataRange();


    /**
     * Creates a new owl:DataRange consisting of a given set of values.
     * This will create an rdf:List to store the values on the fly
     *
     * @param values the values in the owl:oneOf list in the owl:DataRange
     * @return a new OWLDataRange
     */
    OWLDataRange createOWLDataRange(RDFSLiteral[] values);


    /**
     * Creates a new OWLDatatypeProperty (with default settings, e.g. no range).
     *
     * @param name the name of the new property
     * @return a new OWLDatatypeProperty
     */
    OWLDatatypeProperty createOWLDatatypeProperty(String name);


    /**
     * Creates a new OWLDatatypeProperty with a given rdf:type as metaclass.
     *
     * @param name      the name of the new property
     * @param metaclass the rdf:type of the property
     * @return a new OWLDatatypeProperty, instance of the metaclass
     */
    OWLDatatypeProperty createOWLDatatypeProperty(String name, OWLNamedClass metaclass);


    /**
     * Creates a new OWLDatatypeProperty with a given RDFSDatatype as range.
     *
     * @param name     the name of the new property
     * @param datatype the (initial) range (e.g. <CODE>getXSDint()</CODE>
     * @return a new OWLDatatypeProperty
     */
    OWLDatatypeProperty createOWLDatatypeProperty(String name, RDFSDatatype datatype);


    OWLDatatypeProperty createOWLDatatypeProperty(String name, RDFSLiteral[] dataRangeLiterals);


    OWLEnumeratedClass createOWLEnumeratedClass();


    OWLEnumeratedClass createOWLEnumeratedClass(Collection instances);


    OWLHasValue createOWLHasValue();


    /**
     * Creates a new OWLHasValue for a given Slot.
     *
     * @param property the restricted Slot
     * @param value    an Instance, Boolean, Integer, Double, or String
     * @return a new OWLHasValue
     */
    OWLHasValue createOWLHasValue(RDFProperty property, Object value);


    OWLIntersectionClass createOWLIntersectionClass();


    OWLIntersectionClass createOWLIntersectionClass(Collection clses);


    OWLMaxCardinality createOWLMaxCardinality();


    OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value);


    OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value, RDFSClass qualifier);


    OWLMinCardinality createOWLMinCardinality();


    OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value);


    OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value, RDFSClass qualifier);


    OWLNamedClass createOWLNamedClass(String name);


    OWLNamedClass createOWLNamedClass(String name, boolean loadDefaults);


    /**
     * Creates a new named OWL class with a given metaclass, which must be a subclass of
     * owl:Class.
     *
     * @param name      the name of the new class
     * @param metaclass the metaclass (subclass of owl:Class)
     * @return the new OWLNamedClass
     */
    OWLNamedClass createOWLNamedClass(String name, OWLNamedClass metaclass);


    OWLNamedClass createOWLNamedSubclass(String name, OWLNamedClass superclass);


    /**
     * Creates a new OWLObjectProperty.
     *
     * @param name the name of the new property
     * @return a new owl:ObjectProperty with the given name
     */
    OWLObjectProperty createOWLObjectProperty(String name);


    OWLObjectProperty createOWLObjectProperty(String name, OWLNamedClass metaclass);


    OWLObjectProperty createOWLObjectProperty(String name, Collection allowedClasses);


    /**
     * Creates a new OWLOntology for a given prefix.  Note that the prefix of the new ontology
     * must have been defined before.  The new OWLOntology will get the internal name <CODE>prefix + ":"</CODE>.
     *
     * @param prefix a valid namespace prefix
     * @return the new OWLOntology
     * @deprecated developers should not need to create an owl ontology.  There is a rename method in OWLUtils.
     */
    @Deprecated
    OWLOntology createOWLOntology(String prefix) throws AlreadyImportedException;


    /**
     * @see #createOWLOntology(String)
     * @deprecated use the other createOWLOntology method instead
     */
    @Deprecated
    OWLOntology createOWLOntology(String name, String uri) throws AlreadyImportedException;


    OWLSomeValuesFrom createOWLSomeValuesFrom();


    OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues);


    OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFResource filler);


    OWLUnionClass createOWLUnionClass();


    OWLUnionClass createOWLUnionClass(Collection operandClasses);


    /**
     * @deprecated use createRDFUntypedResource instead
     */
    @Deprecated
    RDFExternalResource createRDFExternalResource(String uri);


    /**
     * Creates a new empty rdf:List
     *
     * @return a new RDFList
     */
    RDFList createRDFList();


    /**
     * Creates a new rdf:List.
     *
     * @param values an Iterator on the initial values of this list.
     * @return a new RDFList.
     */
    RDFList createRDFList(Iterator values);


    /**
     * Gets or creates an RDFSClass based on a parsable expression.  If the expression is parsed into an
     * anonymous class, then the anonymous class will be created.  If the expression is just the name of a
     * named class, then the named class will be returned.  Note that the expression must be parsable or an
     * null will be returned.
     *
     * @param parsableExpression a parsable expression such as !(Person | Animal)
     * @return an RDFSClass
     */
    RDFSClass createRDFSClassFromExpression(String parsableExpression);


    RDFSDatatype createRDFSDatatype(String name);


    /**
     * Creates a new RDFSLiteral with a default datatype derived from the value.
     *
     * @param value the value (not null)
     * @return a new RDFSLiteral
     */
    RDFSLiteral createRDFSLiteral(Object value);


    /**
     * Creates a new RDFSLiteral with a given datatype.
     *
     * @param lexicalValue the value in the literal (not null)
     * @param datatype     the RDFSDatatype of the value
     * @return a new RDFSLiteral
     */
    RDFSLiteral createRDFSLiteral(String lexicalValue, RDFSDatatype datatype);


    /**
     * Creates a new string-typed literal with a given language.
     *
     * @param value    the value in the literal (not null)
     * @param language the language tag of the value (possibly null)
     * @return a new RDFSLiteral
     */
    RDFSLiteral createRDFSLiteral(String value, String language);


    /**
     * Creates a new string-typed literal with a given language.
     * If the language is null or empty, then this returns the value as a string only.
     *
     * @param value    the value in the literal (not null)
     * @param language the language tag of the value (possibly null)
     * @return a new RDFSLiteral
     */
    Object createRDFSLiteralOrString(String value, String language);


    /**
     * Creates a new named OWL class which has exactly a given superclass.
     * This is a convenience method which removes the need to remove the root class.
     *
     * @param name       the name of the new class
     * @param superclass the superclass it shall have
     * @return the new OWLNamedClass
     */
    RDFSNamedClass createRDFSNamedSubclass(String name, RDFSNamedClass superclass);


    /**
     * Creates an untyped rdf:Resource for a given URI.
     * The caller must make sure that no resource with the same URI already exists.
     *
     * @param uri the URI to create an untyped resource for
     * @return the new RDFUntypedResource
     */
    RDFUntypedResource createRDFUntypedResource(String uri);


    /**
     * Creates a subclass of a superclass.
     * The new class will have the same rdf:type as its parent.
     *
     * @param name       the name of the new subclass or null for a default name
     * @param superclass the superclass
     * @return the new subclass
     */
    RDFSNamedClass createSubclass(String name, RDFSNamedClass superclass);


    /**
     * Creates a subclass of a number of superclasses.
     * The new class will have the same rdf:type as its first parent
     *
     * @param name         the name of the new subclass or null for a default name
     * @param superclasses a Collection of RDFSClasses
     * @return the new subclass
     */
    RDFSNamedClass createSubclass(String name, Collection superclasses);


    /**
     * Creates a new property as a subproperty of an existing property.
     * The new property will get the same type as the superproperty.
     *
     * @param name          the name of the new subproperty, or null for a default name
     * @param superproperty the superproperty
     * @return a new (sub-) property
     */
    RDFProperty createSubproperty(String name, RDFProperty superproperty);


    RDFSNamedClass createRDFSNamedClass(String name);


    RDFSNamedClass createRDFSNamedClass(String name, boolean loadDefaults);


    /**
     * Creates an RDFSNamedClass (or perhaps an OWLNamedClass) with given name, superclasses and rdf:type.
     *
     * @param name    the name of the new class
     * @param parents the parents
     * @param rdfType the rdf:type of the new class
     * @return the new class
     */
    RDFSNamedClass createRDFSNamedClass(String name, Collection parents, RDFSClass rdfType);


    RDFProperty createRDFProperty(String name);


    /**
     * Creates a new Triple.  This method should be used instead of creating an instance
     * of DefaultTriple directly.  The model is not changed at all by this.
     *
     * @param subject   the subject of the Triple
     * @param predicate the predicate of the Triple
     * @param object    the object of the Triple
     * @return a new Triple
     */
    Triple createTriple(RDFResource subject, RDFProperty predicate, Object object);


   


    /**
     * Executes a SPARQL query on this OWLModel.
     * The method will internally add prefix declarations according to the current
     * namespace declarations.
     * The resulting QueryResults object can be used to iterate over the results.
     *
     * @param partialQueryText the query text, typically beginning with "SELECT"
     * @return a QueryResults object
     * @throws Exception if the query string could not be handled
     */
    QueryResults executeSPARQLQuery(String partialQueryText) throws Exception;


    /**
     * Gets all (transitive) imports in this OWLModel.
     *
     * @return a Set of String URIs of the import statements
     */
    Set<String> getAllImports();


    /**
     * @deprecated internal Protege detail
     */
    @Deprecated
    Cls getAnonymousRootCls();


    /**
     * Gets all named classes where the classification status is
     * OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED, i.e. all
     * classes that were identified to move by the most recent invokation
     * of a classifier.
     *
     * @return a Collection of OWLNamedClass instances
     */
    Collection getChangedInferredClasses();

    
    /**
     * Gets the most specific common named superclasses of a given collection of named classes.
     *
     * @param classes the RDFSNamedClasses to get the superclass of (at least one)
     * @return the most specific common superclass of all classes, e.g. owl:Thing
     */
    Set<RDFSNamedClass> getCommonSuperclasses(Collection<RDFSNamedClass> classes);

    /**
     * Chooses a most specific common named superclass of a given collection of named classes.
     *
     * @param classes the RDFSNamedClasses to get the superclass of (at least one)
     * @return the most specific common superclass of all classes, e.g. owl:Thing
     */
    RDFSNamedClass getCommonSuperclass(Collection<RDFSNamedClass> classes);


    /**
     * Gets the default language specified by the user through an annotation
     * property at owl:Ontology.
     *
     * @return the default language or null for none
     */
    String getDefaultLanguage();


    OWLOntology getDefaultOWLOntology();


    /**
     * @see #setDispatchEventsEnabled
     */
    boolean getDispatchEventsEnabled();


    /**
     * Gets all properties that have owl:Thing as their domain.
     * This includes all properties that have no domain statement at all.
     *
     * @return a Collection of RDFProperties
     */
    Collection getDomainlessProperties();


    /**
     * Gets all float XML Schema datatypes, including xsd:float etc.
     * For these datatypes, the method RDFSLiteral.getFloat() is valid.
     *
     * @return the float types
     */
    Set getFloatDatatypes();

    
    OWLFrameStoreManager getFrameStoreManager();

    /**
     * @see #setGenerateEventsEnabled
     */
    boolean getGenerateEventsEnabled();


    /**
     * Gets all integer XML Schema datatypes, including xsd:int etc.
     * For these datatypes, the method RDFSLiteral.getInt() is valid.
     *
     * @return the integer types
     */
    Set getIntegerDatatypes();


    /**
     * Gets a (read-only) Jena Model serving as a live view upon the Protege TripleStores.
     *
     * @return a Jena Model
     */
    com.hp.hpl.jena.rdf.model.Model getJenaModel();


    com.hp.hpl.jena.ontology.OntModel getOntModel();
    
    RDFProperty getOWLDifferentFromProperty();


    RDFProperty getOWLDisjointWithProperty();


    /**
     * Gets all classes which have been marked as inconsistent.
     *
     * @return a Collection of NamedClses
     */
    Collection getInconsistentClasses();


    RDFSNamedClass getOWLAllDifferentClass();


    /**
     * Gets a collection of all OWLAllDifferents in the knowlegde base.
     *
     * @return a collection of 'em
     */
    Collection getOWLAllDifferents();


    /**
     * Gets all annotation properties.
     *
     * @return the annotation properties
     */
    Collection<RDFProperty> getOWLAnnotationProperties();


    RDFSNamedClass getOWLAnnotationPropertyClass();


    /**
     * Gets the OWLClassParser for the current class expression syntax.
     *
     * @return the currently used parser
     */
    OWLClassParser getOWLClassParser();


    /**
     * Gets the currently used renderer for classes into display strings.
     *
     * @return the OWLClassDisplay
     * @see #setOWLClassDisplay
     */
    OWLClassDisplay getOWLClassDisplay();


    OWLDatatypeProperty getOWLDatatypeProperty(String name);


    /**
     * Gets the standard metaclass of all datatype slots/properties.
     *
     * @return the metaclass
     */
    OWLNamedClass getOWLDatatypePropertyClass();


    RDFSNamedClass getOWLDeprecatedClassClass();


    RDFProperty getOWLEquivalentClassProperty();


    RDFProperty getOWLEquivalentPropertyProperty();


    RDFSNamedClass getOWLFunctionalPropertyClass();


    RDFSNamedClass getOWLInverseFunctionalPropertyClass();


    /**
     * Gets the owl:oneOf property.
     *
     * @return owl:oneOf
     */
    RDFProperty getOWLOneOfProperty();


    /**
     * Gets the associated OWLProject, which can be used to store project-specific settings.
     *
     * @return the OWLProject (never null)
     */
    OWLProject getOWLProject();


    /**
     * Gets the root class of the OWL class hierarchy (owl:Thing).
     *
     * @return owl:Thing
     */
    OWLNamedClass getOWLThingClass();


    /**
     * Gets the owl:valuesFrom property (used for qualified cardinality restrictions.
     *
     * @return the owl:valuesFrom property
     */
    RDFProperty getOWLValuesFromProperty();


    /**
     * Gets the <code>TaskManager</code>
     * that can be used for executing tasks
     * that take a long time.
     */
    TaskManager getTaskManager();


    /**
     * Gets the property protege:allowedParent (if it exists)
     *
     * @return the property or null
     */
    RDFProperty getProtegeAllowedParentProperty();


    /**
     * Gets the property that is used internally to store the classification status of a class.
     *
     * @return the protege:classificationStatus property
     */
    RDFProperty getProtegeClassificationStatusProperty();


    RDFProperty getProtegeInferredSubclassesProperty();


    RDFProperty getProtegeInferredSuperclassesProperty();


    RepositoryManager getRepositoryManager();


    /**
     * @deprecated use getRDFUntypedResource instead
     */
    @Deprecated
    RDFExternalResource getRDFExternalResource(String uri);


    /**
     * @see #getRDFUntypedResourcesClass()
     * @deprecated use getRDFUntypedResourcesClass
     */
    @Deprecated
    RDFSClass getRDFExternalResourceClass();


    /**
     * Gets the rdf:first property.
     *
     * @return rdf:first
     */
    RDFProperty getRDFFirstProperty();


    /**
     * Gets an RDFResource by its name.
     *
     * @param name the name of the resource
     * @return an RDFResource or null if none was found under that name
     */
    RDFResource getRDFResource(String name);


    /**
     * Combines the calls <CODE>getRDFResource(name).as(javaInterface)</CODE>.
     * Warning: No checks are done.
     *
     * @param name          the name of the resource to get
     * @param javaInterface the target type of the result
     * @return an instance of the Java interface representing the resource, or null
     * @see #getRDFResource
     * @see RDFResource#as
     */
    RDFResource getRDFResourceAs(String name, Class javaInterface);


    /**
     * Gets the first resource that has a given browser text.
     * This call is extremely inefficient, because it might
     * iterate through all resources in the knowledge base.
     *
     * @param text the browser text
     * @return the Instance or null
     */
    RDFResource getRDFResourceByBrowserText(String text);


    /**
     * Gets the first instance that has a given name or browser text.
     * This call is much faster than the getRDFResourceByBrowserText if the
     * default browser key (:NAME) is used.
     *
     * @param text
     * @return the Instance or null
     */
    RDFResource getRDFResourceByNameOrBrowserText(String text);


    /**
     * Gets the number of rdfs:Classes (or owl:Classes) in this
     * model, including anonymous classes and system classes.
     * This is equivalent to <CODE>getRDFSClasses().size()</CODE>.
     *
     * @return the number of classes
     */
    int getRDFSClassCount();


    /**
     * Gets all rdfs:Classes (or owl:Classes) in this model, including
     * anonymous classes and system classes.
     *
     * @return a Collection of RDFSClass objects
     */
    Collection getRDFSClasses();


    RDFSDatatypeFactory getRDFSDatatypeFactory();


    RDFProperty getRDFSIsDefinedByProperty();


    RDFProperty getRDFSLabelProperty();


    /**
     * Gets the number of all RDFResources in this model.
     * This method is useful to estimate the number of iterations for
     * an algorithm without accessing all resources.
     * This is equivalent to <CODE>getRDFResources().size()</CODE>, but
     * much more efficient than that.
     *
     * @return the number of RDFResources
     */
    int getRDFResourceCount();


    /**
     * Gets a list of all RDFResources in this model.
     *
     * @return a Collection of RDFResource objects
     */
    Collection getRDFResources();


    /**
     * Gets all RDFResources that have a given value for a given property.
     *
     * @param property the RDFProperty
     * @param value    the value (must match exactly)
     * @return a Collection of RDFResources
     */
    Collection getRDFResourcesWithPropertyValue(RDFProperty property, Object value);


    /**
     * Gets the rdf:List class.
     *
     * @return rdf:List
     */
    RDFSNamedClass getRDFListClass();


    /**
     * Gets an RDFUntypedResource for a given string.
     * If no existing instance of this URI is found, the method can be instructed to create one.
     *
     * @param uri            the URI to look up
     * @param createOnDemand true to have the system create one if it doesn't exist
     * @return the RDFUntypedResource or null if none currently exists.
     */
    RDFUntypedResource getRDFUntypedResource(String uri, boolean createOnDemand);


    /**
     * Gets the internal type of all untyped resources.
     *
     * @return the class of untyped resources
     */
    RDFSNamedClass getRDFUntypedResourcesClass();


    /**
     * Gets all RDFResources that have a matching name (possibly
     * using wildcards).
     *
     * @param nameExpression the name pattern
     * @param maxMatches     the maximum number of hits to be returned
     */
    Collection getResourceNameMatches(String nameExpression, int maxMatches);

    RDFProperty getOWLCardinalityProperty();

    /**
     * Gets the class owl:DataRange, which is used to represent enumerations of
     * datatype values.
     *
     * @return owl:DataRange
     */
    RDFSNamedClass getOWLDataRangeClass();


    RDFProperty getOWLIntersectionOfProperty();


    /**
     * Gets the OWLJavaFactory that is used to create the Java objects for
     * ontology resources.
     *
     * @return the OWLJavaFactory
     * @see #setOWLJavaFactory
     */
    OWLJavaFactory getOWLJavaFactory();

    RDFProperty getOWLMaxCardinalityProperty();
    
    RDFProperty getOWLMinCardinalityProperty();

    /**
     * A convenience method which includes the typecast after <CODE>getCls()</CODE>.
     * The invoker must make sure that the name really reflects a OWLNamedClass.
     *
     * @param name the name of the OWLNamedClass to get
     * @return a OWLNamedClass
     */
    OWLNamedClass getOWLNamedClass(String name);


    /**
     * Gets the standard metaclass of all named classes.
     *
     * @return the metaclass owl:Class
     */
    OWLNamedClass getOWLNamedClassClass();


    /**
     * Gets all RDFResources that have a matching value for a given property.
     *
     * @param property    the (string) property to compare to
     * @param matchString the match string (can include wild cards).  The matching is case insensitive.
     * @param maxMatches  the maximum number of matches (-1 for all matches, which can be a lot!)
     * @return the matching RDFResources
     */
    Collection getMatchingResources(RDFProperty property, String matchString, int maxMatches);


    /**
     * Gets the next name that can be used to create an "anonymous"
     * instance.  Anonymous resources don't exist in Protege, so
     * we need to use naming conventions to mark those anonymous
     * instances, which will then be converted back to real anonymous
     * nodes in the OWL file (e.g. by the JenaCreator).
     *
     * @return a suitable name for an "anonymous" resource
     */
    String getNextAnonymousResourceName();


    OWLIndividual getOWLIndividual(String name);


    /**
     * Gets the system class <CODE>owl:Nothing</CODE>, which represents the empty set
     * for reasoners.
     *
     * @return the Nothing class
     */
    OWLNamedClass getOWLNothing();


    OWLObjectProperty getOWLObjectProperty(String name);


    /**
     * Gets the standard metaclass of all object slots/properties.
     *
     * @return the metaclass
     */
    OWLNamedClass getOWLObjectPropertyClass();


    /**
     * Gets all OWLOntologies in the current model.  This always includes the default ontology.
     *
     * @return a Collection of OWLOntologies
     * @see #getDefaultOWLOntology
     */
    Collection getOWLOntologies();


    /**
     * @deprecated use #getOWLOntologyByURI(URI uri)
     *             DO NOT PASS IN THE URI OF AN UNRESOLVED IMPORT AS THIS WILL
     *             CAUSE A Class Cast Exception
     */
    @Deprecated
    OWLOntology getOWLOntologyByURI(String uri);

    /**
     * Gets the OWLOntology with a given URI.
     *
     * @param uri the URI to look up
     * @return the OWLOntology or OWLIndividual or null if it does not exist yet
     */
    RDFResource getOWLOntologyByURI(URI uri);


    OWLNamedClass getOWLOntologyClass();


    /**
     * Gets the owl:OntologyProperties defined in OWL.
     * These are incompatibleWith etc.
     *
     * @return the Ontology properties
     */
    Collection getOWLOntologyProperties();

    /**
     * Provides low level access to some internal Protege detail - normally not needed.
     * 
     * This call is preferred over getOWLFrameStore() whenever possible.
     */
    FrameStore getHeadFrameStore();

    /**
     * Provides low level access to some internal Protege detail - normally not needed.
     */
    OWLFrameStore getOWLFrameStore();
    

    /**
     * Gets those Instances in the ontology that are instances of a OWLNamedClass.
     * This is equivalent to <CODE>getOWLIndividuals(false)</CODE>.
     *
     * @return the OWL instances
     */
    Collection getOWLIndividuals();


    /**
     * Gets those Instances in the ontology that are instances of a OWLNamedClass.
     *
     * @param onlyVisibleClasses true to only return instanceso of visible classes
     * @return the OWL instances
     */
    Collection getOWLIndividuals(boolean onlyVisibleClasses);


    RDFProperty getOWLInverseOfProperty();


    /**
     * A convenience method including typecast.
     *
     * @param name the name of the property to get
     * @return <CODE>(OWLProperty) getRDFProperty(name)</CODE>
     */
    OWLProperty getOWLProperty(String name);


    /**
     * Gets a Collection of all system resources installed by the OWL Plugin.
     * These are not editable and not included, but isSystem().
     *
     * @return a Collection of system resources
     */
    Collection getOWLSystemResources();


    /**
     * Gets the Protege ValueType that represents a given URI string.
     *
     * @param uri the URI (e.g., "http://www.w3.org/2001/XMLSchema#float")
     * @return the ValueType or null if uri is invalid
     */
    ValueType getOWLValueType(String uri);


    /**
     * The Protege readOnly property has a special behavior required by NCI.  First of all, it is only 
     * relevant for annotation properties.  If we view a change to an annotation value as the removal of
     * an annotation assertion axiom followed by adding a different annotation assertion axiom then the rules 
     * can be stated as
     * 1. if p is a readOnly annotation property then a user is allowed to add an assertion <x p y>
     * 2. if p is a readOnly annotation property then a user is not allowed to remove an assertion <x p y>
     * 
     * @return the Slot ProtegeNames.READ_ONLY
     */
    RDFProperty getProtegeReadOnlyProperty();


    RDFIndividual getRDFIndividual(String name);


    /**
     * Gets those Instances in the ontology that are instances of an RDFSNamedClass.
     * This includes all OWL individuals.
     *
     * @return the RDF instances
     */
    Collection getRDFIndividuals();


    /**
     * Gets those Instances in the ontology that are instances of an RDFSNamedClass.
     *
     * @param onlyVisibleClasses true to only return instances of visible classes.
     * @return the RDF instances
     */
    Collection getRDFIndividuals(boolean onlyVisibleClasses);


    /**
     * Gets the placeholder for empty rdf:Lists.
     *
     * @return rdf:nil
     */
    RDFList getRDFNil();


    /**
     * Gets a collection of all RDFProperties (or subclasses thereof).
     *
     * @return a Collection of RDFProperties
     */
    Collection getRDFProperties();


    /**
     * A convenience method to access a slot as RDFProperty.
     *
     * @param name the name of the slot to get
     * @return <CODE>(RDFProperty) getSlot(name)</CODE>
     */
    RDFProperty getRDFProperty(String name);


    /**
     * Gets the rdf:Property metaclass.
     *
     * @return rdf:Property
     */
    RDFSNamedClass getRDFPropertyClass();


    /**
     * Gets an XML Schema Datatype using the prefix notation (e.g. "xsd:int").
     *
     * @param name the name of the datatype to get
     * @return the RDFSDatatype
     */
    RDFSDatatype getRDFSDatatypeByName(String name);


    /**
     * Gets an XML Schema Datatype by its URI (e.g. XSDDatatype.XSDint).
     *
     * @param uri the URI of the datatype to get
     * @return the RDFSDatatype
     */
    RDFSDatatype getRDFSDatatypeByURI(String uri);


    /**
     * Gets the RDFSDatatype of a given primitive value, which can be either a
     * standard value (e.g. Integer, Float), or an RDFSLiteral.
     *
     * @param valueOrRDFSLiteral the value object
     * @return the RDFSDatatype of this value
     */
    RDFSDatatype getRDFSDatatypeOfValue(Object valueOrRDFSLiteral);


    /**
     * Gets a Collection of the (named) RDF/XML Schema datatypes.
     *
     * @return the datatype objects (instance of RDFSDatatype)
     */
    Collection<RDFSDatatype> getRDFSDatatypes();


    /**
     * Gets the rdfs:Class metaclass
     *
     * @return rdfs:Class
     */
    RDFSNamedClass getRDFSNamedClassClass();


    RDFSNamedClass getRDFSNamedClass(String name);


    OWLDatatypeProperty getRDFSCommentProperty();


    /**
     * Gets the rdf:rest property, which is used to describe the rest of an rdf:List.
     *
     * @return the rdf:rest property (never null)
     */
    RDFProperty getRDFRestProperty();

    /**
     * Gets the meta class that represents some values from restrictions.
     * 
     * @return the class of all somevaluesfrom restrictions.
     */
    RDFSNamedClass getOWLSomeValuesFromRestrictionClass();

    /**
     * Gets the metaclasses of the available OWLRestriction kinds.
     *
     * @return an Array of metaclasses
     */
    RDFSNamedClass[] getOWLRestrictionMetaclasses();


    /**
     * Gets a Collection of all Restrictions that are defined on a given property.
     *
     * @param property the property to get all Restrictions for
     * @return a Collection of OWLRestriction objects
     */
    Collection getOWLRestrictionsOnProperty(RDFProperty property);


    RDFProperty getOWLSameAsProperty();


    RDFProperty getOWLUnionOfProperty();


    RDFProperty getOWLVersionInfoProperty();


    /**
     * Gets the rdfs:domain property.
     *
     * @return rdfs:domain
     */
    RDFProperty getRDFSDomainProperty();


    /**
     * Gets the rdfs:range property.
     *
     * @return rdfs:range
     */
    RDFProperty getRDFSRangeProperty();


    RDFProperty getRDFSSubClassOfProperty();


    /**
     * Gets the property rdfs:subPropertyOf
     *
     * @return rdfs:subPropertyOf
     */
    RDFProperty getRDFSSubPropertyOfProperty();


    /**
     * Gets the name of a Frame by converting a URI.
     * The name in the format prefix:localName.  If the namespace of the URI
     * is the default namespace, then the prefix will be empty and the name
     * will consist of only the local name.
     *
     * @param uri a fully qualified URI
     * @return the name of the corresponding Frame
     */
    String getResourceNameForURI(String uri);
    
    /**
     * Gets the slots that are used for matching when the user searches for a frame
     * in the search fields.  These are used in addition to the :NAME slot in the
     * getFrameNameMatches() method.
     *
     * @return the collection of synonym slots (by default: an empty collection)
     */
    Collection getSearchSynonymProperties();


    /**
     * @return the Slot OWLNames.Slot.SUBCLASSES_DISJOINT
     */
    RDFProperty getProtegeSubclassesDisjointProperty();


    /**
     * Gets the rdf:type property.
     *
     * @return rdf:type as an RDFProperty
     */
    RDFProperty getRDFTypeProperty();


    /**
     * Gets the pre-defined system annotation slots such as rdfs:comment.
     *
     * @return the system annotation slots
     */
    RDFProperty[] getSystemAnnotationProperties();
    
    SWRLSystemFrames getSystemFrames();


    /**
     * Gets the prefix used to distinguish "to-do" list items from other values
     * of the to-do list property.
     *
     * @return the current prefix (as specified in an owl:Ontology annotation)
     */
    String getTodoAnnotationPrefix();


    /**
     * Gets the (annotation) slot that is used to represent "to-do" list items
     * in this knowledge base.  This can be specified using the appropriate
     * annotation value in the owl:Ontology.
     *
     * @return the current to-do slot (default: OWLNames.Slot.VERSION_INFO)
     */
    OWLDatatypeProperty getTodoAnnotationProperty();


    /**
     * Gets the list of language prefixes defined by the user in the owl:Ontology.
     * This can be used to provide default values in language selection boxes.
     *
     * @return a list of language prefixes (e.g. ["de", "en", "fr"])
     */
    String[] getUsedLanguages();


    /**
     * Gets a Collection of all user-defined OWLProperties in this knowledge base.
     *
     * @return a Collection of OWLProperty objects
     */
    Collection getUserDefinedOWLProperties();


    /**
     * Gets a Collection of all user-defined OWLObjectProperties in this knowledge base.
     *
     * @return a Collection of OWLObjectProperty objects.
     */
    Collection getUserDefinedOWLObjectProperties();


    /**
     * Gets a Collection of all user-defined OWLDatatypeProperties in this knowledge base.
     *
     * @return a Collection of OWLDatatypeProperty objects.
     */
    Collection getUserDefinedOWLDatatypeProperties();


    /**
     * Gets a Collection of all user-defined named OWL classes in this knowledge base.
     * Note that this returns neither anonymous classes such as Restrictions nor system classes.
     * like owl:Thing and owl:Nothing.
     *
     * @return a Collection of OWLNamedClass instances
     */
    Collection getUserDefinedOWLNamedClasses();


    /**
     * Gets all non-system individuals in this OWLModel.
     *
     * @param onlyVisibleClasses true to only return instances of visible classes
     * @return a collection of RDFResources
     */
    Collection getUserDefinedRDFIndividuals(boolean onlyVisibleClasses);


    /**
     * Gets all user-defined RDF properties in this OWLModel.
     *
     * @return a collection of RDFProperty instances
     */
    Collection getUserDefinedRDFProperties();


    /**
     * Gets all user-defined RDFSClses including the OWLNamedClses.
     *
     * @return the user-defined RDFSNamedClasses
     */
    Collection getUserDefinedRDFSNamedClasses();


    /**
     * The inverse of <CODE>getValueType</CODE>.
     *
     * @param valueType
     * @return the OWL URI of a given ValueType
     */
    String getValueTypeURI(ValueType valueType);


    /**
     * Gets a Collection of all user-defined, visible OWLProperties in this knowledge base.
     *
     * @return a Collection of OWLProperty objects
     */
    Collection getVisibleUserDefinedOWLProperties();


    /**
     * Gets all visible, user-defined RDF properties in this knowledge base.
     *
     * @return a collection of RDFProperty instances
     */
    Collection getVisibleUserDefinedRDFProperties();


    /**
     * Gets the resource representing the datatype rdf:XMLLiteral.
     *
     * @return rdf:XMLLiteral
     */
    RDFSDatatype getRDFXMLLiteralType();

    /**
     * Gets a list of resources, only containing the visible entries of a given Iterator.
     *
     * @param iterator an Iterator of RDFResources
     * @return a sublist of iterator
     */
    List getVisibleResources(Iterator iterator);


    /**
     * Gets the default datatype for URI values.
     *
     * @return xsd:anyURI
     */
    RDFSDatatype getXSDanyURI();


    /**
     * Gets the default datatype for byte[] values.
     *
     * @return xsd:base64Binary
     */
    RDFSDatatype getXSDbase64Binary();


    /**
     * Gets the default datatype for boolean values.
     *
     * @return xsd:boolean
     */
    RDFSDatatype getXSDboolean();


    /**
     * Gets the default datatype for byte values.
     *
     * @return xsd:byte
     */
    RDFSDatatype getXSDbyte();


    /**
     * Gets the default datatype for date values.
     *
     * @return xsd:date
     */
    RDFSDatatype getXSDdate();


    /**
     * Gets the default datatype for timestamp values.
     *
     * @return xsd:dateTime
     */
    RDFSDatatype getXSDdateTime();


    /**
     * Gets the default datatype for BigDecimal values.
     *
     * @return xsd:decimal
     */
    RDFSDatatype getXSDdecimal();


    /**
     * Gets the default datatype for double values.
     *
     * @return xsd:double
     */
    RDFSDatatype getXSDdouble();


    /**
     * Gets the datatype for duration values.
     *
     * @return xsd:duration
     */
    RDFSDatatype getXSDduration();


    /**
     * Gets the default datatype for float values.
     *
     * @return xsd:float
     */
    RDFSDatatype getXSDfloat();


    /**
     * Gets the default datatype for integer values.
     *
     * @return xsd:int
     */
    RDFSDatatype getXSDint();


    /**
     * Gets the default datatype for BigInteger values.
     *
     * @return xsd:integer
     */
    RDFSDatatype getXSDinteger();


    /**
     * Gets the default datatype for long values.
     *
     * @return xsd:long
     */
    RDFSDatatype getXSDlong();
    
    /**
     * Gets the default datatype for non-negative values
     */
    RDFSDatatype getXSDNonNegativeInteger();


    /**
     * Gets the default datatype for short values.
     *
     * @return xsd:short
     */
    RDFSDatatype getXSDshort();


    /**
     * Gets the default datatype for string values.
     *
     * @return xsd:string
     */
    RDFSDatatype getXSDstring();


    /**
     * Gets the default datatype for time values.
     *
     * @return xsd:time
     */
    RDFSDatatype getXSDtime();


    /**
     * @see RDFResource#isAnonymous()
     * @deprecated
     */
    @Deprecated
    boolean isAnonymousResource(RDFResource resource);


    boolean isAnonymousResourceName(String name);


    /**
     * Checks whether a given frame is one of the results of <CODE>getOWLSystemResources</CODE>.
     *
     * @param frame the Frame to check
     * @return true if frame is one of the system frames
     * @deprecated
     */
    @Deprecated
    boolean isOWLSystemFrame(Frame frame);


    boolean isProtegeMetaOntologyImported();


    /**
     * Checks if a certain object would be a valid value for a given subject/property pair.
     * This can be used by visual components to validate user input.
     * Typical implementations check for constraint violations such as numeric
     * ranges, string length etc.
     * This does not check for cardinality restrictions.
     *
     * @param subject   the subject to check the value for
     * @param predicate the property to check for
     * @param value     the potential value
     * @return true  if value would be valid for the subject/predicate pair
     */
    boolean isValidPropertyValue(RDFResource subject, RDFProperty predicate, Object value);


    /**
     * Checks whether a given String would be a valid name for a given resource.
     *
     * @param name     the potential name of the resource
     * @param resource the RDFResource to test
     * @return true  if the resource could be renamed to name
     */
    boolean isValidResourceName(String name, RDFResource resource);


    /**
     * Gets all resource in this that have a given prefix.
     *
     * @param prefix the prefix to search for
     * @return a Collection of the matching NamespaceInstances
     */
    Collection getResourcesWithPrefix(String prefix);


    /**
     * Gets the local part of an URI, e.g. for <CODE>http://www.aldi.de/ontologies#Hans</CODE>
     * this will return the String <CODE>Hans</CODE>.
     *
     * @param uri the fully qualified URI
     * @return the local name in the URI
     */
    String getLocalNameForURI(String uri);


    String getLocalNameForResourceName(String frameName);


    /**
     * Gets the NamespaceManager used by this knowledge base.
     *
     * @return the NamespaceManager
     */
    NamespaceManager getNamespaceManager();


    String getNamespaceForResourceName(String resourceName);


    /**
     * Gets the namespace part of a given URI.
     * For <CODE>http://www.aldi.de/ontologies#Hans</CODE> this will return
     * <CODE>http://www.aldi.de/ontologies#</CODE>.
     *
     * @param uri the fully qualified URI
     * @return the namespace including the #
     */
    String getNamespaceForURI(String uri);


    String getPrefixForResourceName(String resourceName);


    /**
     * Gets the currently used PropertyValueValidator.
     *
     * @return the validator
     * @see #setPropertyValueValidator
     */
    PropertyValueValidator getPropertyValueValidator();


    TripleStoreModel getTripleStoreModel();


    String getURIForResourceName(String resourceName);

    boolean isExpandShortNameInMethods();
    /**
     * Gets an Iterator of all OWLAnonymousClasses in the ontology.
     *
     * @return an Iterator of OWLAnonymousClass objects
     */
    Iterator listOWLAnonymousClasses();


    /**
     * Gets an Iterator on all OWLNamedClasses in the ontology, including system classes.
     *
     * @return an Iterator of OWLNamedClass objects
     */
    Iterator listOWLNamedClasses();


    /**
     * Gets an Iterator on all RDFProperties in the model, including system properties.
     *
     * @return an Iterator of RDFProperty objects
     */
    Iterator listRDFProperties();


    /**
     * Gets an Iterator on all RDFSNamedClasses in the ontology, including system classes.
     *
     * @return an Iterator of RDFSNamedClass objects
     */
    Iterator listRDFSNamedClasses();


    /**
     * Finds all references to a given object, i.e. all triples that have the given
     * object as their object.
     *
     * @param object     the object to look for
     * @param maxResults the maximum number of expected results
     * @return an Iterator of type Tuple
     */
    Iterator listReferences(Object object, int maxResults);


    /**
     * The the subjects of all triples where a given property has any value.
     * The Iterator does not contain duplicates.
     *
     * @param property the property to look for
     * @return an Iterator of RDFResources
     */
    Iterator listSubjects(RDFProperty property);


    /**
     * Removes a ClassListener that was previously added.
     *
     * @param listener the ClassListener to remove
     * @see #addClassListener
     */
    void removeClassListener(ClassListener listener);


    /**
     * Removes a ModelListener that was previously added.
     *
     * @param listener the ModelListener to remove
     * @see #addModelListener
     */
    void removeModelListener(ModelListener listener);


    /**
     * Removes a PropertyListener that was previously added.
     *
     * @param listener the PropertyListener to remove
     * @see #addPropertyListener
     */
    void removePropertyListener(PropertyListener listener);


    /**
     * Removes a PropertyValueListener previously added.
     *
     * @param listener the listener to remove
     * @see #addPropertyValueListener
     */
    void removePropertyValueListener(PropertyValueListener listener);


    /**
     * Removes a ResourceListener that was previously added.
     *
     * @param listener the ResourceListener to remove
     * @see #addResourceListener
     */
    void removeResourceListener(ResourceListener listener);
    
    
    /**
     * resets the Jena model so that it can be rebuilt.
     */
    void resetJenaModel();
    
    /*
     * resets the OWL ontology cache so that the default ontology is recalculated on 
     * the next call.
     */

    void resetOntologyCache();

    /**
     * @see #getDispatchEventsEnabled
     */
    boolean setDispatchEventsEnabled(boolean enabled);

    void setExpandShortNameInMethods(boolean expandShortNameInMethods);

    
    /**
     * @see #getGenerateEventsEnabled
     */
    boolean setGenerateEventsEnabled(boolean enabled);


    /**
     * Sets the OWLClassDisplay to change the default display of classes.
     * As a side-effect, this call also changes the OWLClassParser, which
     * depends on the display.
     *
     * @param display the new display (not null)
     */
    void setOWLClassDisplay(OWLClassDisplay display);


    /**
     * Sets the OWLJavaFactory for this OWLModel.
     * An OWLJavaFactory creates the Java object for an RDFResource.
     *
     * @param factory the new OWLJavaFactory
     * @see edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory
     * @see #getOWLJavaFactory
     */
    void setOWLJavaFactory(OWLJavaFactory factory);


    /**
     * Sets the associated OWLProject.
     *
     * @param owlProject the new OWLProject
     * @see #getOWLProject
     */
    void setOWLProject(OWLProject owlProject);


    /**
     * Sets the validator that shall be used in future calls of the validation
     * methods.
     *
     * @param validator the new Validator (or null)
     * @see #isValidPropertyValue
     */
    void setPropertyValueValidator(PropertyValueValidator validator);


    /**
     * @see #getSearchSynonymProperties()
     */
    void setSearchSynonymProperties(Collection properties);


    /**
     * Sets the TaskManager.
     *
     * @param taskManager the new TaskManager (not null)
     * @see #getTaskManager
     */
    void setTaskManager(TaskManager taskManager);
    
    WriterSettings getWriterSettings();
    
    void setWriterSettings(WriterSettings writerSettings);
}
