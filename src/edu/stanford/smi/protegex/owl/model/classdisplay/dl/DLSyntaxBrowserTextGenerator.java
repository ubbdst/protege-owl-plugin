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

package edu.stanford.smi.protegex.owl.model.classdisplay.dl;

import java.util.Iterator;
import java.util.Stack;

import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLCardinalityBase;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.model.visitor.Visitable;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 25, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLSyntaxBrowserTextGenerator implements OWLModelVisitor {

    private StringBuffer buffer;

    private Stack<Visitable> resourceStack;

    public DLSyntaxBrowserTextGenerator() {
        buffer = new StringBuffer();
        resourceStack = new Stack();
    }

    public String getBrowserText() {
        return buffer.toString();
    }

    public void reset() {
        buffer = new StringBuffer();
    }

    protected StringBuffer getCurrentBuffer() {
        return buffer;
    }

    protected void write(String s) {
        getCurrentBuffer().append(s);
    }

    protected void write(char c) {
        getCurrentBuffer().append(c);
    }

    protected void writeSpace() {
        getCurrentBuffer().append(" ");
    }

    protected void writeOpenPar()  {
        if (resourceStack.size() > 1) {
            getCurrentBuffer().append("(");
        }
    }

    protected void writeClosePar() {
        if (resourceStack.size() > 1) {
            getCurrentBuffer().append(")");
        }
    }

    public void visitOWLAllDifferent(OWLAllDifferent owlAllDifferent) {

    }

    private void push(RDFSClass v) {
        resourceStack.push(v);
    }

    private void pop() {
        resourceStack.pop();
    }


    protected void writeCardinality(OWLCardinalityBase cardinalityBase) {
        writeOpenPar();
        write(cardinalityBase.getOperator());
        writeSpace();
        write(String.valueOf(cardinalityBase.getCardinality()));
        writeSpace();
        if (cardinalityBase.getOnProperty() != null) {
            cardinalityBase.getOnProperty().accept(this);
        }
        if(cardinalityBase.getQualifier() != null) {
            writeSpace();
            cardinalityBase.getQualifier().accept(this);
        }
        writeClosePar();
    }

    protected void writeQuantifier(OWLQuantifierRestriction quantifierRestriction) {
        writeOpenPar();
        write(quantifierRestriction.getOperator());
        writeSpace();
        if(quantifierRestriction.getOnProperty() != null) {
            quantifierRestriction.getOnProperty().accept(this);
        }
        writeSpace();
        if (quantifierRestriction.getFiller() != null) {
            quantifierRestriction.getFiller().accept(this);
        }
        writeClosePar();
    }

    protected void writeNAryLocical(OWLNAryLogicalClass logicalClass) {
        writeOpenPar();
        for(Iterator it = logicalClass.getOperands().iterator(); it.hasNext(); ) {
            RDFResource res = (RDFResource) it.next();
            res.accept(this);
            if(it.hasNext()) {
                writeSpace();
                write(logicalClass.getOperatorSymbol());
                writeSpace();
            }
        }
        writeClosePar();
    }

    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        push(owlAllValuesFrom);
        writeQuantifier(owlAllValuesFrom);
        pop();
    }

    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        push(owlCardinality);
        writeCardinality(owlCardinality);
        pop();
    }

    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        push(owlComplementClass);
        writeOpenPar();
        write(owlComplementClass.getOperatorSymbol());
        writeSpace();
        if (owlComplementClass.getComplement() != null) {
            owlComplementClass.getComplement().accept(this);
        }
        writeClosePar();
        pop();
    }

    public void visitOWLDataRange(OWLDataRange owlDataRange) {
        write("{");
        for(Iterator it = owlDataRange.getOneOfValueLiterals().iterator(); it.hasNext(); ) {
            RDFSLiteral literal = (RDFSLiteral) it.next();
            literal.accept(this);
            if(it.hasNext()) {
                writeSpace();
            }
        }
        write("}");
    }

    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        write(owlDatatypeProperty.getBrowserText());
    }

    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        push(owlEnumeratedClass);
        write("{");
        for(Iterator it = owlEnumeratedClass.getOneOf().iterator(); it.hasNext(); ) {
            RDFResource res = (RDFResource) it.next();
            res.accept(this);
            if(it.hasNext()) {
                writeSpace();
            }
        }
        write("}");
        pop();
    }

    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        // Has value should be written as someValuesFrom!
        push(owlHasValue);
        writeOpenPar();
        write(DefaultOWLSomeValuesFrom.OPERATOR);
        writeSpace();
        if (owlHasValue.getOnProperty() != null) {
            owlHasValue.getOnProperty().accept(this);
        }
        writeSpace();
        write("{");
        if (owlHasValue.getHasValue() != null) {
            Object value = owlHasValue.getHasValue();
            if(value instanceof RDFResource) {
                ((RDFResource) value).accept(this);
            }
            else {
                owlHasValue.getOWLModel().asRDFSLiteral(value).accept(this);
            }
        }
        write("}");
        writeClosePar();
        pop();
    }

    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        write(owlIndividual.getBrowserText());
    }

    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        push(owlIntersectionClass);
        writeNAryLocical(owlIntersectionClass);
        pop();
    }

    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        push(owlMaxCardinality);
        writeCardinality(owlMaxCardinality);
        pop();
    }

    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        push(owlMinCardinality);
        writeCardinality(owlMinCardinality);
        pop();
    }

    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        push(owlNamedClass);
        write(owlNamedClass.getBrowserText());
        pop();
    }

    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        write(owlObjectProperty.getBrowserText());
    }

    public void visitOWLOntology(OWLOntology owlOntology) {
        write("<OWLOntology>");
    }

    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom someValuesFrom) {
        push(someValuesFrom);
        writeQuantifier(someValuesFrom);
        pop();
    }

    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        push(owlUnionClass);
        writeNAryLocical(owlUnionClass);
        pop();
    }

    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
        write(rdfsDatatype.getBrowserText());
    }

    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        write(rdfIndividual.getBrowserText());
    }

    public void visitRDFList(RDFList rdfList) {
        write("<RDFList>");
    }

    public void visitRDFProperty(RDFProperty rdfProperty) {
        write(rdfProperty.getBrowserText());
    }

    public void visitRDFSLiteral(RDFSLiteral rdfsLiteral) {
        write(rdfsLiteral.getBrowserText());
    }

    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        push(rdfsNamedClass);
        pop();
    }

    public void visitRDFUntypedResource(RDFUntypedResource rdfUntypedResource) {
        write(rdfUntypedResource.getURI());
    }

	public void visitSWRLIndividual(SWRLIndividual swrlIndividual) {		
		visitOWLIndividual(swrlIndividual);
	}

	public void visitSWRLAtomListIndividual(SWRLAtomList swrlAtomList) {		
		visitRDFList(swrlAtomList);
	}

}
