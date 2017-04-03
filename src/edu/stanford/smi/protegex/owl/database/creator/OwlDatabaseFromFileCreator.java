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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;

public class OwlDatabaseFromFileCreator extends AbstractOwlDatabaseCreator {
    private static transient Logger log = Log.getLogger(OwlDatabaseFromFileCreator.class);

    private List<Repository> repositories = new ArrayList<Repository>();
    private boolean isMergeImportMode = false;
	private String ontologySource;

    public OwlDatabaseFromFileCreator() {
        this(new OWLDatabaseKnowledgeBaseFactory());
    }

    public OwlDatabaseFromFileCreator(OWLDatabaseKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(Collection errors) throws OntologyLoadException {
        try {
			initializeTable(errors);
		} catch (IOException e1) {
			throw new OntologyLoadException(e1, "Could not initialize DB tables");
		}

        super.create(errors);
        insertRepositoriesIntoOwlModel(getOwlModel());
        loadProjectRepositories(getOwlModel());

        ProtegeOWLParser parser = new ProtegeOWLParser(getOwlModel());
        boolean initialMergeMode = parser.isMergingImportMode();
        parser.setMergingImportMode(isMergeImportMode);
        try {
        	parser.run(URIUtilities.createURI(ontologySource));
        } finally {
        	parser.setMergingImportMode(initialMergeMode);
        }

        try {
            FactoryUtils.writeOntologyAndPrefixInfo(getOwlModel(), errors);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
        FactoryUtils.adjustBrowserTextBasedOnPreferences(getOwlModel());

        errors.addAll(getOwlModel().getParserErrors());
    }


    protected void loadProjectRepositories(OWLDatabaseModel owlModel) {
    	URI prjUri = owlModel.getProject().getProjectURI(); //hack for relative repositories
		try {
			String pprjString = FileUtilities.replaceExtension(ontologySource, ".pprj");
			owlModel.getProject().setProjectURI(URIUtilities.createURI(pprjString));
			String repString = FileUtilities.replaceExtension(ontologySource, ".repository");
			RepositoryUtil.loadProjectRepositoriesFromURI(owlModel, URIUtilities.createURI(repString), false);
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at loading project repositories", e);
		} finally {
			owlModel.getProject().setProjectURI(prjUri); //end hack
		}

	}

	/*
     * setters and getters
     */

    public void setOntologySource(String uri) {
        ontologySource = uri;
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

    public boolean isMergeImportMode() {
		return isMergeImportMode;
	}

	public void setMergeImportMode(boolean isMergeImportMode) {
		this.isMergeImportMode = isMergeImportMode;
	}

}
