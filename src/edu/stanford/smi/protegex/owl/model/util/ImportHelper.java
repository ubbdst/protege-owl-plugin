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

package edu.stanford.smi.protegex.owl.model.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportHelper {

    private OWLModel owlModel;

    private Collection<URI> ontologyURIs;
    
    private Collection<InputStream> ontologyStreams;


    /**
     * Creates an import helper that will import ontologies into the
     * specified <code>OWLModel</code>.  The pattern of usage is:
     * <ol>
     * <li>Create an instance specifying the <code>OWLModel</code> to be used
     * in the constructor</li>
     * <li>Add the ontology URIs of the ontologies to be imported using the
     * <code>addImport</code> method.</li>
     * <li>Finally call the <code>importOntologies</code> method, which will
     * do the actual importing (and reloading of the UI if necessary).</li>
     * </ol>
     *
     *
     */
    public ImportHelper(OWLModel owlModel) {
        this.owlModel = owlModel;
        ontologyURIs = new ArrayList<URI>();
        ontologyStreams = new ArrayList<InputStream>();
    }


    /**
     * Adds an ontology to the set of ontologies that will be imported.  Note that
     * this does not actually import the ontologies - it merely adds them to the list
     * of ontologies to be imported.  To perform the actual imports, use the
     * <code>importOntologies()</code> method.
     *
     * @param ontologyURI The URI of the ontology to import
     */
    public void addImport(URI ontologyURI) {
        ontologyURIs.add(ontologyURI);
    }
    
    /**
     * Adds the ontology in an input stream to the set of ontologies that will be imported.
     * Note that this does not actually import the ontology - it merely adds it to the list of
     * ontologies to be imported.  To perform the actual imports use the <code>importOntologes()</code>
     * method.
     */
    public void addImport(InputStream is) {
        ontologyStreams.add(is);
    }


    /**
     * Imports the ontologies that this helper was asked to import.  If the
     * Protege-OWL Application GUI is being used, then the UI is reloaded.
     * Use the alternative <code>reloadGUI(boolean)</code> in TabWidget
     * initialisation code to prevent the GUI being reloaded
     */
    public void importOntologies() throws OntologyLoadException {
        importOntologies(true);
    }


    /**
     * Alternative method that allows the caller to block the GUI
     * reload (for example when initialising TabPlugins)
     * @param reloadGUI - false if no GUI reload is desired
     */
    public void importOntologies(boolean reloadGUI) throws OntologyLoadException {
        ArrayList<URI> importedOntologies = new ArrayList<URI>();
        if (ontologyStreams.isEmpty() && ontologyURIs.isEmpty()) {
            return;
        }
        for (InputStream is : ontologyStreams) {
            URI ontologyURI = loadImportedAssertions(is);
            try {
				is.close();
			} catch (IOException e) {
				Log.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
            if (ontologyURI != null) {
                importedOntologies.add(ontologyURI);
            }
        }
        for(URI ontologyURI : ontologyURIs) {
            if (owlModel.getAllImports().contains(ontologyURI.toString()) == false &&
                    !importedOntologies.contains(ontologyURI)) {
                URI realOntologyUri = ((AbstractOWLModel)owlModel).loadImportedAssertions(ontologyURI);
                if (realOntologyUri != null) {
                    importedOntologies.add(realOntologyUri);
                }
            }
        }
        ProtegeOWLParser.doFinalPostProcessing(owlModel);
        OWLOntology  importingOntology = owlModel.getTripleStoreModel().getActiveTripleStore().getOWLOntology();
        for (URI importedOntologyURI : importedOntologies) {
            importingOntology.addImports(importedOntologyURI);
        }       
        
        if(importedOntologies.size() > 0 ) {
        	owlModel.getTripleStoreModel().updateEditableResourceState();        	
        	if (OWLUtil.runsWithGUI(owlModel)) {            
        		OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
        		if (reloadGUI){
        			ProtegeUI.reloadUI(owlModel);
        		}
        	}
        }
    }
    
    private URI loadImportedAssertions(InputStream is) throws OntologyLoadException {
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        parser.setImporting(true);
        TripleStore importedTripleStore = null;
        TripleStore importingTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
        try {
            NarrowFrameStore frameStore = new InMemoryFrameDb("**temp_name**");
            importedTripleStore = owlModel.getTripleStoreModel().createActiveImportedTripleStore(frameStore);
            parser.run(is, null);
            String ontologyName = importedTripleStore.getName();
            return new URI(ontologyName);
        }
        catch (URISyntaxException e) {
            throw new OntologyLoadException(e, "Invalid URI: " + importedTripleStore.getName());
        } finally {
            owlModel.getTripleStoreModel().setActiveTripleStore(importingTripleStore);
        }
    }
}

