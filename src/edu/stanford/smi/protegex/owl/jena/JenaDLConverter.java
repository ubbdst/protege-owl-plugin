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

package edu.stanford.smi.protegex.owl.jena;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;

/**
 * A class that can convert a given OntModel into OWL DL.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaDLConverter {
    private static transient final Logger log = Log.getLogger(JenaDLConverter.class);
    private OntModel ontModel;


    /**
     * Constructs a new JenaDLConverter.
     *
     * @param oldModel the OntModel to convert
     * @param nsm      the current NamespaceManager
     */
    public JenaDLConverter(OntModel oldModel, NamespaceManager nsm) {
        this.ontModel = Jena.cloneOntModel(oldModel);
        new JenaNormalizer(ontModel, null, nsm);
    }


    private void addSubClasses(Collection classes, OntClass ontClass) {
        if (!classes.contains(ontClass)) {
            classes.add(ontClass);
            for (Iterator it = ontClass.listSubClasses(); it.hasNext();) {
                OntClass subClass = (OntClass) it.next();
                addSubClasses(classes, subClass);
            }
        }
    }


    private void convertAnnotationProperties() {
        for (Iterator it = Jena.set(ontModel.listAnnotationProperties()).iterator(); it.hasNext();) {
            AnnotationProperty property = (AnnotationProperty) it.next();
            removeRDFTypesFromAnnotationProperty(property);
            removeDomainFromAnnotationProperty(property);
            removeRangeFromAnnotationProperty(property);
        }
    }


    /**
     * Removed the range of all ObjectProperties that have a metaclass in their range.
     */
    private void convertClassProperties() {
        convertClassProperties(OWL.Class);
        convertClassProperties(OWL.DatatypeProperty);
        convertClassProperties(OWL.ObjectProperty);
    }


    private void convertClassProperties(Resource metaClass) {
        for (Iterator it = ontModel.listObjectProperties(); it.hasNext();) {
            ObjectProperty objectProperty = (ObjectProperty) it.next();
            if (Jena.set(objectProperty.listRange()).contains(metaClass)) {
                objectProperty.removeRange(metaClass);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("- Removed " + metaClass + " from range of property " + objectProperty);
                }
            }
        }
    }


    public OntModel convertOntModel() {
        convertAnnotationProperties();
        convertMetaclasses();
        convertClassProperties();
        removeFullPropertyValues();
        // removeProtegeOntology();
        removeAnnotationsFromAnonymousClasses();
        return ontModel;
    }


    private void convertMetaclasses() {
        convertMetaclasses(OWL.Class);
        convertMetaclasses(OWL.DatatypeProperty);
        convertMetaclasses(OWL.ObjectProperty);
    }


    private void convertMetaclasses(Resource metaClass) {
        Collection subClassesOfMetaClass = new HashSet();
        for (Iterator it = ontModel.listNamedClasses(); it.hasNext();) {
            OntClass namedClass = (OntClass) it.next();
            if (namedClass.hasSuperClass(metaClass)) {
                addSubClasses(subClassesOfMetaClass, namedClass);
            }
        }
        for (Iterator it = Jena.set(ontModel.listIndividuals()).iterator(); it.hasNext();) {
            Individual individual = (Individual) it.next();
            if (subClassesOfMetaClass.contains(individual.getRDFType(true))) {
                individual.setRDFType(metaClass);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("* Set RDF type of class " + individual + " to " + metaClass);
                }
                removeFullPropertyValues(individual);
            }
        }
        for (Iterator it = subClassesOfMetaClass.iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            ontClass.remove();
            if (log.isLoggable(Level.FINE)) {
                log.fine("- Removed metaclass " + ontClass);
            }
        }
    }

    private void removeAnnotationsFromAnonymousClasses() {
        Collection ps = Jena.set(ontModel.listAnnotationProperties());
        ps.add(RDFS.seeAlso);
        ps.add(RDFS.isDefinedBy);
        ps.add(OWL.versionInfo);
        for (Iterator it = ontModel.listClasses(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            if (ontClass.isAnon()) {
                for (Iterator pit = ps.iterator(); pit.hasNext();) {
                    Property property = (Property) pit.next();
                    ontClass.removeAll(property);
                }
            }
        }
    }


    private void removeDomainFromAnnotationProperty(AnnotationProperty property) {
        final Property domainProperty = RDFS.domain;
        for (StmtIterator it = property.listProperties(domainProperty); it.hasNext();) {
            Statement stmt = it.nextStatement();
            if (log.isLoggable(Level.FINE)) {
                log.fine("- Removing domain " + stmt.getObject() + " from annotation property " + property);
            }
            it.remove();
        }
    }


    private void removeFullPropertyValues() {
        removeFullPropertyValues(ontModel.listNamedClasses());
        removeFullPropertyValues(ontModel.listDatatypeProperties());
        removeFullPropertyValues(ontModel.listObjectProperties());
        removeFullPropertyValues(ontModel.listAnnotationProperties());
    }


    private void removeFullPropertyValues(Iterator it) {
        while (it.hasNext()) {
            Resource resource = (Resource) it.next();
            removeFullPropertyValues(resource);
        }
    }


    private void removeFullPropertyValues(Resource resource) {
        for (Iterator it = Jena.set(resource.listProperties()).iterator(); it.hasNext();) {
            Statement statement = (Statement) it.next();
            Property property = statement.getPredicate();
            if (!property.hasProperty(RDF.type, OWL.AnnotationProperty)) {
                // !statement.getObject().canAs(Literal.class)) {
                String namespace = property.getNameSpace();
                if (!namespace.equals(OWL.getURI()) &&
                        !namespace.equals(RDF.getURI()) &&
                        !namespace.equals(RDFS.getURI())) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("- Removed OWL Full property value " + statement);
                    }
                    statement.remove();
                }
            }
        }
    }


    private void removeProtegeOntology() {
        boolean reload = false;
        for (Iterator it = ontModel.listOntologies(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            final Resource resource = ontModel.getResource(ProtegeNames.PROTEGE_OWL_ONTOLOGY);
            if (ontology.hasProperty(OWL.imports, resource)) {
                ontology.removeImport(resource);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("- Removed import of " + resource + " from ontology " + ontology);
                }
                reload = true;
            }
        }
        if (reload) {
            ontModel = Jena.cloneOntModel(ontModel);
        }
    }


    private void removeRangeFromAnnotationProperty(AnnotationProperty property) {
        final Property rangeProperty = RDFS.range;
        for (StmtIterator it = property.listProperties(rangeProperty); it.hasNext();) {
            Statement stmt = it.nextStatement();
            if (log.isLoggable(Level.FINE)) {
                log.fine("- Removing range " + stmt.getObject() + " from annotation property " + property);
            }
            RDFNode range = stmt.getObject();
            it.remove();
            if (range.canAs(DataRange.class)) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("- Removing DataRange " + range);
                }
                ((DataRange) range.as(DataRange.class)).remove();
            }
        }
    }


    private void removeRDFTypesFromAnnotationProperty(AnnotationProperty property) {
        final Property typeProperty = RDF.type;
        for (StmtIterator it = property.listProperties(typeProperty); it.hasNext();) {
            Statement stmt = it.nextStatement();
            Resource type = (Resource) stmt.getObject();
            if (!type.equals(OWL.AnnotationProperty)) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("- Removing type " + type + " from annotation property " + property);
                }
                it.remove();
            }
        }
    }
}
