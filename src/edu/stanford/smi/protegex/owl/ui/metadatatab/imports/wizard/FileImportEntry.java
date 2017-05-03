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

import java.io.File;

import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;
import edu.stanford.smi.protegex.owl.repository.util.FileInputSource;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Dec 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileImportEntry extends AbstractImportEntry {

	private File file;

	public FileImportEntry(File file) {
		this.file = file;
	}


	public boolean isPossibleToImport() {
		try {
			OntologyNameExtractor nameExtractor = new OntologyNameExtractor(new FileInputSource(file));
			if(nameExtractor.isPossiblyValidOntology() == false) {
				throw new IllegalArgumentException("The document pointed to by " + file + " does not " +
				                                   "appear to contain a valid RDF/XML representation of an ontology.");
			}
			setOntologyURI(nameExtractor.getOntologyName());
			setRepository(new LocalFileRepository(file));
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

