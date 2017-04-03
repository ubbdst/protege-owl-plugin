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

import java.util.Collection;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;

public class NewOwlProjectCreator extends AbstractOwlProjectCreator {
    private String ontologyName;
    
    private Project project;
    private JenaOWLModel owlModel;
    
    public NewOwlProjectCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public NewOwlProjectCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @Override
    public void create(Collection errors) throws OntologyLoadException {
        project = Project.createNewProject(factory, errors);
        owlModel = (JenaOWLModel) project.getKnowledgeBase();
        
        if (ontologyName == null) {
            ontologyName = FactoryUtils.generateOntologyURIBase();
        }
        try {
            FactoryUtils.addOntologyToTripleStore(owlModel, owlModel.getTripleStoreModel().getActiveTripleStore(), ontologyName);
        }
        catch (AlreadyImportedException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
        addViewSettings(project.getSources());
    }

    @Override
    public JenaOWLModel getOwlModel() {
        return owlModel;
    }
    
    @Override
    public Project getProject() {
        return project;
    }
    
    /*
     * ---------------------------------------------------------------------
     * setters and getters
     */
    
    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }
    
    

}
