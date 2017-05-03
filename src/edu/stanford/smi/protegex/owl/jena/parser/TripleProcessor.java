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

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

//-- later --
//TODO: Postprocessing GCI - refactor in their own class


public class TripleProcessor {

	private OWLModel owlModel;

	private TripleProcessorForResourceObjects processorResourceObjs;
	private TripleProcessorForLiteralObjects processorLiteralObjs;
	private TripleProcessorForUntypedResources untypedProcessor;
	private TriplePostProcessor postProcessor;

	private GlobalParserCache globalParserCache;


	public TripleProcessor(OWLModel owlModel) {
		this.owlModel = owlModel;

		this.globalParserCache = ((AbstractOWLModel)owlModel).getGlobalParserCache();

		//should come as the last in the initialization
		this.processorResourceObjs = new TripleProcessorForResourceObjects(this);
		this.processorLiteralObjs = new TripleProcessorForLiteralObjects(this);
		this.untypedProcessor = new TripleProcessorForUntypedResources(this);
		this.postProcessor = new TriplePostProcessor(this);
	}


	public boolean processTriple(AResource subj, AResource pred, AResource obj, TripleStore ts, boolean alreadyInUndef) {
		return processorResourceObjs.processTriple(subj, pred, obj, ts, alreadyInUndef);
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, TripleStore ts, boolean alreadyInUndef) {
		return processorLiteralObjs.processTriple(subj, pred, lit, ts, alreadyInUndef);
	}


	public OWLModel getOWLModel() {
		return owlModel;
	}

	public void addUndefTriple(AResource subj, AResource pred, AResource obj, String undefName, boolean alreadyInUndef, TripleStore ts) {
		if (!alreadyInUndef) {
			globalParserCache.addUndefTriple(new UndefTriple(subj, pred, obj, ts), undefName);
		}
	}

	protected void checkUndefinedResources(String uri) {
		Collection<UndefTriple> undefTriples = globalParserCache.getUndefTriples(uri);

		for (Iterator<UndefTriple> iter = undefTriples.iterator(); iter.hasNext();) {
			UndefTriple undefTriple = iter.next();
			Object obj = undefTriple.getTripleObj();

			TripleStore undefTripleStore = undefTriple.getTripleStore();

			boolean success = false;

			if (obj instanceof AResource) {
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), undefTripleStore, true);
			} else if (obj instanceof ALiteral) {
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), undefTripleStore, true);
			}

			if (success) {
				iter.remove();
				//globalParserCache.removeUndefTriple(uri, undefTriple);
			}
		}

		//clean up
		undefTriples = globalParserCache.getUndefTriples(uri);
		if (undefTriples.isEmpty()) {
			globalParserCache.removeUndefTripleKey(uri);
		}

	}


	public GlobalParserCache getGlobalParserCache() {
		return globalParserCache;
	}


	public void doPostProcessing() {
		processUndefTriples();
		postProcessor.doPostProcessing();
	}


	public void processUndefTriples() {
		untypedProcessor.processUndefTriples();
	}

	public void createUntypedResources() {
		untypedProcessor.createUntypedResources();
	}

}
