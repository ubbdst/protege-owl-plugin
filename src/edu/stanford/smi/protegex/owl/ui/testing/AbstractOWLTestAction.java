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

package edu.stanford.smi.protegex.owl.ui.testing;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.AbstractTask;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.Task;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.testing.OWLModelTest;
import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.OWLTestManager;
import edu.stanford.smi.protegex.owl.testing.RDFPropertyTest;
import edu.stanford.smi.protegex.owl.testing.RDFResourceTest;
import edu.stanford.smi.protegex.owl.testing.RDFSClassTest;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLTestAction extends AbstractAction {

    protected OWLModel owlModel;

    protected OWLTestManager testManager;


    public AbstractOWLTestAction() {
    }


    public AbstractOWLTestAction(OWLModel owlModel,
                                 OWLTestManager testManager,
                                 String text,
                                 Icon icon) {
        super(text, icon);
        this.owlModel = owlModel;
        this.testManager = testManager;
    }


    public void actionPerformed(ActionEvent e) {
        OWLTest[] tests = getOWLTests();
        if (tests.length == 0) {
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                    "There are no tests activated.");
        }
        else {
            if (!OWLUI.isConfirmationNeeded(owlModel) ||
                    OWLUI.isConfirmed(owlModel, owlModel.getRDFResourceCount() > OWLUI.getConfirmationThreshold(owlModel))) {
                performAction(tests);
            }
        }
    }


    private void performAction(final OWLTest[] tests) {
        Task task = new AbstractTask("Running Tests", true, owlModel.getTaskManager()) {
            public void runTask() throws Exception {
                List results = run(tests, this);
                if (results.isEmpty()) {
                    showAllTestsPassedMessage(tests.length);
                }
                showResults(results, owlModel);
            }
        };
	    try {
		    owlModel.getTaskManager().run(task);
	    }
	    catch(Exception e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
	    }
    }


    protected OWLTest[] getOWLTests() {
        return testManager.getOWLTests();
    }


    protected OWLTestResultsPanel getTestResultsPanel(OWLModel owlModel, List results) {
        return new OWLTestResultsPanel(owlModel, results, null, true);
    }


    protected abstract List run(OWLTest[] tests, Task task);


    protected void runOWLClsTest(List results, RDFSClassTest test) {
        final Collection owlSystemFrames = ((AbstractOWLModel) owlModel).getOWLSystemResources();
        for (Iterator it = owlModel.getRDFSClasses().iterator(); it.hasNext();) {
            RDFSClass rdfsClass = (RDFSClass) it.next();
            if (!owlSystemFrames.contains(rdfsClass)) {
                results.addAll(test.test(rdfsClass));
            }
        }
    }


    protected void runOWLInstanceTest(List results, RDFResourceTest test) {
        final Collection owlSystemFrames = ((AbstractOWLModel) owlModel).getOWLSystemResources();
        for (Iterator it = owlModel.getRDFSClasses().iterator(); it.hasNext();) {
            RDFSClass rdfsClass = (RDFSClass) it.next();
            if (!owlSystemFrames.contains(rdfsClass)) {
                results.addAll(test.test(rdfsClass));
            }
        }
        for (Iterator it = owlModel.getUserDefinedOWLProperties().iterator(); it.hasNext();) {
            OWLProperty owlProperty = (OWLProperty) it.next();
            results.addAll(test.test(owlProperty));
        }
        for (Iterator it = owlModel.getOWLIndividuals().iterator(); it.hasNext();) {
            RDFResource RDFResource = (RDFResource) it.next();
            results.addAll(test.test(RDFResource));
        }
    }


    protected void runOWLModelTest(List results, OWLModelTest test) {
        results.addAll(test.test(owlModel));
    }


    protected void runOWLPropertyTest(List results, RDFPropertyTest test) {
        for (Iterator it = owlModel.getUserDefinedOWLProperties().iterator(); it.hasNext();) {
            OWLProperty owlProperty = (OWLProperty) it.next();
            results.addAll(test.test(owlProperty));
        }
    }


    protected void showAllTestsPassedMessage(int count) {
        // JOptionPane.showMessageDialog(null, "All " + count + " tests passed successfully.");
    }


    public void showResults(List results, OWLModel owlModel) {
        OWLTestResultsPanel panel = getTestResultsPanel(owlModel, results);
        ResultsPanelManager.addResultsPanel(owlModel, panel, true);
    }
}
