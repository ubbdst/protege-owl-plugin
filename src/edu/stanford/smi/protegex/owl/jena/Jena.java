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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.tidy.Checker;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.NodeIteratorImpl;
import com.hp.hpl.jena.rdf.model.impl.Util;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * A collection of static utility methods for Jena.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class Jena {
    private static transient final Logger log = Log.getLogger(Jena.class);

    public static final String DEFAULT_NAMESPACE_SEPARATOR = "#";

    public final static String OWL_DL = "DL";

    public final static String OWL_FULL = "Full";

    public final static String OWL_LITE = "Lite";

    public final static String XML_TAG_HIDDEN_PROPERTY = "edu.stanford.smi.protegex.owl.jena.XML-Tag-Hidden";

    private final static Set<Resource> systemClasses = new HashSet<Resource>();

    /**
     * @deprecated use ProtegeOWL.PLUGIN_FOLDER instead
     */
    @Deprecated
    public final static String ROOT_FOLDER = "edu.stanford.smi.protegex.owl";

    public final static String DEFAULT_ONT_POLICY_FILE_PATH =
            new File(ProtegeOWL.getPluginFolder(),
                    "ont-policy.rdf").toURI().toString();

    public final static String ONT_POLICY_PROPERTY = "edu.stanford.smi.protegex.owl.jena.loader.JenaLoader.ontPolicy";


    static {
        systemClasses.add(OWL.Class);
        systemClasses.add(OWL.ObjectProperty);
        systemClasses.add(OWL.DatatypeProperty);
        systemClasses.add(OWL.Nothing);
        systemClasses.add(OWL.Thing);
        systemClasses.add(OWL.DeprecatedClass);
        systemClasses.add(OWL.DeprecatedProperty);
    }


    private static void addRDFTypes(Hashtable table, OntProperty property) {
        List types = new ArrayList();
        for (Iterator it = property.listRDFTypes(true); it.hasNext();) {
            Resource type = (Resource) it.next();
            types.add(type);
        }
        table.put(property, types);
    }


    public static DatatypeProperty asDatatypeProperty(Resource resource) {
        OntModel ontModel = (OntModel) resource.getModel();
        return (DatatypeProperty) ontModel.getResource(resource.getURI()).as(DatatypeProperty.class);
    }


    public static OntClass asOntClass(Resource resource) {
        if (resource.canAs(OntClass.class)) {
            return (OntClass) resource.as(OntClass.class);
        }
        else {
            OntModel ontModel = (OntModel) resource.getModel();
            OntClass ontClass = ontModel.getOntClass(resource.getURI());
            if (ontClass == null) {
                throw new IllegalArgumentException("Could not create OntClass");
            }
            return ontClass;
        }
    }


    public static ObjectProperty asObjectProperty(Resource resource) {
        OntModel ontModel = (OntModel) resource.getModel();
        return (ObjectProperty) ontModel.getResource(resource.getURI()).as(ObjectProperty.class);
    }


    public static OntProperty asOntProperty(Resource resource) {
        OntModel ontModel = (OntModel) resource.getModel();
        return ontModel.getOntProperty(resource.getURI());
    }


    /**
     * Checks whether a given OntResource has a certain rdf:type, or a subclass thereof.
     * This is a work-around for the lack of subsumption reasoning in Jena's default model.
     *
     * @param resource
     * @param type
     * @return true if true, false if not true :)
     */
    public static boolean canAs(RDFNode resource, Resource type) {
        if (resource.canAs(OntResource.class)) {
            final OntResource ontResource = (OntResource) resource.as(OntResource.class);
            if (ontResource.hasRDFType(type)) {
                return true;
            }
            if (type instanceof EnhNode &&
                    ((EnhNode) type).getGraph() != null &&
                    type.canAs(OntClass.class)) {
                OntClass c = (OntClass) type.as(OntClass.class);
                for (Iterator it = c.listSubClasses(true); it.hasNext();) {
                    Resource superClass = (Resource) it.next();
                    if (canAs(resource, (OntClass) superClass.as(OntClass.class))) {
                        return true;
                    }
                }
            }
            else if (ontResource.getModel() instanceof OntModel) {
                OntModel ontModel = (OntModel) ontResource.getModel();
                return canAs(ontResource, ontModel.getResource(type.getURI()));
            }
        }
        return false;
    }


    public static boolean canAsDatatypeProperty(RDFNode resource) {
        return canAs(resource, OWL.DatatypeProperty);
    }


    public static boolean canAsOntClass(RDFNode resource) {
        return resource.canAs(OntClass.class) ||
                canAs(resource, OWL.Class) ||
                canAs(resource, RDFS.Class);
    }


    public static boolean canAsObjectProperty(RDFNode resource) {
        return canAs(resource, OWL.ObjectProperty);
    }


    public static boolean canAsOntProperty(RDFNode resource) {
        return canAs(resource, RDF.Property);
    }


    public static boolean canAsOWLProperty(RDFNode resource) {
        return canAsObjectProperty(resource) || canAsDatatypeProperty(resource);
    }


    /**
     * Just creates a cloning Iterator to avoid concurrent modification exceptions.
     *
     * @param it the source Iterator
     * @return a new, independent Iterator
     */
    public static Iterator cloneIt(Iterator it) {
        List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list.iterator();
    }


    public static OntModel cloneOntModel(OntModel oldModel) {
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setReasoner(null);
        return cloneOntModel(oldModel, spec);
    }


    public static OntModel cloneOntModel(OntModel oldModel, OntModelSpec spec) {
        String ns = oldModel.getNsPrefixURI("");

        StringWriter stringWriter = new StringWriter();
        RDFWriter writer = oldModel.getWriter(FileUtils.langXMLAbbrev);
        writer.setProperty("blockRules", "propertyAttr");
        writer.setProperty("relativeURIs", "same-document");
        //Jena.dumpRDF(oldModel);
        writer.write(oldModel.getBaseModel(), stringWriter, ns);
        try {
            stringWriter.close();
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        String buffer = stringWriter.toString();

        OntModel newModel = ModelFactory.createOntologyModel(spec, null);
        Reader reader = new StringReader(buffer);
        newModel.read(reader, ns, FileUtils.langXMLAbbrev);
        try {
            reader.close();
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        return newModel;
    }


    public static Hashtable convertTypedAnnotationPropertiesIntoUntyped(OntModel ontModel) {
        Hashtable result = new Hashtable();
        Resource annotationPropertyClass = ontModel.getProfile().ANNOTATION_PROPERTY();
        for (Iterator it = ontModel.listDatatypeProperties(); it.hasNext();) {
            DatatypeProperty property = (DatatypeProperty) it.next();
            if (property.canAs(AnnotationProperty.class)) {
                addRDFTypes(result, property);
            }
        }
        for (Iterator it = ontModel.listObjectProperties(); it.hasNext();) {
            ObjectProperty property = (ObjectProperty) it.next();
            if (property.canAs(AnnotationProperty.class)) {
                addRDFTypes(result, property);
            }
        }
        for (Iterator it = result.keySet().iterator(); it.hasNext();) {
            OntProperty property = (OntProperty) it.next();
            List types = (List) result.get(property);
            for (Iterator tit = types.iterator(); tit.hasNext();) {
                Resource type = (Resource) tit.next();
                if (!type.equals(annotationPropertyClass)) {
                    property.removeRDFType(type);
                    log.info("Temporarily removed type " + type + " from " + property);
                }
            }
        }
        return result;
    }


    public static void copyPropertyValues(Resource from, Resource to) {
        for (Iterator it = from.listProperties(); it.hasNext();) {
            Property property = (Property) it.next();
            for (StmtIterator pit = from.listProperties(property); pit.hasNext();) {
                Statement s = pit.nextStatement();
                to.addProperty(property, s.getObject());
            }
        }
    }


    public static void dumpRDF(OntModel ontModel) {
        dumpRDF(ontModel, System.out);
    }
    
    public static void dumpRDF(OntModel ontModel, Logger logger, Level level) {
        if (!logger.isLoggable(level)) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        dumpRDF(ontModel, out);
        logger.log(level, "-----------------Ontology Dump Begin-----------------");
        logger.log(level, out.toString());
        logger.log(level, "----------------- Ontology Dump End -----------------");
    }


    public static void dumpRDF(OntModel ontModel, OutputStream stream) {
        String language = FileUtils.langXMLAbbrev;
        String namespace = ontModel.getNsPrefixURI("");
        RDFWriter writer = ontModel.getWriter(language);
        Jena.prepareWriter(writer, language, namespace);
        writer.write(ontModel.getBaseModel(), stream, namespace);
    }


    public static void dumpRDFTopLevel(OntModel ontModel) {
        String language = FileUtils.langXMLAbbrev;
        String namespace = ontModel.getNsPrefixURI("");
        RDFWriter writer = ontModel.getWriter(language);
        Jena.prepareWriter(writer, language, namespace);
        writer.write(ontModel, System.out, namespace);
    }


    /**
     * Simulates the import of the OWL/RDFS meta ontologies.  This method returns
     * a new Model which can be added as subgraph to an ontology, so that the
     * system properties and classes can be safely casted into OntProperty and OntClass.
     *
     * @param ontModel the OntModel to add a new sub model
     * @return the sub model that has been added
     */
    public static Model addOWLFullModel(OntModel ontModel) {
        Model newModel = ModelFactory.createDefaultModel();
        newModel.add(OWL.Thing, RDF.type, OWL.Class);
        newModel.add(OWL.Nothing, RDF.type, OWL.Class);
        newModel.add(OWL.Class, RDF.type, OWL.Class);
        newModel.add(RDF.Property, RDF.type, OWL.Class);
        newModel.add(OWL.DatatypeProperty, RDF.type, OWL.Class);
        newModel.add(OWL.ObjectProperty, RDF.type, OWL.Class);
        newModel.add(RDFS.Class, RDF.type, OWL.Class);
        newModel.add(RDF.List, RDF.type, OWL.Class);
        newModel.add(RDF.first, RDF.type, OWL.ObjectProperty);
        newModel.add(RDF.rest, RDF.type, OWL.ObjectProperty);
        newModel.add(RDF.nil, RDF.type, RDF.List);
        newModel.add(OWL.oneOf, RDF.type, RDF.Property);
        newModel.add(RDFS.Literal, RDF.type, OWL.Class);
        newModel.add(RDF.Statement, RDF.type, RDFS.Class);
        newModel.add(RDFS.Container, RDF.type, RDFS.Class);
        newModel.add(RDFS.Datatype, RDF.type, RDFS.Class);
        newModel.add(OWL.AllDifferent, RDF.type, RDFS.Class);
        newModel.add(OWL.DataRange, RDF.type, RDFS.Class);
        newModel.add(OWL.DeprecatedClass, RDF.type, RDFS.Class);
        newModel.add(OWL.DeprecatedClass, RDFS.subClassOf, RDFS.Class);
        newModel.add(OWL.DeprecatedProperty, RDF.type, RDFS.Class);
        newModel.add(OWL.DeprecatedProperty, RDFS.subClassOf, RDF.Property);
        newModel.add(OWL.AnnotationProperty, RDF.type, RDFS.Class);
        newModel.add(OWL.FunctionalProperty, RDF.type, RDFS.Class);
        newModel.add(OWL.InverseFunctionalProperty, RDF.type, RDFS.Class);
        newModel.add(OWL.SymmetricProperty, RDF.type, RDFS.Class);
        newModel.add(OWL.TransitiveProperty, RDF.type, RDFS.Class);
        newModel.add(OWL.Ontology, RDF.type, OWL.Class);
        newModel.add(newModel.getResource(RDFS.getURI() + "Alt"), RDF.type, RDFS.Class);
        newModel.add(newModel.getResource(RDFS.getURI() + "Bag"), RDF.type, RDFS.Class);
        newModel.add(newModel.getResource(RDFS.getURI() + "Seq"), RDF.type, RDFS.Class);

        newModel.add(RDF.type, RDF.type, RDF.Property);
        newModel.add(RDF.value, RDF.type, RDF.Property);
        newModel.add(RDF.subject, RDF.type, RDF.Property);
        newModel.add(RDF.predicate, RDF.type, RDF.Property);
        newModel.add(RDF.object, RDF.type, RDF.Property);

        newModel.add(OWL.sameAs, RDF.type, RDF.Property);
        newModel.add(OWL.differentFrom, RDF.type, RDF.Property);

        newModel.add(RDFS.subClassOf, RDF.type, RDF.Property);
        newModel.add(RDFS.subPropertyOf, RDF.type, RDF.Property);
        newModel.add(OWL.equivalentProperty, RDF.type, RDF.Property);
        newModel.add(RDFS.domain, RDF.type, RDF.Property);
        newModel.add(RDFS.range, RDF.type, RDF.Property);
        newModel.add(RDFS.seeAlso, RDF.type, RDF.Property);
        newModel.add(RDFS.isDefinedBy, RDF.type, RDF.Property);
        newModel.add(OWL.complementOf, RDF.type, RDF.Property);
        newModel.add(OWL.intersectionOf, RDF.type, RDF.Property);
        newModel.add(OWL.unionOf, RDF.type, RDF.Property);
        newModel.add(OWL.imports, RDF.type, RDF.Property);
        newModel.add(newModel.createProperty(OWL.NS + "valuesFrom"), RDF.type, RDF.Property);

        // Annotation properties
        newModel.add(RDFS.comment, RDF.type, RDF.Property);
        newModel.add(RDFS.isDefinedBy, RDF.type, RDF.Property);
        newModel.add(RDFS.label, RDF.type, RDF.Property);
        newModel.add(RDFS.seeAlso, RDF.type, RDF.Property);
        newModel.add(OWL.backwardCompatibleWith, RDF.type, RDF.Property);
        newModel.add(OWL.incompatibleWith, RDF.type, RDF.Property);
        newModel.add(OWL.priorVersion, RDF.type, RDF.Property);
        newModel.add(OWL.versionInfo, RDF.type, RDF.Property);

        newModel.add(OWL.onProperty, RDF.type, RDF.Property);

        ontModel.addSubModel(newModel);
        return newModel;
    }


    public static void ensureOWLFullModelIsLastModel(OntModel ontModel, Model owlFullModel) {
        ontModel.removeSubModel(owlFullModel);
        ontModel.addSubModel(owlFullModel);
    }


    /**
     * Gets the Graph where the rdf:type statement of a certain resource is.
     *
     * @param ontModel the OntModel
     * @param resource the Resource to look up
     * @return a sub graph of ontModel
     */
    public static Graph getHomeGraph(OntModel ontModel, OntResource resource) {
        Property predicate = RDF.type;
        Resource object = resource.getRDFType();
        //System.out.println("Graphs:");
        //for (Iterator it = ontModel.getSubGraphs().iterator(); it.hasNext();) {
        //    Graph graph = (Graph) it.next();
        //    System.out.println(" - " + Jena.set(graph.find(null, null, null)).size());
        //}
        for (Iterator it = ontModel.getSubGraphs().iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            if (graph.contains(resource.asNode(), predicate.asNode(), object.asNode())) {
                return graph;
            }
        }
        return ontModel.getGraph();
    }


    public static String getImportSource(OntModel ontModel, OntResource resource) {
        Graph homeGraph = getHomeGraph(ontModel, resource);
        if (homeGraph != null) {
            return homeGraph.getPrefixMapping().getNsPrefixURI("");
        }
        return null;
    }


    public static int getOWLSpecies(OntModel ontModel) {
        Checker checker = new Checker(false);
        checker.addGraphAndImports(ontModel.getGraph());
        //checker.add(ontModel);
        String sublanguage = checker.getSubLanguage();
        if (sublanguage.equalsIgnoreCase("Full")) {
            return OntModelProvider.OWL_FULL;
        }
        else if (sublanguage.equalsIgnoreCase("DL")) {
            return OntModelProvider.OWL_DL;
        }
        else {
            return OntModelProvider.OWL_LITE;
        }
    }


    public static String getOWLSpeciesString(int x) {
        if (x == OntModelProvider.OWL_DL) {
            return OWL_DL;
        }
        else if (x == OntModelProvider.OWL_LITE) {
            return OWL_LITE;
        }
        else if (x == OntModelProvider.OWL_FULL) {
            return OWL_FULL;
        }
        throw new IllegalArgumentException("Species constant must be on of OntModelProvider.OWL_xxx");
    }


    public static String getNamespaceFromURI(String namespace) {
        if (namespace.startsWith("urn:")) {
            if (!namespace.endsWith(":")) {
                namespace += ":";
            }
        }
        else if (!namespace.endsWith("/") && !namespace.endsWith("#")) {
            namespace += "#";
        }
        return namespace;
    }


    public static String getNamespaceWithoutSeparator(String namespace) {
        return namespace.substring(0, namespace.length() - 1);
    }


    public static String getURIFromNamespace(String namespace) {
        if (namespace.length() > 1) {
            //char lastChar = namespace.charAt(namespace.length() - 1);
            //if (!XMLChar.isNCName(lastChar)) {
            if (namespace.endsWith(Jena.DEFAULT_NAMESPACE_SEPARATOR) ||
                    (namespace.startsWith("urn:") && namespace.endsWith(":"))) {
                namespace = namespace.substring(0, namespace.length() - 1);
            }
        }
        return namespace;
    }


    public static boolean isDatatypeProperty(OntModel ontModel, OntProperty ontProperty) {
        Resource datatypePropertyClass = ontModel.getResource(com.hp.hpl.jena.vocabulary.OWL.DatatypeProperty.getURI());
        return Jena.canAs(ontProperty, datatypePropertyClass);
    }


    public static boolean isImportedResource(OntModel ontModel, Model owlFullModel, OntResource ontResource) {
        final Node subjectNode = ontResource.asNode();
        final Node predicateNode = RDF.type.asNode();
        if (ontResource.getRDFType() != null) {
            final Node objectNode = ontResource.getRDFType().asNode();
            final Model baseModel = ontModel.getBaseModel();
            if (baseModel.getGraph().contains(subjectNode, predicateNode, objectNode)) {
                return false;
            }
            List subs = new ArrayList(ontModel.getSubGraphs());
            subs.remove(baseModel.getGraph());
            for (Iterator it = subs.iterator(); it.hasNext();) {
                Graph graph = (Graph) it.next();
                if (graph.contains(subjectNode, predicateNode, objectNode)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static Resource getBestType(OntResource resource) {
        Iterator it = resource.listRDFTypes(true);
        Resource result = (Resource) it.next();
        while (it.hasNext()) {
            String ns = result.getNameSpace();
            if (!ns.equals(OWL.getURI().toString()) &&
                    !ns.equals(RDFS.getURI().toString()) &&
                    !ns.equals(RDF.getURI().toString())) {
                break;
            }
            result = (Resource) it.next();
        }
        return result;
    }


    public static boolean isNamespaceWithSeparator(String str) {
        if (str.length() > 0) {
            if (Util.splitNamespace(str) == str.length()) {
                try {
                    new URI(str);
                    return true;
                }
                catch (Exception ex) {
                }
            }
        }
        return false;
    }


    public static boolean isObjectProperty(OntModel ontModel, OntProperty ontProperty) {
        Resource objectPropertyClass = ontModel.getResource(com.hp.hpl.jena.vocabulary.OWL.ObjectProperty.getURI());
        return Jena.canAs(ontProperty, objectPropertyClass);
    }


    public static boolean isSystemProperty(OntProperty property) {
        return isSystemResource(property) ||
                (property.hasDomain(OWL.Class) && property.getNameSpace().equals(OWL.NS));
    }


    public static boolean isSystemResource(Resource ontResource) {
        String nameSpace = ontResource.getNameSpace();
        return (nameSpace.equals(ProtegeNames.PROTEGE_OWL_NAMESPACE) &&
                !ProtegeNames.DEFAULT_LANGUAGE.equals(ontResource.getLocalName()) &&
                !ProtegeNames.USED_LANGUAGE.equals(ontResource.getLocalName()) &&
                !ProtegeNames.TODO_PREFIX.equals(ontResource.getLocalName()) &&
                !ProtegeNames.TODO_PROPERTY.equals(ontResource.getLocalName()) &&
                !ProtegeNames.READ_ONLY.equals(ontResource.getLocalName())) ||
                nameSpace.equals(OWL.getURI()) ||
                nameSpace.equals(RDFS.getURI()) ||
                nameSpace.equals(RDF.getURI());
    }


    public static boolean isSystemClass(OntClass ontClass) {
        return systemClasses.contains(ontClass) ||
                ontClass.getNameSpace().equals(ProtegeNames.PROTEGE_OWL_NAMESPACE);
    }


    public static boolean isValidNamespace(String text) {
        if (Jena.isNamespaceWithSeparator(text) &&
                (text.startsWith("http:") || text.startsWith("urn:") || text.startsWith("file:"))) {
            try {
                URI uri = new URI(text);
                return uri.isAbsolute();
            }
            catch (Exception ex) {
            }
        }
        return false;
    }


    public static boolean isXMLLanguage(String language) {
        return FileUtils.langXMLAbbrev.equals(language) ||
                FileUtils.langXML.equals(language);
    }


    public static boolean isXMLTagHidden() {
        return ApplicationProperties.getBooleanProperty(XML_TAG_HIDDEN_PROPERTY, false);
    }


    public static boolean namespaceEndsWithSeparator(String namespace) {
        return namespace.endsWith("#") || namespace.endsWith(":");
    }

    
    //backward compatibility
    /**
     * @deprecated - Use {@link #prepareWriter(RDFWriter writer, String language, String namespace, String xmlBase)}  
     */
    @Deprecated
    public static void prepareWriter(RDFWriter writer, String language, String namespace) {
        String xmlBase = namespace;
        if (Jena.isNamespaceWithSeparator(xmlBase) && !namespace.endsWith("/")) {
            xmlBase = xmlBase.substring(0, xmlBase.length() - 1);
        }
    	
        prepareWriter(writer, language, namespace, xmlBase);
    }

    public static void prepareWriter(RDFWriter writer, String language, String namespace, String xmlBase) {
        if (FileUtils.langXMLAbbrev.equals(language) || FileUtils.langXML.equals(language)) {
            writer.setProperty("showXmlDeclaration", "" + !Jena.isXMLTagHidden());  // Suggested by Alix
            writer.setProperty("relativeURIs", "same-document");
            writer.setProperty("xmlbase", xmlBase);
            if (FileUtils.langXMLAbbrev.equals(language)) {
                writer.setProperty("blockRules", "propertyAttr");
            }
        }
    }


    /**
     * A convenience method that removes a property value with a given toString serialization
     * from an OntResource.  This method was introduced to overcome the lack of a similar
     * method in Jena.
     *
     * @param resource the OntResource to remove the property value from
     * @param property the Property to remove a value of
     * @param value    the toString value to remove
     */
    public static void removePropertyValue(OntResource resource, Property property, String value) {
        for (StmtIterator it = resource.listProperties(property); it.hasNext();) {
            Statement statement = it.nextStatement();
            RDFNode rdfNode = statement.getObject();
            if (rdfNode.toString().equals(value)) {
                resource.removeProperty(property, rdfNode);
                return;
            }
        }
    }


    /**
     * @deprecated this has a bug (it does not rename the resource in all models) -
     *             use the other renameResource method instead
     */
    @Deprecated
    public static Resource renameResource(Resource old, String uri, Model owlFullModel) {

        OntModel homeModel = (OntModel) old.getModel();

        // Create a new resource to replace old
        Resource res = (uri == null) ? homeModel.createResource() : homeModel.createResource(uri);

        Model m = homeModel.getBaseModel();
        renameResourceInModel(m, old, res);
        if (owlFullModel != null) {
            renameResourceInModel(owlFullModel, old, res);
        }

        return res;
    }


    public static Resource renameResource(OntModel ontModel, Resource old, String uri) {

        OntModel homeModel = (OntModel) old.getModel();

        // Create a new resource to replace old
        Resource res = (uri == null) ? homeModel.createResource() : homeModel.createResource(uri);

        renameResourceInGraph(ontModel.getBaseModel().getGraph(), old, res);
        for (Iterator it = ontModel.getSubGraphs().iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            renameResourceInGraph(graph, old, res);
        }

        return res;
    }


    public static void renameResourceInGraph(Graph graph, Resource old, Resource newResource) {
        List stmts = new ArrayList();

        // Add the statements that mention old as a subject
        for (Iterator i = graph.find(old.asNode(), null, null); i.hasNext();) {
            stmts.add(i.next());
        }

        // Add the statements that mention old an an object
        for (Iterator i = graph.find(null, null, old.asNode()); i.hasNext();) {
            stmts.add(i.next());
        }

        // now move the statements to refer to newResource instead of old
        for (Iterator i = stmts.iterator(); i.hasNext();) {
            Triple triple = (Triple) i.next();
            graph.delete(triple);
            Node subj = triple.getSubject().equals(old.asNode()) ?
                    newResource.asNode() : triple.getSubject();
            Node obj = triple.getObject().equals(old.asNode()) ?
                    newResource.asNode() : triple.getObject();
            graph.add(new Triple(subj, triple.getPredicate(), obj));
        }
    }


    /**
     * @deprecated wrong
     */
    @Deprecated
    public static void renameResourceInModel(Model m, Resource old, Resource newResource) {
        List stmts = new ArrayList();

        // Add the statements that mention old as a subject
        for (Iterator i = m.listStatements(old, null, (RDFNode) null); i.hasNext();) {
            stmts.add(i.next());
        }

        // Add the statements that mention old an an object
        for (Iterator i = m.listStatements(null, null, old); i.hasNext();) {
            stmts.add(i.next());
        }

        // now move the statements to refer to newResource instead of old
        for (Iterator i = stmts.iterator(); i.hasNext();) {
            Statement s = (Statement) i.next();
            s.remove();
            Resource subj = s.getSubject().equals(old) ? newResource : s.getSubject();
            RDFNode obj = s.getObject().equals(old) ? newResource : s.getObject();
            m.add(subj, s.getPredicate(), obj);
        }
    }


    public static void saveOntModel(OWLModel owlModel, File file, OntModel owldlOntModel, String message) {
        try {
            String language = FileUtils.langXMLAbbrev;
            OutputStream outputStream = new FileOutputStream(file);
            PrintStream ps = new PrintStream(outputStream);
            String namespace = owldlOntModel.getNsPrefixURI("");
            RDFWriter writer = owldlOntModel.getWriter(language);
            Jena.prepareWriter(writer, language, namespace);
            writer.write(owldlOntModel.getBaseModel(), ps, namespace);
            outputStream.close();
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, message);
        }
        catch (IOException ex) {
            ProtegeUI.getModalDialogFactory().showThrowable(owlModel, ex);
        }
    }


    /**
     * Converts an Iterator (e.g. those delivered by the Jena listXXX methods) into a List.
     *
     * @param it the Iterator to convert
     * @return a List with the same elements
     */
    public static Set set(Iterator it) {
        Set result = new HashSet();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }


    /**
     * Converts an Iterator (e.g. those delivered by the Jena listXXX methods) into a List.
     *
     * @param it the Iterator to convert
     */
    public static void set(Set set, Iterator it) {
        while (it.hasNext()) {
            set.add(it.next());
        }
    }


    public static void setXMLTagHidden(boolean value) {
        ApplicationProperties.setString(XML_TAG_HIDDEN_PROPERTY, "" + value);
    }

    /*public static boolean isSubclassOfRDFClass(OntClass ontClass) {
       for (Iterator it = ontClass.listSuperClasses(); it.hasNext();) {
           Resource res = (Resource) it.next();
           String namespace = res.getNameSpace();
           if (namespace != null) {
               log.info("Namespace: " + namespace);
               log.info("RDFS: " + RDFS.getURI().toString());
               if (namespace.equals(RDFS.getURI().toString()) ||
                       namespace.equals(RDF.getURI().toString())) {
                   log.info("Note: " + res);
                   return true;
               }
           }
       }
       return false;
   } */

    // These methods are convenience methods copied from OntResource
    // I hope they are moved into Resource later so that I can call
    // them directly


    public static RDFNode getPropertyValue(Resource resource, Property property) {
        NodeIterator it = listPropertyValues(resource, property);
        if (it.hasNext()) {
            return it.nextNode();
        }
        else {
            return null;
        }
    }


    public static NodeIterator listPropertyValues(Resource resource, Property property) {
        return new NodeIteratorImpl(resource.listProperties(property).mapWith(new ObjectMapper()), null);
    }


    /**
     * Gets the direct subclasses of a given OntClass, excluding the class itself which is
     * usually delivered by Jena.
     *
     * @param ontClass the OntClass to get the subclasses of
     * @return a Collection of subclasses (OntClass instances)
     */
    public static Collection getDirectSubClasses(OntClass ontClass) {
        List result = new ArrayList();
        for (Iterator it = ontClass.listSubClasses(true); it.hasNext();) {
            OntClass subClass = (OntClass) it.next();
            if (!subClass.equals(ontClass)) {
                result.add(subClass);
            }
        }
        return result;
    }


    /**
     * Gets the direct superclasses of a given OntClass, excluding the class itself which is
     * usually delivered by Jena.
     *
     * @param ontClass the OntClass to get the superclasses of
     * @return a Collection of superclasses (OntClass instances)
     */
    public static Collection getDirectSuperClasses(OntClass ontClass) {
        List result = new ArrayList();
        for (Iterator it = ontClass.listSuperClasses(true); it.hasNext();) {
            OntClass superClass = (OntClass) it.next();
            if (!superClass.equals(ontClass)) {
                result.add(superClass);
            }
        }
        return result;
    }


    public static Ontology getDefaultJenaOntology(String namespace, OntModel ontModel) {
        if (namespace.endsWith(DEFAULT_NAMESPACE_SEPARATOR)) {
            namespace = getNamespaceWithoutSeparator(namespace);
        }
        for (Iterator it = ontModel.listOntologies(); it.hasNext();) {
            Ontology ontology = (Ontology) it.next();
            String ns = ontology.getURI();
            if (namespace.equals(ns)) {
                return ontology;
            }
        }
        return null;
    }


    /**
     * @deprecated
     */
    @Deprecated
    public static String getOntPolicyFilePath(Project project) {
        if (project == null) {
            return DEFAULT_ONT_POLICY_FILE_PATH;
        }
        String str = project.getSources().getString(ONT_POLICY_PROPERTY);
        if (str == null) {
            return DEFAULT_ONT_POLICY_FILE_PATH;
        }
        else {
            return str;
        }
    }


    /**
     * @deprecated
     */
    @Deprecated
    public static void setOntPolicyFilePath(Project project, String path) {
        project.getSources().setString(ONT_POLICY_PROPERTY, path);
    }


    protected static class ObjectMapper implements Map1 {

        public Object map1(Object x) {
            return (x instanceof Statement) ? ((Statement) x).getObject() : x;
        }
    }


    public static void removePropertyValue(Resource resource, Property property, RDFNode value) {
        StmtIterator i = resource.getModel().listStatements(resource, property, value);
        if (i.hasNext()) {
            i.nextStatement().remove();
        }
        i.close();
    }
}
