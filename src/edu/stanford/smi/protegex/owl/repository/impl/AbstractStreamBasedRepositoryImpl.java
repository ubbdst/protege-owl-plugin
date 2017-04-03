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

package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.XMLBaseExtractor;

public abstract class AbstractStreamBasedRepositoryImpl implements Repository {
    /**
     * Gets an inputstream to read the specified ontology
     * from
     *
     * @param ontologyName The name of the ontology.
     * @return an <code>InputStream</code> to read the ontology
     *         from, or <code>null</code> if the repository does not
     *         contain the ontology or the ontology cannot be retrieved.
     */
    public abstract InputStream getInputStream(URI ontologyName) throws OntologyLoadException;
    
    public TripleStore loadImportedAssertions(OWLModel owlModel, URI ontologyName) throws OntologyLoadException {
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        parser.setImporting(true);
        InputStream is = getInputStream(ontologyName);
        URI xmlBase = XMLBaseExtractor.getXMLBase(getInputStream(ontologyName));
        if (xmlBase == null) {
            xmlBase = ontologyName;
        }
        TripleStore importedTripleStore = null;
        TripleStore importingTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
        try {
            NarrowFrameStore frameStore = new InMemoryFrameDb(ontologyName.toString());
            importedTripleStore = owlModel.getTripleStoreModel().createActiveImportedTripleStore(frameStore);
            parser.loadTriples(ontologyName.toString(), xmlBase, is);
        } finally {
            owlModel.getTripleStoreModel().setActiveTripleStore(importingTripleStore);
        }
        
        /*
         * This call should not be needed anymore.
         * This should be called only once at the end of all the imports. 
         */         
        //((AbstractOWLModel) owlModel).copyFacetValuesIntoNamedClses();
        
        return importedTripleStore;
    }
    
    public boolean hasOutputStream(URI ontologyName) {
        return isWritable(ontologyName);
    }

}
