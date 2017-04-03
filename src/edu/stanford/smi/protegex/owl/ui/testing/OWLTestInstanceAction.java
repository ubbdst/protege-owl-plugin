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

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.testing.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestInstanceAction extends AbstractOWLTestAction {

    private Selectable selectable;


    public OWLTestInstanceAction(OWLModel owlModel,
                                 OWLTestManager testManager,
                                 Selectable selectable) {
        super(owlModel, testManager, "Run ontology tests on this resource",
                OWLIcons.getImageIcon("TestInstance"));
        this.selectable = selectable;
        selectable.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                updateStatus();
            }
        });
    }


    protected OWLTestResultsPanel getTestResultsPanel(OWLModel owlModel, List results) {
        Instance instance = (Instance) selectable.getSelection().iterator().next();
        return new OWLTestResultsPanel(owlModel, results, instance, true);
    }


    protected List run(OWLTest[] tests, Task task) {
        List results = new ArrayList();
        for (Iterator it = selectable.getSelection().iterator(); it.hasNext();) {
            Object o = (Object) it.next();
            if (o instanceof RDFResource) {
                RDFResource instance = (RDFResource) o;
                for (int i = 0; i < tests.length; i++) {
                    OWLTest test = tests[i];
                    if (test instanceof RDFSClassTest && instance instanceof RDFSClass) {
                        results.addAll(((RDFSClassTest) test).test((RDFSClass) instance));
                    }
                    if (test instanceof RDFPropertyTest && instance instanceof OWLProperty) {
                        results.addAll(((RDFPropertyTest) test).test((OWLProperty) instance));
                    }
                    if (test instanceof RDFResourceTest) {
                        results.addAll(((RDFResourceTest) test).test((RDFResource) instance));
                    }
                }
            }
        }
        return results;
    }


    private void updateStatus() {
        Collection selection = selectable.getSelection();
        setEnabled(selection.size() > 0);
    }
}
