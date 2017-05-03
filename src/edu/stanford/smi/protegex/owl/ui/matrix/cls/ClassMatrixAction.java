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

package edu.stanford.smi.protegex.owl.ui.matrix.cls;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixFilter;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassMatrixAction extends AbstractAction {

    private OWLModel owlModel;


    public ClassMatrixAction(OWLModel owlModel) {
        super("Show matrix of all classes", OWLIcons.getImageIcon(OWLIcons.CLASS_MATRIX));
        this.owlModel = owlModel;
    }


    public void actionPerformed(ActionEvent e) {
        if (!OWLUI.isConfirmationNeeded(owlModel) ||
                OWLUI.isConfirmed(owlModel, ((KnowledgeBase) owlModel).getClsCount() > OWLUI.getConfirmationThreshold(owlModel))) {
            performAction();
        }
    }


    private void performAction() {
        MatrixFilter filter = new MatrixFilter() {

            public Collection getInitialValues() {
                Collection clses = owlModel.getUserDefinedOWLNamedClasses();
                for (Iterator it = clses.iterator(); it.hasNext();) {
                    RDFSNamedClass aClass = (RDFSNamedClass) it.next();
                    if (!isSuitable(aClass)) {
                        it.remove();
                    }
                }
                return clses;
            }


            public String getName() {
                return "All Classes";
            }


            public boolean isSuitable(RDFResource instance) {
                return instance instanceof RDFSNamedClass &&
                        instance.isVisible() &&
                        (instance.isEditable() || instance.isIncluded());
            }
        };
        ClassMatrixPanel panel = new ClassMatrixPanel(owlModel, filter);
        ResultsPanelManager.addResultsPanel(owlModel, panel, true);
    }
}
