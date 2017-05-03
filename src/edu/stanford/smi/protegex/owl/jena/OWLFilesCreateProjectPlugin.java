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

package edu.stanford.smi.protegex.owl.jena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.AmalgamatedLoadException;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.AbstractCreateProjectPlugin;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.jena.creator.NewOwlProjectCreator;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.jena.OWLFilesWizardPage;
import edu.stanford.smi.protegex.owl.ui.metadatatab.OntologyURIWizardPage;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfileSelectionWizardPage;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFilesCreateProjectPlugin extends AbstractCreateProjectPlugin implements OWLFilesPlugin {
	
    private static transient Logger log = Log.getLogger(OWLFilesCreateProjectPlugin.class);

    private Class defaultClassView;

    private String ontologyName;

    private String fileURI;

    private String lang;

    private String profileURI;


    public OWLFilesCreateProjectPlugin() {
        super("OWL Files");
        JenaKnowledgeBaseFactory.useStandalone = false;
    }

    @SuppressWarnings("unchecked")
    protected Project buildNewProject(KnowledgeBaseFactory factory) {   
        Collection errors = new ArrayList();
        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator((JenaKnowledgeBaseFactory) factory);
        creator.setOntologyUri(fileURI);
        creator.setLang(lang);
        creator.setDefaultClassView(defaultClassView);
        creator.setProfileURI(profileURI);
        try {
            creator.create(errors);
        }
        catch (OntologyLoadException ioe) {
            errors.add(ioe);
        }
        finally {
            handleErrors(errors);
        }
        return creator.getProject();
    }

    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return factory instanceof JenaKnowledgeBaseFactory;
    }


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard, boolean useExistingSources) {
        ProfileSelectionWizardPage.isBuild = useExistingSources;
        if (useExistingSources) {
            return new OWLFilesWizardPage(wizard, this);
        }
        else {
            return new OntologyURIWizardPage(wizard, this);
        }
    }


    @SuppressWarnings("unchecked")
    protected Project createNewProject(KnowledgeBaseFactory factory) {
        Collection errors = new ArrayList();
        NewOwlProjectCreator creator = new NewOwlProjectCreator((JenaKnowledgeBaseFactory) factory);
        creator.setOntologyName(ontologyName);
        creator.setDefaultClassView(defaultClassView);
        creator.setProfileURI(profileURI);
        try {
            creator.create(errors);
        }
        catch (AmalgamatedLoadException ale) {
            errors.addAll(ale.getErrorList());
        }
        catch (OntologyLoadException ole) {
            errors.add(ole);
        }
        finally {
            handleErrors(errors);
        }
        return creator.getProject();
    }
 

    /*
     * ---------------------------------------------------------------------------
     * setters and getters
     */


    public void setFile(String fileURI) {
        this.fileURI = fileURI;
    }


    public void setLanguage(String lang) {
        this.lang = lang;
    }


    public void setDefaultClassView(Class typeClass) {
        this.defaultClassView = typeClass;
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeClass.getName());
    }


    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }


    public void setProfile(String profileURI) {
        this.profileURI = profileURI;
    }
    
    public void addImport(String uri, String prefix) {
    }
}
