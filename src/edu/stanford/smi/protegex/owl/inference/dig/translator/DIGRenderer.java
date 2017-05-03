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

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * Most of the methods of the DIGRenderer produce XML document elements
 * to represent different Protege-OWL classes in the DIG format.  The methods
 * typically take some kind of RDFSClass, RDFResource etc. and render it into
 * DIG.  Depending on the concrete subclass of the renderer, the Protege-OWL Clses
 * may or may not be representable in DIG.  If an element cannot be generated
 * to represent a Protege-OWL Class then the method will return <code>false</code> if an element
 * (or elements) were successfully generated to represent the Class, RDFIndividual etc. then
 * the return value is <code>true</code> and the elements will be appended to the specified
 * parent node.
 */
public interface DIGRenderer {

    void render(OWLModel kb, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLNamedClass cls, Document doc, Node parentNode);


    boolean render(OWLSomeValuesFrom someRestriction, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLAllValuesFrom allRestriction, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLHasValue hasRestriction, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLMinCardinality minCardiRestriction, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLMaxCardinality maxCardiRestriction, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLCardinality cardiRestriction, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLUnionClass unionCls, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLComplementClass complementCls, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLIntersectionClass intersectionCls, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLEnumeratedClass enumerationCls, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLObjectProperty slot, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(OWLDatatypeProperty slot, Document doc, Node parentNode) throws DIGReasonerException;


    boolean render(RDFIndividual instance, Document doc, Node parentNode) throws DIGReasonerException;


    void renderAxioms(OWLNamedClass cls, Document doc, Node parentNode) throws DIGReasonerException;


    void renderAxioms(OWLProperty property, Document doc, Node parentNode) throws DIGReasonerException;


    void renderAxioms(RDFIndividual instance, Document doc, Node parentNode) throws DIGReasonerException;


    /**
     * Sets the DIG Reasoner Identity that will constrain
     * the representation when OWLModel elements are translated
     * to DIG.
     *
     * @param reasonerIdentity - The reasoner identity, or <code>null</code>
     *                         if the translation to DIG should not be constrasined by the
     *                         capabilities of a particular reasoner.
     */
    void setReasonerIdentity(DIGReasonerIdentity reasonerIdentity);
}
