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

package edu.stanford.smi.protegex.owl.model.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

public abstract class AbstractOwlProjectCreator {
    private Class defaultClassView;
    private String profileURI;

    protected OWLKnowledgeBaseFactory factory;
    
    public AbstractOwlProjectCreator(OWLKnowledgeBaseFactory factory) {
        this.factory = factory;
    }
    
    @SuppressWarnings("unchecked")
    public abstract void create(Collection errors) throws OntologyLoadException;
   
    public abstract OWLModel getOwlModel();
    
    public abstract Project getProject();

    
    protected void addViewSettings(PropertyList sources) {
        String typeName = null;
        if (defaultClassView == null) {
            typeName = SwitchClassDefinitionResourceDisplayPlugin.getDefaultClassView();
        }
        else {
            typeName = defaultClassView.getName();
        }
        SwitchClassDefinitionResourceDisplayPlugin.setClassesView(sources, typeName);
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeName);
        if (profileURI != null) {
            ProfilesManager.setProfile(sources, profileURI);
        }
    }
    
    protected void insertRepositoriesIntoOwlModel(OWLModel owlModel) {
    	RepositoryManager manager = owlModel.getRepositoryManager();
        for (Repository  repository : getRepositories()) {
        	manager.addProjectRepository(repository);
        }
    }
    
    /*
     * ------------------------------------------------------
     * setters and getters
     */
    
    public void setDefaultClassView(Class defaultClassView) {
        this.defaultClassView = defaultClassView;
    }

    public void setProfileURI(String profileURI) {
        this.profileURI = profileURI;
    }
    
    public Collection<Repository> getRepositories() {
    	return Collections.emptyList();
    }
    
    public OWLKnowledgeBaseFactory getFactory() {
        return factory;
    }
    
}
