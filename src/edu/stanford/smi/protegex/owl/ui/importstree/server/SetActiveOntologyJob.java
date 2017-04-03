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

package edu.stanford.smi.protegex.owl.ui.importstree.server;

import java.lang.reflect.Proxy;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;

public class SetActiveOntologyJob extends ProtegeJob {
    private static final long serialVersionUID = 7139921119966372858L;
    
    private OWLOntology ontology;
    
    public SetActiveOntologyJob(OWLModel owlModel, OWLOntology ontology) {
        super(owlModel);
        this.ontology = ontology;
    }

    @Override
    public Boolean run() throws ProtegeException {
        OWLModel owlModel = (OWLModel) getKnowledgeBase();

        if (!((DefaultOWLOntology) ontology).isAssociatedTriplestoreEditable()) {
            return false;
        }
        
        FrameStoreManager fsm = owlModel.getFrameStoreManager();
        ActiveOntologyFrameStoreHandler activeOntologyHandler = fsm.getFrameStoreFromClass(ActiveOntologyFrameStoreHandler.class);
        if (activeOntologyHandler == null) {
            activeOntologyHandler = new ActiveOntologyFrameStoreHandler(owlModel);
            FrameStore toInsert = (FrameStore) Proxy.newProxyInstance(OWLModel.class.getClassLoader(), 
                                                                      new Class [] { FrameStore.class },
                                                                      activeOntologyHandler);
            fsm.insertFrameStore(toInsert, 1);
        }
        activeOntologyHandler.setActiveOntology(ontology);
        return true;
    }
    
    @Override
    public Boolean execute() throws ProtegeException {
        return (Boolean) super.execute();
    }
    
    @Override
    public void localize(KnowledgeBase kb) {
        super.localize(kb);
        if (ontology != null) {
            LocalizeUtils.localize(ontology, kb);
        }
    }

}
