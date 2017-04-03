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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.AbstractCreateProjectPlugin;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.database.creator.OwlDatabaseCreator;

/**
 * @author Ray Fergerson  <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLDatabaseProjectPlugin extends
        AbstractCreateProjectPlugin implements OWLDatabasePlugin {
    private static transient Logger log = Log.getLogger(CreateOWLDatabaseProjectPlugin.class);

    private String driver;

    private String table;

    private String username;

    private String password;

    private String url;
    
    private String ontologyName;


    public CreateOWLDatabaseProjectPlugin() {
        this("OWL Database");
    }


    public CreateOWLDatabaseProjectPlugin(String name) {
        super(name);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Project createNewProject(KnowledgeBaseFactory factory) {
        return makeProject(factory, true);
    }
    
    @Override
    protected Project buildNewProject(KnowledgeBaseFactory factory) {
        return makeProject(factory, false);
    }
    
    private Project makeProject(KnowledgeBaseFactory factory, boolean wipe) {
        OwlDatabaseCreator creator = new OwlDatabaseCreator((OWLDatabaseKnowledgeBaseFactory) factory, wipe);
        creator.setDriver(driver);
        creator.setURL(url);
        creator.setTable(table);
        creator.setUsername(username);
        creator.setPassword(password);
        creator.setOntologyName(ontologyName);
        Collection errors = new ArrayList();
        try {
            creator.create(errors);
        }
        catch (OntologyLoadException ioe) {
            errors.add(ioe);
        }
        handleErrors(errors);
        return creator.getProject();
    }


    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return factory.getClass() == OWLDatabaseKnowledgeBaseFactory.class;
    }
    
    


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard,
                                                    boolean useExistingSources) {
        return useExistingSources ?
                new OWLDatabaseWizardPageExistingSources(wizard, this) :
                new OWLDatabaseWizardPage(wizard, this);
    }


    /*
     * getters and setters
     */
    
    public void setDriver(String driver) {
        this.driver = driver;
    }




    public void setTable(String table) {
        this.table = table;
    }


    public void setURL(String url) {
        this.url = url;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setOntologyName(String name) {
        this.ontologyName = name;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getDriver() {
        return driver;
    }


    public String getTable() {
        return table;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

    public String getOntologyName() {
        return ontologyName;
    }
}