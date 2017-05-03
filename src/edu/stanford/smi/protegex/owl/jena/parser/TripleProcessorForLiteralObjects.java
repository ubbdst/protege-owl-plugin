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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

class TripleProcessorForLiteralObjects extends AbstractStatefulTripleProcessor {
	private static final transient Logger log = Log.getLogger(TripleProcessor.class);


	public TripleProcessorForLiteralObjects(TripleProcessor processor) {
		super(processor);
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, TripleStore ts, boolean alreadyInUndef) {
	    if (log.isLoggable(Level.FINER)) {
	        log.finer("Processing triple with literal: " + subj + " " + pred + " " + lit);
	    }

		String predName = ParserUtil.getResourceName(pred);
		Slot predSlot = getSlot(predName);

		if (predSlot == null) {
			if (!alreadyInUndef) {
				globalParserCache.addUndefTriple(new UndefTriple(subj, pred, lit, ts), predName);
			}
			return false;
		}

		//do some checks if it already exists and is twice defined?
		String subjName = ParserUtil.getResourceName(subj);
		Frame subjFrame = getFrame(subjName);

		//check the order of these calls
		if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
			subjFrame = createRestriction(subjName, predName, ts);
		}

		subjFrame = getFrame(subjName);

		//checking and adding to undefined
		if (subjFrame == null) {
			if (!alreadyInUndef) {
				globalParserCache.addUndefTriple(new UndefTriple(subj, pred, lit, ts), subjName);
			}
			return false;
		}

		RDFSLiteral rdfsLiteral = createRDFSLiteral(lit, (RDFProperty) predSlot);

		if (rdfsLiteral == null) {
			return false;
		}

		addTriple(subjFrame, predSlot, rdfsLiteral, ts);

		return true;
	}


	private void addTriple(Frame subjFrame, Slot predSlot, RDFSLiteral rdfsLiteral, TripleStore ts) {
		// add what it is really in the triple
		FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot,
				AbstractOWLModel.convertRDFSLiteralToInternalFormat(rdfsLiteral), ts);
		// add frame correspondent is missing because there is no mapping for literal properties
	}



	private RDFSLiteral createRDFSLiteral(ALiteral literal, RDFProperty property) {
		if(literal.getLang() != null && literal.getLang().length() > 0) {
			return DefaultRDFSLiteral.create(owlModel, literal.toString(), literal.getLang());
		}
		else if(literal.getDatatypeURI() != null) {
			RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(literal.getDatatypeURI());
			if(datatype == null) {
				return DefaultRDFSLiteral.create(owlModel, literal.toString());
			}
			else {

				return DefaultRDFSLiteral.create(owlModel, literal.toString(), datatype);
			}
		}
		else {
			// If literal has no datatype, make a qualified guess using the property's range
			RDFResource range = property.getRange();
			if(range instanceof RDFSDatatype) {
				RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(range.getURI());
				return DefaultRDFSLiteral.create(owlModel, literal.toString(), datatype);
			}
			else {
				return DefaultRDFSLiteral.create(owlModel, literal.toString());
			}
		}
	}

}
