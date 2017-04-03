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

package edu.stanford.smi.protegex.owl.inference.dig.translator;

import java.util.Iterator;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;


/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 28, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultDIGTranslator implements DIGTranslator {

    private DIGRenderer renderer;

    private DocumentBuilderFactory docBuilderFactory;

    private DocumentBuilder docBuilder;

    public static final String DL_NAMESPACE = "http://dl.kr.org/dig/2003/02/lang";

    //  public static final String DL_SCHEMA_LOCATION = "http://potato.cs.man.ac.uk/dig/level0/dig.xsd";

    //  public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

    //  public static final String XML_SCHEMA_INSTANCE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";


    public DefaultDIGTranslator() {
        this.renderer = new DefaultDIGRenderer();

        try {
            docBuilderFactory = DocumentBuilderFactory.newInstance();

            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void setReasonerIdentity(DIGReasonerIdentity reasonerIdentity) {
        renderer.setReasonerIdentity(reasonerIdentity);
    }


    public Document createTellsDocument(String kbURI) {
        Document doc = createDIGDocument(DIGVocabulary.TELLS, kbURI);
        return doc;
    }


    public Document createAsksDocument(String kbURI) {
        Document doc = createDIGDocument(DIGVocabulary.ASKS, kbURI);
        return doc;
    }


    public Document createDIGDocument(String rootTagName, String kbURI) {
        Document doc = createDIGDocument(rootTagName);

        doc.getDocumentElement().setAttribute("uri", kbURI);

        return doc;
    }


    public Document createDIGDocument(String rootTagName) {
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement(rootTagName);

        // Set up the DIG namespaces
        rootElement.setAttribute("xmlns", DL_NAMESPACE); // Default namespace

//        rootElement.setAttribute("xmlns:xs", XML_SCHEMA_NAMESPACE);

        //       rootElement.setAttribute("xmlns:xsi", XML_SCHEMA_INSTANCE_NAMESPACE);

        //       rootElement.setAttribute("xsi:schemaLocation", DL_SCHEMA_LOCATION);

        doc.appendChild(rootElement);

        return doc;
    }


    public void translateToDIG(OWLModel kb, Document doc, Node node) throws DIGReasonerException {
        renderer.render(kb, doc, node);
    }


    /**
     * Translates an element of an OWLModel to DIG
     *
     * @param i    The element to be translated
     * @param doc  The Document that the rendering will be created in
     * @param node The parent node that the dig rendereing will be appended to
     * @throws edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException
     *
     */
    public void translateToDIG(RDFResource i,
                               Document doc,
                               Node node) throws DIGReasonerException {
        if (i instanceof OWLNamedClass) {
            renderer.render((OWLNamedClass) i, doc, node);
        }
        else if (i instanceof OWLSomeValuesFrom) {
            renderer.render((OWLSomeValuesFrom) i, doc, node);
        }
        else if (i instanceof OWLAllValuesFrom) {
            renderer.render((OWLAllValuesFrom) i, doc, node);
        }
        else if (i instanceof OWLMinCardinality) {
            renderer.render((OWLMinCardinality) i, doc, node);
        }
        else if (i instanceof OWLMaxCardinality) {
            renderer.render((OWLMaxCardinality) i, doc, node);
        }
        else if (i instanceof OWLCardinality) {
            renderer.render((OWLCardinality) i, doc, node);
        }
        else if (i instanceof OWLIntersectionClass) {
            renderer.render((OWLIntersectionClass) i, doc, node);
        }
        else if (i instanceof OWLUnionClass) {
            renderer.render((OWLUnionClass) i, doc, node);
        }
        else if (i instanceof OWLComplementClass) {
            renderer.render((OWLComplementClass) i, doc, node);
        }
        else if (i instanceof OWLEnumeratedClass) {
            renderer.render((OWLEnumeratedClass) i, doc, node);
        }
        else if (i instanceof OWLObjectProperty) {
            renderer.render((OWLObjectProperty) i, doc, node);
        }
        else if (i instanceof OWLDatatypeProperty) {
            renderer.render((OWLDatatypeProperty) i, doc, node);
        }
        else if (i instanceof RDFIndividual) {
            renderer.render((RDFIndividual) i, doc, node);
        }
        else if (i instanceof OWLHasValue) {
	        renderer.render((OWLHasValue) i, doc, node);
        }
        else {
            throw new IllegalArgumentException("Don't know how to translate " + i.getClass().getName());
        }

    }


    public Iterator getDIGQueryResponseIterator(OWLModel kb, Document doc) throws DIGReasonerException {
        return new DefaultDIGQueryResponseIterator(doc, kb);
    }


    protected Element createQueryElement(Document doc, String name, String queryID) {
        Element element = doc.createElement(name);

        element.setAttribute("id", queryID);

        return element;
    }


    // Primitive concept retrieval
    public void createAllConceptNamesQuery(Document doc,
                                           String queryID) {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.ALL_CONCEPT_NAMES, queryID);

        doc.getDocumentElement().appendChild(element);
    }


    public void createAllPropertyNamesQuery(Document doc,
                                            String queryID) {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.ALL_ROLE_NAMES, queryID);

        doc.getDocumentElement().appendChild(element);
    }


    public void createAllIndividualsQuery(Document doc,
                                          String queryID) {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.ALL_INDIVIDUALS, queryID);

        doc.getDocumentElement().appendChild(element);
    }


    // Satisfiability
    public void createSatisfiableQuery(Document doc,
                                       String queryID,
                                       RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.SATISFIABLE, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createSatisfiableQuery(Document doc,
                                       String queryID,
                                       RDFSClass[] clses) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.SATISFIABLE, queryID);

        Element intersectionElement = doc.createElement(DIGVocabulary.Language.AND);

        element.appendChild(intersectionElement);

        for (int i = 0; i < clses.length; i++) {
            translateToDIG(clses[i], doc, intersectionElement);
        }

        doc.getDocumentElement().appendChild(element);
    }


    public void createSubsumesQuery(Document doc,
                                    String queryID,
                                    RDFSClass cls1,
                                    RDFSClass cls2) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.SUBSUMES, queryID);

        translateToDIG(cls1, doc, element);

        translateToDIG(cls2, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createDisjointQuery(Document doc,
                                    String queryID,
                                    RDFSClass cls1,
                                    RDFSClass cls2) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.DISJOINT, queryID);

        translateToDIG(cls1, doc, element);

        translateToDIG(cls2, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    // Concept hierarchy
    public void createDirectSuperConceptsQuery(Document doc,
                                               String queryID,
                                               RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.PARENTS, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createDirectSuperConceptsQuery(Document doc,
                                               String queryID,
                                               RDFSClass[] clses)
            throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.PARENTS, queryID);
        Element andElement = doc.createElement(DIGVocabulary.Language.AND);
        element.appendChild(andElement);
        for (int i = 0; i < clses.length; i++) {
            translateToDIG(clses[i], doc, andElement);
        }
        doc.getDocumentElement().appendChild(element);
    }


    public void createDirectSubConceptsQuery(Document doc,
                                             String queryID,
                                             RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.CHILDREN, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createAncestorConceptsQuery(Document doc,
                                            String queryID,
                                            RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.ANCESTORS, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createDescendantConceptsQuery(Document doc,
                                              String queryID,
                                              RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.DESCENDANTS, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createEquivalentConceptsQuery(Document doc,
                                              String queryID,
                                              RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.EQUIVALENT, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    // Role hierarchy
    public void createDirectSuperPropertiesQuery(Document doc,
                                                 String queryID,
                                                 OWLProperty property) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.R_PARENTS, queryID);

        translateToDIG(property, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createDirectSubPropertiesQuery(Document doc,
                                               String queryID,
                                               OWLProperty property) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.R_CHILDREN, queryID);

        translateToDIG(property, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createAncestorPropertiesQuery(Document doc,
                                              String queryID,
                                              OWLProperty property) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.R_ANCESTORS, queryID);

        translateToDIG(property, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createDescendantPropertiesQuery(Document doc,
                                                String queryID,
                                                OWLProperty property) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.R_DESCENDANTS, queryID);

        translateToDIG(property, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    // Individuals
    public void createInstancesOfConceptQuery(Document doc,
                                              String queryID,
                                              RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.INSTANCES, queryID);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createIndividualTypesQuery(Document doc,
                                           String queryID,
                                           RDFIndividual ins) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.TYPES, queryID);

        translateToDIG(ins, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createIndividualInstanceOfConceptQuery(Document doc,
                                                       String queryID,
                                                       RDFIndividual ins,
                                                       RDFSClass aClass) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.INSTANCE, queryID);

        translateToDIG(ins, doc, element);

        translateToDIG(aClass, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createPropertyFillersQuery(Document doc,
                                           String queryID,
                                           RDFIndividual ins,
                                           OWLProperty property) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.ROLE_FILLERS, queryID);

        translateToDIG(ins, doc, element);

        translateToDIG(property, doc, element);

        doc.getDocumentElement().appendChild(element);
    }


    public void createRelatedIndividualsQuery(Document doc,
                                              String queryID,
                                              OWLProperty property) throws DIGReasonerException {
        Element element = createQueryElement(doc, DIGVocabulary.Ask.RELATED_INDIVIDUALS, queryID);

        translateToDIG(property, doc, element);

        doc.getDocumentElement().appendChild(element);
    }
}

