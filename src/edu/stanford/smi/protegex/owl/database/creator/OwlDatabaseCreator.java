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

package edu.stanford.smi.protegex.owl.database.creator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DefaultDatabaseFrameDb;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;

public class OwlDatabaseCreator extends AbstractOwlDatabaseCreator {
	private boolean wipe;
    private List<Repository> repositories = new ArrayList<Repository>();
	private String ontologyName;
	private Class<? extends DatabaseFrameDb> databaseFrameDbClass;

	public OwlDatabaseCreator(boolean wipe) {
		this(new OWLDatabaseKnowledgeBaseFactory(), wipe);
	}

	public OwlDatabaseCreator(OWLDatabaseKnowledgeBaseFactory factory, boolean wipe) {
		super(factory);
		this.wipe = wipe;
		databaseFrameDbClass = factory.getDatabaseFrameDbClass();
	}

	@Override
	public void create(Collection errors) throws OntologyLoadException {
		if (!useExistingDb()) {
		    createFromNewEmptySources(errors);
		}
		else {
		    createFromExistingSources(errors);
		}
		FactoryUtils.adjustBrowserTextBasedOnPreferences(getOwlModel());
	}
	
	private void createFromNewEmptySources(Collection errors) throws OntologyLoadException {
        try {
            initializeTable(errors);
        } catch (IOException e) {
            throw new OntologyLoadException(e, "Could not initialize DB tables");
        }
        super.create(errors);
        if (ontologyName == null) {
            ontologyName = FactoryUtils.generateOntologyURIBase();
        }
        try {
            FactoryUtils.addOntologyToTripleStore(getOwlModel(), 
                                                  getOwlModel().getTripleStoreModel().getActiveTripleStore(), 
                                                  ontologyName);
            FactoryUtils.writeOntologyAndPrefixInfo(getOwlModel(), errors);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
	}
	
	private void createFromExistingSources(Collection errors) {
	    OWLKnowledgeBaseFactory suppliedFactory = getFactory();
	    OWLDatabaseKnowledgeBaseFactory factory;
	    if (suppliedFactory != null && suppliedFactory instanceof OWLDatabaseKnowledgeBaseFactory) {
	        factory = (OWLDatabaseKnowledgeBaseFactory) suppliedFactory;
	    }
	    else {
	        factory = new OWLDatabaseKnowledgeBaseFactory();
	    }
        project = Project.createBuildProject(factory, errors);
        initializeSources(project.getSources());
        project.createDomainKnowledgeBase(factory, errors, false);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        insertRepositoriesIntoOwlModel(owlModel);
        
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(owlModel);
        owlModel.setGenerateEventsEnabled(false);
        NarrowFrameStore nfs = factory.createNarrowFrameStore("<new>");
        mnfs.addActiveFrameStore(nfs);
        factory.loadKnowledgeBase(owlModel, project.getSources(), errors);
        
        owlModel.setGenerateEventsEnabled(true);
        owlModel.setChanged(false);
        project.getInternalProjectKnowledgeBase().setChanged(false);
	}

	private boolean useExistingDb() throws OntologyLoadException {
		if (wipe) { return false; }
		Connection connection = null;
		try {
			Class.forName(getDriver());
			connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
		}
		catch (ClassNotFoundException cnfe) {
			rethrow(cnfe);
		}
		catch (SQLException sqle) {
			rethrow(sqle);
		}
		try {
			return DatabaseFactoryUtils.getOntologyFromTable(databaseFrameDbClass, 
			                                                 getDriver(), getUrl(), getUsername(), getPassword(), getTable()) != null;
		}
		catch (SQLException sqle) {
			return true;
		}
	}

	private void rethrow(Throwable t) throws OntologyLoadException {
		if (t instanceof OntologyLoadException) {
			throw (OntologyLoadException) t;
		}
		throw new OntologyLoadException(t, t.getMessage());
	}

	/*
	 * setters and getters
	 */

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

    public void addRepository(Repository repository) {
        repositories.add(repository);
    }

    public void clearRepositories() {
        repositories.clear();
    }

    @Override
    public List<Repository> getRepositories() {
        return Collections.unmodifiableList(repositories);
    }

}
