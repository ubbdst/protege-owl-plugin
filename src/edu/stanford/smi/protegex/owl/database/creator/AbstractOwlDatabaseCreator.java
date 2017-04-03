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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;

/**
 * This is the common part  of all the owl database project creators.  It is abstract because
 * it does not ensure that the ontology is not null.
 *
 * @author tredmond
 *
 */

public abstract class AbstractOwlDatabaseCreator extends AbstractOwlProjectCreator {

    private String driver;

    private String table;

    private String username;

    private String password;

    private String url;

    protected Project project;

    protected AbstractOwlDatabaseCreator(OWLDatabaseKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void create(Collection errors) throws OntologyLoadException {
        project = Project.createBuildProject(factory, errors);
        initializeSources(project.getSources());
        project.createDomainKnowledgeBase(factory, errors, true);
        insertRepositoriesIntoOwlModel((OWLModel) project.getKnowledgeBase());
    }

    @Override
    public OWLDatabaseModel getOwlModel() {
        if (project != null) {
            return (OWLDatabaseModel) project.getKnowledgeBase();
        }
        return null;
    }

    @Override
    public Project getProject() {
        return project;
    }

    protected void initializeSources(PropertyList sources) {
        DatabaseKnowledgeBaseFactory.setSources(sources, driver, url, table, username, password);
    }

    @SuppressWarnings("unchecked")
    protected void initializeTable(Collection errors) throws IOException {
        JenaKnowledgeBaseFactory.useStandalone = false;

        Project project = Project.createNewProject(factory, errors);
        initializeSources(project.getSources());
        File tempProjectFile = File.createTempFile("protege", "temp");
        project.setProjectFilePath(tempProjectFile.getPath());
        project.save(errors);
    }

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

    public String getUrl() {
        return url;
    }


}
