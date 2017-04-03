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

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixColumn;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixFilter;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixPanel;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixTableModel;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassMatrixPanel extends MatrixPanel {

    private ResourceSelectionAction addExistentialAction = new ResourceSelectionAction("Add someValuesFrom column...",
            OWLIcons.getAddIcon(OWLIcons.OWL_SOME_VALUES_FROM)) {

        public void resourceSelected(RDFResource resource) {
            RDFProperty property = (RDFProperty) resource;
            ExistentialMatrixColumn col = new ExistentialMatrixColumn(property);
            getTable().addColumn(col);
        }


        public RDFResource pickResource() {
            return ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(ClassMatrixPanel.this, getOWLModel(),
                    getSelectableResources(), "Select a property");
        }


        public Collection getSelectableResources() {
            RDFSNamedClass aClass = (RDFSNamedClass) getTable().getSelectedInstance();
            Collection results = getPotentialProperties(aClass);
            int count = getTableModel().getColumnCount();
            for (int i = 0; i < count; i++) {
                MatrixColumn col = getTableModel().getMatrixColumn(i);
                if (col instanceof ExistentialMatrixColumn) {
                    results.remove(((ExistentialMatrixColumn) col).getProperty());
                }
            }
            return results;
        }
    };


    public ClassMatrixPanel(OWLModel owlModel, MatrixFilter filter) {
        this(owlModel, filter, new ClassMatrixTableModel(owlModel, filter));
    }


    public ClassMatrixPanel(OWLModel owlModel, MatrixFilter filter, MatrixTableModel tableModel) {
        super(owlModel, filter, tableModel);
        getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions();
            }
        });
        addButton(addExistentialAction);
        updateActions();
    }


    static Collection getPotentialProperties(RDFSNamedClass aClass) {
        Collection results = new ArrayList();
        for (Iterator it = aClass.getUnionDomainProperties(true).iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property instanceof OWLObjectProperty && !((OWLObjectProperty) property).isAnnotationProperty()) {
                results.add(property);
            }
        }
        return results;
    }


    private void updateActions() {
        RDFSNamedClass aClass = (RDFSNamedClass) getTable().getSelectedInstance();
        boolean enabled = aClass instanceof OWLNamedClass;
        addExistentialAction.setEnabled(enabled);
    }
}
