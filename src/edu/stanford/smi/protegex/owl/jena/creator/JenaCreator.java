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

package edu.stanford.smi.protegex.owl.jena.creator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.ontology.AllDifferent;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaNormalizer;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLCardinalityBase;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

/**
 * A class that creates a Jena OntModel from a Protege OWL model.
 * The resulting OntModel can then be used in Jena services such as for reasoning.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaCreator {
    private static transient final Logger log = Log.getLogger(JenaCreator.class);

    private Map anonMap = new HashMap();

    private int classCount;

    private int classProgressCount;

    private boolean forReasoning;
    
    private boolean processImports = true;
    
    private Set<String> ignoreImports = new HashSet<String>();

    private boolean inferred;

    private OWLModel owlModel;

    private OntModel ontModel;

    private Model owlFullModel;

    private ProgressDisplay progressDisplay;

    /*
     * what does system own slot mean??
     */
    private Set<RDFProperty> systemOwnSlots = new HashSet<RDFProperty>();
    
    private Set<RDFProperty> ignoredProperties = new HashSet<RDFProperty>();

    /**
     * The classes that shall be processed in this creation process.
     */
    private Collection targetClses;

    /**
     * A Map from RDFIndividuals to Lists of RDFProperties to indicate which (object)
     * property values still need to be assigned outside of the usual loop.
     */
    private Map todoIndividualsWithObjectProperties = new HashMap();

    public JenaCreator(OWLModel owlModel,
                       Collection targetClses,
                       ProgressDisplay progressDisplay) {
        this(owlModel, false, false, targetClses, progressDisplay);
    }


    public JenaCreator(OWLModel owlModel,
                       boolean forReasoning,
                       Collection targetClses,
                       ProgressDisplay progressDisplay) {
        this(owlModel, forReasoning, false, targetClses, progressDisplay);
    }


    public JenaCreator(OWLModel owlModel,
                       boolean forReasoning,
                       boolean inferred,
                       Collection targetClses,
                       ProgressDisplay progressDisplay) {
        this.targetClses = targetClses;
        this.inferred = inferred;
        this.owlModel = owlModel;
        this.forReasoning = forReasoning;
        this.progressDisplay = progressDisplay;

        systemOwnSlots.add(owlModel.getOWLCardinalityProperty());
        systemOwnSlots.add(owlModel.getOWLMinCardinalityProperty());
        systemOwnSlots.add(owlModel.getOWLMaxCardinalityProperty());
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.ALL_VALUES_FROM));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.SOME_VALUES_FROM));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.HAS_VALUE));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.IMPORTS));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.INTERSECTION_OF));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.UNION_OF));
        // systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.ONE_OF));
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.EQUIVALENT_CLASS));
        systemOwnSlots.add(owlModel.getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF));
        systemOwnSlots.add(owlModel.getRDFProperty(RDFNames.Slot.TYPE));
        systemOwnSlots.add(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_SUBCLASSES));
        systemOwnSlots.add(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES));
        systemOwnSlots.add(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE));
        systemOwnSlots.add(owlModel.getProtegeClassificationStatusProperty());
        systemOwnSlots.add(owlModel.getRDFProperty(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY));
    }

    public boolean getProcessImports()  {
        return processImports;
    }
    
    public void setProcessImports(boolean processImports) {
        this.processImports = processImports;
    }
    
    public void addIgnoreImport(String uri) {
        ignoreImports.add(uri);
    }
    
    public void removeIgnoreImport(String uri) {
        ignoreImports.remove(uri);
    }
    
    public Set<String> listIgnoreImports() {
        return ignoreImports;
    }

    private void addDisjointClasses(OWLNamedClass namedCls, OntClass ontClass) {
        for (Iterator it = namedCls.getDisjointClasses().iterator(); it.hasNext();) {
            RDFSClass superClass = (RDFSClass) it.next();
            OntClass superOntClass = getOntClass(superClass);
            ontClass.addDisjointWith(superOntClass);
        }
    }


    private void addEquivalentClasses(OWLNamedClass namedCls, OntClass ontClass) {
    	Collection clses = new HashSet(namedCls.getEquivalentClasses());
    	
    	if (inferred) {
    		clses.addAll(namedCls.getInferredEquivalentClasses());
    	}
    	
        for (Iterator it = clses.iterator(); it.hasNext();) {
            RDFSClass superClass = (RDFSClass) it.next();
            OntClass superOntClass = getOntClass(superClass);
            ontClass.addEquivalentClass(superOntClass);
        }
    }


    private void addEquivalentProperties(OWLProperty property, OntProperty ontProperty) {
        for (Iterator it = property.getEquivalentProperties().iterator(); it.hasNext();) {
            OWLProperty equivalentProperty = (OWLProperty) it.next();
            OntProperty equivalentOntProperty = getOntProperty(equivalentProperty);
            ontProperty.addEquivalentProperty(equivalentOntProperty);
        }
    }


    private void addImports(OWLOntology oi, Ontology ontology) {
        OntDocumentManager documentManager = ontModel.getDocumentManager();
        configureImportsStrategy(documentManager);
        for (Iterator<String> it = oi.getImports().iterator(); it.hasNext();) {
            String uri = it.next();
            ontology.addImport(ontModel.getResource(uri));
            documentManager.loadImport(ontModel, uri);
        }
    }
    
    // Note that the documentManager is an instance - so previous settings must be cleared.
    @SuppressWarnings("unchecked")
    private void configureImportsStrategy(OntDocumentManager documentManager) {
        documentManager.setProcessImports(processImports);
        if (processImports) {
            Set<String> originalIgnoreImports = new HashSet<String>();
            for (Iterator it = documentManager.listIgnoredImports(); it.hasNext();) {
                String uri = (String) it.next();
                originalIgnoreImports.add(uri);
            }
            for (String uri : originalIgnoreImports)  {
                documentManager.removeIgnoreImport(uri);
            }
            for (String uri : ignoreImports) {
                documentManager.addIgnoreImport(uri);
            }
            documentManager.addIgnoreImport(owlModel.getDefaultOWLOntology().getName());
        }
    }


    @SuppressWarnings("unchecked")
    private void addPropertyValues(RDFResource rdfResource, OntResource ontResource) {
        if (!forReasoning) {
            Collection properties = rdfResource.getPossibleRDFProperties();
            if (log.isLoggable(Level.FINER)) {
                log.finer("JenaCreator is adding property values for the resource " + rdfResource);
            }
            for (Iterator it = properties.iterator(); it.hasNext();) {
                RDFProperty property = (RDFProperty) it.next();
                if (!isSystemOwnSlot(rdfResource, property) || property.isAnnotationProperty()) {
                    if (log.isLoggable(Level.FINER)) {
                        log.finer("\tFound Property " + property);
                    }
                    if (rdfResource instanceof RDFIndividual &&
                        property instanceof OWLObjectProperty &&
                        rdfResource.getPropertyValueCount(property) > 0) {
                        java.util.List list = (java.util.List) todoIndividualsWithObjectProperties.get(rdfResource);
                        if (list == null) {
                            list = new ArrayList();
                            todoIndividualsWithObjectProperties.put(rdfResource, list);
                        }
                        list.add(property);
                    }
                    else {
                        addPropertyValues(rdfResource, ontResource, property);
                    }
                }
            }
            if (log.isLoggable(Level.FINER)) {
                log.finer("JenaCreator is done adding property values for the resource " + rdfResource);
            }
        }
    }


    private void addPropertyValues2(RDFResource rdfResource, OntResource ontResource) {
        java.util.List list = (java.util.List) todoIndividualsWithObjectProperties.get(rdfResource);
        for (Iterator it = list.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            addPropertyValues(rdfResource, ontResource, property);
        }
    }


    private void addPropertyValues(RDFResource instance, OntResource ontResource, RDFProperty rdfProperty) {
        Property property = getProperty(rdfProperty);
        RDFSDatatype rangeDatatype = rdfProperty.getRangeDatatype();
        for (Iterator vit = instance.getPropertyValues(rdfProperty).iterator(); vit.hasNext();) {
            Object value = vit.next();
            if (value instanceof RDFResource) {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("\t\tAdding <" + instance + ", " + rdfProperty + ", " + value + ">");
                }
                Resource valueResource = getResource((RDFResource) value);
                ontResource.addProperty(property, valueResource);
            }
            else if (owlModel.getRDFXMLLiteralType().equals(rangeDatatype)) {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("\t\tAdding <" + instance + ", " + rdfProperty + ", " + value + "> using xml literal");
                }
                ontResource.addProperty(property, ontModel.createTypedLiteral(value, XMLLiteralType.theXMLLiteralType));
            }
            else {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("\t\tAdding <" + instance + ", " + rdfProperty + ", " + value + "> using literal");
                }
                Literal literal = createLiteral(value, ontModel);
                ontResource.addProperty(property, literal);
            }
        }
    }


    protected void addSuperclasses(RDFSNamedClass rdfsClass, OntClass ontClass) {
        Collection superClasses = rdfsClass.getPureSuperclasses();
        if (inferred && (rdfsClass instanceof OWLNamedClass)) {
            OWLNamedClass namedCls = (OWLNamedClass) rdfsClass;
            for (Iterator it = superClasses.iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof RDFSNamedClass) {
                    it.remove();
                }
            }
            superClasses.addAll(namedCls.getInferredSuperclasses());
            superClasses.removeAll(namedCls.getInferredEquivalentClasses());
        }
        if (superClasses.size() > 0 &&
            (superClasses.size() > 1 || !superClasses.iterator().next().equals(owlModel.getOWLThingClass()))) {
            for (Iterator it = superClasses.iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                RDFSClass superClass = (RDFSClass) superCls;
                OntClass superOntClass = getOntClass(superClass);
                ontClass.addSuperClass(superOntClass);
            }
        }
    }


    private void addSuperproperties(RDFProperty property, OntProperty ontProperty) {
        for (Iterator it = property.getSuperproperties(false).iterator(); it.hasNext();) {
            RDFProperty superProperty = (RDFProperty) it.next();
            OntProperty superOntProperty = getOntProperty(superProperty);
            ontProperty.addSuperProperty(superOntProperty);
        }
    }


    private void addTodoPropertyValues() {
        Iterator it = todoIndividualsWithObjectProperties.keySet().iterator();
        while (it.hasNext()) {
            RDFResource resource = (RDFResource) it.next();
            OntResource ontResource = getOntResource(resource);
            addPropertyValues2(resource, ontResource);
        }
    }


    protected void createAdditionalAnonymousSuperclassesOfIncludedClass(OWLNamedClass namedCls) {
        OntClass ontClass = ontModel.getOntClass(namedCls.getURI());
        for (Iterator it = namedCls.getSuperclasses(false).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof OWLAnonymousClass && !superCls.isIncluded()) {
                OntClass superClass = getOntClass((OWLAnonymousClass) superCls);
                if (superCls.hasDirectSuperclass(namedCls)) {
                    ontClass.addEquivalentClass(superClass);
                }
                else {
                    ontClass.addSuperClass(superClass);
                }
            }
        }
    }


    private OntClass createAnonymousClass(OWLAnonymousClass anonymousCls) {
        if (anonymousCls instanceof OWLLogicalClass) {
            return createLogicalClass((OWLLogicalClass) anonymousCls);
        }
        else if (anonymousCls instanceof OWLEnumeratedClass) {
            return createEnumeratedClass((OWLEnumeratedClass) anonymousCls);
        }
        else {
            return createRestriction((OWLRestriction) anonymousCls);
        }
    }


    private com.hp.hpl.jena.ontology.Restriction createCardinalityRestriction(OWLCardinalityBase restriction) {
        int cardinality = restriction.getCardinality();
        RDFProperty property = restriction.getOnProperty();
        OntProperty ontProperty = getOntProperty(property);
        if (restriction instanceof OWLMaxCardinality) {
            return ontModel.createMaxCardinalityRestriction(null, ontProperty, cardinality);
        }
        else if (restriction instanceof OWLMinCardinality) {
            return ontModel.createMinCardinalityRestriction(null, ontProperty, cardinality);
        }
        else {
            return ontModel.createCardinalityRestriction(null, ontProperty, cardinality);
        }
    }


    @SuppressWarnings("unchecked")
    public static DataRange createDataRange(OWLDataRange dataRange, OntModel ontModel) {
        Collection members = new ArrayList();
        if (dataRange.getOneOf() != null) {
            for (Iterator it = dataRange.getOneOf().getValues().iterator(); it.hasNext();) {
                Object value = it.next();
                Literal literal = createLiteral(value, ontModel);
                members.add(literal);
            }
        }
        com.hp.hpl.jena.rdf.model.RDFList literals = ontModel.createList(members.iterator());
        return ontModel.createDataRange(literals);
    }


    private static Literal createLiteral(Object value, OntModel ontModel) {
        if (value instanceof RDFSLiteral) {
            RDFSLiteral literal = (RDFSLiteral) value;
            if (literal.getLanguage() != null) {
                return ontModel.createLiteral(literal.getString(),
                                              literal.getLanguage());
            }
            else {
                return ontModel.createTypedLiteral(literal.getString(),
                                                   XMLSchemaDatatypes.getRDFDatatype(literal.getDatatype()));
            }
        }
        else {
            return ontModel.createTypedLiteral(value);
        }
    }


    private OntProperty createDatatypeProperty(OWLDatatypeProperty datatypeProperty) {
        OntProperty ontProperty = ontModel.createDatatypeProperty(datatypeProperty.getURI());
        adjustOntPropertyRDFType(datatypeProperty, ontProperty);
        return ontProperty;
    }


    private EnumeratedClass createEnumeratedClass(OWLEnumeratedClass enumerationCls) {
        RDFList list = createOntResourceList(enumerationCls.getOneOf());
        return ontModel.createEnumeratedClass(null, list);
    }


    private HasValueRestriction createHasValueRestriction(OWLHasValue hasRestriction) {
        OntProperty property = getOntProperty(hasRestriction.getOnProperty());
        RDFNode node = null;
        Object value = hasRestriction.getHasValue();
        if (value instanceof RDFResource) {
            node = getIndividual((RDFResource) value);
        }
        else {
            node = ontModel.createTypedLiteral(value);
        }
        return ontModel.createHasValueRestriction(null, property, node);
    }


    private Individual createIndividual(RDFResource rdfResource, boolean anon) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Creating RDFIndividual for " + rdfResource.getBrowserText());
        }
        Iterator it = null;
        if (inferred && !rdfResource.getInferredTypes().isEmpty()) {
            it = rdfResource.getInferredTypes().iterator();
        }
        else {
            it = rdfResource.getProtegeTypes().iterator();
        }
        RDFSClass type = (RDFSClass) it.next();
        OntClass ontClass = getOntClass(type);
        Individual individual = ontModel.getIndividual(rdfResource.getURI());
        if (individual == null) {  // Maybe created in the meantime by getOntClass
            String uri = null;
            if (!rdfResource.isAnonymous() && !type.equals(owlModel.getRDFListClass())) {
                uri = rdfResource.getURI();
            }
            individual = ontModel.createIndividual(uri, ontClass);
            while (it.hasNext()) {  // Add additional types
                RDFSClass addType = (RDFSClass) it.next();
                if (log.isLoggable(Level.FINE)) {
                    log.fine("\tAdding type " + addType + " to individual " + rdfResource);
                }
                OntClass addOntClass = getOntClass(addType);
                if (addOntClass != null) {
                    individual.addRDFType(addOntClass);
                }
            }
            if (anon) {
                anonMap.put(rdfResource, individual);
            }
            addPropertyValues(rdfResource, individual);
        }
        return individual;
    }


    /*
     * This is scrambled since we are doing so much OWL-fullish stuff.  What are 
     * individuals?  Since we have already collected OWLOntologies, RDFSClasses and RDFSProperties 
     * I think I will view an individual as anything that is not one of the above.
     * Argh.
     */
    private void createIndividuals() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("JenaCreator looking for individuals");
        }
        Iterator<RDFResource> it = getRDFSIndividualIterator();
        while (it.hasNext()) {
            RDFResource resource = it.next();
            if (log.isLoggable(Level.FINE)) {
                log.fine("\tFound individual " + resource);
            }
            getOntResource(resource);
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("JenaCreator finished looking for individuals");
        }
    }


    private OntClass createLogicalClass(OWLLogicalClass logicalCls) {
        if (logicalCls instanceof OWLNAryLogicalClass) {
            Collection operands = new ArrayList();
            OWLNAryLogicalClass nc = (OWLNAryLogicalClass) logicalCls;
            for (Iterator it = nc.getOperands().iterator(); it.hasNext();) {
                RDFSClass operandClass = (RDFSClass) it.next();
                operands.add(getOntClass(operandClass));
            }
            if (logicalCls instanceof OWLIntersectionClass) {
                return ontModel.createIntersectionClass(null, ontModel.createList(operands.iterator()));
            }
            else { // OWLUnionClass
                return ontModel.createUnionClass(null, ontModel.createList(operands.iterator()));
            }
        }
        else {
            OWLComplementClass cc = (OWLComplementClass) logicalCls;
            RDFSClass complement = cc.getComplement();
            return ontModel.createComplementClass(null, getOntClass(complement));
        }
    }


    private OntClass createNamedClass(RDFSNamedClass rdfsClass) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Creating named OntClass for " + rdfsClass.getBrowserText());
        }
        classProgressCount++;
        if (progressDisplay != null) {
            progressDisplay.setProgressValue(classProgressCount / (double) classCount);
        }
        OntClass ontClass = ontModel.createClass(rdfsClass.getURI());
        if (!forReasoning) {

            // add any further types (for possibly multiple metaclasses
            for (Iterator types = rdfsClass.getRDFTypes().iterator(); types.hasNext();) {
                RDFSClass type = (RDFSClass) types.next();
                if (!owlModel.getOWLNamedClassClass().equals(type)) {
                    ontClass.addRDFType(getOntClass(type));
                }
            }
        }
        if (rdfsClass.isDeprecated()) {
            ontClass.addRDFType(OWL.DeprecatedClass);
        }
        addSuperclasses(rdfsClass, ontClass);
        if (rdfsClass instanceof OWLNamedClass) {
            OWLNamedClass namedCls = (OWLNamedClass) rdfsClass;
            addEquivalentClasses(namedCls, ontClass);
            addDisjointClasses(namedCls, ontClass);
        }
        addPropertyValues(rdfsClass, ontClass);
        return ontClass;
    }


    private OntProperty createObjectProperty(OWLObjectProperty objectProperty) {
        OntProperty ontProperty;
        ontProperty = ontModel.createObjectProperty(objectProperty.getURI());
        adjustOntPropertyRDFType(objectProperty, ontProperty);
        if (objectProperty.isSymmetric()) {
            ontProperty.addRDFType(OWL.SymmetricProperty);
        }
        if (objectProperty.isTransitive()) {
            ontProperty.addRDFType(OWL.TransitiveProperty);
        }
        if (objectProperty.getInverseProperty() instanceof OWLObjectProperty) {
            OWLObjectProperty inverseSlot = (OWLObjectProperty) objectProperty.getInverseProperty();
            OntProperty inverseProperty = getOntProperty(inverseSlot);
            ontProperty.setInverseOf(inverseProperty);
        }
        return ontProperty;
    }


    private void createOntClasses() {
        if (log.isLoggable(Level.FINER)) {
            log.finer("Jena Creator is collecting ontology classes");
        }
        Iterator it = getRDFSClassIterator();
        while (it.hasNext()) {
            final RDFSNamedClass rdfsClass = (RDFSNamedClass) it.next();
            if (log.isLoggable(Level.FINER)) {
                log.finer("\tFound class " + rdfsClass);
            }
            getOntClass(rdfsClass);
            if (rdfsClass instanceof OWLNamedClass && rdfsClass.isIncluded()) {
                createAdditionalAnonymousSuperclassesOfIncludedClass((OWLNamedClass) rdfsClass);
            }
        }
        if (log.isLoggable(Level.FINER)) {
            log.finer("Jena Creator is finished collecting ontology classes");
        }
    }


    public OntModel createOntModel() {
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setReasoner(null);

    	return createOntModel(spec);
    }

    public OntModel createOntModel(OntModelSpec ontModelSpec) {
        OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec, null);
        Model owlFullModel = Jena.addOWLFullModel(ontModel);
        run(ontModel, owlFullModel);
        return ontModel; 
    }
    

    public OntModel createOntModelWithoutOWLFullModel() {
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setReasoner(null);
        OntModel ontModel = ModelFactory.createOntologyModel(spec, null);
        Model owlFullModel = Jena.addOWLFullModel(ontModel);
        run(ontModel, owlFullModel);
        ontModel.removeSubModel(owlFullModel);
        return ontModel;
    }


    private void createOntologies() {
        for (Iterator it = owlModel.getOWLOntologyClass().getInstances(false).iterator(); it.hasNext();) {
            OWLOntology owlOntology = (OWLOntology) it.next();
            if (!owlOntology.isIncluded() || owlOntology.equals(owlModel.getDefaultOWLOntology())) {
                String ontologyURI = owlOntology.getURI();
                Ontology ontology = ontModel.createOntology(ontologyURI);
                addImports(owlOntology, ontology);
                JenaNormalizer.assignRDFTypesToMetaclassInstances(ontModel, owlFullModel);
                addPropertyValues(owlOntology, ontology);
            }
        }
        Jena.ensureOWLFullModelIsLastModel(ontModel, owlFullModel);
    }


    OntProperty createOntProperty(RDFProperty property) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Creating OntProperty for " + property.getBrowserText());
        }
        OntProperty ontProperty = null;
        if (property instanceof OWLDatatypeProperty) {
            ontProperty = createDatatypeProperty((OWLDatatypeProperty) property);
        }
        else if (property instanceof OWLObjectProperty) {
            ontProperty = createObjectProperty((OWLObjectProperty) property);
        }
        else {
            ontProperty = createRDFProperty(property);
        }
        if (property.isAnnotationProperty()) {
            ontProperty.addRDFType(OWL.AnnotationProperty);
        }
        if (property.isDeprecated()) {
            ontProperty.addRDFType(OWL.DeprecatedProperty);
        }
        addSuperproperties(property, ontProperty);
        addPropertyValues(property, ontProperty);
        setPropertyDomain(property, ontProperty);
        if (property.isFunctional()) {
            ontProperty.addRDFType(OWL.FunctionalProperty);
        }
        if (property instanceof OWLProperty) {
            OWLProperty owlProperty = (OWLProperty) property;
            if (owlProperty.isInverseFunctional()) {
                ontProperty.addRDFType(OWL.InverseFunctionalProperty);
            }
            addEquivalentProperties(owlProperty, ontProperty);
        }
        return ontProperty;
    }


    private void adjustOntPropertyRDFType(RDFProperty property, OntProperty ontProperty) {
        if (!forReasoning && !property.getProtegeType().isSystem()) {
            OntClass metaClass = getOntClass(property.getProtegeType());
            ontProperty.setRDFType(metaClass);
            owlFullModel.add(ontProperty, RDF.type, RDF.Property);
        }
    }


    private void createOntProperties() {
        for (Iterator it = owlModel.getUserDefinedRDFProperties().iterator(); it.hasNext();) {
            RDFProperty rdfProperty = (RDFProperty) it.next();
            if (rdfProperty.isEditable()) {
                getOntProperty(rdfProperty);
            }
        }
    }


    private RDFList createOntResourceList(Collection instances) {
        Collection members = new ArrayList();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            RDFResource RDFResource = (RDFResource) it.next();
            OntResource ontResource = getOntResource(RDFResource);
            members.add(ontResource);
        }
        return ontModel.createList(members.iterator());
    }


    private void createPrefixes(OntModel ontModel) {
        final Set set = ontModel.getNsPrefixMap().keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            ontModel.removeNsPrefix(prefix);
        }
        String defaultNS = owlModel.getNamespaceManager().getDefaultNamespace();
        if (defaultNS != null) {
        	ontModel.setNsPrefix("", defaultNS);
        }
        for (Iterator it = owlModel.getNamespaceManager().getPrefixes().iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            String ns = owlModel.getNamespaceManager().getNamespaceForPrefix(prefix);
            ontModel.setNsPrefix(prefix, ns);
        }
    }


    private com.hp.hpl.jena.ontology.Restriction createQuantifierRestriction(OWLQuantifierRestriction restriction) {
        OntProperty property = getOntProperty(restriction.getOnProperty());
        RDFResource filler = restriction.getFiller();
        Resource jenaFiller = null;
        if (filler instanceof RDFSClass) {
            jenaFiller = getOntResource(restriction.getFiller());
        }
        else {
            if (filler instanceof OWLDataRange) {
                jenaFiller = createDataRange((OWLDataRange) filler, ontModel);
            }
            else {
                String uri = filler.getURI();
                jenaFiller = ontModel.getResource(uri);
            }
        }
        if (restriction instanceof OWLAllValuesFrom) {
            return ontModel.createAllValuesFromRestriction(null, property, jenaFiller);
        }
        else {
            return ontModel.createSomeValuesFromRestriction(null, property, jenaFiller);
        }
    }


    private OntProperty createRDFProperty(RDFProperty rdfProperty) {
        OntProperty ontProperty = ontModel.createOntProperty(rdfProperty.getURI());
        adjustOntPropertyRDFType(rdfProperty, ontProperty);
        return ontProperty;
    }


    private com.hp.hpl.jena.ontology.Restriction createRestriction(OWLRestriction restriction) {
        if (restriction instanceof OWLQuantifierRestriction) {
            return createQuantifierRestriction((OWLQuantifierRestriction) restriction);
        }
        else if (restriction instanceof OWLCardinalityBase) {
            return createCardinalityRestriction((OWLCardinalityBase) restriction);
        }
        else {
            return createHasValueRestriction((OWLHasValue) restriction);
        }
    }


    private void ensureProtegeMetaOntologyImported() {
        Ontology defaultOntology = getDefaultOntology();
        Resource o = ontModel.getResource(ProtegeNames.PROTEGE_OWL_ONTOLOGY);
        if (!Jena.set(defaultOntology.listImports()).contains(o)) {
            defaultOntology.addImport(o);
            ontModel.getDocumentManager().loadImport(ontModel, ProtegeNames.PROTEGE_OWL_ONTOLOGY);
        }
    }


    private Ontology getDefaultOntology() {
        return (Ontology) ontModel.listOntologies().next();
    }


    private Individual getIndividual(RDFResource rdfResource) {
        if (rdfResource.isAnonymous()) {
            Individual individual = (Individual) anonMap.get(rdfResource);
            if (individual != null) {
                return individual;
            }
            else {
                return createIndividual(rdfResource, true);
            }
        }
        else {
            Individual individual = ontModel.getIndividual(rdfResource.getURI());
            if (individual == null) {
                individual = createIndividual(rdfResource, false);
            }
            return individual;
        }
    }


    private Iterator getRDFSClassIterator() {
        Collection collection = null;
        if (targetClses != null) {
            collection = targetClses;
        }
        else {
            collection = owlModel.getUserDefinedRDFSNamedClasses();
            collection.add(owlModel.getOWLThingClass());
        }
        classCount = collection.size();
        return collection.iterator();
    }

    /*
     * I am unhappy because we are owl fullish - what is an individual exactly.
     * This logic seems somehow broken.
     * If we do all instances of rdf individual we get too much if we do all instances
     * of owl individual we might get too little.
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    private Iterator<RDFResource> getRDFSIndividualIterator() {
        Collection values = ((Cls) owlModel.getOWLThingClass()).getInstances();
        Set<RDFResource> individuals = new HashSet<RDFResource>();
        for (Object o : values) {
            if (o instanceof RDFResource 
                    && !(o instanceof RDFProperty) && !(o instanceof RDFSNamedClass) && !(o instanceof OWLOntology)
                    && !(o instanceof OWLAnonymousClass) && !(o instanceof edu.stanford.smi.protegex.owl.model.RDFList) && !(o instanceof OWLAllDifferent)
                    && !((RDFResource) o).isSystem()
                    && !((RDFResource) o).hasDirectType(owlModel.getSystemFrames().getOwlOntologyPointerClass())) {
                individuals.add((RDFResource) o);
            }
        }
        return individuals.iterator();
    }

    protected OntClass getOntClass(RDFSClass rdfsClass) {
        if (rdfsClass instanceof RDFSNamedClass) {
            if (rdfsClass.isIncluded()) {
                Resource resource = ontModel.getResource(rdfsClass.getURI());
                if (!resource.hasProperty(RDF.type, RDFS.Class) &&
                    !resource.hasProperty(RDF.type, OWL.Class)) {
                    owlFullModel.add(ontModel.getResource(rdfsClass.getURI()), RDF.type, OWL.Class);
                }
            }
            OntClass ontClass = ontModel.getOntClass(rdfsClass.getURI());
            if (ontClass == null) {
                ontClass = createNamedClass((RDFSNamedClass) rdfsClass);
            }
            return ontClass;
        }
        else {
            OntClass ontClass = createAnonymousClass((OWLAnonymousClass) rdfsClass);
            addPropertyValues(rdfsClass, ontClass);
            return ontClass;
        }
    }


    public OntModel getOntModel() {
        return ontModel;
    }


    private OntProperty getOntProperty(RDFProperty property) {
        String uri = property.getURI();
        Resource res = ontModel.getResource(uri);
        if (res != null && res.canAs(OntProperty.class)) {
            return (OntProperty) res.as(OntProperty.class);
        }
        else {
            return createOntProperty(property);
        }
    }


    private OntResource getOntResource(RDFResource rdfResource) {
        if (rdfResource instanceof RDFSClass) {
            return getOntClass((RDFSClass) rdfResource);
        }
        else if (rdfResource instanceof RDFProperty) {
            return getOntProperty((RDFProperty) rdfResource);
        }
        else {
            return getIndividual(rdfResource);
        }
    }


    public Model getOWLFullModel() {
        return owlFullModel;
    }


    private Property getProperty(RDFProperty property) {
        if (property.isEditable()) {
            return getOntProperty(property);
        }
        else {
            return ontModel.getProperty(property.getURI());
        }
    }


    private Resource getResource(RDFResource rdfResource) {
        if (rdfResource instanceof RDFSDatatype && !rdfResource.isEditable()) {
            return ontModel.getResource(rdfResource.getURI());
        }
        else {
            return getOntResource(rdfResource);
        }
    }


    private boolean isSystemOwnSlot(Instance instance, Slot slot) {
        if (instance instanceof OWLEnumeratedClass && slot.getName().equals(OWLNames.Slot.ONE_OF)) {
            return true;
        }
        else {
            return systemOwnSlots.contains(slot);
        }
    }


    public void run(OntModel ontModel, Model owlFullModel) {
        long startTime = System.currentTimeMillis();
        if (log.isLoggable(Level.FINE)) {
            log.fine("Starting JenaCreator...");
        }
        this.ontModel = ontModel;
        this.owlFullModel = owlFullModel;
        if (progressDisplay != null) {
            progressDisplay.start();
            progressDisplay.setProgressText("Creating classes...");
        }
        createPrefixes(ontModel);
        createOntologies();
        createOntClasses();
        if (targetClses == null) {
            if (progressDisplay != null) {
                progressDisplay.setProgressText("Creating properties...");
            }
            createOntProperties();
        }
        if (progressDisplay != null) {
            progressDisplay.setProgressText("Creating individuals...");
        }
        createIndividuals();
        addTodoPropertyValues();
        createAllDifferents();
        if (log.isLoggable(Level.FINE)) {
            log.fine("Terminated after " + (System.currentTimeMillis() - startTime) + " ms");
        }
        if (progressDisplay != null) {
            progressDisplay.stop();
        }
    }


    private void createAllDifferents() {
        RDFSNamedClass adc = owlModel.getRDFSNamedClass(OWLNames.Cls.ALL_DIFFERENT);
        for (Iterator it = adc.getInstances(true).iterator(); it.hasNext();) {
            OWLAllDifferent owlAllDifferent = (OWLAllDifferent) it.next();
            if (!owlAllDifferent.isIncluded()) {
                AllDifferent allDifferent = ontModel.createAllDifferent();
                for (Iterator mit = owlAllDifferent.getDistinctMembers().iterator(); mit.hasNext();) {
                    RDFResource resource = (RDFResource) mit.next();
                    Resource r = getResource(resource);
                    allDifferent.addDistinctMember(r);
                }
            }
        }
    }


    private void setPropertyDomain(RDFProperty property, OntProperty ontProperty) {
        if (owlModel.getOWLThingClass().equals(property.getDomain(false))) {
            ontProperty.removeDomain(ontProperty.getDomain());
        }
        /*if (property.isDomainDefined()) {
            Collection domainClses = new ArrayList();
            for (Iterator it = property.getUnionDomain().iterator(); it.hasNext();) {
                Cls cls = (Cls) it.next();
                if (cls instanceof OWLNamedClass) {
                    domainClses.add(cls);
                }
            }
            if (domainClses.size() == 1) {
                RDFSClass domainClass = (RDFSClass) domainClses.iterator().next();
                ontProperty.setDomain(getOntClass(domainClass));
            }
            else if (domainClses.size() > 1) {
                RDFList operands = createOntResourceList(domainClses);
                ontProperty.setDomain(ontModel.createUnionClass(null, operands));
            }
        } */
    }
}
