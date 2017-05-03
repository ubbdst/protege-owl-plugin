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

package edu.stanford.smi.protegex.owl.repository.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.ARPHandlers;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.StatementHandler;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 12, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that extracts the name (URI) of
 * an ontology from an OWL file.  The name of an ontology is
 * the xml:base URI.
 */
public class OntologyNameExtractor {
    private static Logger log = Log.getLogger(OntologyNameExtractor.class);
    
    private InputStreamSource source;

    private URI uri;

    private boolean valid = true;


    /**
     * Constructs an <code>OntologyNameExtractor</code> that
     * should obtain the name of an ontology which can be
     * read via the specified input stream.
     *
     * @param source The input stream from which the ontology
     *           can be read.
     */
    public OntologyNameExtractor(InputStreamSource source) throws IOException {
    	try {
    		this.source = source;
    		init();
    	}
    	catch (Throwable t) {
    		IOException ioe = new IOException(t.getMessage());
    		ioe.initCause(t);
    		throw ioe;
    	}
    }
    
    private void init() {
        searchForOntologyDeclaration();
        if (uri == null && valid) {
            searchForXMLBaseDeclaration();
        }
        if (uri == null && valid) {
            useDocumentLocation();
        }
    }


    private void searchForOntologyDeclaration() {
        ARP arp = new ARP();
        ARPHandlers handlers = arp.getHandlers();
        handlers.setStatementHandler(new StatementHandler() {
            
            public void statement(AResource subject, AResource predicate, ALiteral value) {

            }
            
            public void statement(AResource subject, AResource predicate, AResource object) {
                if (predicate.getURI().equals(RDFNames.Slot.TYPE) && object.getURI().equals(OWLNames.Cls.ONTOLOGY)) {
                    uri = URI.create(subject.getURI());
                    throw new OntologyDeclarationFoundException();
                }
            }
        });
        handlers.setErrorHandler(new ErrorHandler() {
            
            public void warning(SAXParseException exception) throws SAXException {

            }
            
            public void fatalError(SAXParseException exception) throws SAXException {
                valid = false;
            }
            
            public void error(SAXParseException exception) throws SAXException {
                valid = false;
            }
        });
        InputStream is = null; 
        try {
            is = source.getInputStream();
            arp.load(is);
        }
        catch (OntologyDeclarationFoundException odfe) {
            ;
        }
        catch (Throwable t) {
            log.log(Level.WARNING, "Exception caught trying to parse " + source.getURL() + " for an ontology declaration", t);
            valid = false;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Exception found closing ontology", e);
            }
        }
    }
    
    private void searchForXMLBaseDeclaration() {
        InputStream is = null;
        try {
            is = source.getInputStream();
            uri = XMLBaseExtractor.getXMLBase(is);
        }
        catch (Throwable t) {
            valid  = false;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Exception found closing ontology", e);
            }  
        }
        
    }
    
    private void useDocumentLocation() {
    	String failMsg = "Could not find name for ontology.  Even the document location didn't work";
        try {
        	uri = source.getURL().toURI();
        }
        catch (URISyntaxException urise) {
        	log.log(Level.WARNING, failMsg, urise);
        	return;
        }
    }


    public boolean isPossiblyValidOntology() {
        return valid;
    }


    public URI getOntologyName() throws IOException {
        return uri;
    }
    
    public static class OntologyDeclarationFoundException extends RuntimeException {
        
    }
    
    
}

