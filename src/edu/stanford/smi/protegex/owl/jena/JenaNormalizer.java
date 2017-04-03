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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.ComplementClass;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.FunctionalProperty;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.ontology.SymmetricProperty;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

/**
 * A class that is able to normalize a given OntModel.
 * Normalizing makes sure that a given OWL file (edited outside of Protege) fulfills some
 * syntactic constraints.  This process does the following:
 * <UL>
 * <LI>Converts named class expressions (enumerations, boolean classes and restrictions)
 * into normal named classes with the expression as equivalent class.</LI>
 * <LI>Turns all absolute class, property and individual names into local names, so that
 * they get the default namespace.</LI>
 * <LI>Converts multiple domain statements into a single intersectionOf</LI>
 * </UL>
 * <p/>
 * This code could clearly be optimized (generalized) to better exploit the RDF structure.
 * However, we try to make use the of the Ontology API wherever possible, so that we could
 * easier copy this functionality into other OWL APIs.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaNormalizer {
    private static transient final Logger log = Log.getLogger(JenaNormalizer.class);

    private OntModel ontModel;

    private Model owlFullModel;


    public JenaNormalizer(OntModel ontModel,
                          Model owlFullModel,
                          NamespaceManager namespaceManager) {

        this.owlFullModel = owlFullModel;
        this.ontModel = ontModel;

        Jena.ensureOWLFullModelIsLastModel(ontModel, owlFullModel);

        final String defaultNamespace = namespaceManager.getDefaultNamespace();
        standardizePrefixes(ontModel);
        alignPrefixes(ontModel, namespaceManager);
        createPrefixesForImports(ontModel, namespaceManager);
        mergeOntologies();
        ensureAnnotationPropertiesHaveRange();
        if (owlFullModel != null) {
            ensureDeprecatedClassesAreOntClasses(owlFullModel);
            ensureDeprecatedPropertiesAreOntProperties(owlFullModel);
            ensureFunctionalPropertiesAreOntProperties(owlFullModel);
            ensureSymmetricPropertiesAreOntProperties(owlFullModel);
            ensureTransitivePropertiesAreOntProperties(owlFullModel);
        }

        convertCyclicPropertyInheritance();

        assignNamesToAnonymousTopLevelClasses(ontModel, defaultNamespace);
        // assignNamesToAnonymousIndividuals(ontModel, defaultNamespace);
        convertNamedClassExpressions();
        if (owlFullModel != null) {
            assignRDFTypesToMetaclassInstances(ontModel, owlFullModel);
            ensureSuperclassesAreRDFSClasses(owlFullModel);
            ensureSuperPropertiesAreRDFProperties(owlFullModel);
        }

        removeRedundantDomains(ontModel.listDatatypeProperties());
        removeRedundantDomains(ontModel.listObjectProperties());
        convertMultipleDomainsIntoIntersection(ontModel);
        convertMultipleRangesIntoIntersection(ontModel);
    }


    /**
     * Makes sure that there is a prefix for all non-anonymous owl:Ontologies.
     * Otherwise the system runs into problems with import statements
     *
     * @param ontModel
     */
    private void createPrefixesForImports(OntModel ontModel, NamespaceManager nsm) {
        String def = ontModel.getNsPrefixURI("");
        for (Iterator it = ontModel.listOntologies(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            for (Iterator iit = ontology.listImports(); iit.hasNext();) {
                Resource o = (Resource) iit.next();
                String uri = o.getURI();
                if (!uri.endsWith("/") && !uri.endsWith("#")) {
                    uri += Jena.DEFAULT_NAMESPACE_SEPARATOR;
                }
                if (!def.equals(uri)) {
                    if (ontModel.getOntology(o.getURI()) != null) {
                        String pre = ontModel.getNsURIPrefix(uri);
                        if (pre == null || pre.length() == 0) {
                            String prefix = createImportURI(ontModel);
                            ontology.getModel().setNsPrefix(prefix, uri);
                            nsm.setPrefix(uri, prefix);
                        }
                    }
                }
            }
        }
    }


    private String createImportURI(OntModel ontModel) {
        String prefix = null;
        String prefixBase = "import";
        int i = 1;
        do {
            prefix = prefixBase + i++;
        }
        while (ontModel.getNsPrefixURI(prefix) != null);
        return prefix;
    }


    private void alignPrefixes(OntModel ontModel, NamespaceManager nsm) {
        List graphs = ontModel.getSubGraphs();
        for (Iterator it = graphs.iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            Map map = graph.getPrefixMapping().getNsPrefixMap();
            for (Iterator mit = map.keySet().iterator(); mit.hasNext();) {
                String localPrefix = (String) mit.next();
                if (localPrefix.length() > 0) {
                    String uri = (String) map.get(localPrefix);
                    String globalPrefix = ontModel.getNsURIPrefix(uri);
                    if (!localPrefix.equals(globalPrefix)) {
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("Prefix mismatch " + localPrefix + " aligned to " + globalPrefix + " for namespace " + uri);
                        }
                        graph.getPrefixMapping().removeNsPrefix(localPrefix);
                        graph.getPrefixMapping().setNsPrefix(globalPrefix, uri);
                        nsm.setPrefix(uri, globalPrefix);
                        nsm.removePrefix(localPrefix);
                    }
                }
            }
        }
    }


    private void ensureDeprecatedClassesAreOntClasses(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listSubjectsWithProperty(RDF.type, OWL.DeprecatedClass)).iterator(); it.hasNext();) {
            Resource c = (Resource) it.next();
            if (!c.canAs(OntClass.class)) {
                ontModel.add(c, RDF.type, RDFS.Class);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ Made deprecated class " + c + " an RDFS class");
                }
            }
        }
    }


    private void ensureDeprecatedPropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listSubjectsWithProperty(RDF.type, OWL.DeprecatedProperty)).iterator(); it.hasNext();) {
            Resource property = (Resource) it.next();
            if (!property.canAs(OntProperty.class)) {
                ontModel.add(property, RDF.type, RDF.Property);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ Made deprecated property " + property + " an RDF property");
                }
            }
        }
    }


    private void ensureFunctionalPropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listFunctionalProperties()).iterator(); it.hasNext();) {
            FunctionalProperty property = (FunctionalProperty) it.next();
            if (!hasValidPropertyType(property)) {
                ontModel.add(property, RDF.type, RDF.Property);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ Made functional property " + property + " an RDF property");
                }
            }
        }
    }


    private boolean hasValidPropertyType(Property property) {
        for (StmtIterator it = property.listProperties(RDF.type); it.hasNext();) {
            Statement s = it.nextStatement();
            Resource object = (Resource) s.getObject();
            if (object.equals(OWL.ObjectProperty) ||
                    object.equals(OWL.DatatypeProperty) ||
                    object.equals(RDF.Property) ||
                    !object.getURI().startsWith(OWL.NS)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Needed for pure RDF files that define subclasses of classes that are
     * not imported.  This method will make these superclasses rdfs:Classes.
     *
     * @param owlFullModel the Model to write the additional rdf:type triples to
     */
    private void ensureSuperclassesAreRDFSClasses(Model owlFullModel) {
        for (Iterator it = ontModel.listObjectsOfProperty(RDFS.subClassOf); it.hasNext();) {
            RDFNode node = (RDFNode) it.next();
            if (!Jena.canAsOntClass(node)) {
                if (node.canAs(Resource.class)) {
                    owlFullModel.add((Resource) node.as(Resource.class), RDF.type, RDFS.Class);
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("+ Made non-imported superclass " + node + " an " + RDFS.Class);
                    }
                }
            }
        }
    }


    /**
     * Needed for pure RDF files that define sub-properties of properties that are
     * not imported.  This method will make these superclasses rdf:Property.
     *
     * @param owlFullModel the Model to write the additional rdf:type triples to
     */
    private void ensureSuperPropertiesAreRDFProperties(Model owlFullModel) {
        for (Iterator it = ontModel.listObjectsOfProperty(RDFS.subPropertyOf); it.hasNext();) {
            RDFNode node = (RDFNode) it.next();
            if (!node.canAs(OntProperty.class)) {
                if (node.canAs(Resource.class)) {
                    owlFullModel.add((Resource) node.as(Resource.class), RDF.type, RDF.Property);
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("+ Made non-imported superclass " + node + " an " + RDF.Property);
                    }
                }
            }
        }
    }


    private void ensureSymmetricPropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listSymmetricProperties()).iterator(); it.hasNext();) {
            SymmetricProperty property = (SymmetricProperty) it.next();
            if (!hasValidPropertyType(property)) {
                ontModel.add(property, RDF.type, OWL.ObjectProperty);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ Made symmetric property " + property + " an object property");
                }
            }
        }
    }


    private void ensureTransitivePropertiesAreOntProperties(Model owlFullModel) {
        for (Iterator it = Jena.set(ontModel.listTransitiveProperties()).iterator(); it.hasNext();) {
            TransitiveProperty property = (TransitiveProperty) it.next();
            if (!hasValidPropertyType(property)) {
                ontModel.add(property, RDF.type, OWL.ObjectProperty);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ Made transitive property " + property + " an object property");
                }
            }
        }
    }



    private void assignNamesToAnonymousTopLevelClasses(OntModel ontModel, String defaultNamespace) {
        int index = 1;
        for (Iterator it = Jena.set(listAnonTopLevelClasses(ontModel)).iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            String name = AbstractOWLModel.getNextAnonymousResourceNameStatic();
            ResourceUtils.renameResource(ontClass, name);
            if (log.isLoggable(Level.FINE)) {
                log.fine("* Assigned name " + name + " to anonymous top-level class " + ontClass);
            }
        }
    }


    public static void assignRDFTypesToMetaclassInstances(OntModel ontModel, Model owlFullModel) {
        int i = 0;
        boolean repeat = false;
        do {
            final Set individuals = Jena.set(ontModel.listIndividuals());
            repeat = false;
            for (Iterator it = individuals.iterator(); it.hasNext();) {
                Individual individual = (Individual) it.next();
                if (++i % 100 == 0) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("- Assigning RDF types to individual " + i + " of " + individuals.size());
                    }
                }
                if (Jena.canAsOntClass(individual)) {
                    repeat = ensureRDFType(individual, OWL.Class, owlFullModel) || repeat;
                }
                else if (Jena.canAsObjectProperty(individual)) {
                    repeat = ensureRDFType(individual, OWL.ObjectProperty, owlFullModel) || repeat;
                }
                else if (Jena.canAsDatatypeProperty(individual)) {
                    repeat = ensureRDFType(individual, OWL.DatatypeProperty, owlFullModel) || repeat;
                }
            }
        }
        while (repeat);
    }


    private void convertCyclicPropertyInheritance() {
        List errors = new ArrayList();
        convertCyclicPropertyInheritance(ontModel.listDatatypeProperties(), errors);
        convertCyclicPropertyInheritance(ontModel.listObjectProperties(), errors);
        if (errors.size() > 0) {
            for (Iterator eit = errors.iterator(); eit.hasNext();) {
                Object o = eit.next();
                System.err.println("[JenaNormalizer] " + o);
            }
        }
    }


    private void convertCyclicPropertyInheritance(ExtendedIterator it, List errors) {
        while (it.hasNext()) {
            OntProperty ontProperty = (OntProperty) it.next();
            try {
                convertCyclicPropertyInheritance(ontProperty, ontProperty, new HashSet(), errors);
            }
            catch (Throwable t) {
                log.warning("Can't convertCyclicPropertyInheritance for "
                        + ontProperty.getLocalName());
            }
        }
    }


    private void convertCyclicPropertyInheritance(OntProperty start,
                                                  OntProperty ontProperty,
                                                  Set visited, Collection errors) {
        visited.add(ontProperty);
        for (Iterator it = ontProperty.listSuperProperties(true); it.hasNext();) {
            OntProperty superProperty = (OntProperty) it.next();
            if (visited.contains(superProperty)) {
            	String message = "Error: Property " + ontProperty +
                        " has a cyclic superproperty relation with " + superProperty +
                        " - please use owl:equivalentProperty instead!";
            	Log.getLogger().warning(message);
            	errors.add(new MessageError(message));
            }
            else {
                Set newSet = new HashSet(visited);
                convertCyclicPropertyInheritance(start, superProperty, newSet, errors);
            }
        }
    }


    private void convertMultipleDomainsIntoIntersection(OntModel ontModel) {
        convertMultipleDomainsIntoIntersection(ontModel.listDatatypeProperties());
        convertMultipleDomainsIntoIntersection(ontModel.listObjectProperties());
    }


    private void convertMultipleDomainsIntoIntersection(Iterator ontProperties) {
        while (ontProperties.hasNext()) {
            OntProperty ontProperty = (OntProperty) ontProperties.next();
            Set domainSet = Jena.set(ontProperty.listDomain());
            if (domainSet.size() > 1) {
                if (log.isLoggable(Level.FINE)) {
                log.fine("* Converted multiple domains for " + ontProperty.getLocalName() +
                    " into an intersection class");
                }
                RDFList list = ontModel.createList(domainSet.iterator());
                IntersectionClass intersectionClass = ontModel.createIntersectionClass(null, list);
                ontProperty.setDomain(intersectionClass);
            }
        }
    }


    private void convertMultipleRangesIntoIntersection(OntModel ontModel) {
        // convertMultipleRangesIntoIntersection(ontModel.listDatatypeProperties());
        convertMultipleRangesIntoIntersection(ontModel.listObjectProperties());
    }


    private void convertMultipleRangesIntoIntersection(Iterator ontProperties) {
        while (ontProperties.hasNext()) {
            OntProperty ontProperty = (OntProperty) ontProperties.next();
            Set rangeSet = Jena.set(ontProperty.listRange());
            if (rangeSet.size() > 1) {
                RDFList list = ontModel.createList(rangeSet.iterator());
                IntersectionClass intersectionClass = ontModel.createIntersectionClass(null, list);
                ontProperty.setRange(intersectionClass);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("* Converted multiple ranges for " + ontProperty.getLocalName() +
                        " into an intersection class");
                }
            }
        }
    }


    private void convertNamedClassExpressions() {
        Collection set = Jena.set(ontModel.listNamedClasses());
        for (Iterator it = set.iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            if (Jena.isSystemResource(ontClass)) {
                continue;
            }
            OntClass equivalentClass = null;
            if (ontClass.isComplementClass()) {
                equivalentClass = createComplementClass(ontClass.asComplementClass());
            }
            else if (ontClass.isEnumeratedClass()) {
                equivalentClass = createEnumeratedClass(ontClass); //.asEnumeratedClass());
            }
            else if (ontClass.isIntersectionClass()) {
                equivalentClass = createIntersectionClass(ontClass.asIntersectionClass());
            }
            else if (ontClass.isUnionClass()) {
                equivalentClass = createUnionClass(ontClass.asUnionClass());
            }
            else if (ontClass.isRestriction()) {
                equivalentClass = createRestriction(ontClass.asRestriction());
            }

            if (equivalentClass != null) {
                if (Jena.isImportedResource(ontModel, owlFullModel, ontClass)) {
                    Graph homeGraph = Jena.getHomeGraph(ontModel, ontClass);
                    homeGraph.add(new Triple(ontClass.asNode(), OWL.equivalentClass.asNode(), equivalentClass.asNode()));
                    for (Iterator ss = Jena.set(equivalentClass.listProperties()).iterator(); ss.hasNext();) {
                        Statement statement = (Statement) ss.next();
                        ontModel.remove(statement);
                        Node subject = statement.getSubject().asNode();
                        Node predicate = statement.getPredicate().asNode();
                        Node object = statement.getObject() == null ? null : statement.getObject().asNode();
                        Triple triple = new Triple(subject, predicate, object);
                        homeGraph.add(triple);
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("  * Moved triple into imported graph: " + triple);
                        }
                    }
                }
                else {
                    ontClass.addEquivalentClass(equivalentClass);
                }
            }
        }
    }


    private AllValuesFromRestriction createAllValuesFromRestriction(AllValuesFromRestriction base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous AllValuesFromRestriction for " + base);
        }
        AllValuesFromRestriction result = ontModel.createAllValuesFromRestriction(null, base.getOnProperty(), base.getAllValuesFrom());
        base.removeAll(ontModel.getProfile().ALL_VALUES_FROM());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private CardinalityRestriction createCardinalityRestriction(CardinalityRestriction base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous OWLCardinalityBase for " + base);
        }
        CardinalityRestriction result = ontModel.createCardinalityRestriction(null, base.getOnProperty(), base.getCardinality());
        base.removeAll(ontModel.getProfile().CARDINALITY());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private ComplementClass createComplementClass(ComplementClass base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous ComplementClass for " + base);
        }
        OntResource operand = base.getOperand();
        base.removeAll(ontModel.getProfile().COMPLEMENT_OF());
        return ontModel.createComplementClass(null, operand);
    }


    private EnumeratedClass createEnumeratedClass(OntClass enumeratedClass) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous EnumeratedClass for " + enumeratedClass);
        }
        Property oneOfProperty = ontModel.getProperty(OWL.oneOf.getURI());
        Statement s = enumeratedClass.getProperty(oneOfProperty);
        RDFList members = (RDFList) s.getObject().as(RDFList.class);
        ontModel.remove(s);
        return ontModel.createEnumeratedClass(null, members);
    }


    private HasValueRestriction createHasValueRestriction(HasValueRestriction base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous HasValueRestriction for " + base);
        }
        HasValueRestriction result =
                ontModel.createHasValueRestriction(null, base.getOnProperty(), base.getHasValue());
        base.removeAll(ontModel.getProfile().HAS_VALUE());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private IntersectionClass createIntersectionClass(IntersectionClass intersectionClass) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous IntersectionClass for " + intersectionClass);
        }
        RDFList operands = intersectionClass.getOperands();
        IntersectionClass result = ontModel.createIntersectionClass(null, operands);
        intersectionClass.removeAll(ontModel.getProfile().INTERSECTION_OF());
        return result;
    }


    private MaxCardinalityRestriction createMaxCardinalityRestriction(MaxCardinalityRestriction base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous MaxCardinalityRestriction for " + base);
        }
        MaxCardinalityRestriction result = ontModel.createMaxCardinalityRestriction(null, base.getOnProperty(), base.getMaxCardinality());
        base.removeAll(ontModel.getProfile().CARDINALITY());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private MinCardinalityRestriction createMinCardinalityRestriction(MinCardinalityRestriction base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous MinCardinalityRestriction for " + base);
        }
        MinCardinalityRestriction result = ontModel.createMinCardinalityRestriction(null, base.getOnProperty(), base.getMinCardinality());
        base.removeAll(ontModel.getProfile().CARDINALITY());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private Restriction createRestriction(Restriction restriction) {
        if (restriction.isAllValuesFromRestriction()) {
            return createAllValuesFromRestriction(restriction.asAllValuesFromRestriction());
        }
        else if (restriction.isSomeValuesFromRestriction()) {
            return createSomeValuesFromRestriction(restriction.asSomeValuesFromRestriction());
        }
        else if (restriction.isHasValueRestriction()) {
            return createHasValueRestriction(restriction.asHasValueRestriction());
        }
        else if (restriction.isMaxCardinalityRestriction()) {
            return createMaxCardinalityRestriction(restriction.asMaxCardinalityRestriction());
        }
        else if (restriction.isMinCardinalityRestriction()) {
            return createMinCardinalityRestriction(restriction.asMinCardinalityRestriction());
        }
        else if (restriction.isCardinalityRestriction()) {
            return createCardinalityRestriction(restriction.asCardinalityRestriction());
        }
        return null;
    }


    private SomeValuesFromRestriction createSomeValuesFromRestriction(SomeValuesFromRestriction base) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous SomeValuesFromRestriction for " + base);
        }
        SomeValuesFromRestriction result =
                ontModel.createSomeValuesFromRestriction(null, base.getOnProperty(), base.getSomeValuesFrom());
        base.removeAll(ontModel.getProfile().SOME_VALUES_FROM());
        base.removeAll(ontModel.getProfile().ON_PROPERTY());
        return result;
    }


    private UnionClass createUnionClass(UnionClass unionClass) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Adding anonymous UnionClass for " + unionClass);
        }
        RDFList operands = unionClass.getOperands();
        unionClass.removeAll(ontModel.getProfile().UNION_OF());
        return ontModel.createUnionClass(null, operands);
    }


    private void ensureAnnotationPropertiesHaveRange() {
        for (Iterator it = Jena.set(ontModel.listAnnotationProperties()).iterator(); it.hasNext();) {
            AnnotationProperty annotationProperty = (AnnotationProperty) it.next();
            if (!annotationProperty.canAs(DatatypeProperty.class) &&
                    !annotationProperty.canAs(ObjectProperty.class)) {
                String ns = annotationProperty.getNameSpace();
                if (!ns.equals(OWL.NS) && !ns.equals(RDFS.getURI())) {
                    Graph graph = ontModel.getGraph();
                    if (Jena.isImportedResource(ontModel, owlFullModel, annotationProperty)) {
                        graph = getHomeGraph(annotationProperty);
                    }
                    graph.add(new Triple(annotationProperty.asNode(), RDF.type.asNode(), OWL.DatatypeProperty.asNode()));
                    XSDDatatype datatype = XSDDatatype.XSDstring;
                    if (annotationProperty.getURI().equals(ProtegeNames.PROTEGE_OWL_NAMESPACE + ProtegeNames.READ_ONLY)) {
                        datatype = XSDDatatype.XSDboolean;
                    }
                    graph.add(new Triple(annotationProperty.asNode(), RDFS.range.asNode(), ontModel.getResource(datatype.getURI()).asNode()));
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("Made annotationProperty " +   annotationProperty.getURI() + " a datatype property with range " + datatype);
                    }
                }
            }
        }
    }


    private static boolean ensureRDFType(OntResource individual, Resource type, Model owlFullModel) {
        if (!individual.hasRDFType(type)) {
            owlFullModel.add(individual, RDF.type, type);
            if (log.isLoggable(Level.FINE)) {
                log.fine("+ Added " + type + " to rdf:types of " + individual + " in OWL Full model");
            }
            return true;
        }
        else {
            return false;
        }
    }


    private Graph getHomeGraph(OntResource resource) {
        Property predicate = RDF.type;
        Resource object = resource.getRDFType();
        for (Iterator it = ontModel.getSubGraphs().iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            if (graph.contains(resource.asNode(), predicate.asNode(), object.asNode())) {
                return graph;
            }
        }
        return null;
    }


    public static Iterator listAnonTopLevelClasses(final OntModel m) {
        final Set objects = Jena.set(m.listObjects());
        return m.listClasses().filterDrop(new Filter() {
            @Override
            public boolean accept(Object x) {
                OntClass c = (OntClass) x;
                return !c.isAnon() || objects.contains(c);
            }
        });
    }


    private void mergeOntologies() {
        for (Iterator it = ontModel.listOntologies(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            if (ontology.getURI() == null) {
                System.err.println("Warning: Ontology without URI detected: The correct syntax is <owl:Ontology rdf:about=\"\"> and there should be a default namespace");
            }
        }

        // TODO: Move own slot values of namesake ontologies into a single one
    }


    private void removeRedundantDomains(Iterator it) {
        while (it.hasNext()) {
            OntProperty ontProperty = (OntProperty) it.next();
            Collection domain = Jena.set(ontProperty.listDomain());
            if (domain.contains(OWL.Thing)) {
                ontProperty.removeAll(RDFS.domain);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("- Removed redundant domain from property " + ontProperty);
                }
            }
        }
    }


    private static Resource setResourceNamespace(Resource resource, String newNamespace) {
        String cmp = newNamespace + "#";
        if (!Jena.isSystemResource(resource) && !cmp.equals(resource.getNameSpace())) {
            String localName = resource.getLocalName();
            if (!AbstractOWLModel.isValidOWLFrameName(null, localName)) {
                localName = "_" + localName;
            }
            final String newURI = (Jena.isNamespaceWithSeparator(newNamespace) ? newNamespace : cmp) + localName;
            if (log.isLoggable(Level.FINE)) {
                log.fine("* Namespacing " + resource.getURI() + " to " + newURI);
            }
            return ResourceUtils.renameResource(resource, newURI);
        }
        else {
            return null;
        }
    }


    public static void standardizePrefixes(OntModel ontModel) {
        ontModel.setNsPrefix("owl", OWL.NS);
    }


    public static void unifyNamespace(OntModel ontModel, String oldNamespace, String newNamespace) {
        if (!oldNamespace.equals(newNamespace)) {
            unifyNamespaceOfOntologies(ontModel, oldNamespace, newNamespace);
            unifyNamespaceOfProperties(ontModel, oldNamespace, newNamespace);
            unifyNamespaceOfNamedClasses(ontModel, oldNamespace, newNamespace);
            unifyNamespaceOfIndividuals(ontModel, oldNamespace, newNamespace);
        }
    }


    private static void unifyNamespaceOfIndividuals(OntModel ontModel,
                                                    String oldNamespace, String newNamespace) {
        Collection individuals = Jena.set(ontModel.listIndividuals());
        unifyNamespaceOfResources(individuals.iterator(), oldNamespace, newNamespace);
        unifyNamespaceOfResources(ontModel.listObjects(), oldNamespace, newNamespace);
    }


    private static void unifyNamespaceOfResources(Iterator it, String oldNamespace, String newNamespace) {
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof Resource && ((Resource) next).canAs(Individual.class)) {
                Individual resource = (Individual) ((Resource) next).as(Individual.class);
                final String namespace = resource.getNameSpace();
                if (namespace != null && (oldNamespace == null || namespace.equals(oldNamespace))) {
                    setResourceNamespace(resource, newNamespace);
                }
            }
        }
    }


    private static void unifyNamespaceOfNamedClasses(OntModel ontModel,
                                                     String oldNamespace, String newNamespace) {
        Collection namedClasses = Jena.set(ontModel.listNamedClasses());
        for (Iterator it = namedClasses.iterator(); it.hasNext();) {
            OntClass ontClass = (OntClass) it.next();
            if (oldNamespace == null || ontClass.getNameSpace().equals(oldNamespace)) {
                setResourceNamespace(ontClass, newNamespace);
            }
        }
    }


    private static void unifyNamespaceOfOntologies(OntModel ontModel, String oldNamespace, String newNamespace) {
        Collection ontologies = Jena.set(ontModel.listOntologies());
        for (Iterator it = ontologies.iterator(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            String uri = ontology.getURI();
            if (uri != null) {
                String ontologyNS = Jena.isNamespaceWithSeparator(uri) ?
                        uri : uri + Jena.DEFAULT_NAMESPACE_SEPARATOR;
                if (ontologyNS.equals(oldNamespace)) {
                    String newURI = newNamespace;
                    if (newURI.endsWith(Jena.DEFAULT_NAMESPACE_SEPARATOR)) {
                        newURI = newNamespace.substring(0, newNamespace.length() - 1);
                    }
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("* Renaming ontology " + ontology.getURI() + " into namespace " + newURI);
                    }
                    ResourceUtils.renameResource(ontology, newURI);
                }
            }
        }
    }


    private static void unifyNamespaceOfProperties(OntModel ontModel,
                                                   String oldNamespace, String newNamespace) {
        Set properties = Jena.set(ontModel.listDatatypeProperties());
        Jena.set(properties, ontModel.listObjectProperties());
        Jena.set(properties, ontModel.listAnnotationProperties());
        for (Iterator it = properties.iterator(); it.hasNext();) {
            Property ontProperty = (Property) it.next();
            if (oldNamespace == null || ontProperty.getNameSpace().equals(oldNamespace)) {
                Resource renamed = setResourceNamespace(ontProperty, newNamespace);
                if (renamed != null) {
                    Property newProperty = (Property) renamed.as(Property.class);
                    Collection statements = Jena.set(ontModel.listStatements(null, ontProperty, (RDFNode) null));
                    for (Iterator sit = statements.iterator(); sit.hasNext();) {
                        Statement s = (Statement) sit.next();
                        s.remove();
                        ontModel.add(s.getSubject(), newProperty, s.getObject());
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("* Changed statement (" + s.getSubject() + ", " + newProperty + ", " + s.getObject());
                        }
                    }
                }
            }
        }
    }
}
