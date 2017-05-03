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

package edu.stanford.smi.protegex.owl.model.visitor;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 19, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of <code>OWLModelVisitor</code> that
 * provides null methods.
 */
public class OWLModelVisitorAdapter implements OWLModelVisitor {

    public void visitOWLAllDifferent(OWLAllDifferent owlAllDifferent) {
    }


    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
    }


    public void visitOWLCardinality(OWLCardinality owlCardinality) {
    }


    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
    }


    public void visitOWLDataRange(OWLDataRange owlDataRange) {
    }


    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
    }


    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
    }


    public void visitOWLHasValue(OWLHasValue owlHasValue) {
    }


    public void visitOWLIndividual(OWLIndividual owlIndividual) {
    }


    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
    }


    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
    }


    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
    }


    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
    }


    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
    }


    public void visitOWLOntology(OWLOntology owlOntology) {
    }


    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom someValuesFrom) {
    }


    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
    }


    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
    }


    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
    }


    public void visitRDFList(RDFList rdfList) {
    }


    public void visitRDFProperty(RDFProperty rdfProperty) {
    }


    public void visitRDFSLiteral(RDFSLiteral rdfsLiteral) {
    }


    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
    }


    public void visitRDFUntypedResource(RDFUntypedResource rdfUntypedResource) {
    }


	public void visitSWRLIndividual(SWRLIndividual swrlIndividual) {		
	}


	public void visitSWRLAtomListIndividual(SWRLAtomList swrlAtomList) {		
	}
}

