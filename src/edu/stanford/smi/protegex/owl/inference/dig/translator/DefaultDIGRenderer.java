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

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogger;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerPreferences;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.util.ClassCommenter;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.*;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultDIGRenderer implements DIGRenderer {

    private HashSet slotsToIgnore;

    private HashSet clsesToIgnore;

    private DIGReasonerIdentity reasonerIdentity;
    
    private SWRLFactory swrlFactory;
       


    public DefaultDIGRenderer() {
        slotsToIgnore = new HashSet();

        clsesToIgnore = new HashSet();

        reasonerIdentity = null;
    }


    public void setReasonerIdentity(DIGReasonerIdentity reasonerIdentity) {
        this.reasonerIdentity = reasonerIdentity;
    }


    protected boolean renderAppropriateCls(RDFSClass aClass,
                                           Document doc,
                                           Node parentNode)
            throws DIGReasonerException {
        boolean b = false;

        if (aClass instanceof OWLNamedClass) {
            b = render((OWLNamedClass) aClass, doc, parentNode);
        } else if (aClass instanceof OWLSomeValuesFrom) {
            b = render((OWLSomeValuesFrom) aClass, doc, parentNode);
        } else if (aClass instanceof OWLAllValuesFrom) {
            b = render((OWLAllValuesFrom) aClass, doc, parentNode);
        } else if (aClass instanceof OWLHasValue) {
            b = render((OWLHasValue) aClass, doc, parentNode);
        } else if (aClass instanceof OWLMinCardinality) {
            b = render((OWLMinCardinality) aClass, doc, parentNode);
        } else if (aClass instanceof OWLMaxCardinality) {
            b = render((OWLMaxCardinality) aClass, doc, parentNode);
        } else if (aClass instanceof OWLCardinality) {
            b = render((OWLCardinality) aClass, doc, parentNode);
        } else if (aClass instanceof OWLIntersectionClass) {
            b = render((OWLIntersectionClass) aClass, doc, parentNode);
        } else if (aClass instanceof OWLUnionClass) {
            b = render((OWLUnionClass) aClass, doc, parentNode);
        } else if (aClass instanceof OWLComplementClass) {
            b = render((OWLComplementClass) aClass, doc, parentNode);
        } else if (aClass instanceof OWLEnumeratedClass) {
            b = render((OWLEnumeratedClass) aClass, doc, parentNode);
        } else {
            logErrorOrWarning(aClass,
                    "Don't know how to render the type: " + aClass.getClass().getName(),
                    "Ignoring this type and attempting to continue.");
        }

        return b;
    }


    protected boolean renderAppropriateSlot(Slot slot,
                                            Document doc,
                                            Node parentNode) throws DIGReasonerException {
        boolean ret = true;

        if (slot instanceof OWLObjectProperty) {
            ret = render((OWLObjectProperty) slot, doc, parentNode);
        } else if (slot instanceof OWLDatatypeProperty) {
            ret = render((OWLDatatypeProperty) slot, doc, parentNode);
        }

        return ret;
    }


    public void render(OWLModel kb,
                       Document doc,
                       Node parentNode)
            throws DIGReasonerException {

        clsesToIgnore.removeAll(clsesToIgnore);

        slotsToIgnore.removeAll(slotsToIgnore);
        
        //If this is a SWRLModel, add to the ignored classes, properties and individuals all the SWRL concepts.
        updateIgnoredCollections(kb);
        
        // Render classes and their axioms
        renderClasses(kb, doc, parentNode);

        // Render property defs
        renderProperties(kb, doc, parentNode);

        // Render invidual defs
        renderIndividuals(kb, doc, parentNode);

        // Render the All Different instances
        Collection allDifferent = kb.getOWLAllDifferents();
        if (allDifferent.isEmpty() == false) {
            Iterator allDifferentIt = allDifferent.iterator();
            
            while (allDifferentIt.hasNext()) {
            	 Element allDifferentElement = doc.createElement(DIGVocabulary.Tell.ALL_DIFFERENT);
                OWLAllDifferent curOWLAllDifferent = (OWLAllDifferent) allDifferentIt.next();
                if (isSupportedTellElement(DIGVocabulary.Tell.ALL_DIFFERENT) == true) {
                    Iterator distinctMembersIt = curOWLAllDifferent.getDistinctMembers().iterator();
                    while (distinctMembersIt.hasNext()) {
                        RDFIndividual curInstance = (RDFIndividual) distinctMembersIt.next();
                        render(curInstance, doc, allDifferentElement);
                    }
                } else {
                    renderFakedDifferentFrom(curOWLAllDifferent.getDistinctMembers(), doc, parentNode);
                }
                if (allDifferentElement.getChildNodes().getLength() > 0) {
                    parentNode.appendChild(allDifferentElement);
                }
            }

        }
    }



	protected void renderFakedDifferentFrom(Collection differentFromIndividuals, Document doc, Node parentNode) throws DIGReasonerException {
        // We want to put each individual into its
        // own iset element, and make these disjoint from each other.
        if (isSupportedTellElement(DIGVocabulary.Tell.DISJOINT)) {
            Element disjointElement = doc.createElement(DIGVocabulary.Tell.DISJOINT);
            Iterator allDifferentIt = differentFromIndividuals.iterator();
            while (allDifferentIt.hasNext()) {
                RDFIndividual curInstance = (RDFIndividual) allDifferentIt.next();
                Element curIsetElement = doc.createElement(DIGVocabulary.Language.ISET);
                render(curInstance, doc, curIsetElement);
                disjointElement.appendChild(curIsetElement);
            }
            parentNode.appendChild(disjointElement);
        }

    }


    /**
     * Renders the classes and their axioms
     *
     * @param kb         The knowledge base that contains the classes to be rendered
     * @param doc        The document that the renderings will be inseted into
     * @param parentNode
     */
    protected void renderClasses(OWLModel kb,
                                 Document doc,
                                 Node parentNode)
            throws DIGReasonerException {
        Collection namedClses = ReasonerUtil.getInstance().getNamedClses(kb);
        
        //ignoring ignored classes
        namedClses.removeAll(clsesToIgnore);

        Iterator namedClsesIt = namedClses.iterator();

        while (namedClsesIt.hasNext()) {
            final OWLNamedClass curNamedCls = (OWLNamedClass) namedClsesIt.next();

            final Element curElement = doc.createElement(DIGVocabulary.Tell.DEF_CONCEPT);

            curElement.setAttribute("name", curNamedCls.getName());

            parentNode.appendChild(curElement);

            renderAxioms(curNamedCls, doc, parentNode);
        }
    }


    /**
     * Renders the properties and their axioms
     */
    protected void renderProperties(OWLModel kb,
                                    Document doc,
                                    Node parentNode)
            throws DIGReasonerException {
        Collection properties = ReasonerUtil.getInstance().getProperties(kb);
        
        //ignoring ignored properties
        properties.removeAll(slotsToIgnore);

        Iterator propertiesIt = properties.iterator();

        while (propertiesIt.hasNext()) {
            final OWLProperty curProperty = (OWLProperty) propertiesIt.next();
            final Element curDefPropElement;

            if (curProperty.isAnnotationProperty() == false) {
                if (curProperty instanceof OWLObjectProperty) {
                    curDefPropElement = doc.createElement(DIGVocabulary.Tell.DEF_ROLE);
                } else {
                    curDefPropElement = doc.createElement(DIGVocabulary.Tell.DEF_ATTRIBUTE);
                }

                curDefPropElement.setAttribute("name", curProperty.getName());

                parentNode.appendChild(curDefPropElement);

                renderAxioms(curProperty, doc, parentNode);
            }
        }
    }


    protected void renderIndividuals(OWLModel kb,
                                     Document doc,
                                     Node parentNode)
            throws DIGReasonerException {
        Collection individuals = ReasonerUtil.getInstance().getIndividuals(kb);
        Iterator individualsIt = individuals.iterator();
        while (individualsIt.hasNext()) {
            final OWLIndividual owlIndividual = (OWLIndividual) individualsIt.next();
            if (!isSWRLResource(owlIndividual)) {
            	final Element curElement = doc.createElement(DIGVocabulary.Tell.DEF_INDIVIDUAL);
            	curElement.setAttribute("name", owlIndividual.getName());
            	parentNode.appendChild(curElement);
            	renderAxioms(owlIndividual, doc, parentNode);
            }
        }
    }


    public boolean render(OWLNamedClass cls,
                          Document doc,
                          Node parentNode) {
        boolean ret = true;

        if (clsesToIgnore.contains(cls) == false) {
            Element element;
            OWLModel model = cls.getOWLModel();
            if (cls.equals(model.getOWLThingClass()) == true) {
                element = doc.createElement(DIGVocabulary.Language.TOP);
            } else if (cls.equals(model.getOWLNothing()) == true) {
                element = doc.createElement(DIGVocabulary.Language.BOTTOM);
            } else {
                String name = cls.getName();
                element = doc.createElement(DIGVocabulary.Language.CATOM);
                element.setAttribute("name", name);
            }

            parentNode.appendChild(element);
        } else {
            ret = false;
        }

        return ret;
    }


    public boolean render(OWLSomeValuesFrom someRestriction,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;

        // Check that SOME is supported by the reasoner
        ret = isSupportedLanguageElement(DIGVocabulary.Language.SOME);

        if (ret == true) {
            // Delegate to the quantifier restriction render method
            ret = render((OWLQuantifierRestriction) someRestriction, doc, parentNode);
        } else {
            logErrorOrWarning(someRestriction,
                    "Existential (some values from) restrictions are not\n" +
                            "supported by this reasoner.",
                    "Ignoring restriction and attempting to continue.");
        }

        return ret;
    }


    public boolean render(OWLAllValuesFrom allRestriction,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;

        // Check that ALL is supported by the reasoner
        ret = isSupportedLanguageElement(DIGVocabulary.Language.ALL);

        if (ret == true) {
            // Delegate to the quantifier restriction render method
            ret = render((OWLQuantifierRestriction) allRestriction, doc, parentNode);
        } else {
            logErrorOrWarning(allRestriction,
                    "Universal (all values from) restrictions are not\n" +
                            "supported by this reasoner.",
                    "Ignoring restriction and attempting to continue.");
        }

        return ret;
    }


    protected boolean render(OWLQuantifierRestriction quantifierRestriction,
                             Document doc,
                             Node parentNode)
            throws DIGReasonerException {
        // Assume true
        boolean ret = true;
        if (quantifierRestriction.getFiller() instanceof RDFSClass) {
            ret = renderQuantifierObjectRestriction(quantifierRestriction, doc, parentNode);
        } else {
            ret = renderQuantifierDataRestriction(quantifierRestriction, doc, parentNode);
        }

        return ret;
    }


    public boolean renderQuantifierObjectRestriction(OWLQuantifierRestriction quantifierRestriction,
                                                     Document doc,
                                                     Node parentNode)
            throws DIGReasonerException {
        // Quantifier restrictions may be SOME or ALL
        // restrictions.
        Element element;
        String elementName;
        boolean ret = true;

        // Create the SOME or ALL element depending on the
        // type of restriction
        if (quantifierRestriction instanceof OWLSomeValuesFrom) {
            elementName = DIGVocabulary.Language.SOME;
        } else {
            // Must be an ALL restriction
            elementName = DIGVocabulary.Language.ALL;
        }

        // Create the SOME or ALL wrapper element
        element = doc.createElement(elementName);

        // Insert the slot
        if (renderAppropriateSlot(quantifierRestriction.getOnProperty(), doc, element) == true) {
            // Render the quantifier cls
            ret = renderAppropriateCls((RDFSClass) quantifierRestriction.getFiller(), doc, element);
        }

        // If we created the rendering of the restriction
        // without problems, then append it to our parentNode
        if (ret == true) {
            parentNode.appendChild(element);
        }

        return ret;
    }


    public boolean renderQuantifierDataRestriction(OWLQuantifierRestriction quantifierRestriction,
                                                   Document doc,
                                                   Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        Element element = null;
        // If the filler is an enumerated datatype then
        // we can represent this in DIG

        RDFResource filler = quantifierRestriction.getFiller();
        if (filler instanceof OWLDataRange) {
            // Retrive the values
            OWLDataRange dataRange = (OWLDataRange) filler;
            RDFList oneOf = dataRange.getOneOf();
            Collection values = oneOf == null ?
                    Collections.EMPTY_LIST : oneOf.getValues();
// Represent as a disjunction of values
            element = doc.createElement(DIGVocabulary.Language.OR);
            Iterator valuesIt = values.iterator();
// Process each value
            while (valuesIt.hasNext()) {
                final RDFSLiteral curValue = quantifierRestriction.getOWLModel().asRDFSLiteral(valuesIt.next());
                DIGDataTypes digDataTypes = DIGDataTypes.getInstance(quantifierRestriction.getOWLModel());
// Ensure that we can support the value type
                if (digDataTypes.isSupported(curValue)) {
// Render each value as a value restriction along the
// specified property
                    String tagName = digDataTypes.getConcreteDomainExpressionTagName(curValue);
                    String datatypeRendering = digDataTypes.getDataTypeRendering(curValue);
                    Element valueRestrictionElement = doc.createElement(tagName);
                    valueRestrictionElement.setAttribute("val", datatypeRendering);
                    ret = renderAppropriateSlot(quantifierRestriction.getOnProperty(), doc, valueRestrictionElement);
                    if (ret == true) {
                        element.appendChild(valueRestrictionElement);
                    }
                } else {
// Ignore this restriction
                    ret = false;
                    logErrorOrWarning(quantifierRestriction,
                            "The enumerated datatypes filler contains an unsupported\n" + "datatype (" + curValue + ").",
                            "Ignoring this restriction and attempting to continue.");
                    break;
                }
            }
        } else {
// We should be able to represent sVF|aVF (xs:DATATYPE) but we can't
            logErrorOrWarning(quantifierRestriction,
                    "The current version of the DIG interface does not support\n" + "quantifier restrictions on datatype properties.",
                    "Ignoring this restriction and attempting to continue.");

//TODO: Fix this - Can't seem to do this in the current version of DIG!
//ret = render(quantifierRestriction.getQuantifierValueType(), doc, element);
            ret = false;
        }

        // If we created the rendering of the restriction
        // without problems, then append it to our parentNode
        if (ret == true) {
            parentNode.appendChild(element);
        }

        return ret;

    }


    public boolean render(OWLHasValue hasRestriction,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;

        Element element = null;

        // Slot rendered successfully - attempt to append
        // the filler

        OWLProperty restrictedProperty = (OWLProperty) hasRestriction.getOnProperty();

        // We need to do different things for object restrictions
        // and datatype restrictions.
        if (restrictedProperty.isObjectProperty()) {
            // Object restriction - the filler must be an individual
            // for the ontology to be in OWL-DL.

            if (hasRestriction.getHasValue() instanceof RDFIndividual) {
                // A hasValue restriction can only be rendered as a
                // someValuesFrom restriction, with an enumeration class
                // of the individual that is the filler.
                if (isSupportedLanguageElement(DIGVocabulary.Language.SOME) == true) {
                    element = doc.createElement(DIGVocabulary.Language.SOME);

// Render the slot, and check the outcome
                    ret = renderAppropriateSlot(hasRestriction.getOnProperty(), doc, element);

                    if (ret == true) {
// Wrap in iset element
                        // First check that isets are supported
                        if (isSupportedLanguageElement(DIGVocabulary.Language.ISET) == true) {
                            Element isetElement = doc.createElement(DIGVocabulary.Language.ISET);
                            ret = render((RDFIndividual) hasRestriction.getHasValue(), doc, isetElement);
                            if (ret == true) {
                                // Add our iset to the someValuesFrom element
                                element.appendChild(isetElement);
                            }
                        } else {
                            logErrorOrWarning(hasRestriction,
                                    "Has value restrictions are not supported by this reasoner.\n" +
                                            "(Indirectly caused by no support for individual enumerations).",
                                    "Ignoring this restriction and attempting to continue.");
                            ret = false;
                        }

                    }
                } else {
                    logErrorOrWarning(hasRestriction,
                            "Has value restrictions are not supported by this reasoner.\n" +
                                    "(Indirectly caused by no support for existential restrictions).",
                            "Ignoring this restriction and attempting to continue.");
                    ret = false;
                }
            } else {
                // The filler is not an individual.
                // Log an error and mark that we will ignore
                // this restriction
                ret = false;

                logErrorOrWarning(hasRestriction,
                        "The ontology is in OWL-FULL.\n" + "This is because the filler for a hasValue object property\n" + "restriction is not an individual.",
                        "Ignoring restriction and attempting to continue.");
            }
        } else {
            // DIG 1.1 does not support this.
            ret = false;
            logErrorOrWarning(hasRestriction,
                    "DIG 1.1 (The language used to communicate with the reasoner)\n" +
                            "does not support hasValue restrictions on datatype properties\n",
                    "Ignoring restriction and attempting to continue.");

        }

        // If we have created the restriction successfully then
        // append it to our parent node
        if (ret == true) {
            parentNode.appendChild(element);
        }

        return ret;
    }


    public boolean render(OWLMinCardinality minCardiRestriction,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        // Check thay min cardi restrictions are supported
        if (isSupportedLanguageElement(DIGVocabulary.Language.ATLEAST) == true) {
            ret = render(minCardiRestriction, doc, parentNode, DIGVocabulary.Language.ATLEAST);
        } else {
            ret = false;
            logErrorOrWarning(minCardiRestriction,
                    "Minimum cardinality restrictions are not supported by this reasoner.",
                    "Ignoring this restriction and attempting to continue.");
        }

        return ret;
    }


    public boolean render(OWLMaxCardinality maxCardiRestriction,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        if (isSupportedLanguageElement(DIGVocabulary.Language.ATMOST) == true) {
            ret = render(maxCardiRestriction, doc, parentNode, DIGVocabulary.Language.ATMOST);
        } else {
            ret = false;
            logErrorOrWarning(maxCardiRestriction,
                    "Maximum cardinality restrictions are not supported by this reasoner.",
                    "Ignoring this restriction and attempting to continue.");
        }

        return ret;
    }


    public boolean render(OWLCardinality cardiRestriction,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        // Cardinality restrictions should be renderered  as a pair
        // of min and max cardinality restrictions.
        boolean ret = true;

        if (isSupportedLanguageElement(DIGVocabulary.Language.AND) == true) {
            // Wrap the pair up in an AND element
            Element andElement = doc.createElement(DIGVocabulary.Language.AND);
            if (isSupportedLanguageElement(DIGVocabulary.Language.ATLEAST) == true) {
                ret = render(cardiRestriction, doc, andElement, DIGVocabulary.Language.ATLEAST);
            } else {
                ret = false;
                logErrorOrWarning(cardiRestriction,
                        "Cardinality restrictions are not supported by this reasoner.\n" +
                                "(Indirectly caused by lack of support for max cardinality restrictions).",
                        "Ignoring this restriction and attempting to continue.");
            }

            if (ret == true) {
                if (isSupportedLanguageElement(DIGVocabulary.Language.ATMOST) == true) {
                    ret = render(cardiRestriction, doc, andElement, DIGVocabulary.Language.ATMOST);
                } else {
                    ret = false;
                    logErrorOrWarning(cardiRestriction,
                            "Cardinality restrictions are not supported by this reasoner.\n" +
                                    "(Indirectly caused by lack of support for min cardinality restrictions).",
                            "Ignoring this restriction and attempting to continue.");
                }
            }

            if (ret == true) {
                parentNode.appendChild(andElement);
            }
        } else {
            ret = false;
            logErrorOrWarning(cardiRestriction,
                    "Cardinality restrictions are not supported by this reasoner.\n" +
                            "(Indirectly causedby lack of support for intersection classes.)",
                    "Ignoring this restriction and attepting to continue.");
        }

        return ret;
    }


    protected boolean render(OWLCardinalityBase cardinalityRestriction,
                             Document doc,
                             Node parentNode,
                             String digTypeName)
            throws DIGReasonerException {
        boolean ret = true;

        if (cardinalityRestriction.getOnProperty() instanceof OWLProperty == false) {
            logErrorOrWarning(cardinalityRestriction,
                    "The property " + cardinalityRestriction.getOnProperty().getBrowserText() + " on the restriction\n" +
                            cardinalityRestriction.getBrowserText() + " is not an OWLProperty.\n",
                    "Ignoring this restriction and attempting to continue.");
            ret = false;
            return ret;
        }
        // If the propertyerty is an object property, check that
        // it is not transitive
        // If it is, then the ontology is in OWL-Full
        OWLProperty property = (OWLProperty) cardinalityRestriction.getOnProperty();

        if (property.isObjectProperty()) {
            if (((OWLObjectProperty) property).isTransitive() == true) {
                ret = false;

                logErrorOrWarning(cardinalityRestriction,
                        "The property " + property.getBrowserText() + " is transitive.\n" + "OWL-DL does not allow transitive properties to be used\n" + "is cardinality restrictions.",
                        "Ignoring this restriction and attempting to continue.");
            } else {
                // Check that the inverse is not transitive
                Slot inverseSlot = property.getInverseProperty();

                if (inverseSlot != null) {
                    if (((OWLObjectProperty) inverseSlot).isTransitive()) {
                        ret = false;

                        logErrorOrWarning(cardinalityRestriction,
                                "The inverse property of " + property.getBrowserText() + " (" + inverseSlot.getBrowserText() + ")\n" + "is transitive. OWL-DL does not allow transitive properties, or\n" + "properties whose inverse property is transitive to be used in\n" + "cardinality restrictions.",
                                "Ignoring this restriction and attempting to continue.");
                    }
                }
            }

        } else {
            // Can't render cardianlity on datatype properties as there
            // is no way (it seems) to translate this into DIG 1.1
            ret = false;

            logErrorOrWarning(cardinalityRestriction,
                    "Not able to convert datatype property cardinality restrictions\n" +
                            "to DIG (the langauge used to communicate with the reasoner).",
                    "Ignoring this restriction and attempting to continue.");
        }

        if (ret == true) {
            Element element = null;
            element = doc.createElement(digTypeName);
            element.setAttribute("num", Integer.toString(cardinalityRestriction.getCardinality()));
            ret = renderAppropriateSlot(cardinalityRestriction.getOnProperty(), doc, element);
            if (ret == true) {
                // Protege-OWL custom support of QCRs! :)
                renderAppropriateCls(cardinalityRestriction.getQualifier(), doc, element);
            }

            if (ret == true) {
                parentNode.appendChild(element);
            }
        }

        return ret;
    }


    public boolean render(OWLUnionClass unionCls,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        if (isSupportedLanguageElement(DIGVocabulary.Language.OR) == true) {
            ret = render(unionCls, doc, parentNode, DIGVocabulary.Language.OR);
        } else {
            ret = false;
            logErrorOrWarning(unionCls,
                    "Union classes (disjunctions) are not supported by this reasoner.",
                    "Ignoring this class and attemptng to continue.");
        }

        return ret;
    }


    public boolean render(OWLComplementClass complementCls,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        if (isSupportedLanguageElement(DIGVocabulary.Language.NOT) == true) {
            ret = render(complementCls, doc, parentNode, DIGVocabulary.Language.NOT);
        } else {
            ret = false;
            logErrorOrWarning(complementCls,
                    "Complement classes are not supported by this reasoner.",
                    "Ignoring this class and attemptng to continue.");
        }
        return ret;
    }


    public boolean render(OWLIntersectionClass intersectionCls,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        if (isSupportedLanguageElement(DIGVocabulary.Language.AND) == true) {
            ret = render(intersectionCls, doc, parentNode, DIGVocabulary.Language.AND);
        } else {
            ret = false;
            logErrorOrWarning(intersectionCls,
                    "Intersection classes (conjunctions) are not supported by this reasoner.",
                    "Ignoring this class and attemptng to continue.");
        }
        return ret;
    }


    protected boolean render(OWLLogicalClass logicalCls,
                             Document doc,
                             Node parentNode,
                             String digTypeName)
            throws DIGReasonerException {
        boolean ret = true;
        Element element = null;
        element = doc.createElement(digTypeName);
        Collection operands = null;
        if (logicalCls instanceof OWLNAryLogicalClass) {
            operands = ((OWLNAryLogicalClass) logicalCls).getOperands();
        } else {
            operands = Collections.singleton(((OWLComplementClass) logicalCls).getComplement());
        }
        Iterator operandsIt = operands.iterator();

        while (operandsIt.hasNext()) {
            final RDFResource curInstance = (RDFResource) operandsIt.next();

            if (curInstance instanceof RDFSClass) {
                ret = renderAppropriateCls((RDFSClass) curInstance, doc, element);

                if (ret == false) {
                    break;
                }

            } else {
                logErrorOrWarning(logicalCls,
                        "Intersection, Union and Complement classes may only\n" +
                                "contain operands that are OWL Classes.",
                        "Ignoring class and attempting to continue.");

                ret = false;

                break;
            }
        }

        if (ret == true) {
            parentNode.appendChild(element);
        }

        return ret;
    }


    public boolean render(OWLEnumeratedClass enumerationCls,
                          Document doc,
                          Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;
        if (isSupportedLanguageElement(DIGVocabulary.Language.ISET) == true) {
            // Enumerations are wrapped up in an iset element
            Element element = doc.createElement(DIGVocabulary.Language.ISET);
            Collection enumerationValues = enumerationCls.getOneOf();
            Iterator enumerationValuesIt = enumerationValues.iterator();

            // Go through the enumeration values and add them
            // to the iset element.
            while (enumerationValuesIt.hasNext()) {
                final Object curValue = enumerationValuesIt.next();

                // We can only render individuals here
                // If anything but an individual is used
                // in the enumeration, then the ontology is in
                // OWL-Full
                if (curValue instanceof RDFIndividual) {
                    ret = render((RDFIndividual) curValue, doc, element);

                    if (ret == false) {
                        // If for some reason the individual could not
                        // be rendered then we fail the rendering of
                        // this enumeration class
                        break;
                    }
                } else {
                    logErrorOrWarning(enumerationCls,
                            "The ontology is in OWL-Full.\n" + "Enumerated classes can only be made up of enumerations\n" + "of individuals.",
                            "Ignoring enumerated class.");
                    // Fail the rendering of this enumeration class
                    ret = false;
                    break;
                }
            }
            // If everything was ok, append the iset element
            if (ret == true) {
                parentNode.appendChild(element);
            }
        } else {
            ret = false;
            logErrorOrWarning(enumerationCls,
                    "Enumeration classes are not supported by this reasoner.",
                    "Ignoring this class and attempting to continue.");
        }

        return ret;
    }

    // Properties


    public boolean render(OWLObjectProperty slot,
                          Document doc,
                          Node parentNode) {
        boolean ret = true;

        Element element = null;

        if (slotsToIgnore.contains(slot) == false) {
            element = doc.createElement(DIGVocabulary.Language.RATOM);

            element.setAttribute("name", slot.getName());

            parentNode.appendChild(element);
        } else {
            ret = false;
        }

        return ret;
    }


    public boolean render(OWLDatatypeProperty slot,
                          Document doc,
                          Node parentNode) throws DIGReasonerException {
        boolean ret = true;
        if (isSupportedLanguageElement(DIGVocabulary.Language.ATTRIBUTE) == true) {
            if (slotsToIgnore.contains(slot) == false) {
                Element element = doc.createElement(DIGVocabulary.Language.ATTRIBUTE);
                element.setAttribute("name", slot.getName());
                parentNode.appendChild(element);
            } else {
                ret = false;
            }
        } else {
            ret = false;
            logErrorOrWarning(slot,
                    "Datatype properties are not supported by this reasoner.",
                    "Ignoring property and attempting to continue.");
        }

        return ret;
    }


    public boolean render(RDFIndividual instance,
                          Document doc,
                          Node parentNode) throws DIGReasonerException {
    	
    	//ignoring SWRL individuals
    	if (isSWRLResource(instance)) {
    		return false;
    	}
    	
        boolean ret = true;
        //if(isSupportedLanguageElement(DIGVocabulary.Language.INDIVIDUAL) == true) {
        Element element = doc.createElement(DIGVocabulary.Language.INDIVIDUAL);
        element.setAttribute("name", instance.getName());
        parentNode.appendChild(element);
//		}
//	    else {
//		    ret = false;
//		    logErrorOrWarning(instance,
//		                      "Individuals are not supported by this reasoner.",
//		                      "Ignoring this individual and attempting to continue.");
//	    }

        return ret;
    }


    public void renderAxioms(OWLNamedClass cls,
                             Document doc,
                             Node parentNode)
            throws DIGReasonerException {
        boolean ret = true;

        // I guess that we could check if implies is supported.
        // (But it would be a desparate situation if it isn't!!)
        boolean ignoreNecessaryConditions = DIGTranslatorPreferences.getInstance().isIgnoreNecessaryConditionsOnDefinedClasses();
        if ((cls.isDefinedClass() && ignoreNecessaryConditions) == false) {
            // Render superclasses (as implications)
            Collection directPureSuperClses = cls.getPureSuperclasses();
            Iterator directPureSuperClsesIt = directPureSuperClses.iterator();

            while (directPureSuperClsesIt.hasNext()) {
                final RDFSClass curSuperClass = (RDFSClass) directPureSuperClsesIt.next();
                if (curSuperClass.equals(curSuperClass.getOWLModel().getOWLThingClass()) == false) {
                    final ClassCommenter commenter = new ClassCommenter(cls.getOWLModel());
                    if (commenter.isCommentedOut(curSuperClass) == false) {
                        final Element impliesElement = doc.createElement(DIGVocabulary.Tell.IMPLIES_C);
                        ret = render(cls, doc, impliesElement);
                        if (ret == true) {
                            ret = renderAppropriateCls((RDFSClass) curSuperClass, doc, impliesElement);
                            if (ret == true) {
                                parentNode.appendChild(impliesElement);
                            }
                        }
                    }
                }
            }

            // Render disjoint classes
            Collection disjointClses = cls.getDisjointClasses();

            Iterator disjointClsesIt = disjointClses.iterator();

            while (disjointClsesIt.hasNext()) {
                final RDFSClass curDisjointClass = (RDFSClass) disjointClsesIt.next();
                if ((curDisjointClass.getEquivalentClasses().size() > 0 &&
                        ignoreNecessaryConditions) == false) {
                    final ClassCommenter commenter = new ClassCommenter(cls.getOWLModel());
                    if (commenter.isCommentedOut(curDisjointClass) == false) {
                        final Element disjointElement = doc.createElement(DIGVocabulary.Tell.DISJOINT);
                        ret = render(cls, doc, disjointElement);
                        if (ret == true) {
                            ret = renderAppropriateCls(curDisjointClass, doc, disjointElement);

                            if (ret = true) {
                                parentNode.appendChild(disjointElement);
                            }
                        }
                    }
                }
            }
        }

        // Render equivalent classes
        Collection equivalentClses = cls.getEquivalentClasses();

        Iterator equivalentClsesIt = equivalentClses.iterator();

        while (equivalentClsesIt.hasNext()) {
            final RDFSClass curEquivClass = (RDFSClass) equivalentClsesIt.next();
            ClassCommenter commenter = new ClassCommenter(cls.getOWLModel());
            if (commenter.isCommentedOut(curEquivClass) == false) {
                final Element equalElement = doc.createElement(DIGVocabulary.Tell.EQUAL_C);
                ret = render(cls, doc, equalElement);
                if (ret == true) {
                    // Racer does not seem to like it if equivalent classes
                    // aren't nested inside an AND element
                    Element andElement;
                    if (curEquivClass instanceof OWLIntersectionClass == false) {
                        andElement = doc.createElement(DIGVocabulary.Language.AND);
                        ret = renderAppropriateCls(curEquivClass, doc, andElement);
                        equalElement.appendChild(andElement);
                    } else {
                        ret = renderAppropriateCls(curEquivClass, doc, equalElement);
                    }

                    if (ret = true) {
                        parentNode.appendChild(equalElement);
                    }
                }
            }
        }


    }


    public void renderAxioms(OWLProperty property,
                             Document doc,
                             Node parentNode)
            throws DIGReasonerException {

        // Render super slots
        renderSuperSlots(property, doc, parentNode);

        // Render equivalent slots
        renderEquivalentSlots(property, doc, parentNode);

        // Render domain
        renderSlotDomain(property, doc, parentNode);

        // Render range
        renderSlotRange(property, doc, parentNode);

        // Inverse Property
        renderInverseSlot(property, doc, parentNode);

        // Render Property characteristics
        if (property.isObjectProperty()) {
            // Transitive
            renderTransitiveSlot(property, doc, parentNode);

            // Inverse functional - (if transitive, may NOT
            // be inverse functional).
            if (property.isInverseFunctional()) {
                renderInverseFunctionalSlot(property, doc, parentNode);
            }

            // If a property is symmetric, then its inverse is
            // itself. (No restrictions on transitivity here)
            if ((((OWLObjectProperty) property).isSymmetric()) == true) {
                renderSymmetricSlot(property, doc, parentNode);
            }
        }

        // Functional
        if (property.isFunctional()) {
            renderFunctionalSlot(property, doc, parentNode);
        }

    }


    protected void renderSuperSlots(OWLProperty property,
                                    Document doc,
                                    Node parentNode)
            throws DIGReasonerException {
        Collection superSlots = property.getSuperproperties(false);
        Iterator superSlotsIt = superSlots.iterator();

        while (superSlotsIt.hasNext()) {
            final Slot curSlot = (Slot) superSlotsIt.next();

            final Element impliesElement = doc.createElement(DIGVocabulary.Tell.IMPLIES_R);

            boolean ret = renderAppropriateSlot(property, doc, impliesElement);

            if (ret == true) {
                ret = renderAppropriateSlot(curSlot, doc, impliesElement);

                if (ret == true) {
                    parentNode.appendChild(impliesElement);
                }
            }
        }

    }


    protected void renderEquivalentSlots(OWLProperty property,
                                         Document doc,
                                         Node parentNode)
            throws DIGReasonerException {
        Collection equivSlots = property.getEquivalentProperties();

        Iterator equivSlotsIt = equivSlots.iterator();

        while (equivSlotsIt.hasNext()) {
            final Slot curSlot = (Slot) equivSlotsIt.next();

            final Element equivElement = doc.createElement(DIGVocabulary.Tell.EQUAL_R);

            boolean ret = renderAppropriateSlot(property, doc, equivElement);

            if (ret == true) {
                ret = renderAppropriateSlot(curSlot, doc, equivElement);

                if (ret == true) {
                    parentNode.appendChild(equivElement);
                }
            }
        }
    }


    protected void renderSlotDomain(OWLProperty property,
                                    Document doc,
                                    Node parentNode)
            throws DIGReasonerException {
        Collection directDomain = property.getDomains(false);

        Iterator directDomainIt = directDomain.iterator();

        Element domainElement = doc.createElement(DIGVocabulary.Tell.DOMAIN);


        RDFSClass rootCls = property.getOWLModel().getOWLThingClass();
        while (directDomainIt.hasNext()) {
            final Object curObj = directDomainIt.next();

            if (curObj instanceof OWLClass) {
                if (!rootCls.equals(curObj)) {

                    if (renderAppropriateSlot(property, doc, domainElement)) {
                        parentNode.appendChild(domainElement);
                        renderAppropriateCls((OWLClass) curObj, doc, domainElement);
                    }
                }
            }
        }
    }


    protected void renderSlotRange(OWLProperty property,
                                   Document doc,
                                   Node parentNode)
            throws DIGReasonerException {
        if (property.isObjectProperty()) {
            Collection allowedClses = property.getUnionRangeClasses();

            Iterator allowedClsesIt = allowedClses.iterator();

            Element rangeOrElement = doc.createElement(DIGVocabulary.Language.OR);

            Element rangeElement = doc.createElement(DIGVocabulary.Tell.RANGE);

            while (allowedClsesIt.hasNext()) {
                final Object curObj = allowedClsesIt.next();

                if (curObj instanceof RDFSClass) {
                    renderAppropriateCls((RDFSClass) curObj, doc, rangeOrElement);
                }
            }

            if (rangeOrElement.getChildNodes().getLength() > 0) {
                if (renderAppropriateSlot(property, doc, rangeElement)) {
                    rangeElement.appendChild(rangeOrElement);

                    parentNode.appendChild(rangeElement);
                }
            }
        } else {
            // Datatype property
            RDFSDatatype datatype = property.getRangeDatatype();
            DIGDataTypes digDataTypes = DIGDataTypes.getInstance(property.getOWLModel());

            if (digDataTypes.isSupported(datatype) == true) {
                final Element rangeElement = doc.createElement(digDataTypes.getPropertyRangeTagName(datatype));

                boolean ret = renderAppropriateSlot(property, doc, rangeElement);

                if (ret == true) {
                    parentNode.appendChild(rangeElement);
                }
            } else {
                logErrorOrWarning(property,
                        "DIG 1.1 (The language used to communicate with the reasoner)\n" + "does not support the range specified for the datatype property\n" + property.getBrowserText() + ".",
                        "Ignoring this range on this property.");
            }

        }
    }


    protected void renderInverseSlot(OWLProperty property,
                                     Document doc,
                                     Node parentNode)
            throws DIGReasonerException {
        OWLProperty inverseProperty = (OWLProperty) property.getInverseProperty();

        if (inverseProperty != null) {
            final Element inverseElement = doc.createElement(DIGVocabulary.Language.INVERSE);

            boolean ret = renderAppropriateSlot(inverseProperty, doc, inverseElement);

            if (ret == true) {
                final Element equalRoleElement = doc.createElement(DIGVocabulary.Tell.EQUAL_R);

                ret = renderAppropriateSlot(property, doc, equalRoleElement);

                if (ret == true) {
                    equalRoleElement.appendChild(inverseElement);

                    parentNode.appendChild(equalRoleElement);
                }
            }
        }
    }


    protected void renderInverseFunctionalSlot(OWLProperty property,
                                               Document doc,
                                               Node parentNode)
            throws DIGReasonerException {
        // Check for transitivity
        OWLProperty inverseProperty = (OWLProperty) property.getInverseProperty();

        if (((OWLObjectProperty) property).isTransitive()) {
            logErrorOrWarning(property,
                    "The ontology is in OWL-Full.\n" + "This has been caused by the property " + property.getBrowserText() + "\n" + "being transitive and inverse functional.",
                    "Ignoring inverse functional characteristic on this property\n" + "and attempting to continue.");
        } else {
            // Inverse property must be marked as functional
            if (inverseProperty == null) {
                // Create FAKE inverse property
                Element fakeInverseSlotElementDef = doc.createElement(DIGVocabulary.Tell.DEF_ROLE);
                String fakeName = "*Inverse_of_" + property.getName();
                fakeInverseSlotElementDef.setAttribute("name", fakeName);
                parentNode.appendChild(fakeInverseSlotElementDef);
                Element fakeInverseSlotElement = doc.createElement(DIGVocabulary.Language.RATOM);
                fakeInverseSlotElement.setAttribute("name", fakeName);
                final Element equalRoleElement = doc.createElement(DIGVocabulary.Tell.EQUAL_R);
                renderAppropriateSlot(property, doc, equalRoleElement);
                final Element inverseElement = doc.createElement(DIGVocabulary.Language.INVERSE);
                inverseElement.appendChild(fakeInverseSlotElement);
                equalRoleElement.appendChild(inverseElement);
                parentNode.appendChild(equalRoleElement);
                Element functionalElement = doc.createElement(DIGVocabulary.Tell.FUNCTIONAL);
                Element fakeInverseSlotElement2 = doc.createElement(DIGVocabulary.Language.RATOM);
                fakeInverseSlotElement2.setAttribute("name", fakeName);
                functionalElement.appendChild(fakeInverseSlotElement2);
                doc.getDocumentElement().appendChild(functionalElement);
            } else {
                renderFunctionalSlot(inverseProperty, doc, parentNode);
            }
        }
    }


    protected void renderTransitiveSlot(OWLProperty property,
                                        Document doc,
                                        Node parentNode) throws DIGReasonerException {
        if (((OWLObjectProperty) property).isTransitive() == true) {
            // Transitive
            Element transitiveElement = doc.createElement(DIGVocabulary.Tell.TRANSITIVE);

            boolean ret = renderAppropriateSlot(property, doc, transitiveElement);

            if (ret == true) {
                parentNode.appendChild(transitiveElement);
            }
        }
    }


    protected void renderSymmetricSlot(OWLProperty property,
                                       Document doc,
                                       Node parentNode) throws DIGReasonerException {
        final Element equalRoleElement = doc.createElement(DIGVocabulary.Tell.EQUAL_R);

        boolean ret = renderAppropriateSlot(property, doc, equalRoleElement);

        if (ret == true) {
            final Element inverseElement = doc.createElement(DIGVocabulary.Language.INVERSE);

            renderAppropriateSlot(property, doc, inverseElement);

            equalRoleElement.appendChild(inverseElement);

            parentNode.appendChild(equalRoleElement);
        }
    }


    protected void renderFunctionalSlot(OWLProperty property,
                                        Document doc,
                                        Node parentNode)
            throws DIGReasonerException {

        if (property.isObjectProperty()) {
            if (((OWLObjectProperty) property).isTransitive()) {
                // Transitive slots cannot be functional as well
                logErrorOrWarning(property,
                        "The ontology is in OWL-Full.\n" + "This is because the property " + property.getBrowserText() + " is both\n" + "transitive and functional.",
                        "Ignoring the functional characteristic of this property and\n" + "attempting to continue.");

                return;
            }
        }

        Element functionalElement = doc.createElement(DIGVocabulary.Tell.FUNCTIONAL);

        boolean ret = renderAppropriateSlot(property, doc, functionalElement);

        if (ret == true) {
            parentNode.appendChild(functionalElement);
        }
    }


    private void renderFakeSameAs(Collection sameAsInstances, Document doc, Node parentNode) throws DIGReasonerException {
        // For each individuals in the collection
        // wrap it in an iset element and then make
        // them pairwise equal to each other.
        Object[] sameAsInstancesArray = sameAsInstances.toArray();
        for (int i = 0; i < sameAsInstancesArray.length; i++) {
            for (int j = 0; j < sameAsInstancesArray.length; j++) {
                if (i != j) {
                    // Make equal
                    RDFIndividual instI = (RDFIndividual) sameAsInstancesArray[i];
                    Element isetI = doc.createElement(DIGVocabulary.Language.ISET);
                    render(instI, doc, isetI);
                    RDFIndividual instJ = (RDFIndividual) sameAsInstancesArray[j];
                    Element isetJ = doc.createElement(DIGVocabulary.Language.ISET);
                    render(instJ, doc, isetJ);
                    Element equalElement = doc.createElement(DIGVocabulary.Tell.EQUAL_C);
                    equalElement.appendChild(isetI);
                    equalElement.appendChild(isetJ);
                    parentNode.appendChild(equalElement);
                }
            }
        }

    }


    public void renderAxioms(RDFIndividual instance,
                             Document doc,
                             Node parentNode)
            throws DIGReasonerException {
        boolean ret;

        // Types
        renderTypes(instance, doc, parentNode);

        // Same As
        Collection sameAs = instance.getSameAs();
        if (sameAs.isEmpty() == false) {
            if (isSupportedTellElement(DIGVocabulary.Tell.SAME_AS) == true) {
                Iterator sameAsIt = sameAs.iterator();
                while (sameAsIt.hasNext()) {
                    RDFIndividual curSameAs = (RDFIndividual) sameAsIt.next();
                    Element sameAsElement = doc.createElement(DIGVocabulary.Tell.SAME_AS);
                    if (render(instance, doc, sameAsElement)) {
                        if (render(curSameAs, doc, sameAsElement)) {
                            parentNode.appendChild(sameAsElement);
                        }
                    }
                }
            } else {
                //	renderFakeSameAs(sameAs, doc, parentNode);
                logErrorOrWarning(instance,
                        "SameAs construct for individuals is not supported\n" +
                                "by this reasoner.",
                        "Ignoring this assertion.");
            }
        }

        // Different From
        RDFProperty differentFromSlot = instance.getOWLModel().getRDFProperty(OWLNames.Slot.DIFFERENT_FROM);
        Collection differentFrom = instance.getPropertyValues(differentFromSlot, true);
        if (differentFrom.isEmpty() == false) {
            if (isSupportedTellElement(DIGVocabulary.Tell.DIFFERENT_FROM) == true) {
                Iterator differentFromIt = differentFrom.iterator();
                while (differentFromIt.hasNext()) {
                    RDFIndividual curDifferentFrom = (RDFIndividual) differentFromIt.next();
                    Element differentFromElement = doc.createElement(DIGVocabulary.Tell.DIFFERENT_FROM);
                    if (render(instance, doc, differentFromElement)) {
                        if (render(curDifferentFrom, doc, differentFromElement)) {
                            parentNode.appendChild(differentFromElement);
                        }
                    }
                }

            } else {
                Iterator differentFromIt = differentFrom.iterator();
                ArrayList allDifferentList = new ArrayList();
                allDifferentList.add(instance);
                while (differentFromIt.hasNext()) {
                    allDifferentList.add(differentFromIt.next());
                }
                renderFakedDifferentFrom(allDifferentList, doc, parentNode);
//			    logErrorOrWarning(instance,
//								  "DifferentFrom construct for individuals is not supported\n" +
//								  "by this reasoner.",
//								  "Ignoring this assertion.");
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        for (Iterator propIt = instance.getOWLModel().getRDFProperties().iterator(); propIt.hasNext();) {
            RDFProperty curProp = (RDFProperty) propIt.next();
            // Filter out annotation properties
            if (curProp.isAnnotationProperty() == false && curProp instanceof OWLProperty) {
                if (((OWLProperty) curProp).isObjectProperty()) {
                    // Object property
                    for (Iterator it = instance.getPropertyValues(curProp).iterator(); it.hasNext();) {
                        Object curObjVal = it.next();
                        if (curObjVal instanceof OWLIndividual) {
                            Element relatedElement = doc.createElement(DIGVocabulary.Tell.RELATED);
                            ret = render(instance, doc, relatedElement);
                            if (ret == true) {
                                if (ret = renderAppropriateSlot(curProp, doc, relatedElement)) {
                                    if (ret = render((OWLIndividual) curObjVal, doc, relatedElement)) {
                                        parentNode.appendChild(relatedElement);
                                    }

                                }

                            }
                        } else {
                            logErrorOrWarning(instance,
                                    "The ontology is in OWL-Full.\n" + "The filler for an object property relationship is not\n" + "an individual.",
                                    "Ignoring this relationship and attempting to continue.");
                        }
                    }
                } else {
                    for (Iterator it = instance.getPropertyValueLiterals(curProp).iterator(); it.hasNext();) {
                        OWLModel owlModel = curProp.getOWLModel();
                        DIGDataTypes digDataTypes = DIGDataTypes.getInstance(owlModel);
                        Object curValue = it.next();
                        RDFSLiteral value = owlModel.asRDFSLiteral(curValue);
                        if (digDataTypes.isSupported(value)) {
                            Element valueElement = doc.createElement(DIGVocabulary.Tell.VALUE);

                            if (ret = render(instance, doc, valueElement)) {
                                if (ret = renderAppropriateSlot(curProp, doc, valueElement)) {
                                    Element datatypeValueElement;
                                    String datatypeValueElementName;
                                    datatypeValueElementName = digDataTypes.getIndividualAxiomValueTagName(value);
                                    datatypeValueElement = doc.createElement(datatypeValueElementName);
                                    valueElement.appendChild(datatypeValueElement);
                                    Text valueContent = doc.createTextNode(digDataTypes.getDataTypeRendering(value));
                                    datatypeValueElement.appendChild(valueContent);
                                    parentNode.appendChild(valueElement);
                                }
                            }
                        } else {
                            logErrorOrWarning(instance,
                                    "The current version of DIG (the language used to communicate)\n" + "with the reasoner) does not support the datatype used in the\n" + curProp.getBrowserText() + " relationship.",
                                    "Ignoring this relationship and attempting to continue.");

                        }


                    }

                }
            }
        }
    }


    protected void renderTypes(RDFIndividual instance, Document doc, Node parentNode) throws DIGReasonerException {
        Collection directTypes = instance.getRDFTypes();
        Iterator directTypesIterator = directTypes.iterator();
        while (directTypesIterator.hasNext()) {
            final Object curObj = directTypesIterator.next();
            if (curObj instanceof OWLClass &&
                    curObj instanceof OWLEnumeratedClass == false) {
                final Element instanceOfElement = doc.createElement(DIGVocabulary.Tell.INSTANCE_OF);
                boolean ret = render(instance, doc, instanceOfElement);
                if (ret == true) {
                    ret = renderAppropriateCls((OWLClass) curObj, doc, instanceOfElement);
                    if (ret == true) {
                        parentNode.appendChild(instanceOfElement);
                    }
                }
            }
        }
    }


    protected boolean isSupportedTellElement(String elementTagName) {

        // Assume that the element is supported
        // until we know otherwise
        boolean retVal = true;
        if (reasonerIdentity != null) {
            retVal = reasonerIdentity.supportsTellElemement(elementTagName);
        }

        return retVal;
    }


    protected boolean isSupportedAskElement(String elementTagName) {

        // Assume that the element is supported
        // until we know otherwise
        boolean retVal = true;
        if (reasonerIdentity != null) {
            retVal = reasonerIdentity.supportsAskElement(elementTagName);
        }

        return retVal;
    }


    protected boolean isSupportedLanguageElement(String elementTagName) {

        // Assume that the element is supported
        // until we know otherwise
        boolean retVal = true;
        if (reasonerIdentity != null) {
            retVal = reasonerIdentity.supportsLanguageElement(elementTagName);
        }

        return retVal;
    }


    protected void logErrorOrWarning(RDFResource cause,
                                     String message,
                                     String recoveryMessage)
            throws DIGReasonerException {
        if (ReasonerPreferences.getInstance().isWarningAsErrors()) {
            throw new DIGReasonerException(message);
        } else {
            String msg;

            msg = message + "\n" + recoveryMessage;

            ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

            ReasonerLogRecord logRecord = logRecordFactory.createWarningMessageLogRecord(cause, msg, null);

            ReasonerLogger.getInstance().postLogRecord(logRecord);
        }
    }
    
    
    //Methods for ignoring SWRL concepts in the DIG translation
   
    private void updateIgnoredCollections(OWLModel owlModel) {
    	if (!isSWRLModel(owlModel)) {
    		return;
    	}
    	
    	swrlFactory = new SWRLFactory(owlModel);
    	clsesToIgnore.addAll(swrlFactory.getSWRLClasses());
    	slotsToIgnore.addAll(swrlFactory.getSWRLProperties());
    	slotsToIgnore.addAll(swrlFactory.getSWRLBProperties());
	}
    
    private boolean isSWRLModel(OWLModel owlModel) {    
    	return (owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory);    	
    }
    
    /* This returns correctly only if the swrlFactory was initialized before
     * in the updateIgnoredCollections(), which is the typical case.
     */
    private boolean isSWRLResource(RDFResource resource) {
    	return (swrlFactory == null ? false : swrlFactory.isSWRLResource(resource));
    }
    
}

