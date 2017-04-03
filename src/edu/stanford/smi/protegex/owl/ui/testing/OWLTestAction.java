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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.Task;
import edu.stanford.smi.protegex.owl.model.TaskManager;
import edu.stanford.smi.protegex.owl.testing.OWLModelTest;
import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.RDFPropertyTest;
import edu.stanford.smi.protegex.owl.testing.RDFResourceTest;
import edu.stanford.smi.protegex.owl.testing.RDFSClassTest;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestAction extends AbstractOWLModelTestAction {

    public String getIconFileName() {
        return OWLIcons.TEST;
    }


    public String getName() {
        return "Run ontology tests...";
    }


    protected List run(OWLTest[] tests, Task task) {
        TaskManager taskManager = owlModel.getTaskManager();
        List results = new ArrayList();
        double count = tests.length * 4;
        int j = 0;
        for (int i = 0; i < tests.length && !task.isCancelled(); i++) {
            try {
                OWLTest test = tests[i];
                String className = test.getClass().getName();
                int index = className.lastIndexOf('.');
                taskManager.setMessage(task, className.substring(index + 1));
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof OWLModelTest) {
                    runOWLModelTest(results, (OWLModelTest) test);
                }
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof RDFSClassTest) {
                    runOWLClsTest(results, (RDFSClassTest) test);
                }
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof RDFPropertyTest) {
                    runOWLPropertyTest(results, (RDFPropertyTest) test);
                }
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof RDFResourceTest) {
                    runOWLInstanceTest(results, (RDFResourceTest) test);
                }
            }
            catch (Exception ex) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        }
        return results;
    }
}
