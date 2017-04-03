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

package edu.stanford.smi.protegex.owl.inference.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner.GetSuperConceptsTask;
import edu.stanford.smi.protegex.owl.inference.reasoner.AbstractProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.ui.ReasonerActionRunner;
import edu.stanford.smi.protegex.owl.inference.ui.RunnableReasonerAction;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 8, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetInferredSuperClassesAction extends ResourceAction {

    public GetInferredSuperClassesAction() {
        super("Get inferred superclasses",
                OWLIcons.getImageIcon(OWLIcons.GET_INFERRED_SUPERCLASSES), ActionConstants.ACTION_GROUP);
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        if (resource instanceof RDFSClass) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        final OWLModel owlModel = getResource().getOWLModel();
        
        final ProtegeReasoner reasoner = ReasonerManager.getInstance().getProtegeReasoner(owlModel);
        
        ReasonerActionRunner runner = new ReasonerActionRunner(new RunnableReasonerAction() {
            public void executeReasonerActions(ReasonerTaskListener taskListener) throws ProtegeReasonerException {
            	reasoner.setReasonerTaskListener(taskListener);
                //reasoner.getSuperclasses((OWLClass) getResource());
                
            	//ugly handling of different dig vs. direct reasoner for backwards compatibility reasons
                if (reasoner instanceof AbstractProtegeReasoner && !(reasoner instanceof ProtegeOWLReasoner)) {
                	AbstractProtegeReasoner protegeReasoner = (AbstractProtegeReasoner) reasoner;
                	GetSuperConceptsTask task = new GetSuperConceptsTask((OWLClass) getResource(), reasoner);
            		protegeReasoner.performTask(task);
            		
            		return;
        		} else {
        			reasoner.getSuperclasses((OWLClass) getResource());
                }
                
            }

            public OWLModel getOWLModel() {
                return owlModel;
            }
        }, false);

        runner.execute();
    }
}

