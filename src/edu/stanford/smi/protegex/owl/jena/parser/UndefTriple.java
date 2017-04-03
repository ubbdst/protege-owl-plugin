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

package edu.stanford.smi.protegex.owl.jena.parser;


import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class UndefTriple {

	private AResource tripleSubj;
	private AResource triplePred;
	private Object tripleObj;
	private TripleStore tripleStore;


	public UndefTriple(AResource subj, AResource pred, AResource obj, TripleStore ts) {
		this.tripleSubj = subj;
		this.triplePred = pred;
		this.tripleObj = obj;
		this.tripleStore = ts;
	}

	public UndefTriple(AResource subj, AResource pred, ALiteral obj, TripleStore ts) {
		this.tripleSubj = subj;
		this.triplePred = pred;
		this.tripleObj = obj;
		this.tripleStore = ts;
	}


	public Object getTripleObj() {
		return tripleObj;
	}

	public AResource getTriplePred() {
		return triplePred;
	}

	public AResource getTripleSubj() {
		return tripleSubj;
	}

	public TripleStore getTripleStore() {
		return tripleStore;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof UndefTriple)) {
			return false;
		}

		UndefTriple other = (UndefTriple) obj;

		return other.getTripleSubj().equals(getTripleSubj()) &&
		       other.getTriplePred().equals(getTriplePred()) &&
		       other.getTripleObj().equals(getTripleObj()) &&
		       other.getTripleStore().equals(getTripleStore());
	}

	@Override
	public int hashCode() {
		return 2*tripleSubj.hashCode() + 27*triplePred.hashCode() + tripleObj.hashCode();
	}


	@Override
	public String toString() {
		return "(" + getTripleSubj() + "  " + getTriplePred() + "  " + getTripleObj() + ") triplestore: " + getTripleStore();
	}

}
