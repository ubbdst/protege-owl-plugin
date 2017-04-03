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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 4, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DublinCoreDLVersionRedirectRepository extends AbstractStreamBasedRepositoryImpl {

    private URI dublinCoreOntologyURI;

    private URL redirectURL;

    public static final String DESCRIPTOR = "[Dublin Core DL Redirect]";

	public final static String DC = "http://purl.org/dc/elements/1.1/";

    public final static String DC_ALT = "http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl";


    public DublinCoreDLVersionRedirectRepository() {
        try {
            dublinCoreOntologyURI = new URI(DC);
            redirectURL = new URL(DC_ALT);
        }
        catch (MalformedURLException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        catch (URISyntaxException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public boolean contains(URI ontologyName) {
        return dublinCoreOntologyURI.equals(ontologyName);
    }


    public void refresh() {

    }


    public Collection<URI> getOntologies() {
        return Collections.singleton(dublinCoreOntologyURI);
    }


    @Override
    public InputStream getInputStream(URI ontologyName)
            throws OntologyLoadException {
        return ProtegeOWLParser.getInputStream(redirectURL);
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
        return "Dublin Core DL Version";
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        return "Redirect to " + redirectURL.toString();
    }


    public String getRepositoryDescriptor() {
        return DESCRIPTOR;
    }


    @Override
    public int hashCode() {
        return redirectURL.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DublinCoreDLVersionRedirectRepository) {
            return true;
        }
        else {
            return false;
        }
    }
}

