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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard;

import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.ForcedURLRetrievalRepository;
import edu.stanford.smi.protegex.owl.repository.impl.HTTPRepository;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;
import edu.stanford.smi.protegex.owl.repository.util.URLInputSource;

import java.net.URI;
import java.net.URL;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Dec 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class URLImportEntry extends AbstractImportEntry {

	private URL url;

	public URLImportEntry(URL url) {
		this.url = url;
	}


	public boolean isPossibleToImport() {
		try {
			OntologyNameExtractor extractor = new OntologyNameExtractor(new URLInputSource(url));
			URI uri = extractor.getOntologyName();
			if(uri != null) {
				setRepository(new HTTPRepository(url));
			}
			else {
				if(extractor.isPossiblyValidOntology()) {
					uri = new URI(url.toString());
					setRepository(new ForcedURLRetrievalRepository(url));
				} else {
					throw new IllegalArgumentException("The document pointed to by " + url + " does not " +
					                                   "appear to be a valid ontology.");
				}
			}
			setOntologyURI(uri);
			return true;
		}
		catch(Exception e) {
			addError(e);
			setOntologyURI(null);
			setRepository(null);
			return false;
		}
	}


	public Repository getRepositoryToAdd() {
		return getRepository();
	}
}

