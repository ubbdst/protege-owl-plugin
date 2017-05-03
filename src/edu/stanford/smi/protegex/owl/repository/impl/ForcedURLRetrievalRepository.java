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
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;
import edu.stanford.smi.protegex.owl.repository.util.URLInputSource;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 21, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ForcedURLRetrievalRepository extends AbstractStreamBasedRepositoryImpl {

    private URL url;

    private URI uri;

    private URI actualOntologyName;


    public ForcedURLRetrievalRepository(URL url) {
        this.url = url;
        this.uri = null;
        refresh();
    }


    public boolean contains(URI ontologyName) {
        if (uri != null) {
            return ontologyName.equals(uri);
        }
        return false;
    }


    public void refresh() {
        try {
            OntologyNameExtractor extractor = new OntologyNameExtractor(new URLInputSource(url));
            if (extractor.isPossiblyValidOntology()) {
                actualOntologyName = extractor.getOntologyName();
                try {
                    uri = new URI(url.toString());
                }
                catch (URISyntaxException e) {
                  Log.getLogger().log(Level.SEVERE, "Exception caught", e);
                }
            }
            else {
                uri = null;
            }
        } catch (IOException e) {
        	uri = null;
		}
    }


    public Collection<URI> getOntologies() {
        return Collections.singleton(uri);
    }


    @Override
    public InputStream getInputStream(URI ontologyName)
            throws OntologyLoadException {
        if (uri != null) {
            return ProtegeOWLParser.getInputStream(url);
        }
        else {
            return null;
        }
    }


    public boolean isWritable(URI ontologyName) {
        return false;
    }


    public OutputStream getOutputStream(URI ontologyName)
            throws IOException {
        return null;
    }


    public boolean isSystem() {
        return false;
    }


    public String getRepositoryDescription() {
        return "Forced HTTP retrieval";
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        return url.toString() + " [actual ontology at this location: " + actualOntologyName + "]";
    }


    public String getRepositoryDescriptor() {
        return url.toString();
    }

}

