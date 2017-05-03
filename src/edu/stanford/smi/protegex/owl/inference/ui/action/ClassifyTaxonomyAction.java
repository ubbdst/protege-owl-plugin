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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.ui.ReasonerActionRunner;
import edu.stanford.smi.protegex.owl.inference.ui.RunnableReasonerAction;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 18, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClassifyTaxonomyAction extends AbstractOWLModelAction implements RunnableReasonerAction {

    private OWLModel owlModel;
    private ProtegeReasoner reasoner = null;
    
    public void executeReasonerActions(ReasonerTaskListener taskListener) throws ProtegeReasonerException {
    	reasoner = ReasonerManager.getInstance().getProtegeReasoner(owlModel);
        reasoner.setReasonerTaskListener(taskListener);
        reasoner.classifyTaxonomy();
    }


    public String getIconFileName() {
        return OWLIcons.CLASSIFY;
    }


    public String getMenubarPath() {
        return REASONING_MENU + PATH_SEPARATOR + ActionConstants.ACTION_GROUP;
    }


    public String getToolbarPath() {
        return ActionConstants.ACTION_GROUP;
    }


    public String getName() {
        return "Classify taxonomy...";
    }


    public void run(OWLModel owlModel) {
        this.owlModel = owlModel;
        ReasonerActionRunner runner = new ReasonerActionRunner(this, true);
        runner.execute();
    }


    public OWLModel getOWLModel() {
        return owlModel;
    }
}

