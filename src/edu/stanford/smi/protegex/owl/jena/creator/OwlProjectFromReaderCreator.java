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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.repository.Repository;



public class OwlProjectFromReaderCreator extends AbstractOwlProjectCreator {
    private Reader reader;
    private String xmlBase;
    
    private Project project;
    private JenaOWLModel owlModel;
    
    private List<Repository> repositories = new ArrayList<Repository>();


    public OwlProjectFromReaderCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public OwlProjectFromReaderCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(Collection errors) throws OntologyLoadException {
        project = Project.createNewProject(factory, errors);
        owlModel = (JenaOWLModel) project.getKnowledgeBase();
        insertRepositoriesIntoOwlModel(owlModel);
        
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        try {
            parser.run(reader, xmlBase);
        }
        catch (OntologyLoadException e) {
            errors.add(e);
        }
    }
    
    @Override
    public JenaOWLModel getOwlModel() {
        return owlModel;
    }
    
    @Override
    public Project getProject() {
        return project;
    }
    
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setXmlBase(String xmlBase) {
        this.xmlBase = xmlBase;
    }
    
    public void addRepository(Repository repository) {
    	repositories.add(repository);
    }
    
    public void clearRepositories() {
    	repositories.clear();
    }
    
    public List<Repository> getRepositories() {
    	return Collections.unmodifiableList(repositories);
    }

}
