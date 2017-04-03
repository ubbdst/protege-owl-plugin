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

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixFilter;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixTableModel;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.search.SearchNamedClassAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExistentialMatrixAction extends ResourceAction {

    public ExistentialMatrixAction() {
        super("Show existential matrix of subclasses...",
                Icons.getBlankIcon(),
                SearchNamedClassAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        OWLModel owlModel = getOWLModel();
        RDFSNamedClass parentClass = (RDFSNamedClass) getResource();
        final Collection slots = selectProperties(parentClass);
        if (!slots.isEmpty()) {
            MatrixFilter filter = new SubclassesMatrixFilter(parentClass);
            MatrixTableModel tableModel = new MatrixTableModel(owlModel, filter) {
                protected void addDefaultColumns() {
                    super.addDefaultColumns();
                    for (Iterator it = slots.iterator(); it.hasNext();) {
                        RDFProperty property = (RDFProperty) it.next();
                        addColumn(new ExistentialMatrixColumn(property));
                    }
                }
            };
            ClassMatrixPanel panel = new ClassMatrixPanel(owlModel, filter, tableModel);
            ResultsPanelManager.addResultsPanel(owlModel, panel, true);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource instanceof RDFSNamedClass;
    }


    private Collection selectProperties(RDFSNamedClass parentClass) {
        Collection properties = ClassMatrixPanel.getPotentialProperties(parentClass);
        OWLModel owlModel = parentClass.getOWLModel();
        return ProtegeUI.getSelectionDialogFactory().selectResourcesFromCollection(getComponent(), owlModel,
                properties, "Select properties to show");
    }
}
