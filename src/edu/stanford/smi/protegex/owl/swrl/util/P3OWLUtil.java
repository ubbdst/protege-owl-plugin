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


package edu.stanford.smi.protegex.owl.swrl.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.classparser.AmbiguousNameException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

/**
 * Class that wraps some common Protege-OWL API methods and throws meaningful exceptions when errors are encountered.
 * <p>
 * Covers a fairly arbitrary method set.
 */
public class P3OWLUtil
{
	public static JenaOWLModel createJenaOWLModel(String owlFileName) throws P3OWLUtilException
	{
		JenaOWLModel owlModel = null;

		try {
			owlModel = ProtegeOWL.createJenaOWLModelFromURI(new File(owlFileName).toURI().toString());
		} catch (Exception e) {
			throwException("error opening OWL file " + owlFileName + ": " + e.getMessage());
		}

		return owlModel;
	}

	public static JenaOWLModel createJenaOWLModel() throws P3OWLUtilException
	{
		JenaOWLModel owlModel = null;

		try {
			owlModel = ProtegeOWL.createJenaOWLModel();
		} catch (Exception e) {
			throw new P3OWLUtilException("error creating Jena OWL model: " + e.getMessage());
		}

		return owlModel;
	}

	public static void importOWLFile(JenaOWLModel owlModel, String importOWLFileName) throws P3OWLUtilException
	{
		try {
			ImportHelper importHelper = new ImportHelper(owlModel);
			URI importUri = URIUtilities.createURI(new File(importOWLFileName).toURI().toString());
			importHelper.addImport(importUri);
			importHelper.importOntologies(false);
		} catch (Exception e) {
			throwException("error importing OWL file " + importOWLFileName + ": " + e.getMessage());
		}
	}

	public static void setPrefix(OWLModel owlModel, String prefix, String namespace) throws P3OWLUtilException
	{
		try {
			owlModel.getNamespaceManager().setPrefix(new URI(namespace), prefix);
		} catch (URISyntaxException e) {
			throwException("error setting prefix " + prefix + " for namespace " + namespace + ": " + e.getMessage());
		}
	}

	public static void writeJenaOWLModel2File(JenaOWLModel owlModel, String outputOWLFileName) throws P3OWLUtilException
	{
		List<?> errors = new ArrayList<Object>();
		URI outputURI = URIUtilities.createURI(new File(outputOWLFileName).toURI().toString());
		owlModel.save(outputURI, FileUtils.langXMLAbbrev, errors);
		if (errors.size() != 0)
			throwException("error creating output OWL file " + outputOWLFileName + ": " + errors);
	}

	public static OWLNamedClass createOWLNamedClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		RDFResource resource;
		OWLNamedClass cls;

		checkIfIsValidClassName(owlModel, className);

		resource = owlModel.getRDFResource(className);

		if (resource != null) {
			if (resource instanceof OWLNamedClass)
				cls = (OWLNamedClass)resource;
			else
				throw new P3OWLUtilException("class " + className + " is not an OWL named class");
		} else
			cls = owlModel.createOWLNamedClass(className);

		return cls;
	}

	public static OWLNamedClass createOWLNamedClass(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return createOWLNamedClass(owlModel, classURI.toString());
	}

	public static OWLIndividual createOWLIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		OWLIndividual individual = owlModel.getOWLIndividual(individualName);

		if (individual == null)
			individual = createIndividual(owlModel, individualName);

		return individual;
	}

	public static OWLIndividual createOWLIndividual(OWLModel owlModel, URI individualURI) throws P3OWLUtilException
	{
		return createOWLIndividual(owlModel, individualURI.toString());
	}

	public static RDFSNamedClass createRDFSNamedClass(OWLModel owlModel, String className)
	{
		RDFSNamedClass cls;

		cls = owlModel.getRDFSNamedClass(className);

		if (cls == null)
			cls = owlModel.createRDFSNamedClass(className);

		return cls;
	}

	public static OWLNamedClass createOWLNamedClass(OWLModel owlModel)
	{
		return owlModel.createOWLNamedClass(null);
	}

	public static OWLObjectProperty createOWLObjectProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLObjectProperty property = owlModel.getOWLObjectProperty(propertyName);

		if (property == null)
			property = owlModel.createOWLObjectProperty(propertyName);

		if (property == null)
			throw new P3OWLUtilException("error creating OWL object property " + propertyName);

		return property;
	}

	public static OWLObjectProperty createOWLObjectProperty(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return createOWLObjectProperty(owlModel, propertyURI.toString());
	}

	public static OWLObjectProperty createOWLObjectProperty(OWLModel owlModel)
	{
		return owlModel.createOWLObjectProperty(null);
	}

	public static OWLDatatypeProperty createOWLDatatypeProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLDatatypeProperty property = owlModel.getOWLDatatypeProperty(propertyName);

		if (property == null)
			property = owlModel.createOWLDatatypeProperty(propertyName);

		if (property == null)
			throw new P3OWLUtilException("error creating OWL data property " + propertyName);

		return property;
	}

	public static OWLDatatypeProperty createOWLDatatypeProperty(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return createOWLDatatypeProperty(owlModel, propertyURI.toString());
	}

	public static RDFProperty createOWLAnnotationProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		RDFProperty property = owlModel.getRDFProperty(propertyName);

		if (property == null)
			property = owlModel.createAnnotationProperty(propertyName);
		else if (!property.isAnnotationProperty())
			throw new P3OWLUtilException("property " + propertyName + " is not an annotation property");

		if (property == null)
			throw new P3OWLUtilException("error creating OWL annotation property " + propertyName);

		return property;
	}

	public static RDFProperty createOWLAnnotationProperty(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return createOWLAnnotationProperty(owlModel, propertyURI.toString());
	}

	public static RDFSNamedClass createRDFSNamedClassUsingLabelAnnotation(OWLModel owlModel, String labelText, boolean allowDuplicates)
	{
		RDFSNamedClass cls = null;
		Collection<?> resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

		if (!allowDuplicates && resources != null && !resources.isEmpty()) {
			for (Object resource : resources) {
				if (resource instanceof OWLNamedClass) {
					RDFSNamedClass candidateClass = (OWLNamedClass)resource;
					// Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
					for (Object value : candidateClass.getPropertyValues(owlModel.getRDFSLabelProperty())) {
						if (value instanceof String) {
							String stringValue = (String)value;
							if (stringValue.equalsIgnoreCase(labelText))
								return candidateClass; // Pick the first matching one
						} else if (value instanceof RDFSLiteral) {
							RDFSLiteral literalValue = (RDFSLiteral)value;
							if (literalValue.getString().equalsIgnoreCase(labelText))
								return candidateClass; // Pick the first matching one
						}
					}
				}
			}
		}

		cls = owlModel.createRDFSNamedClass(null); // We may not have found a matching class above.

		if (!cls.hasPropertyValue(owlModel.getRDFSLabelProperty(), labelText))
			cls.addPropertyValue(owlModel.getRDFSLabelProperty(), labelText);

		return cls;
	}

	// TODO: case sensitivity option
	public static OWLNamedClass createOWLNamedClassWithRDFSLabel(OWLModel owlModel, String namespace, String labelText, String language, boolean allowDuplicates)
		throws P3OWLUtilException
	{
		OWLNamedClass cls = null;
		Collection<?> resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

		if (!allowDuplicates && resources != null && !resources.isEmpty()) {
			for (Object resource : resources) {
				if (resource instanceof OWLNamedClass) {
					OWLNamedClass candidateClass = (OWLNamedClass)resource;

					if (!candidateClass.getNamespace().equals(namespace))
						continue; // TODO: need more principled way of comparing namespaces

					// Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
					for (Object value : candidateClass.getPropertyValues(owlModel.getRDFSLabelProperty())) {
						if (value instanceof String) {
							String stringValue = (String)value;
							if ((language == null || language.length() == 0) && stringValue.equalsIgnoreCase(labelText))
								return candidateClass; // Pick the first matching one
						} else if (value instanceof RDFSLiteral) {
							RDFSLiteral literalValue = (RDFSLiteral)value;
							if (literalValue.getLanguage() == language && literalValue.getString().equalsIgnoreCase(labelText))
								return candidateClass; // Pick the first matching one
						}
					}
				}
			}
		}

		cls = owlModel.createOWLNamedClass(null); // We may not have found a matching class above.

		addRDFSLabel(cls, labelText, language);

		return cls;
	}

	public static OWLObjectProperty createOWLObjectPropertyUsingLabelAnnotation(OWLModel owlModel, String namespace, String labelText, String language,
																																							boolean allowDuplicates) throws P3OWLUtilException
	{
		OWLObjectProperty property = null;
		Collection<?> resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

		if (!allowDuplicates && resources != null && !resources.isEmpty()) {
			for (Object resource : resources) {
				if (resource instanceof OWLNamedClass) {
					OWLObjectProperty candidateProperty = (OWLObjectProperty)resource;

					if (!candidateProperty.getNamespace().equals(namespace))
						continue;

					// Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
					for (Object value : candidateProperty.getPropertyValues(owlModel.getRDFSLabelProperty())) {
						if (value instanceof String) {
							String stringValue = (String)value;
							if ((language == null || language.length() == 0) && stringValue.equalsIgnoreCase(labelText))
								return candidateProperty; // Pick the first matching one
						} else if (value instanceof RDFSLiteral) {
							RDFSLiteral literalValue = (RDFSLiteral)value;
							if (literalValue.getLanguage() == language && literalValue.getString().equalsIgnoreCase(labelText))
								return candidateProperty; // Pick the first matching one
						}
					}
				}
			}
		}

		property = owlModel.createOWLObjectProperty(null); // We may not have found a matching property above.

		addRDFSLabel(property, labelText, language);

		return property;
	}

	public static OWLDatatypeProperty createOWLDataPropertyUsingLabelAnnotation(OWLModel owlModel, String namespace, String labelText, String language,
																																							boolean allowDuplicates) throws P3OWLUtilException
	{
		OWLDatatypeProperty property = null;
		Collection<?> resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

		if (!allowDuplicates && resources != null && !resources.isEmpty()) {
			for (Object resource : resources) {
				if (resource instanceof OWLNamedClass) {
					OWLDatatypeProperty candidateProperty = (OWLDatatypeProperty)resource;

					if (!candidateProperty.getNamespace().equals(namespace))
						continue; // TODO: need more principled way of comparing namespaces

					// Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
					for (Object value : candidateProperty.getPropertyValues(owlModel.getRDFSLabelProperty())) {
						if (value instanceof String) {
							String stringValue = (String)value;
							if ((language == null || language.length() == 0) & stringValue.equalsIgnoreCase(labelText))
								return candidateProperty; // Pick the first matching one
						} else if (value instanceof RDFSLiteral) {
							RDFSLiteral literalValue = (RDFSLiteral)value;
							if (literalValue.getLanguage() == language && literalValue.getString().equalsIgnoreCase(labelText))
								return candidateProperty; // Pick the first matching one
						}
					}
				}
			}
		}

		property = owlModel.createOWLDatatypeProperty(null); // We may not have found a matching property above.

		addRDFSLabel(property, labelText, language);

		return property;
	}

	public static OWLIndividual createOWLIndividualWithRDFSLabel(OWLModel owlModel, String namespace, String labelText, String language, boolean allowDuplicates)
		throws P3OWLUtilException
	{
		OWLIndividual individual = null;
		Collection<?> resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

		if (!allowDuplicates && resources != null && !resources.isEmpty()) {
			for (Object resource : resources) {
				if (resource instanceof OWLNamedClass) {
					OWLIndividual candidateIndividual = (OWLIndividual)resource;

					if (!candidateIndividual.getNamespace().equals(namespace))
						continue; // TODO: need more principled way of comparing namespaces

					// Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
					for (Object value : candidateIndividual.getPropertyValues(owlModel.getRDFSLabelProperty())) {
						if (value instanceof String) {
							String stringValue = (String)value;
							if ((language == null || language.length() == 0) && stringValue.equalsIgnoreCase(labelText))
								return candidateIndividual; // Pick the first matching one
						} else if (value instanceof RDFSLiteral) {
							RDFSLiteral literalValue = (RDFSLiteral)value;
							if (literalValue.getLanguage() == language && literalValue.getString().equalsIgnoreCase(labelText))
								return candidateIndividual; // Pick the first matching one
						}
					}
				}
			}
		}

		individual = createOWLIndividual(owlModel); // We may not have found a matching individual above.

		addRDFSLabel(individual, labelText, language);

		return individual;
	}

	public static Set<OWLIndividual> getMatchingIndividuals(OWLModel owlModel, String propertyName, String matchString) throws P3OWLUtilException
	{
		RDFProperty property = getOWLProperty(owlModel, propertyName, true);
		Collection<?> matchingResources = owlModel.getMatchingResources(property, matchString, -1);
		Set<OWLIndividual> matchingIndividuals = new HashSet<OWLIndividual>();

		for (Object o : matchingResources)
			if (o instanceof OWLIndividual)
				matchingIndividuals.add((OWLIndividual)o);

		return matchingIndividuals;
	}

	public static Set<OWLIndividual> getMatchingIndividualsOfClass(OWLModel owlModel, String className, String propertyName, String matchString)
		throws P3OWLUtilException
	{
		Set<OWLIndividual> matchingIndividuals = new HashSet<OWLIndividual>();

		for (OWLIndividual owlIndividual : getMatchingIndividuals(owlModel, propertyName, matchString))
			if (isOWLIndividualOfClass(owlModel, owlIndividual, className))
				matchingIndividuals.add(owlIndividual);

		return matchingIndividuals;
	}

	public static boolean isOWLNamedClass(OWLModel owlModel, String className)
	{
		RDFResource resource = owlModel.getRDFResource(className);

		return resource instanceof OWLNamedClass;
	}

	public static boolean isOWLNamedClass(OWLModel owlModel, URI classURI)
	{
		return isOWLNamedClass(owlModel, classURI.toString());
	}

	public static boolean isOWLClass(OWLModel owlModel, String className)
	{
		RDFResource resource = owlModel.getRDFResource(className);

		return resource != null && resource instanceof OWLClass;
	}

	public static boolean isOWLClass(OWLModel owlModel, URI classURI)
	{
		return isOWLClass(owlModel, classURI.toString());
	}

	public static OWLNamedClass getOWLNamedClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		OWLNamedClass cls = owlModel.getOWLNamedClass(className);

		if (cls == null)
			throw new P3OWLUtilException("unknown OWL named class " + className);

		return cls;
	}

	public static OWLNamedClass getOWLNamedClass(OWLModel owlModel, URI classURI)
	{
		if (isOWLNamedClass(owlModel, classURI.toString()))
			return owlModel.getOWLNamedClass(classURI.toString());
		else
			return null;
	}

	public static OWLClass getOWLClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(className);

		if (resource == null || !(resource instanceof OWLClass))
			throw new P3OWLUtilException("invalid or unknown OWL class " + className);

		return (OWLClass)resource;
	}

	public static OWLClass getOWLClass(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getOWLClass(owlModel, classURI.toString());
	}

	public static boolean isRDFResource(OWLModel owlModel, String resourceName)
	{
		return owlModel.getRDFResource(resourceName) != null;
	}

	public static boolean isRDFResource(OWLModel owlModel, URI resourceURI)
	{
		return isRDFResource(owlModel, resourceURI.toString());
	}

	public static RDFResource getRDFResource(OWLModel owlModel, String resourceName)
	{
		return owlModel.getRDFResource(resourceName);
	}

	public static RDFResource getRDFResource(OWLModel owlModel, URI resourceURI)
	{
		return getRDFResource(owlModel, resourceURI.toString());
	}

	public static RDFSClass getRDFSClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(className);

		if (resource == null || !(resource instanceof RDFSClass))
			throw new P3OWLUtilException("invalid or unknown RDFS class " + className);

		return (RDFSClass)resource;
	}

	public static OWLClass getOWLClassDescription(OWLModel owlModel, String descriptionClassName) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(descriptionClassName);

		if (resource == null || !(resource instanceof OWLClass))
			throw new P3OWLUtilException("unknown OWL class description name " + descriptionClassName);

		return (OWLClass)resource;
	}

	public static OWLIndividual createIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		return createIndividualOfClass(owlModel, getOWLThingClass(owlModel), individualName);
	}

	public static OWLIndividual createIndividual(OWLModel owlModel, URI individualURI) throws P3OWLUtilException
	{
		return createIndividual(owlModel, individualURI.toString());
	}

	public static OWLIndividual createOWLIndividual(OWLModel owlModel) throws P3OWLUtilException
	{
		return (OWLIndividual)getOWLThingClass(owlModel).createInstance(null);
	}

	public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		return createIndividualOfClass(owlModel, className, null);
	}

	public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String className, String individualName) throws P3OWLUtilException
	{
		OWLNamedClass cls = getNamedClass(owlModel, className);

		return createIndividualOfClass(owlModel, cls, individualName);
	}

	public static OWLIndividual createIndividualOfClass(OWLModel owlModel, OWLClass cls) throws P3OWLUtilException
	{
		return createIndividualOfClass(owlModel, cls, null);
	}

	public static OWLIndividual createIndividualOfClass(OWLModel owlModel, OWLClass cls, String individualName) throws P3OWLUtilException
	{
		RDFResource resource = null;
		OWLIndividual individual = null;

		if (individualName != null)
			resource = owlModel.getRDFResource(individualName);

		if (resource == null) {
			individual = (OWLIndividual)cls.createInstance(individualName);
			if (!individual.hasRDFType(cls, true))
				individual.setRDFType(cls);
		} else {
			if (resource instanceof OWLIndividual) {
				individual = (OWLIndividual)resource;
				if (!individual.hasRDFType(cls, true))
					individual.addRDFType(cls);
			} else
				throwException("could not create individual " + individualName + " because another resource of that name already exists");
		}

		return individual;
	}

	public static OWLNamedClass getNamedClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		return getOWLNamedClass(owlModel, className, true);
	}

	public static OWLClass getClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		return getOWLClass(owlModel, className, true);
	}

	public static OWLClass getClass(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getClass(owlModel, classURI.toString());
	}

	public static boolean isOWLIndividualOfClass(OWLModel owlModel, OWLIndividual individual, String className)
	{
		RDFResource cls = owlModel.getRDFResource(className);

		return (cls instanceof OWLNamedClass) && individual.hasRDFType((OWLNamedClass)cls, true);
	}

	public static boolean isOWLIndividualOfType(OWLModel owlModel, URI individualURI, URI classURI)
	{
		return isOWLIndividualOfType(owlModel, individualURI.toString(), classURI.toString());
	}

	public static boolean isOWLIndividualOfType(OWLModel owlModel, String individualName, String className)
	{
		RDFResource cls = owlModel.getRDFResource(className);
		RDFResource individual = owlModel.getRDFResource(individualName);

		return (cls instanceof OWLNamedClass) && (individual instanceof OWLIndividual) && individual.hasRDFType((OWLNamedClass)cls, true);
	}

	public static boolean isOWLIndividualOfType(OWLModel owlModel, String individualName, OWLNamedClass cls)
	{
		RDFResource individual = owlModel.getRDFResource(individualName);

		return (individual instanceof OWLIndividual) && individual.hasRDFType(cls, true);
	}

	public static boolean isOWLIndividualOfDirectTypeOWLThing(OWLModel owlModel, String individualName)
	{
		RDFResource individual = owlModel.getRDFResource(individualName);

		return (individual instanceof OWLIndividual) && individual.hasRDFType(getOWLThingClass(individual.getOWLModel()), false);
	}

	public static void setType(OWLModel owlModel, String individualName, String className) throws P3OWLUtilException
	{
		OWLClass cls = getOWLClass(owlModel, className);
		OWLIndividual individual = getIndividual(owlModel, individualName);

		if (!individual.hasRDFType(cls, true))
			individual.setRDFType(cls);
	}

	public static void addObjectPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String objectName) throws P3OWLUtilException
	{
		RDFResource subject = null;
		OWLProperty property = getOWLProperty(owlModel, propertyName);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);
		else
			throw new P3OWLUtilException("invalid or unknown subject name " + subjectName + "; must be OWLClass or OWLIndividual");

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (objectName == null)
			throwException("null value for property " + propertyName + " for subject " + objectName);

		if (property.isObjectProperty()) {
			if (isOWLIndividual(owlModel, objectName)) {
				OWLIndividual objectIndividual = getOWLIndividual(owlModel, objectName);
				if (!subject.hasPropertyValue(property, objectIndividual))
					subject.addPropertyValue(property, objectIndividual);
			} else if (isOWLNamedClass(owlModel, objectName)) {
				OWLClass objectClass = getOWLNamedClass(owlModel, objectName);
				if (!subject.hasPropertyValue(property, objectClass))
					subject.addPropertyValue(property, objectClass);
			} else
				throw new P3OWLUtilException("invalid property value " + objectName + " for object property " + propertyName + " for subject " + subjectName
						+ "; value must be class or individual");

		} else
			throw new P3OWLUtilException("invalid property value " + objectName + " for object property " + propertyName + " for subject " + subjectName
					+ "; value must be a data property value");
	}

	public static void addStringDataPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String propertyValue) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName);
		RDFResource subject = null;

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (!subject.hasPropertyValue(property, propertyValue))
			subject.addPropertyValue(property, propertyValue);
	}

	public static void addDataPropertyValueWithLanguage(OWLModel owlModel, String subjectName, String propertyName, String propertyValue, String language)
		throws P3OWLUtilException
	{
		RDFResource subject = null;
		OWLProperty property = getOWLProperty(owlModel, propertyName);
		RDFSLiteral value = owlModel.createRDFSLiteral(propertyValue, language);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);
		else
			throw new P3OWLUtilException("invalid or unknown subject name " + subjectName + "; must be OWLClass or OWLIndividual");

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (!subject.hasPropertyValue(property, value))
			subject.addPropertyValue(property, value);
	}

	public static void addAnnotationObjectPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String propertyValue)
		throws P3OWLUtilException
	{
		RDFResource subject = null;
		RDFProperty property = getRDFProperty(owlModel, propertyName);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);
		else
			throw new P3OWLUtilException("invalid or unknown subject name " + subjectName + "; must be OWLClass or OWLIndividual");

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (isOWLIndividual(owlModel, propertyValue)) {
			OWLIndividual objectIndividual = getOWLIndividual(owlModel, propertyValue);
			if (!subject.hasPropertyValue(property, objectIndividual))
				subject.addPropertyValue(property, objectIndividual);
		} else if (isOWLNamedClass(owlModel, propertyValue)) {
			OWLClass objectClass = getOWLNamedClass(owlModel, propertyValue);
			if (!subject.hasPropertyValue(property, objectClass))
				subject.addPropertyValue(property, objectClass);
		} else
			throw new P3OWLUtilException("invalid object property value " + propertyValue + " for annotation property " + propertyName + " for subject "
					+ subjectName + "; value must be class or individual");
	}

	public static void addAnnotationPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String propertyValue) throws P3OWLUtilException
	{
		RDFResource subject = null;
		RDFProperty property = owlModel.getRDFProperty(propertyName);

		if (property == null)
			throw new P3OWLUtilException("unknown annotation property " + propertyName);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (isOWLIndividual(owlModel, propertyValue)) {
			OWLIndividual objectIndividual = getOWLIndividual(owlModel, propertyValue);
			if (!subject.hasPropertyValue(property, objectIndividual))
				subject.addPropertyValue(property, objectIndividual);
		} else if (isOWLNamedClass(owlModel, propertyValue)) {
			OWLClass objectClass = getOWLNamedClass(owlModel, propertyValue);
			if (!subject.hasPropertyValue(property, objectClass))
				subject.addPropertyValue(property, objectClass);
		} else {
			if (!subject.hasPropertyValue(property, propertyValue))
				subject.addPropertyValue(property, propertyValue);
		}
	}

	public static void addAnnotationStringDataPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String propertyValue)
		throws P3OWLUtilException
	{
		RDFResource subject = null;
		RDFProperty property = getRDFProperty(owlModel, propertyName);
		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (!subject.hasPropertyValue(property, propertyValue))
			subject.addPropertyValue(property, propertyValue);
	}

	public static void addRDFSLabelPropertyValueWithLanguage(OWLModel owlModel, String subjectName, String propertyValue, String language)
		throws P3OWLUtilException
	{
		addAnnotationDataPropertyValueWithLanguage(owlModel, subjectName, owlModel.getRDFSLabelProperty().getURI(), propertyValue, language);
	}

	public static void addAnnotationDataPropertyValueWithLanguage(OWLModel owlModel, String subjectName, String propertyName, String propertyValue,
																																String language) throws P3OWLUtilException
	{
		RDFResource subject = null;
		RDFProperty property = getRDFProperty(owlModel, propertyName);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);
		else
			throw new P3OWLUtilException("invalid or unknown subject name " + subjectName + "; must be OWLClass or OWLIndividual");

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (language == null || language.length() == 0) {
			String value = propertyValue;
			if (!subject.hasPropertyValue(property, value))
				subject.addPropertyValue(property, value);
		} else {
			RDFSLiteral value = owlModel.createRDFSLiteral(propertyValue, language);
			if (!subject.hasPropertyValue(property, value))
				subject.addPropertyValue(property, value);
		}
	}

	public static void addDataPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String propertyValue, String datatypeName)
		throws P3OWLUtilException
	{
		RDFResource subject = null;
		OWLProperty property = getOWLProperty(owlModel, propertyName);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);
		else
			throw new P3OWLUtilException("invalid or unknown subject name " + subjectName + "; must be OWLClass or OWLIndividual");

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		if (property.isObjectProperty())
			throw new P3OWLUtilException("attempt to assign data property with value " + propertyValue + " and type " + datatypeName + " to annotation property "
					+ propertyName + " on individual " + subjectName);

		RDFSDatatype datatype = getRDFSDatatype(owlModel, datatypeName);

		if (datatype == null)
			throw new P3OWLUtilException("invalid datatype name " + datatypeName);

		RDFSLiteral literal = owlModel.createRDFSLiteral(propertyValue, datatype);

		// Protege-OWL stores some RDFS literals in native form
		if (literal.getPlainValue() == null) {
			if (!subject.hasPropertyValue(property, literal))
				subject.addPropertyValue(property, literal);
		} else {
			if (!subject.hasPropertyValue(property, literal.getPlainValue()))
				subject.addPropertyValue(property, literal.getPlainValue());
		}
	}

	public static void addAnnotationDataPropertyValue(OWLModel owlModel, String subjectName, String propertyName, String propertyValue, String datatypeName)
		throws P3OWLUtilException
	{
		RDFResource subject = null;
		RDFProperty property = getRDFProperty(owlModel, propertyName);

		if (subjectName.startsWith("'"))
			subjectName = subjectName.substring(1, subjectName.length() - 1);

		if (isOWLIndividual(owlModel, subjectName))
			subject = getOWLIndividual(owlModel, subjectName);
		else if (isOWLClass(owlModel, subjectName))
			subject = getOWLClass(owlModel, subjectName);
		else
			throw new P3OWLUtilException("invalid or unknown subject name " + subjectName + "; must be OWLClass or OWLIndividual");

		if (subject == null)
			throwException("invalid or unknown subject name " + subjectName);
		if (property == null)
			throwException("invalid or unknown property name " + propertyName);
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for subject " + subjectName);

		RDFSDatatype datatype = getRDFSDatatype(owlModel, datatypeName);

		if (datatype == null)
			throw new P3OWLUtilException("invalid datatype name " + datatypeName);

		RDFSLiteral literal = owlModel.createRDFSLiteral(propertyValue, datatype);

		// Protege-OWL stores some RDFS literals in native form
		if (literal.getPlainValue() == null) {
			if (!subject.hasPropertyValue(property, literal))
				subject.addPropertyValue(property, literal);
		} else {
			if (!subject.hasPropertyValue(property, literal.getPlainValue()))
				subject.addPropertyValue(property, literal.getPlainValue());
		}
	}

	public static void addType(OWLModel owlModel, String resourceName, String className) throws P3OWLUtilException
	{
		OWLClass cls = getOWLClass(owlModel, className);
		RDFResource resource = null;

		if (cls == null)
			throw new P3OWLUtilException("could not find class: " + className);

		if (isOWLIndividual(owlModel, resourceName))
			resource = getOWLIndividual(owlModel, resourceName);
		else if (isOWLClass(owlModel, resourceName))
			resource = getOWLClass(owlModel, resourceName);
		else
			throw new P3OWLUtilException("invalid or unknown resource name " + resourceName + "; must be name ow OWL class or individual");

		if (!resource.hasProtegeType(cls, true))
			resource.addProtegeType(cls);
	}

	public static void addType(OWLModel owlModel, URI resourceURI, URI classURI) throws P3OWLUtilException
	{
		addType(owlModel, resourceURI.toString(), classURI.toString());
	}

	public static void addType(RDFResource resource, OWLClass cls)
	{
		if (!resource.hasRDFType(cls, true))
			resource.addRDFType(cls);
	}

	public static void removeType(OWLIndividual individual, OWLClass cls) throws P3OWLUtilException
	{
		if (individual.hasRDFType(cls, true))
			individual.removeRDFType(cls);
	}

	public static String getFullNameFromName(OWLModel owlModel, String prefixedName) throws P3OWLUtilException
	{
		String result = OWLUtil.getInternalFullName(owlModel, prefixedName, true);

		if (result == null)
			throw new P3OWLUtilException("cannot get full name for resource " + prefixedName);

		return result;
	}

	public static URI getURIFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		try {
			return new URI(OWLUtil.getInternalFullName(owlModel, name, true));
		} catch (URISyntaxException e) {
			throw new P3OWLUtilException("error creating URI from name " + name + ": " + e.getMessage());
		}
	}

	public static Set<String> getRDFSLabels(OWLModel owlModel, String resourceName)
	{
		return getRDFSLabels(owlModel, resourceName, "");
	}

	public static Set<String> getRDFSLabels(OWLModel owlModel, String resourceName, String language)
	{
		RDFResource resource = owlModel.getRDFResource(resourceName);
		Set<String> result = new HashSet<String>();

		if (resource != null) {
			for (Object label : resource.getPropertyValues(owlModel.getRDFSLabelProperty())) {
				if (label instanceof String)
					result.add((String)label);
				else if (label instanceof RDFSLiteral) {
					RDFSLiteral literal = (RDFSLiteral)label;
					if (language.length() == 0 || (language.equals(literal.getLanguage())))
						result.add(literal.getString());
				}
			}
		}
		return result;
	}

	public static Set<String> getRDFSLabels(OWLModel owlModel, URI resourceURI, String language)
	{
		return getRDFSLabels(owlModel, resourceURI.toString(), language);
	}

	public static Set<String> getRDFSLabelLanguages(OWLModel owlModel, String resourceName)
	{
		RDFResource resource = owlModel.getRDFResource(resourceName);
		Set<String> result = new HashSet<String>();

		if (resource != null) {
			for (Object label : resource.getPropertyValues(owlModel.getRDFSLabelProperty())) {
				if (label instanceof RDFSLiteral) {
					RDFSLiteral literal = (RDFSLiteral)label;
					result.add(literal.getLanguage());
				}
			}
		}
		return result;
	}

	public static Set<String> getRDFSLabelLanguages(OWLModel owlModel, URI resourceURI)
	{
		return getRDFSLabelLanguages(owlModel, resourceURI.toString());
	}

	public static void addRDFSLabel(RDFResource resource, String labelText, String language)
	{
		if (!hasRDFSLabel(resource, labelText, language))
			resource.addLabel(labelText, language);
	}

	public static boolean hasRDFSLabel(RDFResource resource, String labelText, String language)
	{
		for (Object o : resource.getLabels()) { // First see if we already have this label.
			if (o instanceof String) { // No language associated with label value
				String s = (String)o;
				if ((language == null || language.length() == 0) && s.equals(labelText))
					return true;
			} else if (o instanceof RDFSLiteral) { // Value and language
				RDFSLiteral literal = (RDFSLiteral)o;
				if (literal.getString().equals(labelText) && literal.getLanguage().equals(language))
					return true;
			}
		}
		return false;
	}

	public static boolean hasRDFSLabelIgnoringLanguage(RDFResource resource, String labelText)
	{
		for (Object o : resource.getLabels()) { // First see if we already have this label.
			if (o instanceof String) { // No language associated with label value
				String s = (String)o;
				if (s.equals(labelText))
					return true;
			} else if (o instanceof RDFSLiteral) { // Value and language
				RDFSLiteral literal = (RDFSLiteral)o;
				if (literal.getString().equals(labelText))
					return true;
			}
		}
		return false;
	}

	public static OWLSomeValuesFrom getOWLSomeValuesFrom(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return (OWLSomeValuesFrom)owlModel.getOWLSomeValuesFromRestrictionClass().createInstance(classURI.toString());
	}

	public static OWLIndividual getOWLIndividual(OWLNamedClass cls, boolean mustExist, int mustHaveExactlyN) throws P3OWLUtilException
	{
		Collection<?> instances;
		Object firstInstance;

		if (mustExist && cls.getInstanceCount(true) == 0)
			throwException("no individuals of class " + cls.getPrefixedName() + " in ontology");
		else if (cls.getInstanceCount(true) != mustHaveExactlyN)
			throwException("expecting exactly " + mustHaveExactlyN + " individuals of class " + cls.getPrefixedName() + " in ontology - got "
					+ cls.getInstanceCount(true) + "");

		instances = cls.getInstances(true);

		if (!instances.isEmpty()) {
			firstInstance = cls.getInstances(true).iterator().next();

			if (firstInstance instanceof OWLIndividual)
				return (OWLIndividual)firstInstance;
			else
				throw new P3OWLUtilException("instance of class " + cls.getPrefixedName() + " is not an OWL individual");
		} else
			return null;
	}

	public static Set<OWLIndividual> getAllOWLIndividuals(OWLModel owlModel) throws P3OWLUtilException
	{
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

		for (Object o : owlModel.getOWLIndividuals()) {
			if (o instanceof OWLIndividual)
				individuals.add((OWLIndividual)o);
		}

		return individuals;
	}

	public static Set<OWLIndividual> getOWLIndividualsOfClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		OWLNamedClass cls = getNamedClass(owlModel, className);

		return new HashSet<OWLIndividual>(getOWLIndividualsOfClass(cls));
	}

	public static Set<OWLIndividual> getOWLIndividualsOfClass(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getOWLIndividualsOfClass(owlModel, classURI.toString());
	}

	public static Set<OWLIndividual> getOWLIndividualsOfClass(OWLNamedClass cls) throws P3OWLUtilException
	{
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

		for (Object o : cls.getInstances(true)) {
			if (o instanceof OWLIndividual)
				individuals.add((OWLIndividual)o);
		}

		return individuals;
	}

	public static OWLProperty getProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return getOWLProperty(owlModel, propertyName, true);
	}

	public static OWLDatatypeProperty getOWLDataProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return getOWLDataProperty(owlModel, propertyName, true);
	}

	public static OWLDatatypeProperty getOWLDataProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLDatatypeProperty property = owlModel.getOWLDatatypeProperty(propertyName);
		if (mustExist && property == null)
			throwException("no " + propertyName + " datatype property in ontology");

		return property;
	}

	public static OWLObjectProperty getOWLObjectProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLObjectProperty property = owlModel.getOWLObjectProperty(propertyName);

		if (mustExist && property == null)
			throwException("no " + propertyName + " object property in ontology");

		return property;
	}

	public static OWLObjectProperty getOWLObjectProperty(OWLModel owlModel, String propertyName)
	{
		if (isOWLObjectProperty(owlModel, propertyName))
			return owlModel.getOWLObjectProperty(propertyName);
		else
			return null;
	}

	public static OWLObjectProperty getOWLObjectProperty(OWLModel owlModel, URI propertyURI)
	{
		return getOWLObjectProperty(owlModel, propertyURI.toString());
	}

	public static OWLDatatypeProperty getOWLDatatypeProperty(OWLModel owlModel, String propertyName)
	{
		if (isOWLDataProperty(owlModel, propertyName))
			return owlModel.getOWLDatatypeProperty(propertyName);
		else
			return null;
	}

	public static OWLDatatypeProperty getOWLDatatypeProperty(OWLModel owlModel, URI propertyURI)
	{
		return getOWLDatatypeProperty(owlModel, propertyURI.toString());
	}

	public static boolean isOWLEquivalentProperty(OWLModel owlModel, String propertyName1, String propertyName2, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property1 = getOWLProperty(owlModel, propertyName1, mustExist);
		OWLProperty property2 = getOWLProperty(owlModel, propertyName2, mustExist);

		return (property1 != null && property2 != null && property1.getEquivalentProperties().contains(property2));
	}

	public static boolean isOWLEquivalentClass(OWLModel owlModel, String className1, String className2, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass class1 = getOWLNamedClass(owlModel, className1, mustExist);
		OWLNamedClass class2 = getOWLNamedClass(owlModel, className2, mustExist);

		return (class1 != null && class2 != null && class1.hasEquivalentClass(class2));
	}

	public static boolean isOWLDisjointClass(OWLModel owlModel, String className1, String className2, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass class1 = getOWLNamedClass(owlModel, className1, mustExist);
		OWLNamedClass class2 = getOWLNamedClass(owlModel, className2, mustExist);

		return (class1 != null && class2 != null && class1.getDisjointClasses().contains(class2));
	}

	public static boolean isOWLSubPropertyOf(OWLModel owlModel, String subPropertyName, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty subProperty = getOWLProperty(owlModel, subPropertyName, mustExist);
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (subProperty != null && property != null && subProperty.isSubpropertyOf(property, true));
	}

	public static boolean isOWLSubPropertyOf(OWLModel owlModel, URI subPropertyURI, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLSubPropertyOf(owlModel, subPropertyURI.toString(), propertyURI.toString(), mustExist);
	}

	public static boolean isOWLSuperPropertyOf(OWLModel owlModel, String superPropertyName, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty superProperty = getOWLProperty(owlModel, superPropertyName, mustExist);
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (superProperty != null && property != null && property.isSubpropertyOf(superProperty, true)); // No isSuperpropertyOf call
	}

	public static boolean isOWLSuperPropertyOf(OWLModel owlModel, URI superPropertyURI, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLSuperPropertyOf(owlModel, superPropertyURI.toString(), propertyURI.toString(), mustExist);
	}

	public static boolean isOWLDirectSuperPropertyOf(OWLModel owlModel, String superPropertyName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		OWLProperty superProperty = getOWLProperty(owlModel, superPropertyName, mustExist);
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (superProperty != null && property != null && property.isSubpropertyOf(superProperty, false)); // No isSuperpropertyOf call
	}

	public static boolean isOWLDirectSuperPropertyOf(OWLModel owlModel, URI superPropertyURI, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLDirectSuperPropertyOf(owlModel, superPropertyURI.toString(), propertyURI.toString(), mustExist);
	}

	public static boolean isOWLDirectSubPropertyOf(OWLModel owlModel, String subPropertyName, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty subProperty = getOWLProperty(owlModel, subPropertyName, mustExist);
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (subProperty != null && property != null && subProperty.isSubpropertyOf(property, false));
	}

	public static boolean isOWLDirectSubPropertyOf(OWLModel owlModel, URI subPropertyURI, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLDirectSubPropertyOf(owlModel, subPropertyURI.toString(), propertyURI.toString(), mustExist);
	}

	public static boolean isOWLDirectSubClassOf(OWLModel owlModel, String subClassName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass subClass = getOWLNamedClass(owlModel, subClassName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (subClass != null && cls != null && subClass.isSubclassOf(cls));
	}

	public static boolean isOWLDirectSubClassOf(OWLModel owlModel, URI subClassURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLDirectSubClassOf(owlModel, subClassURI.toString(), classURI.toString(), mustExist);
	}

	public static boolean isOWLSubClassOf(OWLModel owlModel, String subClassName, String superClassName, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass subClass = getOWLNamedClass(owlModel, subClassName, mustExist);
		OWLNamedClass superClass = getOWLNamedClass(owlModel, superClassName, mustExist);

		return isOWLSubClassOf(subClass, superClass);
	}

	public static boolean isOWLSubClassOf(OWLClass subClass, OWLClass superClass)
	{
		return (subClass != null && superClass != null && subClass.getSuperclasses(true).contains(superClass));
	}

	public static boolean isOWLSubClassOf(OWLModel owlModel, URI subClassURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLSubClassOf(owlModel, subClassURI.toString(), classURI.toString(), mustExist);
	}

	public static void addOWLSuperClass(OWLModel owlModel, String subClassName, String superClassName) throws P3OWLUtilException
	{
		OWLClass subClass = getOWLClass(owlModel, subClassName);
		OWLClass superClass = getOWLClass(owlModel, superClassName);

		addOWLSuperClass(subClass, superClass);
	}

	public static void addOWLSuperClass(OWLModel owlModel, URI subClassURI, URI superClassURI) throws P3OWLUtilException
	{
		addOWLSuperClass(owlModel, subClassURI.toString(), superClassURI.toString());
	}

	public static void addOWLSuperClass(OWLClass subClass, OWLClass superClass)
	{
		if (!isOWLSubClassOf(subClass, superClass))
			subClass.addSuperclass(superClass);
	}

	public static void addRDFSSuperClass(RDFSClass subClass, RDFSClass superClass)
	{
		subClass.addSuperclass(superClass);
	}

	public static boolean isOWLDirectSuperClassOf(OWLModel owlModel, String superClassName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass superClass = getOWLNamedClass(owlModel, superClassName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (superClass != null && cls != null && cls.isSubclassOf(superClass)); // No isSuperclassOf call
	}

	public static boolean isOWLDirectSuperClassOf(OWLModel owlModel, URI superClassURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLDirectSuperClassOf(owlModel, superClassURI.toString(), classURI.toString(), mustExist);
	}

	public static boolean isOWLSuperClassOf(OWLModel owlModel, String superClassName, String className) throws P3OWLUtilException
	{
		return isOWLSuperClassOf(owlModel, superClassName, className, true);
	}

	public static boolean isOWLSuperClassOf(OWLModel owlModel, String superClassName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass superClass = getOWLNamedClass(owlModel, superClassName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (superClass != null && cls != null && cls.getSuperclasses(true).contains(superClass));
	}

	public static boolean isOWLSuperClassOf(OWLModel owlModel, URI superClassURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLSuperClassOf(owlModel, superClassURI.toString(), classURI.toString(), mustExist);
	}

	public static int getNumberOfOWLIndividualsOfClass(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		return getNumberOfOWLIndividualsOfClass(owlModel, className, true);
	}

	public static int getNumberOfOWLIndividualsOfClass(OWLModel owlModel, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);
		int numberOfIndividuals = 0;

		if (cls != null)
			numberOfIndividuals = cls.getInstances(true).size();

		return numberOfIndividuals;
	}

	public static int getNumberOfDirectOWLInstancesOfClass(OWLModel owlModel, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);
		int numberOfIndividuals = 0;

		if (cls != null)
			numberOfIndividuals = cls.getInstances(false).size();

		return numberOfIndividuals;
	} // getNumberOfDirectInstancesOfClass

	public static boolean isConsistentOWLClass(OWLModel owlModel, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return cls != null && cls.isConsistent();
	}

	public static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return getOWLDomainClasses(owlModel, propertyName, true, true);
	}

	public static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getOWLDomainClasses(owlModel, propertyURI.toString());
	}

	public static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		return getOWLDomainClasses(owlModel, propertyName, mustExist, true);
	}

	public static Set<OWLNamedClass> getDirectOWLDomainClasses(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return getOWLDomainClasses(owlModel, propertyName, true, false);
	}

	public static Set<OWLNamedClass> getDirectOWLDomainClasses(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getDirectOWLDomainClasses(owlModel, propertyURI.toString());
	}

	public static Set<OWLNamedClass> getDirectOWLDomainClasses(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		return getOWLDomainClasses(owlModel, propertyName, mustExist, false);
	}

	private static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, String propertyName, boolean mustExist, boolean includingSuperproperties)
		throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
		Collection<?> domainClasses = property.getUnionDomain(includingSuperproperties);

		if (domainClasses == null)
			return result;

		Iterator<?> iterator = domainClasses.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof OWLNamedClass)
				result.add((OWLNamedClass)o);
		}

		return result;
	}

	public static void removeOWLThingSuperclass(OWLModel owlModel, OWLClass owlClass)
	{
		if (owlClass.getSuperclasses(false).contains(getOWLThingClass(owlModel)))
			owlClass.removeSuperclass(getOWLThingClass(owlModel));
	}

	public static void removeOWLThingSuperclass(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		removeOWLThingSuperclass(owlModel, getOWLClass(owlModel, classURI));
	}

	public static Set<OWLNamedClass> getOWLRangeClasses(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return getOWLRangeClasses(owlModel, propertyName, true);
	}

	public static Set<OWLNamedClass> getOWLRangeClasses(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getOWLRangeClasses(owlModel, propertyURI.toString());
	}

	public static Set<OWLNamedClass> getOWLDirectRangeClasses(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		return getOWLRangeClasses(owlModel, propertyName, mustExist);
	}

	public static Set<OWLNamedClass> getOWLDirectRangeClasses(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return getOWLRangeClasses(owlModel, propertyName, true);
	}

	public static Set<OWLNamedClass> getOWLDirectRangeClasses(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getOWLDirectRangeClasses(owlModel, propertyURI.toString());
	}

	private static Set<OWLNamedClass> getOWLRangeClasses(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
		Collection<?> rangeClasses = property.getUnionRangeClasses(); // TODO: no includingSuperproperties argument supported
		Iterator<?> iterator;

		if (rangeClasses == null)
			return result;

		iterator = rangeClasses.iterator();
		while (iterator.hasNext()) {
			RDFResource resource = (RDFResource)iterator.next();
			if (resource instanceof OWLNamedClass)
				result.add((OWLNamedClass)resource);
		}

		return result;
	}

	public static boolean isInOWLPropertyDomain(OWLModel owlModel, String propertyName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (property != null && cls != null && property.getDomains(true).contains(cls));
	}

	public static boolean isInOWLPropertyDomain(OWLModel owlModel, URI propertyURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isInOWLPropertyDomain(owlModel, propertyURI.toString(), classURI.toString(), mustExist);
	}

	public static boolean isInDirectOWLPropertyDomain(OWLModel owlModel, String propertyName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (property != null && cls != null && property.getDomains(false).contains(cls));
	}

	public static boolean isInDirectOWLPropertyDomain(OWLModel owlModel, URI propertyURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isInDirectOWLPropertyDomain(owlModel, propertyURI.toString(), classURI.toString(), mustExist);
	}

	public static boolean isInPropertyRange(OWLModel owlModel, String propertyName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (property != null && cls != null && property.getRanges(true).contains(cls));
	}

	public static boolean isInPropertyRange(OWLModel owlModel, URI propertyURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isInPropertyRange(owlModel, propertyURI.toString(), classURI.toString(), mustExist);
	}

	public static boolean isInDirectPropertyRange(OWLModel owlModel, String propertyName, String className, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		OWLNamedClass cls = getOWLNamedClass(owlModel, className, mustExist);

		return (property != null && cls != null && property.getRanges(false).contains(cls));
	}

	public static boolean isInDirectPropertyRange(OWLModel owlModel, URI propertyURI, URI classURI, boolean mustExist) throws P3OWLUtilException
	{
		return isInDirectPropertyRange(owlModel, propertyURI.toString(), classURI.toString(), mustExist);
	}

	public static boolean isOWLObjectProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (property != null && property.isObjectProperty());
	}

	public static boolean isOWLObjectProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLObjectProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static boolean isOWLIndividual(OWLModel owlModel, String individualName)
	{
		RDFResource resource = owlModel.getRDFResource(individualName);

		return (resource != null && resource instanceof OWLIndividual);
	}

	public static boolean isOWLIndividual(OWLModel owlModel, URI individualURI)
	{
		return isOWLIndividual(owlModel, individualURI.toString());
	}

	public static boolean isOWLObjectProperty(OWLModel owlModel, String propertyName)
	{
		RDFResource resource = owlModel.getRDFResource(propertyName);

		return resource instanceof OWLObjectProperty;
	}

	public static boolean isOWLObjectProperty(OWLModel owlModel, URI propertyURI)
	{
		return isOWLObjectProperty(owlModel, propertyURI.toString());
	}

	public static boolean isOWLDataProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (property != null && !property.isObjectProperty());
	}

	public static boolean isOWLDataProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLDataProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static boolean hasXSDStringRange(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		if (isOWLDataProperty(owlModel, propertyName)) {
			OWLDatatypeProperty property = getOWLDataProperty(owlModel, propertyName);
			return property.getRange() == owlModel.getXSDstring();
		} else
			return false;
	}

	public static boolean isOWLDataProperty(OWLModel owlModel, String propertyName)
	{
		RDFResource resource = owlModel.getRDFResource(propertyName);

		return resource instanceof OWLDatatypeProperty;
	}

	public static boolean isOWLDatatype(OWLModel owlModel, String datatypeName)
	{
		RDFSDatatype datatype = owlModel.getRDFSDatatypeByName(datatypeName);
		return datatype != null;
	}

	public static boolean isOWLDatatype(OWLModel owlModel, URI datatypeURI)
	{
		return isOWLDatatype(owlModel, datatypeURI);
	}

	public static boolean isOWLDataProperty(OWLModel owlModel, URI propertyURI)
	{
		return isOWLDataProperty(owlModel, propertyURI.toString());
	}

	public static boolean isOWLTransitiveProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isTransitive());
	}

	public static boolean isOWLTransitiveProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		return isOWLTransitiveProperty(owlModel, propertyName, true);
	}

	public static boolean isOWLTransitiveProperty(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return isOWLTransitiveProperty(owlModel, propertyURI.toString());
	}

	public static boolean isOWLSymmetricProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isSymmetric());
	}

	public static boolean isOWLSymmetricProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLSymmetricProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static boolean isOWLFunctionalProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return property != null && property.isFunctional();
	}

	public static boolean isOWLFunctionalProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLFunctionalProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static boolean isAnnotationProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return property != null && property.isAnnotationProperty();
	}

	public static boolean isAnnotationProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isAnnotationProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static boolean isAnnotationProperty(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, true);

		return property != null && property.isAnnotationProperty();
	}

	public static boolean isInverseFunctionalProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		return property != null && property.isInverseFunctional();
	}

	public static boolean isInverseFunctionalProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isInverseFunctionalProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static boolean isAnonymousResourceName(OWLModel owlModel, String resourceName) throws P3OWLUtilException
	{
		return owlModel.isAnonymousResourceName(resourceName);
	}

	public static boolean isAnonymousResourceName(OWLModel owlModel, URI resourceURI) throws P3OWLUtilException
	{
		return isAnonymousResourceName(owlModel, resourceURI.toString());
	}

	public static OWLIndividual getIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		return getOWLIndividual(owlModel, individualName, true);
	}

	public static OWLIndividual getIndividual(OWLModel owlModel, URI individualURI) throws P3OWLUtilException
	{
		return getIndividual(owlModel, individualURI.toString());
	}

	public static OWLIndividual getOWLIndividual(OWLModel owlModel, String individualName, boolean mustExist) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(individualName);

		if (mustExist && (resource == null || !(resource instanceof OWLIndividual)))
			throwException("no individual named " + individualName + " in ontology");

		if (resource != null) {
			if (resource instanceof OWLIndividual)
				return (OWLIndividual)resource;
			else
				return null;
		} else
			return null;
	}

	public static OWLNamedClass getOWLNamedClass(OWLModel owlModel, String className, boolean mustExist) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(className);

		if (mustExist && (resource == null || !(resource instanceof OWLNamedClass)))
			throwException("no class named " + className + " in ontology");

		if (resource != null) {
			if (resource instanceof OWLNamedClass)
				return (OWLNamedClass)resource;
			else
				return null;
		} else
			return null;
	}

	public static OWLClass getOWLClass(OWLModel owlModel, String className, boolean mustExist) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(className);

		if (mustExist && (resource == null || !(resource instanceof OWLClass)))
			throwException("no class named " + className + " in ontology");

		if (resource != null) {
			if (resource instanceof OWLClass)
				return (OWLClass)resource;
			else
				return null;
		} else
			return null;
	}

	public static Set<OWLNamedClass> getOWLClassesOfIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		return getOWLClassesOfIndividual(owlModel, individualName, true);
	}

	public static Set<OWLNamedClass> getOWLNamedClasses(OWLModel owlModel)
	{
		Set<OWLNamedClass> classes = new HashSet<OWLNamedClass>();

		for (Object o : owlModel.getRDFSClasses()) {
			if (o instanceof OWLNamedClass) {
				classes.add((OWLNamedClass)o);
			}
		}
		return classes;
	}

	public static Set<OWLClass> getOWLClasses(OWLModel owlModel)
	{
		Set<OWLClass> classes = new HashSet<OWLClass>();

		for (Object o : owlModel.getRDFSClasses()) {
			if (o instanceof OWLClass) {
				classes.add((OWLClass)o);
			}
		}
		return classes;
	}

	public static Set<OWLIndividual> getOWLIndividuals(OWLModel owlModel)
	{
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

		for (Object o : owlModel.getOWLIndividuals()) {
			if (o instanceof OWLIndividual) {
				OWLIndividual owlIndividual = (OWLIndividual)o;
				if (!owlIndividual.isAnonymous())
					individuals.add(owlIndividual);
			}
		}
		return individuals;
	}

	public static Set<OWLObjectProperty> getOWLObjectProperties(OWLModel owlModel)
	{
		Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();

		for (Object o : owlModel.getRDFProperties()) {
			if (o instanceof OWLObjectProperty) {
				OWLObjectProperty property = (OWLObjectProperty)o;
				if (!property.isAnnotationProperty())
					properties.add((OWLObjectProperty)o);
			}
		}
		return properties;
	}

	public static Set<OWLDatatypeProperty> getOWLDatatypeProperties(OWLModel owlModel)
	{
		Set<OWLDatatypeProperty> properties = new HashSet<OWLDatatypeProperty>();

		for (Object o : owlModel.getRDFProperties()) {
			if (o instanceof OWLDatatypeProperty) {
				OWLDatatypeProperty property = (OWLDatatypeProperty)o;
				if (!property.isAnnotationProperty())
					properties.add((OWLDatatypeProperty)o);
			}
		}
		return properties;
	}

	public static Set<RDFProperty> getOWLAnnotationProperties(OWLModel owlModel)
	{
		Set<RDFProperty> properties = new HashSet<RDFProperty>();

		for (RDFProperty property : owlModel.getOWLAnnotationProperties())
			properties.add(property);

		return properties;
	}

	public static Set<OWLNamedClass> getOWLClassesOfIndividual(OWLModel owlModel, URI individualURI) throws P3OWLUtilException
	{
		return getOWLClassesOfIndividual(owlModel, individualURI.toString());
	}

	public static Set<OWLNamedClass> getOWLClassesOfIndividual(OWLModel owlModel, String individualName, boolean mustExist) throws P3OWLUtilException
	{
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, mustExist);

		return individual == null ? new HashSet<OWLNamedClass>() : getClassesOfIndividual(individual);
	}

	public static Set<OWLNamedClass> getClassesOfIndividual(OWLIndividual individual) throws P3OWLUtilException
	{
		Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
		Collection<?> types = individual.getRDFTypes();
		Iterator<?> iterator = types.iterator();
		while (iterator.hasNext()) {
			RDFResource resource = (RDFResource)iterator.next();
			if (resource instanceof OWLNamedClass)
				result.add((OWLNamedClass)resource); // Ignore anonymous classes
		}

		return result;
	}

	public static String getNextAnonymousResourceName(OWLModel owlModel)
	{
		return owlModel.getNextAnonymousResourceName();
	}

	public static URI getNextAnonymousResourceURI(OWLModel owlModel) throws P3OWLUtilException
	{
		try {
			return new URI(owlModel.getNextAnonymousResourceName());
		} catch (URISyntaxException e) {
			throw new P3OWLUtilException("error getting an anonymous resource URI: " + e.getMessage());
		}
	}

	public static boolean isOWLNamedEntity(OWLModel owlModel, String name)
	{
		return isOWLNamedClass(owlModel, name) || isOWLProperty(owlModel, name) || isOWLIndividual(owlModel, name);
	}

	public static boolean isOWLNamedClass(OWLModel owlModel, String className, boolean mustExist) throws P3OWLUtilException
	{
		return (getOWLNamedClass(owlModel, className, mustExist) != null);
	}

	public static boolean isOWLProperty(OWLModel owlModel, String propertyName)
	{
		RDFResource resource = owlModel.getRDFResource(propertyName);

		return resource instanceof OWLProperty;
	}

	public static boolean isOWLProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		return (getOWLProperty(owlModel, propertyName, mustExist) != null);
	}

	public static boolean isOWLProperty(OWLModel owlModel, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return isOWLProperty(owlModel, propertyURI.toString(), mustExist);
	}

	public static Collection<OWLNamedClass> getUserDefinedOWLNamedClasses(OWLModel owlModel)
	{
		Set<OWLNamedClass> classes = new HashSet<OWLNamedClass>();

		for (Object o : owlModel.getUserDefinedOWLNamedClasses()) {
			if (o instanceof OWLNamedClass)
				classes.add((OWLNamedClass)o);
		}

		return classes;
	}

	public static Collection<OWLProperty> getUserDefinedOWLProperties(OWLModel owlModel)
	{
		Set<OWLProperty> properties = new HashSet<OWLProperty>();

		for (Object o : owlModel.getUserDefinedOWLProperties()) {
			if (o instanceof OWLProperty)
				properties.add((OWLProperty)o);
		}

		return properties;
	}

	public static Collection<OWLProperty> getUserDefinedOWLObjectProperties(OWLModel owlModel)
	{
		Set<OWLProperty> properties = new HashSet<OWLProperty>();

		for (Object o : owlModel.getUserDefinedOWLObjectProperties()) {
			if (o instanceof OWLProperty)
				properties.add((OWLProperty)o);
		}

		return properties;
	}

	public static Collection<OWLProperty> getUserDefinedOWLDatatypeProperties(OWLModel owlModel)
	{
		Set<OWLProperty> properties = new HashSet<OWLProperty>();

		for (Object o : owlModel.getUserDefinedOWLDatatypeProperties()) {
			if (o instanceof OWLProperty)
				properties.add((OWLProperty)o);
		}

		return properties;
	}

	public static boolean isIndividual(OWLModel owlModel, String individualName, boolean mustExist) throws P3OWLUtilException
	{
		return (getOWLIndividual(owlModel, individualName, mustExist) != null);
	}

	public static boolean isSWRLVariable(OWLModel owlModel, String individualName, boolean mustExist) throws P3OWLUtilException
	{
		return (getOWLIndividual(owlModel, individualName, mustExist) != null && getOWLIndividual(owlModel, individualName, mustExist) instanceof SWRLVariable);
	}

	public static boolean isIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		return (getOWLIndividual(owlModel, individualName, false) != null);
	}

	public static boolean isIndividual(OWLModel owlModel, URI individualURI) throws P3OWLUtilException
	{
		return isIndividual(owlModel, individualURI.toString());
	}

	public static int getNumberOfPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.

		return individual.getPropertyValues(property).size();
	}

	public static int getNumberOfPropertyValues(OWLModel owlModel, URI individualURI, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return getNumberOfPropertyValues(owlModel, individualURI.toString(), propertyURI.toString(), mustExist);
	}

	public static Set<OWLProperty> getOWLPropertiesOfIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, true);
		HashSet<OWLProperty> properties = new HashSet<OWLProperty>();
		Collection<?> rdfProperties = individual.getRDFProperties();

		Iterator<?> iterator = rdfProperties.iterator();
		while (iterator.hasNext()) {
			RDFProperty property = (RDFProperty)iterator.next();
			if (property instanceof OWLProperty)
				properties.add((OWLProperty)property);
		}

		return properties;
	}

	public static Set<OWLProperty> getOWLPropertiesOfIndividual(OWLModel owlModel, URI individualURI) throws P3OWLUtilException
	{
		return getOWLPropertiesOfIndividual(owlModel, individualURI.toString());
	}

	public static Set<OWLProperty> getPossibleOWLPropertiesOfIndividual(OWLModel owlModel, String individualName) throws P3OWLUtilException
	{
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, true);
		HashSet<OWLProperty> properties = new HashSet<OWLProperty>();
		Collection<?> rdfProperties = individual.getPossibleRDFProperties();

		Iterator<?> iterator = rdfProperties.iterator();
		while (iterator.hasNext()) {
			RDFProperty property = (RDFProperty)iterator.next();
			if (property instanceof OWLProperty)
				properties.add((OWLProperty)property);
		}

		return properties;
	}

	public static String getURI(OWLModel owlModel, String resourceName) throws P3OWLUtilException
	{
		RDFResource resource = owlModel.getRDFResource(resourceName);

		if (resource == null)
			throwException("invalid or unknown resource " + resourceName);

		return resource.getURI();
	}

	public static int getNumberOfOWLPropertyValues(OWLModel owlModel, String individualName, String propertyName, Object propertyValue, boolean mustExist)
		throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true
		int numberOfPropertyValues = 0;

		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for OWL individual " + individualName);

		for (Object value : individual.getPropertyValues(property)) {
			if (value instanceof RDFResource) {
				RDFResource resource = (RDFResource)value;
				String name = resource.getName();
				if (name.equals(propertyValue))
					numberOfPropertyValues++;
			} else {
				if (value.equals(propertyValue))
					numberOfPropertyValues++;
			}
		}

		return numberOfPropertyValues;
	}

	public static void addOWLPropertyValue(OWLModel owlModel, String resourceName, String propertyName, Object propertyValue) throws P3OWLUtilException
	{
		OWLProperty property = owlModel.getOWLProperty(propertyName);
		RDFResource resource = owlModel.getRDFResource(resourceName);

		if (resource == null)
			throwException("invalid or unknown resource name " + resourceName);
		if (property == null)
			throwException("no " + propertyName + " property in ontology");
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for RDF resource " + resource.getPrefixedName());

		if (!resource.hasPropertyValue(property, propertyValue))
			resource.addPropertyValue(property, propertyValue);
	}

	public static void addOWLPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, Object propertyValue) throws P3OWLUtilException
	{
		OWLProperty property = owlModel.getOWLProperty(propertyName);

		if (individual == null)
			throwException("null individual name");
		if (property == null)
			throwException("no " + propertyName + " property in ontology");
		if (propertyValue == null)
			throwException("null value for property " + propertyName + " for OWL individual " + individual.getPrefixedName());

		if (!individual.hasPropertyValue(property, propertyValue))
			individual.addPropertyValue(property, propertyValue);
	}

	public static void addPropertyValue(OWLIndividual individual, OWLProperty property, Object propertyValue) throws P3OWLUtilException
	{
		if (individual == null)
			throwException("null individual name");
		if (property == null)
			throwException("null property for individual " + individual.getPrefixedName());
		if (propertyValue == null)
			throwException("null value for property " + property.getPrefixedName() + " for OWL individual " + individual.getPrefixedName());

		if (!individual.hasPropertyValue(property, propertyValue))
			individual.addPropertyValue(property, propertyValue);
	}

	public static RDFResource getObjectPropertyValue(OWLIndividual individual, OWLProperty property) throws P3OWLUtilException
	{
		Object o = individual.getPropertyValue(property);

		if (!(o instanceof RDFResource))
			throw new P3OWLUtilException("value " + o + " of object property " + property.getPrefixedName() + " associated with individual "
					+ individual.getPrefixedName() + " is not a valid object value");

		return (RDFResource)o;
	}

	public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getObjectPropertyValues(owlModel, individualName, propertyName, true);
	}

	public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName) throws P3OWLUtilException
	{
		return getObjectPropertyValues(owlModel, individual.getName(), propertyName, true);
	}

	public static Set<OWLIndividual> getOWLObjectPropertyIndividualValues(OWLModel owlModel, OWLIndividual individual, String propertyName,
																																				String expectedInstanceClassName) throws P3OWLUtilException
	{
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

		for (RDFResource value : getObjectPropertyValues(owlModel, individual, propertyName)) {
			if (!(value instanceof OWLIndividual))
				throw new P3OWLUtilException("value " + value + " for property " + propertyName + " associated with individual " + individual.getPrefixedName()
						+ " is not an OWL individual");

			OWLIndividual individualValue = (OWLIndividual)value;
			if (!isOWLIndividualOfClass(owlModel, individualValue, expectedInstanceClassName))
				throw new P3OWLUtilException("object " + individual.getPrefixedName() + " value for property " + propertyName + " associated with individual "
						+ individual.getPrefixedName() + " is not of type " + expectedInstanceClassName);

			individuals.add(individualValue);
		}
		return individuals;
	}

	public static OWLIndividual getOWLObjectPropertyIndividualValue(OWLModel owlModel, OWLIndividual individual, String propertyName,
																																	String expectedInstanceClassName) throws P3OWLUtilException
	{
		RDFResource value = getObjectPropertyValue(owlModel, individual, propertyName);

		if (!(value instanceof OWLIndividual))
			throw new P3OWLUtilException("invalid value for " + propertyName + " property associated with individual " + individual.getPrefixedName() + "; found "
					+ value + ", expecting individual");
		OWLIndividual individualValue = (OWLIndividual)value;
		if (!isOWLIndividualOfClass(owlModel, individualValue, expectedInstanceClassName))
			throw new P3OWLUtilException("object " + individualValue.getPrefixedName() + " value for property " + propertyName + " associated with individual "
					+ individual.getPrefixedName() + " is not of type " + expectedInstanceClassName);

		return individualValue;
	}

	public static OWLProperty getOWLObjectPropertyPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName) throws P3OWLUtilException
	{
		RDFResource value = getObjectPropertyValue(owlModel, individual, propertyName);

		if (!(value instanceof OWLProperty))
			throw new P3OWLUtilException("invalid type for " + propertyName + " property associated with individual " + individual.getPrefixedName() + "; found "
					+ value + ", expecting property");

		return (OWLProperty)value;
	}

	public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getObjectPropertyValues(owlModel, individual.getName(), propertyName, mustExist);
	}

	public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
		Set<RDFResource> result = new HashSet<RDFResource>();

		if (individual != null && property != null) {
			Iterator<?> iterator = individual.getPropertyValues(property).iterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (o instanceof RDFResource)
					result.add((RDFResource)o);
			}
		}
		return result;
	}

	public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, URI individualURI, URI propertyURI, boolean mustExist) throws P3OWLUtilException
	{
		return getObjectPropertyValues(owlModel, individualURI.toString(), propertyURI.toString(), mustExist);
	}

	public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, URI individualURI, URI propertyURI) throws P3OWLUtilException
	{
		return getObjectPropertyValues(owlModel, individualURI, propertyURI, true);
	}

	public static RDFResource getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName) throws P3OWLUtilException
	{
		return getObjectPropertyValue(owlModel, individual, propertyName, true);
	}

	public static RDFResource getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getObjectPropertyValue(owlModel, individual.getName(), propertyName, mustExist);
	}

	public static RDFResource getObjectPropertyValue(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getObjectPropertyValue(owlModel, individualName, propertyName, true);
	}

	public static RDFResource getObjectPropertyValue(OWLModel owlModel, String individualName, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
		OWLProperty property = getOWLProperty(owlModel, propertyName, true);
		Object propertyValue = (property == null && individual == null ? null : individual.getPropertyValue(property));

		if (mustExist && property == null) {
			throwException("no property " + propertyName + " associated with individual " + individual.getPrefixedName());
		}

		if (!(propertyValue instanceof RDFResource))
			throw new P3OWLUtilException("value " + propertyValue + " of object property " + propertyName + " associated with individual "
					+ individual.getPrefixedName() + " is not a valid object value");

		return (RDFResource)propertyValue;
	}

	public static RDFResource getObjectPropertyValue(OWLModel owlModel, URI individualURI, URI propertyURI, boolean mustExist)
	{
		return getObjectPropertyValue(owlModel, individualURI, propertyURI, mustExist);
	}

	public static RDFResource getObjectPropertyValue(OWLModel owlModel, URI individualURI, URI propertyURI)
	{
		return getObjectPropertyValue(owlModel, individualURI, propertyURI, true);
	}

	public static Object getDatavaluedPropertyValue(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValue(owlModel, individualName, propertyName, true);
	}

	public static Object getDatavaluedPropertyValue(OWLModel owlModel, String individualName, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLIndividual individual = getOWLIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
		Object propertyValue = (individual == null || property == null) ? null : individual.getPropertyValue(property);

		if (mustExist && propertyValue == null)
			throwException("no property " + propertyName + " associated with individual " + individualName);

		return propertyValue;
	}

	public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getDatavaluedPropertyValue(owlModel, individual.getName(), propertyName, mustExist);
	}

	public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
		throws P3OWLUtilException
	{
		return getDatavaluedPropertyValue(owlModel, individual.getName(), property.getName(), mustExist);
	}

	public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, true);
		Collection<?> propertyValues = (property == null ? null : individual.getPropertyValues(property));
		Set<Object> result = new HashSet<Object>();

		if (property.isObjectProperty())
			throwException("expecting datatype property " + propertyName + " for " + individual.getPrefixedName());

		if (mustExist && propertyValues == null) {
			throwException("no property " + propertyName + " associated with individual " + individual.getPrefixedName());
		}

		if (propertyValues != null) {
			Iterator<?> iterator = propertyValues.iterator();
			while (iterator.hasNext())
				result.add(iterator.next());
		}

		return result;
	}

	public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		OWLIndividual individual = getIndividual(owlModel, individualName);

		return getDatavaluedPropertyValues(owlModel, individual, propertyName, mustExist);
	}

	public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValues(owlModel, individualName, propertyName, true);
	}

	public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, URI individualURI, URI propertyURI) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValues(owlModel, individualURI.toString(), propertyURI.toString());
	}

	public static int getOWLDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, mustExist);
		int result = -1;

		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throwException("cannot convert property value " + s + " of property " + propertyName + " associated with individual " + individual.getPrefixedName()
					+ " to integer");
		}
		return result;
	}

	public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getOWLDatavaluedPropertyValueAsInteger(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
	}

	public static int getOWLDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyName) throws P3OWLUtilException
	{
		return getOWLDatavaluedPropertyValueAsInteger(owlModel, individual, propertyName, true);
	}

	public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getOWLDatavaluedPropertyValueAsInteger(owlModel, getIndividual(owlModel, individualName), propertyName, true);
	}

	public static long getOWLDatavaluedPropertyValueAsLong(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, mustExist);
		long result = -1;

		try {
			result = Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw new P3OWLUtilException("cannot convert property value " + s + " of property " + propertyName + " associated with individual "
					+ individual.getPrefixedName() + " to long");
		}
		return result;
	}

	public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getOWLDatavaluedPropertyValueAsLong(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
	}

	public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, OWLIndividual individual, String propertyName) throws P3OWLUtilException
	{
		return getOWLDatavaluedPropertyValueAsLong(owlModel, individual, propertyName, true);
	}

	public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getOWLDatavaluedPropertyValueAsLong(owlModel, getIndividual(owlModel, individualName), propertyName, true);
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, true);
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);

		if (property == null)
			return null;

		return getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualName, String propertyName, boolean mustExist, String defaultValue)
		throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist, defaultValue);
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualName), propertyName, true);
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist,
																													String defaultValue) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName, mustExist);
		String propertValueAsString;

		if (property == null)
			return defaultValue;

		propertValueAsString = getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);

		return propertValueAsString == null ? defaultValue : propertValueAsString;
	}

	public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
		throws P3OWLUtilException
	{
		Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);
		String result = null;

		if (propertyValue instanceof Boolean) {
			Boolean b = (Boolean)propertyValue;
			if (b.booleanValue())
				result = "true";
			else
				result = "false";
		}
		if (propertyValue == null) {
			result = null;
		} else
			result = propertyValue.toString();

		return result;
	}

	public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsBoolean(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
	}

	public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsBoolean(owlModel, getIndividual(owlModel, individualName), propertyName, true);
	}

	public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyName, mustExist);

		if (propertyValue == null)
			return null;

		if (!(propertyValue instanceof Boolean)) {
			throwException("property value for " + propertyName + " associated with individual " + individual.getPrefixedName() + " is not a Boolean");
		}

		return (Boolean)propertyValue;
	}

	public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
		throws P3OWLUtilException
	{
		Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);

		if (propertyValue == null)
			return null;

		if (!(propertyValue instanceof Boolean))
			throwException("property value for " + property.getPrefixedName() + " in individual " + individual.getPrefixedName() + " is not a Boolean");

		return (Boolean)propertyValue;
	}

	public static Collection<?> getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyName)
		throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsCollection(owlModel, individual, propertyName, true);
	}

	public static Collection<?> getDatavaluedPropertyValueAsCollection(OWLModel owlModel, String individualName, String propertyName) throws P3OWLUtilException
	{
		return getDatavaluedPropertyValueAsCollection(owlModel, getIndividual(owlModel, individualName), propertyName, true);
	}

	public static Collection<?> getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
		throws P3OWLUtilException
	{
		Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyName, mustExist);
		Collection<Object> result = new ArrayList<Object>();

		if (propertyValue == null)
			return result;

		if (propertyValue instanceof RDFSLiteral)
			result.add(propertyValue);
		else if (propertyValue instanceof Collection) {
			for (Object o : ((Collection<?>)propertyValue))
				result.add(o);
		} else
			throwException("property value for " + propertyName + " associated with individual " + individual.getPrefixedName() + " is not a Collection or a literal");

		return result;
	}

	public static Collection<?> getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
		throws P3OWLUtilException
	{
		Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);
		Collection<Object> result = new ArrayList<Object>();

		if (propertyValue == null)
			return result;

		if (propertyValue instanceof RDFSLiteral)
			result.add(propertyValue);
		else if (propertyValue instanceof Collection) {
			for (Object o : ((Collection<?>)propertyValue))
				result.add(o);
		} else
			throwException("value for property " + property.getPrefixedName() + " associated with individual " + individual.getPrefixedName()
					+ " is not a Collection or a literal");

		return result;
	}

	public static List<OWLNamedClass> getDirectSubClassesOf(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		OWLNamedClass cls = getNamedClass(owlModel, className);

		return getDirectSubClassesOf(cls);
	}

	public static List<OWLNamedClass> getDirectSubClassesOf(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getDirectSubClassesOf(owlModel, classURI.toString());
	}

	public static List<OWLNamedClass> getSubClassesOf(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		OWLNamedClass cls = getNamedClass(owlModel, className);

		return getSubClassesOf(cls);
	}

	public static List<OWLNamedClass> getSubClassesOf(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getSubClassesOf(owlModel, classURI.toString());
	}

	public static List<OWLNamedClass> getDirectSuperClassesOf(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		OWLNamedClass cls = getNamedClass(owlModel, className);

		return getDirectSuperClassesOf(cls);
	}

	public static List<OWLNamedClass> getDirectSuperClassesOf(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getDirectSuperClassesOf(owlModel, classURI.toString());
	}

	public static List<OWLNamedClass> getSuperClassesOf(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		OWLNamedClass cls = getNamedClass(owlModel, className);

		return getSuperClassesOf(cls);
	}

	public static List<OWLNamedClass> getSuperClassesOf(OWLModel owlModel, URI classURI) throws P3OWLUtilException
	{
		return getSuperClassesOf(owlModel, classURI.toString());
	}

	public static List<OWLNamedClass> getSubClassesOf(OWLNamedClass cls)
	{
		List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();

		for (Object o : cls.getSubclasses(true))
			if (o instanceof OWLNamedClass)
				result.add((OWLNamedClass)o);

		return result;
	}

	public static List<OWLNamedClass> getDirectSubClassesOf(OWLNamedClass cls)
	{
		List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();

		for (Object o : cls.getSubclasses(false))
			if (o instanceof OWLNamedClass)
				result.add((OWLNamedClass)o);

		return result;
	}

	public static List<OWLNamedClass> getSuperClassesOf(OWLNamedClass cls)
	{
		List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();

		for (Object o : cls.getSuperclasses(true))
			if (o instanceof OWLNamedClass)
				result.add((OWLNamedClass)o);

		return result;
	}

	public static List<OWLNamedClass> getDirectSuperClassesOf(OWLNamedClass cls)
	{
		List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();

		for (Object o : cls.getSuperclasses(false))
			if (o instanceof OWLNamedClass)
				result.add((OWLNamedClass)o);

		return result;
	}

	public static Set<OWLProperty> getDirectSubPropertiesOf(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName);

		return getDirectSubPropertiesOf(property);
	}

	public static Set<OWLProperty> getDirectSubPropertiesOf(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getDirectSubPropertiesOf(owlModel, propertyURI.toString());
	}

	public static Set<OWLProperty> getSubPropertiesOf(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName);

		return getSubPropertiesOf(property);
	}

	public static Set<OWLProperty> getSubPropertiesOf(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getSubPropertiesOf(owlModel, propertyURI.toString());
	}

	public static Set<OWLProperty> getDirectSuperPropertiesOf(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName);

		return getDirectSuperPropertiesOf(property);
	}

	public static Set<OWLProperty> getDirectSuperPropertiesOf(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getDirectSuperPropertiesOf(owlModel, propertyURI.toString());
	}

	public static Set<OWLProperty> getSuperPropertiesOf(OWLModel owlModel, String propertyName) throws P3OWLUtilException
	{
		OWLProperty property = getOWLProperty(owlModel, propertyName);

		return getSuperPropertiesOf(property);
	}

	public static Set<OWLProperty> getSuperPropertiesOf(OWLModel owlModel, URI propertyURI) throws P3OWLUtilException
	{
		return getSuperPropertiesOf(owlModel, propertyURI.toString());
	}

	public static Set<OWLProperty> getSubPropertiesOf(OWLProperty property)
	{
		Set<OWLProperty> result = new HashSet<OWLProperty>();

		for (Object o : property.getSubproperties(true))
			if (o instanceof OWLProperty)
				result.add((OWLProperty)o);

		return result;
	}

	public static Set<OWLProperty> getEquivalentPropertiesOf(OWLProperty property)
	{
		Set<OWLProperty> result = new HashSet<OWLProperty>();

		for (Object o : property.getEquivalentProperties())
			if (o instanceof OWLProperty)
				result.add((OWLProperty)o);

		return result;
	}

	public static Set<OWLProperty> getDirectSubPropertiesOf(OWLProperty property) throws P3OWLUtilException
	{
		Set<OWLProperty> result = new HashSet<OWLProperty>();

		for (Object o : property.getSubproperties(false))
			if (o instanceof OWLProperty)
				result.add((OWLProperty)o);

		return result;
	}

	public static Set<OWLProperty> getSuperPropertiesOf(OWLProperty property) throws P3OWLUtilException
	{
		Set<OWLProperty> result = new HashSet<OWLProperty>();

		for (Object o : property.getSuperproperties(true))
			if (o instanceof OWLProperty)
				result.add((OWLProperty)o);

		return result;
	}

	public static Set<OWLProperty> getDirectSuperPropertiesOf(OWLProperty property) throws P3OWLUtilException
	{
		Set<OWLProperty> result = new HashSet<OWLProperty>();

		for (Object o : property.getSuperproperties(false))
			if (o instanceof OWLProperty)
				result.add((OWLProperty)o);

		return result;
	}

	public static Set<OWLProperty> getDomainProperties(OWLModel owlModel, String className, boolean transitive) throws P3OWLUtilException
	{
		OWLClass cls = getClass(owlModel, className);
		Set<OWLProperty> result = new HashSet<OWLProperty>();
		Collection<?> domainProperties = cls.getUnionDomainProperties(transitive);

		// TODO: bug in Property.getUnionDomain that causes it to return non RDFResource objects so we need to work around it.
		// for (RDFResource resource : resources) result.add(resource.getName());

		if (domainProperties != null) {
			Iterator<?> iterator = cls.getUnionDomainProperties().iterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (o instanceof OWLProperty)
					result.add((OWLProperty)o);
			}
		}
		return result;
	}

	public static Set<OWLProperty> getDomainProperties(OWLModel owlModel, URI classURI, boolean transitive) throws P3OWLUtilException
	{
		return getDomainProperties(owlModel, classURI.toString(), transitive);
	}

	public static Set<String> rdfResources2OWLNamedClassNames(Collection<?> resources)
	{
		Set<String> result = new HashSet<String>();

		Iterator<?> iterator = resources.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof OWLNamedClass)
				result.add(((OWLNamedClass)o).getName());
		}

		return result;

	}

	public static Set<String> rdfResources2URIs(Collection<?> resources)
	{
		Set<String> result = new HashSet<String>();

		Iterator<?> iterator = resources.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof RDFResource)
				result.add(((RDFResource)o).getURI());
		}

		return result;
	}

	public static Set<URI> rdfResources2OWLNamedClassURIs(Collection<?> resources) throws P3OWLUtilException
	{
		Set<URI> uris = new HashSet<URI>();

		Iterator<?> iterator = resources.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof OWLNamedClass)
				uris.add(getURI(((OWLNamedClass)o)));
		}

		return uris;
	}

	public static Set<URI> rdfResources2OWLPropertyURIs(Collection<?> resources) throws P3OWLUtilException
	{
		Set<URI> result = new HashSet<URI>();

		Iterator<?> iterator = resources.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof OWLProperty)
				result.add(getURI((OWLProperty)o));
		}

		return result;
	}

	public static boolean hasInconsistentClasses(OWLModel owlModel)
	{
		return !owlModel.getInconsistentClasses().isEmpty();
	}

	public static String createNewResourceName(OWLModel owlModel, String localNamePrefix)
	{
		return owlModel.createNewResourceName(localNamePrefix);
	}

	public static URI createNewResourceURI(OWLModel owlModel, String localNamePrefix) throws P3OWLUtilException
	{
		return createURI(owlModel.createNewResourceName(localNamePrefix));
	}

	public static RDFSDatatype getRDFSDatatype(OWLModel owlModel, String datatypeName)
	{
		return owlModel.getRDFSDatatypeByName(datatypeName);
	}

	public static RDFSDatatype getRDFSDatatype(OWLModel owlModel, URI datatypeURI)
	{
		return getRDFSDatatype(owlModel, datatypeURI.toString());
	}

	public static RDFSLiteral createRDFSLiteral(OWLModel owlModel, String value, RDFSDatatype datatype) throws P3OWLUtilException
	{
		RDFSLiteral literal = owlModel.createRDFSLiteral(value, datatype);

		if (literal == null)
			throw new P3OWLUtilException("error creating RDFSLiteral " + value + " of type " + datatype);

		return literal;
	}

	public static OWLClass createOWLClassDescription(OWLModel owlModel, String classExpression) throws P3OWLUtilException
	{
		OWLClassParser parser = owlModel.getOWLClassParser();
		OWLClass cls = null;

		try {
			cls = (OWLClass)parser.parseClass(owlModel, classExpression);
		} catch (OWLClassParseException e) {
			throw new P3OWLUtilException("OWL class expression " + classExpression + " not valid: " + e.getMessage());
		}

		return cls;
	}

	public static OWLNamedClass getOWLThingClass(OWLModel owlModel)
	{
		return owlModel.getOWLThingClass();
	}

	public static RDFProperty getOWLSameAsProperty(OWLModel owlModel)
	{
		return owlModel.getOWLSameAsProperty();
	}

	public static RDFProperty getRDFSSubPropertyOfProperty(OWLModel owlModel)
	{
		return owlModel.getRDFSSubPropertyOfProperty();
	}

	public static RDFProperty getOWLInverseOfProperty(OWLModel owlModel)
	{
		return owlModel.getOWLInverseOfProperty();
	}

	public static RDFProperty getRDFSDomainProperty(OWLModel owlModel)
	{
		return owlModel.getRDFSDomainProperty();
	}

	public static RDFProperty getRDFSRangeProperty(OWLModel owlModel)
	{
		return owlModel.getRDFSRangeProperty();
	}

	public static RDFProperty getOWLEquivalentPropertyProperty(OWLModel owlModel)
	{
		return owlModel.getOWLEquivalentPropertyProperty();
	}

	public static RDFProperty getOWLDisjointWithProperty(OWLModel owlModel)
	{
		return owlModel.getOWLDisjointWithProperty();
	}

	public static RDFProperty getOWLEquivalentClassProperty(OWLModel owlModel)
	{
		return owlModel.getOWLEquivalentClassProperty();
	}

	public static RDFProperty getOWLDisjointClassProperty(OWLModel owlModel)
	{
		return owlModel.getOWLDisjointWithProperty();
	}

	public static RDFProperty getRDFSSubClassOfProperty(OWLModel owlModel)
	{
		return owlModel.getRDFSSubClassOfProperty();
	}

	public static void makeSameAs(OWLModel owlModel, Set<URI> individualURIs) throws P3OWLUtilException
	{
		for (URI individualURI : individualURIs) {
			makeSameAs(owlModel, individualURI, individualURI);
			Set<URI> otherIndividualURIs = new HashSet<URI>(individualURIs);
			otherIndividualURIs.remove(individualURI);
			for (URI otherIndividualURI : otherIndividualURIs)
				makeSameAs(owlModel, individualURI, otherIndividualURI);
		}
	}

	public static void makeSameAs(OWLModel owlModel, String individualName1, String individualName2) throws P3OWLUtilException
	{
		OWLIndividual individual1 = getOWLIndividual(owlModel, individualName1);
		OWLIndividual individual2 = getOWLIndividual(owlModel, individualName2);

		if (!individual1.hasPropertyValue(getOWLSameAsProperty(owlModel), individual2))
			individual1.addPropertyValue(getOWLSameAsProperty(owlModel), individual2);

		if (!individual2.hasPropertyValue(getOWLSameAsProperty(owlModel), individual1))
			individual2.addPropertyValue(getOWLSameAsProperty(owlModel), individual1);
	}

	public static void makeSameAs(OWLModel owlModel, URI individual1URI, URI individual2URI) throws P3OWLUtilException
	{
		makeSameAs(owlModel, individual1URI.toString(), individual2URI.toString());
	}

	public static void makeDifferentFrom(OWLModel owlModel, String individualName1, String individualName2) throws P3OWLUtilException
	{
		OWLIndividual individual1 = getOWLIndividual(owlModel, individualName1);
		OWLIndividual individual2 = getOWLIndividual(owlModel, individualName2);

		if (!individual1.hasPropertyValue(getOWLDifferentFromProperty(owlModel), individual2))
			individual1.addPropertyValue(getOWLDifferentFromProperty(owlModel), individual2);
		if (!individual2.hasPropertyValue(getOWLDifferentFromProperty(owlModel), individual1))
			individual2.addPropertyValue(getOWLDifferentFromProperty(owlModel), individual1);
	}

	public static void makeDifferentFrom(OWLModel owlModel, URI individual1URI, URI individual2URI) throws P3OWLUtilException
	{
		makeDifferentFrom(owlModel, individual1URI.toString(), individual2URI.toString());
	}

	public static void makeDifferentFrom(OWLModel owlModel, Set<URI> individualURIs) throws P3OWLUtilException
	{
		for (URI individualURI : individualURIs) {
			Set<URI> otherIndividualURIs = new HashSet<URI>(individualURIs);
			otherIndividualURIs.remove(individualURI);
			for (URI otherIndividualURI : otherIndividualURIs)
				makeDifferentFrom(owlModel, individualURI, otherIndividualURI);
		}
	}

	public static Collection<?> getOWLAllDifferents(OWLModel owlModel)
	{
		return owlModel.getOWLAllDifferents();
	}

	public static RDFProperty getOWLDifferentFromProperty(OWLModel owlModel)
	{
		return owlModel.getOWLDifferentFromProperty();
	}

	public static OWLProperty getOWLProperty(OWLModel owlModel, String propertyName)
	{
		return owlModel.getOWLProperty(propertyName);
	}

	public static OWLProperty getOWLProperty(OWLModel owlModel, URI propertyURI)
	{
		return getOWLProperty(owlModel, propertyURI.toString());
	}

	public static RDFProperty getRDFProperty(OWLModel owlModel, String propertyName)
	{
		return owlModel.getRDFProperty(propertyName);
	}

	public static OWLProperty getOWLProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws P3OWLUtilException
	{
		OWLProperty property = owlModel.getOWLProperty(propertyName);

		if (mustExist && property == null)
			throw new P3OWLUtilException("no property named " + propertyName + " in ontology");

		return property;
	}

	public static OWLIndividual getOWLIndividual(OWLModel owlModel, String individualName)
	{
		if (isOWLIndividual(owlModel, individualName))
			return owlModel.getOWLIndividual(individualName);
		else
			return null;
	}

	public static OWLIndividual getOWLIndividual(OWLModel owlModel, URI individualURI)
	{
		return getOWLIndividual(owlModel, individualURI.toString());
	}

	public static RDFSNamedClass getRDFSNamedClass(OWLModel owlModel, String className)
	{
		return owlModel.getRDFSNamedClass(className);
	}

	public static RDFSNamedClass getRDFSNamedClass(OWLModel owlModel, URI classURI)
	{
		return getRDFSNamedClass(owlModel, classURI.toString());
	}

	public static boolean isSWRLBuiltIn(OWLModel owlModel, String builtInName)
	{
		RDFResource resource = owlModel.getRDFResource(builtInName);
		return resource != null && resource.getProtegeType().getName().equals(edu.stanford.smi.protegex.owl.swrl.model.SWRLNames.Cls.BUILTIN);
	}

	public static boolean isValidClassName(OWLModel owlModel, String className)
	{
		return owlModel.isValidResourceName(className, owlModel.getRDFSNamedClassClass());
	}

	public static void checkIfIsValidClassName(OWLModel owlModel, String className) throws P3OWLUtilException
	{
		if (!isValidClassName(owlModel, className))
			throw new P3OWLUtilException("invalid named class name " + className);
	}

	public static boolean isValidURI(String uri)
	{
		return URIUtilities.isValidURI(uri);
	}

	public static String getPrefixForResourceName(OWLModel owlModel, String resourceName)
	{
		return owlModel.getPrefixForResourceName(resourceName);
	}

	public static String getLocalNameForURI(OWLModel owlModel, String uri)
	{
		return owlModel.getLocalNameForURI(uri);
	}

	// Name can be rdf:ID or other annotation property (e.g., rdfs:label)
	public static OWLNamedClass getOWLClassFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		OWLNamedClass cls = null;

		try {
			cls = ParserUtils.getOWLClassFromName(owlModel, id);
			if (cls == null)
				throw new P3OWLUtilException("unknown OWL named class " + name);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous class name " + name);
		}

		return cls;
	}

	// Name can be rdf:ID or other annotation property (e.g., rdfs:label)
	public static OWLDatatypeProperty getOWLDataPropertyFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		OWLDatatypeProperty property = null;

		try {
			property = ParserUtils.getOWLDatatypePropertyFromName(owlModel, id);
			if (property == null)
				throw new P3OWLUtilException("unknown OWL data property " + name);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous OWL data property name " + name);
		}

		return property;
	}

	// Name can be rdf:ID or other annotation property (e.g., rdfs:label)
	public static OWLObjectProperty getOWLObjectPropertyFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		OWLObjectProperty property = null;

		try {
			property = ParserUtils.getOWLObjectPropertyFromName(owlModel, id);
			if (property == null)
				throw new P3OWLUtilException("unknown OWL object property " + name);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous OWL object property name " + name);
		}

		return property;
	}

	// Name can be rdf:ID or other annotation property (e.g., rdfs:label)
	public static OWLIndividual getOWLIndividualFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		OWLIndividual individual = null;

		try {
			individual = ParserUtils.getOWLIndividualFromName(owlModel, id);
			if (individual == null)
				throw new P3OWLUtilException("unknown OWL individual " + name);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous OWL individual name " + name);
		}

		return individual;
	}

	public static RDFProperty getRDFPropertyFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		RDFProperty property = null;

		try {
			property = ParserUtils.getRDFPropertyFromName(owlModel, id);
			if (property == null)
				throw new P3OWLUtilException("unknown RDF property " + name);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous RDF property name " + name);
		}

		return property;
	}

	public static RDFResource getRDFResourceFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		RDFResource resource = null;

		try {
			resource = ParserUtils.getRDFResourceFromName(owlModel, id);
			if (resource == null)
				throw new P3OWLUtilException("unknown RDF resource " + name);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous RDF resource name " + name);
		}

		return resource;
	}

	public static boolean isExistingRDFResourceWithRDFSLabel(OWLModel owlModel, String label)
	{
		String id = ParserUtils.dequoteIdentifier(label);

		try {
			if (getRDFResourceFromRDFSLabel(owlModel, id) != null)
				return true;
		} catch (P3OWLUtilException e) {
			return false;
		} catch (AmbiguousNameException e) {
			return true;
		}

		return false;
	}

	public static RDFResource getRDFResourceFromRDFSLabel(OWLModel owlModel, String label) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(label);
		RDFResource resource = null;

		try {
			resource = ParserUtils.getRDFResourceFromRDFSLabel(owlModel, id);
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous RDF resource name " + label);
		}

		return resource;
	}

	public static RDFSClass getRDFSClassFromName(OWLModel owlModel, String name) throws P3OWLUtilException
	{
		String id = ParserUtils.dequoteIdentifier(name);
		RDFSClass cls = null;

		try {
			RDFResource resource = ParserUtils.getRDFSClassFromName(owlModel, id);
			if (resource == null)
				throw new P3OWLUtilException("unknown RDF resource " + name);

			if (!(resource instanceof RDFSClass))
				throw new P3OWLUtilException("name " + name + " is not an RDFSClass");

			cls = (RDFSClass)resource;
		} catch (AmbiguousNameException e) {
			throw new P3OWLUtilException("ambiguous RDFS class name " + name);
		}

		return cls;
	}

	private static URI getURI(RDFResource resource) throws P3OWLUtilException
	{
		try {
			return new URI(resource.getURI());
		} catch (URISyntaxException e) {
			throw new P3OWLUtilException("error converting resource URI " + resource.getURI() + ": " + e.getMessage());
		}
	}

	private static URI createURI(String fullName) throws P3OWLUtilException
	{
		try {
			return new URI(fullName);
		} catch (URISyntaxException e) {
			throw new P3OWLUtilException("error converting full name to URI " + fullName + ": " + e.getMessage());
		}
	}

	private static void throwException(String message) throws P3OWLUtilException
	{
		throw new P3OWLUtilException(message);
	}
}
