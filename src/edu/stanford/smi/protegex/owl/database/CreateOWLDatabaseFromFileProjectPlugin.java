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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.database.creator.OwlDatabaseFromFileCreator;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLDatabaseFromFileProjectPlugin extends CreateOWLDatabaseProjectPlugin {

	private static String MERGE_MODE_PROP = "protege.owl.parser.convert.db.merge.mode";

    protected URI ontologyInputSource;
    private boolean isMergeImportMode = ApplicationProperties.getBooleanProperty(MERGE_MODE_PROP, false);

	public CreateOWLDatabaseFromFileProjectPlugin() {
        super("OWL File (.owl or .rdf)");
    }

    @Override
	@SuppressWarnings("unchecked")
    protected Project buildNewProject(KnowledgeBaseFactory factory) {
        JenaKnowledgeBaseFactory.useStandalone = false;
        Collection errors = new ArrayList();
        OwlDatabaseFromFileCreator creator = new OwlDatabaseFromFileCreator((OWLDatabaseKnowledgeBaseFactory) factory);
        creator.setDriver(getDriver());
        creator.setURL(getUrl());
        creator.setTable(getTable());
        creator.setUsername(getUsername());
        creator.setPassword(getPassword());
        creator.setOntologySource(getOntologyInputSource().toString());
        creator.setMergeImportMode(isMergeImportMode);

        try {
            creator.create(errors);
        }
        catch (OntologyLoadException ioe) {
            errors.add(ioe);
        }
        handleErrors(errors);
        return creator.getProject();
    }


    @Override
	public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return super.canCreateProject(factory, useExistingSources) && useExistingSources;
    }


    @Override
	public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard, boolean useExistingSources) {
        return new InitOWLDatabaseFromFileWizardPage(wizard, this);
    }



    public void setOntologyInputSource(URI uri) {
        this.ontologyInputSource = uri;
    }


    /**
     * @deprecated Use {@link #setOntologyInputSource(URI)}
     */
    @Deprecated
    public void setOntologyFileURI(String uri) {
    	URI uritemp = URIUtilities.createURI(uri);
    	if (uritemp == null) {
    		throw new RuntimeException("Invalid uri " + uri);
    	}
    	setOntologyInputSource(uritemp);
    }

    public URI getOntologyInputSource() {
        return ontologyInputSource;
    }

    public boolean isMergeImportMode() {
		return isMergeImportMode;
	}

	public void setMergeImportMode(boolean isMergeImportMode) {
		this.isMergeImportMode = isMergeImportMode;
	}

}
