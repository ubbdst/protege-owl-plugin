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

package edu.stanford.smi.protegex.owl.database;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.menu.OWLBackwardsCompatibilityProjectFixups;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A DatabaseKnowledgeBaseFactory with an even longer name.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseKnowledgeBaseFactory extends DatabaseKnowledgeBaseFactory
        implements OWLKnowledgeBaseFactory {
    private static Logger log = Log.getLogger(OWLDatabaseKnowledgeBaseFactory.class);

    public final static String NAMESPACE_PREFIX_SEPARATOR = ":";


    public OWLDatabaseKnowledgeBaseFactory() {
     	setOwlMode(true);
    }


    @Override
    public KnowledgeBase createKnowledgeBase(Collection errors) {

        ResourceSelectionAction.setActivated(false);

        OWLDatabaseModel owlModel = new OWLDatabaseModel(this);

        return owlModel;
    }



    private void dump(Cls cls, String tabs) {
      if (log.isLoggable(Level.FINE)) {
        log.fine(tabs + cls);
        for (Iterator it = cls.getDirectSubclasses().iterator(); it.hasNext();) {
            Cls subCls = (Cls) it.next();
            if (!subCls.isEditable()) {
                try {
                    dump(subCls, tabs + "  ");
                }
                catch (Exception ex) {
                    log.fine("ERROR at " + cls + " / " + subCls);
                    for (Iterator sit = subCls.getDirectSubclasses().iterator(); sit.hasNext();) {
                        Object o = sit.next();
                        log.fine("- " + o + " = " + (o instanceof Slot) + " " +
                                ((Instance) o).getDirectType());
                    }
                    Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                }
            }
        }
      }
    }


    @Override
    public String getProjectFilePath() {
        return "OWL.pprj";
    }


    @Override
    public String getDescription() {
        return "OWL / RDF Database";
    }

    @Override
    public void loadKnowledgeBase(KnowledgeBase kb,
                                  PropertyList sources,
                                  Collection errors) {
        OWLModel owlModel = (OWLModel) kb;

        super.loadKnowledgeBase(kb, sources, errors);
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        tripleStoreModel.setTopTripleStore(activeTripleStore);
        if (DatabaseFactoryUtils.readOWLOntologyFromDatabase(owlModel, activeTripleStore)) {
            FactoryUtils.loadEncodedNamespaceFromModel(owlModel, activeTripleStore, errors);
            FactoryUtils.addPrefixesToModelListener(owlModel, activeTripleStore);
            owlModel.resetOntologyCache();
            RepositoryFileManager.loadProjectRepositories(owlModel);
            DatabaseFactoryUtils.loadImports(owlModel, errors);
            try {
				ProtegeOWLParser.doFinalPostProcessing(owlModel);
			} catch (OntologyLoadException e) {
				Log.getLogger().log(Level.SEVERE, "Errors at loading knowledge base", e);
				errors.add(new MessageError(e, "Errors at loading knowledge base"));
			}
        }
    }

    @Override
    protected void initializeKB(KnowledgeBase kb,
    		String driver,
    		String url,
    		String user,
    		String password,
    		String table,
    		boolean isInclude) {

    	AbstractOWLModel dkb = (AbstractOWLModel) kb;

    	DatabaseFrameDb db = getDatabaseFrameDb(dkb);
    	db.initialize(dkb.getFrameFactory(), driver, url, user, password, table, isInclude);
    	kb.flushCache();
    }

    @Override
    public void saveKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;
            TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
            OWLBackwardsCompatibilityProjectFixups.insertVersionData(sources);

            //move this from here
            if (owlModel instanceof JenaOWLModel) {
                TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
                DatabaseFactoryUtils.writeOWLOntologyToDatabase(owlModel, activeTripleStore);
            	FactoryUtils.encodeNamespaceIntoModel(owlModel, activeTripleStore);
            }

            if (owlModel instanceof JenaOWLModel) {
                kb.removeFrameStore(owlModel.getOWLFrameStore());
            }
            super.saveKnowledgeBase(kb, sources, errors);
            RepositoryFileManager.saveProjectRepositories(owlModel);
            if (owlModel instanceof JenaOWLModel) {
                kb.insertFrameStore(owlModel.getOWLFrameStore(), 0);
            }
        }
        else {
        	String message = "You can only save OWL projects to OWL Database format.";
            errors.add(new MessageError(message));
            Log.getLogger().severe(message);
        }
    }

}
