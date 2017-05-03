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

package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;

import java.util.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLExpressivityChecker extends OWLModelVisitorAdapter {

	private OWLModel owlModel;

	private Set constructors;

	public static final String FL0 = "FL0";

	public static final String FL_MINUS = "FL-";

	public static final String AL = "AL";

	public static final String C = "C";

	public static final String TRAN_ROLES = "Tran";

	public static final String E = "E";

	public static final String U = "U";

	public static final String I = "I";

	public static final String O = "O";

	public static final String N = "N";

	public static final String Q = "Q";

	public static final String F = "F";

	public static final String H = "H";

	public static final String DATATYPE = "(D)";

	public static final String S = "S";


	public DLExpressivityChecker(OWLModel owlModel) {
		this.owlModel = owlModel;
		constructors = new TreeSet();
	}

	public void check() {
		constructors = new TreeSet();
		constructors.add(FL0);
		for(Iterator it = owlModel.getRDFResources().iterator(); it.hasNext(); ) {
			RDFResource res = (RDFResource) it.next();
			if(res.isSystem() == false) {
				res.accept(this);
			}
		}
		tidy();
	}

	public String getDLName() {
		Collection langFeatures = getSortedName();
		String s = "";
		for(Iterator it = langFeatures.iterator(); it.hasNext(); ) {
			s += it.next() + " ";
		}
		return s.trim();
	}

	public Collection getDL() {
		return getSortedName();
	}

	public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
		// Universal quantification - minimal language is FL0
		constructors.add(FL0);
	}


	public void visitOWLCardinality(OWLCardinality owlCardinality) {
		processCardinality(owlCardinality);
	}


	public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
		// Minimun is AL
		constructors.add(AL);
		RDFSClass complementedCls = owlComplementClass.getComplement();
		if(complementedCls instanceof OWLNamedClass)
		if(isAtomic((OWLNamedClass) complementedCls) == false) {
			// Negation in front of complex class
			constructors.add(C);
		}
		else {
			constructors.add(C);
		}
	}


	public void visitOWLDataRange(OWLDataRange owlDataRange) {
		constructors.add(DATATYPE);
	}


	public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
		constructors.add(DATATYPE);
		if(owlDatatypeProperty.isFunctional()) {
			constructors.add(F);
		}
		if(owlDatatypeProperty.getSuperproperties(false).size() > 0) {
			constructors.add(H);
		}
	}


	public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
		constructors.add(AL);
		constructors.add(O);
	}


	public void visitOWLHasValue(OWLHasValue owlHasValue) {
		constructors.add(AL);
		// Syntactic sugar for existential with a nominal filler
		constructors.add(E);
		constructors.add(O);
	}

	public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
		// Min lang is FL0
		constructors.add(FL0);
	}


	public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
		processCardinality(owlMaxCardinality);
	}


	public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
		processCardinality(owlMinCardinality);
	}

	private void processCardinality(OWLCardinalityBase cardinalityBase) {
		constructors.add(AL);
		if(cardinalityBase.getQualifier().equals(owlModel.getOWLThingClass()) == false) {
			// QCR
			constructors.add(Q);
		}
		else {
			constructors.add(N);
		}
	}

	public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
		constructors.add(FL0);
		Collection disjointClasses = owlNamedClass.getDisjointClasses();
		if(disjointClasses.size() > 0) {
			constructors.add(AL);
			for(Iterator it = disjointClasses.iterator(); it.hasNext(); ) {
				RDFSClass cls = (RDFSClass) it.next();
				if(cls instanceof OWLNamedClass) {
					if(isAtomic((OWLNamedClass) cls) == false) {
						constructors.add(C);
						break;
					}
				}
				else {
					constructors.add(C);
					break;
				}
			}
		}


	}


	public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
		constructors.add(AL);
		if(owlObjectProperty.isFunctional()) {
			constructors.add(F);
		}
		if(owlObjectProperty.getSuperproperties(false).size() > 0) {
			// Role hierarchy
			constructors.add(H);
		}
		if(owlObjectProperty.getInverseProperty() != null) {
			// Inverse roles
			constructors.add(I);
		}
		if(owlObjectProperty.isSymmetric()) {
			// Symmetric roles are simply inverses of themselves
			constructors.add(I);
		}
		if(owlObjectProperty.isTransitive()) {
			// Transitive roles
			constructors.add(TRAN_ROLES);
		}

	}

	public void visitOWLSomeValuesFrom(OWLSomeValuesFrom someValuesFrom) {
		if(someValuesFrom.getFiller().equals(owlModel.getOWLThingClass())) {
			// Limited existential - minimal language is FL-
			constructors.add(FL_MINUS);
		}
		else {
			// Full existential
			constructors.add(E);
			constructors.add(AL);
		}
	}


	public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
		constructors.add(AL);
		constructors.add(U);
	}

	private Collection getSortedName() {
		TreeSet ts = new TreeSet(new LangFeatureComparator());
		ts.addAll(constructors);
		return ts;
	}

	public String toString() {
		String s = "";
		for(Iterator it = constructors.iterator(); it.hasNext(); ) {
			s += it.next() + " ";
		}
		return s.trim();
	}

	private boolean isAtomic(OWLNamedClass cls) {
		// An atomic class is a named class that does
		// not appear on the left hand side of any class
		// axioms.
		Collection superClses = cls.getSuperclasses(false);
		superClses.remove(owlModel.getOWLThingClass());
		if(superClses.size() > 0) {
			return false;
		}
		return cls.getDisjointClasses().size() == 0;
	}


	private void tidy() {
		// FL- is more general than FL0
		if(constructors.contains(FL_MINUS)) {
			constructors.remove(FL0);
		}
		if(constructors.contains(AL)) {
			constructors.remove(FL_MINUS);
			constructors.remove(FL0);
		}
		// Replace E and U with C  i.e. ALUE with ALC
		if(constructors.contains(E) &&
		   constructors.contains(U)) {
			constructors.add(C);
			constructors.remove(E);
			constructors.remove(U);
		}
		else if(constructors.contains(C)) {
			constructors.remove(E);
			constructors.remove(U);
		}
		// Functional roles can be represented with N
		if(constructors.contains(N)) {
			constructors.remove(F);
		}
		// QCRs override number restrictions
		if(constructors.contains(Q)) {
			constructors.remove(N);
		}
		// ALC + transitive roles can be replaced with S
		if(constructors.contains(AL) &&
		   constructors.contains(C) &&
		   constructors.contains(TRAN_ROLES)) {
			constructors.remove(AL);
			constructors.remove(C);
			constructors.remove(TRAN_ROLES);
			constructors.add(S);
		}
	}

	private class LangFeatureComparator implements Comparator {

		private List ORDER = Arrays.asList(new String [] {AL, C, U, E, S, H, O, I, F, N, Q, DATATYPE, TRAN_ROLES});

		public int compare(Object o1,
		                   Object o2) {

			return ORDER.indexOf(o1) - ORDER.indexOf(o2);
		}
	}
}

