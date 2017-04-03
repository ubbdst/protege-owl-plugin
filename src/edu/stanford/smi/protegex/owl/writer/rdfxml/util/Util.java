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

package edu.stanford.smi.protegex.owl.writer.rdfxml.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.visitor.Visitable;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.NativeValueComparator;
import edu.stanford.smi.protegex.owl.writer.rdfxml.renderer.RDFResourceRenderer;
import edu.stanford.smi.protegex.owl.writer.rdfxml.renderer.Vocab;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriterNamespaceManager;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: March 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * Various utility methods that are needed by the
 * resource renderer and axiom renderer etc.
 */
public class Util {

     private static Set<String> excludedPropertyNames;


    static {
        excludedPropertyNames = new HashSet<String>();
        // We handle subclassOf in a special way, so
        // that we can also deal with equivalent classes
        // using an equivalent class statement rather
        // that two subclassOf statements
        excludedPropertyNames.add(RDFSNames.Slot.SUB_CLASS_OF);
        excludedPropertyNames.add(OWLNames.Slot.EQUIVALENT_CLASS);

        // Type is handled in a special way because
        // we use the abbreviated form for the first
        // type that we encounter and then list the rest
        excludedPropertyNames.add(RDFNames.Slot.TYPE);

        // Don't want classifcation status to be written into the ontology,
        // and neither do we want inferred information being saved.
        excludedPropertyNames.add(ProtegeNames.Slot.CLASSIFICATION_STATUS);
        excludedPropertyNames.add(ProtegeNames.Slot.INFERRED_SUBCLASSES);
        excludedPropertyNames.add(ProtegeNames.Slot.INFERRED_SUPERCLASSES);
        excludedPropertyNames.add(ProtegeNames.Slot.INFERRED_TYPE);

        // We render owl:AllDifferent from members using
        // an owl:distinctMemebers and rdf:parseType="Collection"
        // rather than using an rdf:List
        excludedPropertyNames.add(OWLNames.Slot.DISTINCT_MEMBERS);

        // The following are part of other OWL anonymous class
        // constructs, which we render in a more formatted fashion
        excludedPropertyNames.add(OWLNames.Slot.ALL_VALUES_FROM);
        excludedPropertyNames.add(OWLNames.Slot.CARDINALITY);
        excludedPropertyNames.add(OWLNames.Slot.COMPLEMENT_OF);

        excludedPropertyNames.add(OWLNames.Slot.HAS_VALUE);
        excludedPropertyNames.add(OWLNames.Slot.INTERSECTION_OF);
        excludedPropertyNames.add(OWLNames.Slot.MAX_CARDINALITY);
        excludedPropertyNames.add(OWLNames.Slot.MIN_CARDINALITY);
        excludedPropertyNames.add(OWLNames.Slot.ON_PROPERTY);
        excludedPropertyNames.add(OWLNames.Slot.ONE_OF);
        excludedPropertyNames.add(OWLNames.Slot.SOME_VALUES_FROM);
        excludedPropertyNames.add(OWLNames.Slot.UNION_OF);

        excludedPropertyNames.add(OWLNames.OWL_PREFIX);
        excludedPropertyNames.add(OWLNames.Slot.ONTOLOGY_PREFIXES);
        
        excludedPropertyNames.add(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY);

        // Handle imports in a special way
        excludedPropertyNames.add(OWLNames.Slot.IMPORTS);

        // Handle domain in a special way to filter out owl:Thing
        excludedPropertyNames.add(RDFSNames.Slot.DOMAIN);
    }
    
    
    @SuppressWarnings("deprecation")
    public static boolean isExcludedResource(RDFResource resource) {
        if (resource.isSystem() || resource.isAnonymous()) {
            return true;
        }
        OWLModel model = resource.getOWLModel();
        if (resource.hasDirectType(model.getSystemFrames().getOwlOntologyPointerClass())) {
            return true;
        }
        return false;
    }


    /**
     * Inserts a property-value triple for a specified resource.
     *
     * @param resource The resource that the property-value triple
     *                 is to be inserted for. It is expected that the resource element
     */
    public static void insertProperties(RDFResource resource,
                                        TripleStore tripleStore,
                                        XMLWriter writer, 
                                        boolean sort) throws IOException {
        Collection<RDFProperty> properties;
        if (sort) {
            properties = new TreeSet<RDFProperty>();
            for (Object o : resource.getRDFProperties()) {
                if (o instanceof RDFProperty) {
                    properties.add((RDFProperty) o);
                }
            }
        }
        else {
            properties = resource.getRDFProperties();
        }
        for (RDFProperty curProp :properties) {
            if (excludedPropertyNames.contains(curProp.getName()) == false) {
                Collection values;
                if (sort) {
                    values = new TreeSet(new NativeValueComparator());
                    values.addAll(resource.getPropertyValues(curProp));
                }
                else {
                    values = resource.getPropertyValues(curProp);
                }
                for (Object curVal : values) {
                    if (tripleStore.contains(resource, curProp, curVal)) {
                        if (curVal instanceof RDFResource || curVal instanceof RDFExternalResource) {
                            insertResourceAsElement(curProp, writer);
                            Visitable valRes = (Visitable) curVal;
                            inlineObject(valRes, tripleStore, writer);
                            writer.writeEndElement(); // end of property predicate element
                        }
                        else {
                            insertResourceAsElement(curProp, writer);
                            RDFSLiteral literal = resource.getOWLModel().asRDFSLiteral(curVal);
                            if (literal.getLanguage() != null) {
                                writer.writeAttribute(Vocab.XML_LANG, literal.getLanguage());
                            }
                            else {
                                writer.writeAttribute(RDFNames.Slot.DATATYPE, literal.getDatatype().getURI());
                            }
                            writer.writeTextContent(literal.toString());
                            writer.writeEndElement();
                        }
                    }
                }

            }
        }
    }


    /**
     * Inserts the object of a triple.  If the object can be inlined
     * as an attribute (for example if it is a named class) it will be.
     *
     * @param object The object of a triple to be inserted.
     * @param writer The XMLWriter
     */
    public static void inlineObject(Visitable object,
                                    TripleStore tripleStore,
                                    XMLWriter writer) throws IOException {
        InlineResourceChecker inlineResourceChecker = new InlineResourceChecker();
        object.accept(inlineResourceChecker);
        if (inlineResourceChecker.isCanInline()) {
            if (object instanceof RDFResource) {
                RDFResource rdfRes = (RDFResource) object;
                insertResourceAttribute(rdfRes, writer);
            }
            else if (object instanceof RDFExternalResource) {
                writer.writeAttribute(RDFNames.Slot.RESOURCE, ((RDFExternalResource) object).getResourceURI());
            }
        }
        else {
            new RDFResourceRenderer(object, tripleStore, writer).write();

        }
    }


    /**
     * Inserts an rdf:about attribute for the specified resource.  If the resource
     * is in the rdfwriter's default namespace then the attribute value will be
     * a # plus the local name.  If the resource is not in the rdfwriter's default
     * namespace then the attribute value will be the full resource URI.
     *
     * @param resource The resource
     * @param writer   The rdfwriter
     */
    public static void insertAboutAttribute(RDFResource resource,
                                            XMLWriter writer) throws IOException {
        writer.writeAttribute(RDFNames.Slot.ABOUT, getResourceAttributeName(resource, writer));
    }


    /**
     * Inserts an rdf:ID attribute for the specified resource.  If the
     * resource is in the rdfwriter's default namespace then the local name is used.
     * If the resource is not in the rdfwriter's default namespace then the full
     * resource URI is used.
     *
     * @param resource The resource
     * @param writer   The rdfwriter
     */
    public static void insertIDOrAboutAttribute(RDFResource resource,
                                                TripleStore tripleStore,
                                                XMLWriter writer) throws IOException {
        String name = getResourceAttributeName(resource, writer);
        if (name.startsWith("#")) {
            // Can use rdf:ID because this is a fragment
            writer.writeAttribute(RDFNames.Slot.ID, name.substring(1, name.length()));
        }
        else {
            // Must use the full URI
            writer.writeAttribute(RDFNames.Slot.ABOUT, name);
        }
    }


    /**
     * Inserts an rdf:resource attribute for the specified resource.  If the resource
     * is in the rdfwriter's default namespace then the attribute value will be
     * a # with the local name concatenated.  If the resource is not in the rdfwriter's default
     * namespace then the attribute value will be the full resource URI.
     *
     * @param resource The resource
     * @param writer   The rdfwriter
     */
    public static void insertResourceAttribute(RDFResource resource,
                                               XMLWriter writer) throws IOException {
        writer.writeAttribute(RDFNames.Slot.RESOURCE, getResourceAttributeName(resource, writer));
    }


    /**
     * Gets the value for a resource attribute value such as rdf:about or rdf:resource.
     * If possible, the resource name is written as a relative URI that is resolved against
     * the xml:base of the document.
     *
     * @param resource The resource
     */
    public static String getResourceAttributeName(RDFResource resource, XMLWriter writer) {
        String name;
        if (resource.getURI().startsWith(writer.getXMLBase())) {
            name = resource.getURI().substring(writer.getXMLBase().length(), resource.getURI().length());
        }
        else {
            name = resource.getURI();
        }
        return name;
    }


    /**
     * Creates a new element, with a tag name that reflects the default
     * namespace of the <code>XMLWriter</code>
     *
     * @param resource The resource to be inserted as an element.
     * @param writer   The <code>XML</code> rdfwriter that the element will be written to.
     */
    public static void insertResourceAsElement(RDFResource resource,
                                               XMLWriter writer) throws IOException {
        writer.writeStartElement(resource.getNamespace(), resource.getLocalName());
    }


    /**
     * Checks whether a resource is in the default namespace for an
     * <code>XMLWriter</code>
     *
     * @param resource The resource to be checked.
     * @param writer   The <code>XMLWriter</code> that the resource's namespace should
     *                 be checked against.
     * @return <code>true</code> if the resource is in the rdfwriter's default namespace
     *         or <code>false</code> if the resource is not in the rdfwriter's default namespace.
     */
    public static boolean isInDefaultNamespace(RDFResource resource, XMLWriter writer) {
        return writer.getNamespacePrefixes().getDefaultNamespace().equals(resource.getNamespace());
    }


    /**
     * Gets the namespace prefixes for the combination of a
     * <code>NamespaceManager</code> and <code>XMLWriter</code>.
     * The default namespace for the rdfwriter may not be the same as the
     * default namespace in the namespace manager, so the result of
     * this method does not include the prefix for the default namespace
     * in the rdfwriter.
     *
     * @param nsm              The <code>NamspaceManager</code>
     * @param defaultNamespace The default namespace
     * @return A <code>Map</code> of <code>String</code> pairs.  The keyset
     *         contains the prefixes.
     */
    public static XMLWriterNamespaceManager getNamespacePrefixes(NamespaceManager nsm, String defaultNamespace) {    	
        XMLWriterNamespaceManager xmlWriterNamespaceManager = new XMLWriterNamespaceManager(defaultNamespace);
        for (Iterator it = nsm.getPrefixes().iterator(); it.hasNext();) {
            String curPrefix = (String) it.next();
            String curNamespace = nsm.getNamespaceForPrefix(curPrefix);
            if (defaultNamespace != null && curNamespace.equals(defaultNamespace) == false) {
                xmlWriterNamespaceManager.setPrefix(curPrefix, nsm.getNamespaceForPrefix(curPrefix));
            }
        }
        // Create a prefix for the default namespace if the default namespace is
        // not equal to the Writer's default namespace
        String tsDefaultNamespace = nsm.getDefaultNamespace();
		if (tsDefaultNamespace != null && tsDefaultNamespace.equals(defaultNamespace) == false) {
            xmlWriterNamespaceManager.createPrefixForNamespace(tsDefaultNamespace);
        }
        return xmlWriterNamespaceManager;
    }

//	/**
//	 * Gets the XML base given a default namespace.  In OWL ontologies,
//	 * namspaces typically end in the hash sign (#).  When this is the
//	 * case for the default namspace, the XML base that is returned will
//	 * be the default namespace without the hash sign.  This is so that
//	 * attibutes such as rdf:about and rdf:ID resolve to absolute URIs
//	 * correctly.
//	 * @param defaultNamespace
//	 * @return The xml:base that corresponds the the default namespace.
//	 */
//	public static String getXMLBaseFromDefaultNamespace(String defaultNamespace) {
//		if(defaultNamespace.endsWith("#")) {
//			return defaultNamespace.substring(0, defaultNamespace.length() - 1);
//		}
//		else {
//			return defaultNamespace;
//		}
//	}


    /**
     * Renders the types for a resource as rdf:type triples.
     *
     * @param resource    The resource whose rdf:type triples are to
     *                    be rendered.
     * @param tripleStore The triple store that the renderer should render with
     *                    respect to - only type triples that are asserted in this triple store will be
     *                    rendered.
     * @param excludeType The one type that should not be rendered - this
     *                    method expects that this type will have been used in the RDF/XML optimisation
     *                    practice of putting one of the types as an element tag name.
     * @param writer      The <code>XMLWriter</code> that the triples will be added to.
     */
    public static void renderTypes(RDFResource resource, TripleStore tripleStore,
                                   RDFResource excludeType, XMLWriter writer)
            throws IOException {
        // Special rendering for types to make the RDF/XML more readable
        // Render types
        RDFProperty prop = resource.getOWLModel().getRDFProperty(RDFNames.Slot.TYPE);
        for (Iterator it = tripleStore.listObjects(resource, prop); it.hasNext();) {
            RDFResource curType = (RDFResource) it.next();
            if (curType.equals(excludeType) == false) {
                writer.writeStartElement(getPrefixedName(RDFNames.Slot.TYPE, tripleStore));
                Util.inlineObject(curType, tripleStore, writer);
                writer.writeEndElement();
            }
        }
    }


    public static RDFResource getType(RDFResource resource, TripleStore ts) {
        RDFResource type = null;
        RDFProperty typeProp = resource.getOWLModel().getRDFProperty(RDFNames.Slot.TYPE);
        for (Iterator it = ts.listObjects(resource, typeProp); it.hasNext();) {
            RDFResource curType = (RDFResource) it.next();
            if (curType.isAnonymous() == false) {
                type = curType;
                break;
            }
        }
        return type;
    }


    public static String getOntologyName(OWLModel model, TripleStore tripleStore) {
    	return tripleStore.getName();
    }
    
    public static String getPrefixedName(String fullName, TripleStore tripleStore) {
        return NamespaceUtil.getPrefixedName(tripleStore.getNamespaceManager(), fullName);
    }
}
