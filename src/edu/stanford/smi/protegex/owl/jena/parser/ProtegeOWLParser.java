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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.ARPHandlers;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.NamespaceHandler;
import com.hp.hpl.jena.rdf.arp.StatementHandler;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.MessageError.Severity;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStoreManager;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.repository.impl.AbstractStreamBasedRepositoryImpl;
import edu.stanford.smi.protegex.owl.repository.util.XMLBaseExtractor;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * An OWL parser that reads an OWL stream triple-by-triple and writes the
 * corresponding Protege-OWL Triples into one or more TripleStores.
 * The current implementation uses the Jena ARP parser for triple loading.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Matthew Horridge
 */
public class ProtegeOWLParser {
    private static transient Logger log = Log.getLogger(ProtegeOWLParser.class);

    public final static String JENA_ERROR_LEVEL_PROPERTY = "jena.parser.error_level";
    public final static String CREATE_UNTYPED_RESOURCES = "protegeowl.parser.create.untyped.resources";
    public final static String PRINT_LOAD_TRIPLES_LOG = "protegeowl.parser.print.load.triples.log";
    public final static String PRINT_LOAD_TRIPLES_LOG_INCREMENT = "protegeowl.parser.print.load.triples.log.increment";

	private static String topOntologyName;
	//needed for merging imports to support import by location
	private static String currentlyParsingOntologyLocation;

	private OWLModel owlModel;
	private boolean importing = false;
	private boolean isMergeImportMode = false;

	private int tripleCount;
	private boolean printLoadTriplesLog = true;
	private int printLoadTriplesLogIncrement = 10000;

	private TripleProcessor tripleProcessor;


	public ProtegeOWLParser(OWLModel owlModel) {
		tripleCount = 0;
		topOntologyName = null;
		printLoadTriplesLog = ApplicationProperties.getBooleanProperty(PRINT_LOAD_TRIPLES_LOG, true);
		printLoadTriplesLogIncrement = ApplicationProperties.getIntegerProperty(PRINT_LOAD_TRIPLES_LOG_INCREMENT, 10000);

		this.owlModel = owlModel;
	}



	/************************************ ARP ****************************************/

	/**
	 * An interface needed as an abstraction of the various methods to invoke the Jena ARP
	 * (the various load methods with different parameters).
	 */
	public interface ARPInvokation {

		public void invokeARP(ARP arp)
		        throws Exception;
	}


	private ARPInvokation createARPInvokation(final InputStream inputStream, final String uri) {
		ARPInvokation invokation = new ARPInvokation() {
			public void invokeARP(ARP arp)
			throws Exception {

				setErrorLevel(arp);
				arp.load(inputStream, uri);
				inputStream.close();
			}
		};
		return invokation;
	}


	private ARPInvokation createARPInvokation(final Reader reader, final String uri) {
		ARPInvokation invokation = new ARPInvokation() {
			public void invokeARP(ARP arp) throws Exception {
				setErrorLevel(arp);
				arp.load(reader, uri);
				reader.close();
			}
		};
		return invokation;
	}


	protected ARP createARP(TripleStore tripleStore) {
		ARP arp = new ARP();
		ARPHandlers handlers = arp.getHandlers();
		handlers.setStatementHandler(new ProtegeOWLStatementHandler(tripleStore));
		handlers.setErrorHandler(new ProtegeOWLErrorHandler());
		if (isMergingImportMode()) {
			handlers.setNamespaceHandler(new ProtegeOWLMergingNamespaceHandler(tripleStore));
		} else {
			handlers.setNamespaceHandler(new ProtegeOWLNamespaceHandler(tripleStore));
		}
		arp.setHandlersWith(handlers);
		return arp;
	}


	public void setErrorLevel(ARP arp) {
		String errorLevel = ApplicationProperties.getApplicationOrSystemProperty(ProtegeOWLParser.JENA_ERROR_LEVEL_PROPERTY, "lax");

		if (errorLevel.equalsIgnoreCase("default")) {
			arp.getOptions().setDefaultErrorMode();
		} else if (errorLevel.equalsIgnoreCase("lax")) {
			arp.getOptions().setLaxErrorMode();
		} else if (errorLevel.equalsIgnoreCase("strict")) {
			arp.getOptions().setStrictErrorMode();
		} else {
			arp.getOptions().setLaxErrorMode();
		}

		arp.getOptions().setErrorMode(ARPErrorNumbers.WARN_REDEFINITION_OF_ID, ARPErrorNumbers.EM_IGNORE);
	}


	/**
	 * This method loads an ontology pointed to by the specified URI.
	 *
	 * @param uri The <code>URI</code> that points to the ontology.
	 */
	public void run(final URI uri)
	        throws OntologyLoadException {

		URL url;
		try {
			url = uri.toURL();
		} catch (MalformedURLException e) {
			throw new OntologyLoadException(e);
		}
		URI xmlBase = null;
		if (uri != null) {
		    try {
		        xmlBase = XMLBaseExtractor.getXMLBase(uri.toString());
		    } catch (MalformedURLException e) {
		        throw new OntologyLoadException(e, "Malformed URL: " + uri);
		    } catch (IOException e) {
		        throw new OntologyLoadException(e);
		    }
		}
		if (xmlBase == null) {
		    xmlBase = uri;
		}

		loadTriples(url.toString(), xmlBase, createARPInvokation(getInputStream(url), xmlBase.toString()));
	}


	/**
	 * This method loads an ontology from the specified input stream using
	 * the specified base uri.
	 *
	 * @param is
	 * @param xmlBase The XML base to use when loading the ontology.  This is typically
	 *                the URI from where the ontology was loaded.
	 * @throws Exception
	 */
	public void run(final InputStream is,
	                final String xmlBase)
	        throws OntologyLoadException {

	    loadTriples(null, URIUtilities.createURI(xmlBase), createARPInvokation(is, xmlBase));
	}


	/**
	 * This method loads an ontology using the specified Reader and using
	 * the specified base uri.  Note that the preferred method of loading an
	 * ontology is to use the run method that takes an <code>InputStream</code>
	 * as a parameter rather than a <code>Reader</code>.
	 *
	 * @param reader  The reader used to read the ontology
	 * @param xmlBase The XML base to use when loading the ontology.  This is typically
	 *                the URI from where the ontology was loaded.
	 * @throws Exception
	 */
	public void run(final Reader reader,
	                final String xmlBase)
	        throws OntologyLoadException {

	    loadTriples(null, URIUtilities.createURI(xmlBase),  createARPInvokation(reader, xmlBase));
	}


	/************************************** Loading triples *********************************/


	public void loadTriples(String ontologyLocation, URI xmlBase, InputStream is) throws OntologyLoadException {
	    loadTriples(ontologyLocation, xmlBase, createARPInvokation(is, ontologyLocation.toString()));
	}

	/*
	 * ontologyURI - is not clearly defined - it is either the location URL (the top-level parse), or the
	 *  ontology name (for imports)
	 */
	private void loadTriples(final String ontologyURI, URI xmlBase, final ARPInvokation invokation)
						throws OntologyLoadException {
		//the triple store where the parsing will write the parsed triples
	    final TripleStore tripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
	    ((AbstractOWLModel) owlModel).getGlobalParserCache().getParsedTripleStores().add(tripleStore);

	    if (ontologyURI != null) {
	    	tripleStore.addIOAddress(ontologyURI.toString());
	    }

	    if (xmlBase != null) {
	    	tripleStore.addIOAddress(xmlBase.toString());
	    	tripleStore.setOriginalXMLBase(xmlBase.toString());
	    }

	    topOntologyName = ontologyURI != null ? ontologyURI :
	    					xmlBase != null ? xmlBase.toString() : null;

	    boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);
	    boolean isExpandShortNamesEnabled = owlModel.isExpandShortNameInMethods();
	    owlModel.setExpandShortNameInMethods(false);

	    OWLFrameStoreManager frameStoreManager = owlModel.getFrameStoreManager();
	    try {
	        tripleProcessor = ((AbstractOWLModel) owlModel).getGlobalParserCache().getTripleProcessor();

	        Log.getLogger().info("Loading triples for: " + ontologyURI);

	        ARP arp = createARP(tripleStore);

	        currentlyParsingOntologyLocation = ontologyURI;
	        
	        long startTime = System.currentTimeMillis();

	        try {
	            invokation.invokeARP(arp);
	        } catch (ProtegeOWLParserException e) {
	            throw e;
	        } catch (Exception e) {
	        	ProtegeOWLParserException ioe = new ProtegeOWLParserException(e, e.getMessage(), "");
	            throw ioe;
	        }

	        long endTime = System.currentTimeMillis();

	        currentlyParsingOntologyLocation = null;
	        
	        Log.getLogger().info("    Completed triple loading after " + (endTime - startTime) + " ms");
	        tripleProcessor.getGlobalParserCache().dumpUndefTriples(Level.FINE);

	        if (log.isLoggable(Level.FINE)) {
	            log.fine("Start processing imports ...");
	        }

	        if (isMergingImportMode()) {
	        	processMergingImports(tripleStore, ontologyURI);
	        } else {
	        	processImports(tripleStore);
	        }

	        if (log.isLoggable(Level.FINE)) {
	            log.fine("End processing imports");
	        }

	        handleNoOntologyDeclarationFound(tripleStore, ontologyURI, xmlBase);

	        if (!importing) {
	            doFinalPostProcessing(owlModel);
	            if (isMergingImportMode()) {
					doFinalPostProcessingMergingMode(owlModel);
				}
	        }

	    } catch (AlreadyImportedException e) {
	        Log.getLogger().warning("Broken import led to attempt to import the same ontology twice");
	        owlModel.getTripleStoreModel().deleteTripleStore(tripleStore);
	    }
	    finally {
	        owlModel.setGenerateEventsEnabled(eventsEnabled);
	        owlModel.setExpandShortNameInMethods(isExpandShortNamesEnabled);
	    }
	}


	private void handleNoOntologyDeclarationFound(TripleStore tripleStore, String ontologyName, URI xmlBase) throws AlreadyImportedException {
		 // no ontology declaration was found
        if (tripleStore.getName() == null) {
            String oname = null;

            if (ontologyName != null) {
            	oname = ontologyName;
            } else if (xmlBase != null) {
            	oname = xmlBase.toString();
            }
            else {
            	oname = FactoryUtils.generateOntologyURIBase();
            }

            FactoryUtils.addOntologyToTripleStore(owlModel, tripleStore, oname);
        }
	}


	public static void doFinalPostProcessing(OWLModel owlModel) throws OntologyLoadException {
		try {
			TripleProcessor tripleProcessor = ((AbstractOWLModel) owlModel).getGlobalParserCache().getTripleProcessor();
			tripleProcessor.doPostProcessing();

			//copy restrictions in facets - check whether this can be moved to the postprocessor
			if (owlModel instanceof JenaOWLModel)  {
				long t0 = System.currentTimeMillis();
				((JenaOWLModel) owlModel).copyFacetValuesIntoNamedClses();
				((JenaOWLModel) owlModel).copyFacetValuesIntoProperties();
				log.info("Updating underlying frames model in " + (System.currentTimeMillis() - t0) + " ms");
			}

			if (OWLUI.getSortClassTreeAfterLoadOption()) {
				long t0 = System.currentTimeMillis();
				TripleStoreUtil.sortSubclasses(owlModel);
				log.info("Sorting OWL class tree in " + (System.currentTimeMillis() - t0) + " ms");
			}
		} catch (Exception e) {
			throw new OntologyLoadException(e, " Errors at post processing ontology");
		}
	}


	@SuppressWarnings("deprecation")
	private void doFinalPostProcessingMergingMode(OWLModel owlModel) {
		//delete ontology instances - should destroy also imports tree
		Collection ontologies = owlModel.getOWLOntologies();
		ontologies.remove(owlModel.getDefaultOWLOntology());

		for (Iterator iterator = ontologies.iterator(); iterator.hasNext();) {
			RDFResource ont = (RDFResource) iterator.next();
			ont.delete();
		}
		//make sure to remove invalid ontologies
		Collection<String> allOntologies = OWLImportsCache.getAllOntologies();
		allOntologies.remove(owlModel.getDefaultOWLOntology().getName());
		for (String ont : allOntologies) {
			RDFResource res = owlModel.getRDFResource(ont);
			if (res != null && res.hasDirectType(owlModel.getSystemFrames().getRdfExternalResourceClass())) {
				res.delete();
			}
		}
	}


	/************************************* Imports *************************************/


	public void setImporting(boolean importing) {
	    this.importing = importing;
	}

	private void processImports(TripleStore tripleStore) throws OntologyLoadException {
		Set<String> thisOntoImports = OWLImportsCache.getOWLImportsURI(tripleStore.getName());
		    for (String import_ : new TreeSet<String>(thisOntoImports)) {
		        URI u = ((AbstractOWLModel) owlModel).loadImportedAssertions(URIUtilities.createURI(import_));
		        if (u == null) {
		            ((AbstractOWLModel) owlModel).addParserError(new MessageError("Could not import ontology from " + import_));
		        }
		    }
	}

	private void processMergingImports(TripleStore tripleStore, String importingOntologyName) throws OntologyLoadException {
		if (!importing && topOntologyName != null) {
			//In the top level parse, we get the ontology name from the triple processor
			importingOntologyName = topOntologyName;
		}

		if (importingOntologyName == null) {
			return; // no imports if no ontology declaration
		}

		Set<String> thisOntoImports = OWLImportsCache.getOWLImportsURI(importingOntologyName);

		for (String importedOntologyName : thisOntoImports) {
			if (OWLImportsCache.isImported(importedOntologyName)) {
				continue;
			}

			URI ontologyURI = URIUtilities.createURI(importedOntologyName);
			URI xmlBase = null;

			try {
				//does other checking, too
				xmlBase = getXMLBaseForMerge(ontologyURI);
			} catch (OntologyLoadException e) {
				Log.getLogger().warning("Could not load import from: " + ontologyURI + " (Skipping this import)");
				((AbstractOWLModel) owlModel).addParserError(new MessageError("Could not import ontology from " + ontologyURI));
				continue;
			}

			ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
			parser.setMergingImportMode(isMergingImportMode());
			parser.setImporting(true);
			parser.loadTriples(importedOntologyName, xmlBase , getInputStreamForMerge(ontologyURI));
		}
	}

	private InputStream getInputStreamForMerge(URI ontologyURI) throws OntologyLoadException {
		AbstractStreamBasedRepositoryImpl rep = (AbstractStreamBasedRepositoryImpl) owlModel.getRepositoryManager().getRepository(ontologyURI);
		InputStream is = null;
		try {
			is = rep == null ? getInputStream(ontologyURI.toURL()) : rep.getInputStream(ontologyURI);

		} catch (MalformedURLException e) {
			throw new OntologyLoadException(e, "Malformed ontology name: " + ontologyURI);
		}
		return is;
	}

	private URI getXMLBaseForMerge(final URI ontologyURI) throws OntologyLoadException {
		URI xmlBase = ontologyURI;
		try {
		    URI uri = XMLBaseExtractor.getXMLBase(getInputStreamForMerge(ontologyURI));
		    if (uri != null) {
		        xmlBase = uri;
		    }
		    else {
		        xmlBase = ontologyURI;
		    }
		} catch (Throwable e) {
			throw new OntologyLoadException(e);
		}
		return xmlBase;
	}

	/*************************************** Management  *******************************/

	static void setTopOntologyName(String ontName) {
		topOntologyName = ontName;
	}

	static String getCurrentlyParsingOntologyLocation() {
		return currentlyParsingOntologyLocation;
	}
	
	public static Collection<MessageError> getParserErrors(OWLModel owlModel) {
	    return ((AbstractOWLModel) owlModel).getParserErrors();
	}

	public static InputStream getInputStream(URL url) throws OntologyLoadException {
		try {
			if(url.getProtocol().equals("http")) {
				URLConnection conn;

				conn = url.openConnection();
				conn.setConnectTimeout(ApplicationProperties.getUrlConnectTimeout()*1000);
				conn.setReadTimeout(ApplicationProperties.getUrlConnectReadTimeout()*1000);

				conn.setRequestProperty("Accept", "application/rdf+xml");
				conn.addRequestProperty("Accept", "text/xml");
				conn.addRequestProperty("Accept", "*/*");
				return conn.getInputStream();
			}
			else {
				return url.openStream();
			}
		} catch (IOException e) {
			throw new OntologyLoadException(e, "Could not get input stream for " + url);
		}
	}


	/*********************************** ARP Interface Implementations *****************************/

	private class ProtegeOWLErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception)
		        throws SAXException {
			saveErrors(exception, Severity.ERROR);
		}


		public void fatalError(SAXParseException exception)
		        throws SAXException {
			saveErrors(exception, Severity.FATAL);
		}


		public void warning(SAXParseException exception)
		        throws SAXException {
            saveErrors(exception, Severity.WARNING);
		}

		protected void saveErrors(SAXParseException ex, Severity severity) {

			String message = (severity == Severity.WARNING ? "A warning " : "An error " ) + "occurred at parsing the OWL ontology ";

			message = message + "\n\n    " + topOntologyName + "\n\n";
           	message = message + "    at line " + ex.getLineNumber() + " and column " + ex.getColumnNumber() + ".\n";
        	message = message + "    Jena parse error message: " + ex.getMessage();

        	Log.getLogger().log(severity == Severity.WARNING ? Level.WARNING : Level.SEVERE, message, ex);

        	((AbstractOWLModel) owlModel).addParserError(new MessageError(ex, message, severity));
		}
	}


	class ProtegeOWLStatementHandler implements StatementHandler {

		TripleStore tripleStore;

		public ProtegeOWLStatementHandler(TripleStore tripleStore) {
			this.tripleStore = tripleStore;
		}


		public void statement(AResource subj, AResource pred, AResource obj) {
			if (log.isLoggable(Level.FINE)) {
				log.fine("NewStatementHandler: " + subj + "  " + pred + "  " + obj);
			}

			tripleCount++;
			printTriplesLoadLogMessage();

			try {
				tripleProcessor.processTriple(subj, pred, obj, tripleStore, false);
			} catch (Exception e) { //specialize
				Log.getLogger().log(Level.SEVERE, "Error at parsing triple: " +
						subj + " " + pred + " " + obj, e);
			}
		}


		public void statement(AResource subj, AResource pred, ALiteral lit) {
			if (log.isLoggable(Level.FINE)) {
				log.fine(subj + "  " + pred + "  " + lit);
			}

			tripleCount++;
			printTriplesLoadLogMessage();

			try {
				tripleProcessor.processTriple(subj, pred, lit, tripleStore, false);
			} catch (Exception e) { //specialize
				Log.getLogger().log(Level.SEVERE, "Error at parsing triple: " +
						subj + " " + pred + " " + lit, e);
			}
		}

		protected void printTriplesLoadLogMessage() {
			if(printLoadTriplesLog && tripleCount % printLoadTriplesLogIncrement == 0) {
				Log.getLogger().info("    Loaded " + tripleCount + " triples " + tripleProcessor.getGlobalParserCache().getUndefTripleSize());
			}
		}
	}


	class ProtegeOWLNamespaceHandler implements NamespaceHandler {

		TripleStore tripleStore;

	    public ProtegeOWLNamespaceHandler(TripleStore tripleStore) {
	    	this.tripleStore = tripleStore;
		}

		public void endPrefixMapping(String prefix) { // does nothing, just for logging
	        NamespaceManager namespaceManager = tripleStore.getNamespaceManager();
	        if (log.isLoggable(Level.FINE)) {
                log.fine("*** " + prefix + " -> " + namespaceManager.getNamespaceForPrefix(prefix));
            }
	    }

		public void startPrefixMapping(String prefix, String namespace) {
		    NamespaceManager namespaceManager = tripleStore.getNamespaceManager();
		    namespaceManager.setPrefix(namespace, prefix);
		}
	}

	/**
	 * Will merge automatically the imported prefixes.
	 * Depends on the fact that the the top is parsed first,
	 * then the imports, in a deep-search way.
	 * It does not catch the namespaces without prefixes
	 */
	class ProtegeOWLMergingNamespaceHandler implements NamespaceHandler {

		TripleStore tripleStore;

	    public ProtegeOWLMergingNamespaceHandler(TripleStore tripleStore) {
	    	this.tripleStore = tripleStore;
		}

		public void endPrefixMapping(String prefix) { // does nothing, just for logging
	        NamespaceManager namespaceManager = tripleStore.getNamespaceManager();
	        if (log.isLoggable(Level.FINE)) {
                log.fine("*** " + prefix + " -> " + namespaceManager.getNamespaceForPrefix(prefix));
            }
	    }

		public void startPrefixMapping(String prefix, String namespace) {
		    NamespaceManager namespaceManager = tripleStore.getNamespaceManager();
		    if (!namespaceManager.getPrefixes().contains(prefix)) {
		    	namespaceManager.setPrefix(namespace, prefix);
		    }
		}
	}


	//TODO: Maybe we'll move these methods somewhere else later
	public boolean isMergingImportMode() {
		return isMergeImportMode;
	}

	public void setMergingImportMode(boolean isMergingImport) {
		isMergeImportMode = isMergingImport;
	}

}