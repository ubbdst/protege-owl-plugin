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

package edu.stanford.smi.protegex.owl.ui.matrix;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class MatrixPanel extends ResultsPanel {

    private ResourceSelectionAction addAnnotationPropertyAction = new ResourceSelectionAction("Add column for annotation property...",
            OWLIcons.getAddIcon(OWLIcons.OWL_DATATYPE_ANNOTATION_PROPERTY)) {

        public void resourceSelected(RDFResource resource) {
            addAnnotationProperty((RDFProperty) resource);
        }


        public RDFResource pickResource() {
            return ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(MatrixPanel.this, getOWLModel(),
                    getSelectableResources(), "Select annotation property");
        }


        public Collection getSelectableResources() {
            Collection results = new ArrayList(owlModel.getOWLAnnotationProperties());
            results.removeAll(tableModel.getVisibleAnnotationProperties());
            for (Iterator it = results.iterator(); it.hasNext();) {
                RDFProperty property = (RDFProperty) it.next();
                if (!(property instanceof OWLDatatypeProperty)) {
                    it.remove();
                }
            }
            return results;
        }
    };

    private MatrixFilter filter;

    private OWLModel owlModel;

    private MatrixTable table;

    private MatrixTableModel tableModel;


    public MatrixPanel(OWLModel owlModel, MatrixFilter filter, MatrixTableModel tableModel) {

        super(owlModel);

        this.filter = filter;
        this.owlModel = owlModel;
        this.tableModel = tableModel;

        table = createMatrixTable(tableModel);

        addRefreshButton();

        addAnnotationButtons();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(table.getBackground());
        setCenterComponent(scrollPane);
    }


    private void addRefreshButton() {
        Action refreshAction = new AbstractAction("Refresh", OWLIcons.getImageIcon(OWLIcons.REFRESH)) {
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        };
        addButton(refreshAction);
    }


    private void addAnnotationButtons() {
        JButton addButton = addButton(addAnnotationPropertyAction);
        addAnnotationPropertyAction.activateComboBox(addButton);
    }


    private void addAnnotationProperty(RDFProperty property) {
        AnnotationPropertyMatrixColumn col = new AnnotationPropertyMatrixColumn(property);
        table.addColumn(col);
    }


    protected MatrixTable createMatrixTable(MatrixTableModel tableModel) {
        return new MatrixTable(tableModel);
    }


    public void dispose() {
        super.dispose();
        tableModel.dispose();
    }


    public MatrixTable getTable() {
        return table;
    }


    public MatrixTableModel getTableModel() {
        return tableModel;
    }


    public String getTabName() {
        return filter.getName();
    }


    private void refresh() {
        tableModel.refill();
    }
}
