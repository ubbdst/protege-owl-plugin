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

package edu.stanford.smi.protegex.owl.jena.creator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.repository.Repository;

public class OwlProjectFromUriCreator extends AbstractOwlProjectCreator {

	private static String MERGE_MODE_PROP = "protege.owl.parser.convert.file.merge.mode";

	private boolean isMergeImportMode = ApplicationProperties.getBooleanProperty(MERGE_MODE_PROP, false);

	private String ontologyUri;

    private String lang = FileUtils.langXMLAbbrev;

    private Project project;

    private List<Repository> repositories = new ArrayList<Repository>();

    public  OwlProjectFromUriCreator() {
        this(new JenaKnowledgeBaseFactory());
    }

    public OwlProjectFromUriCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }


    @Override
	@SuppressWarnings("unchecked")
    public void create(Collection errors) throws OntologyLoadException {

    	boolean initialMergeMode = JenaKnowledgeBaseFactory.isMergingImportMode();

    	try {
    		JenaKnowledgeBaseFactory.setMergingImportMode(isMergeImportMode);

    		project = Project.createBuildProject(factory, errors);

    		initializeSources(project.getSources());
    		URI uri = getBuildProjectURI();
    		if (uri != null) {
    			project.setProjectURI(uri);
    		}
    		project.createDomainKnowledgeBase(factory, errors, true);
    		FactoryUtils.adjustBrowserTextBasedOnPreferences(getOwlModel());
    	} finally {
    		JenaKnowledgeBaseFactory.setMergingImportMode(initialMergeMode);
    	}
    }


    @Override
    public JenaOWLModel getOwlModel() {
        if (project != null) {
            return (JenaOWLModel) project.getKnowledgeBase();
        }
        else {
            return null;
        }
    }

    @Override
    public Project getProject() {
        return project;
    }


    protected void initializeSources(PropertyList sources) {
        JenaKnowledgeBaseFactory.setOWLFileName(sources, ontologyUri);
        JenaKnowledgeBaseFactory.setOWLFileLanguage(sources, lang);
        for (Repository repository : repositories) {
            JenaKnowledgeBaseFactory.addRepository(repository);
        }
        addViewSettings(sources);
    }

    protected URI getBuildProjectURI() {
        if (ontologyUri != null) {
            if (ontologyUri.startsWith("file:")) {
                int index = ontologyUri.lastIndexOf('.');
                if (index > 0) {
                    String uri = FileUtilities.replaceExtension(ontologyUri, ".pprj");
                    try {
                        return new URI(uri);
                    }
                    catch (Exception ex) {
                      Log.emptyCatchBlock(ex);
                    }
                }
            }
        }
        return null;
    }

    /*
     * ------------------------------------------------------
     * setters and getters
     */

    public void setOntologyUri(String fileURI) {
        this.ontologyUri = fileURI;
    }


    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
	public JenaKnowledgeBaseFactory getFactory() {
        return (JenaKnowledgeBaseFactory) super.getFactory();
    }

    public void addRepository(Repository repository) {
        repositories.add(repository);
    }

    public void clearRepositories() {
        repositories.clear();
    }

    @Override
	public Collection<Repository> getRepositories() {
        return Collections.unmodifiableCollection(repositories);
    }


    public boolean isMergeImportMode() {
		return isMergeImportMode;
	}

	public void setMergeImportMode(boolean isMergeImportMode) {
		this.isMergeImportMode = isMergeImportMode;
	}

}
