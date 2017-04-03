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

package edu.stanford.smi.protegex.owl.ui.clsproperties;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFPropertiesComponent extends JPanel {

    private Action addPropertyAction = new ResourceSelectionAction("Add property...", OWLIcons.getAddIcon(OWLIcons.RDF_PROPERTY)) {

        public Collection getSelectableResources() {
            Collection allowedProperties = new ArrayList(cls.getOWLModel().getUserDefinedRDFProperties());
            allowedProperties.removeAll(cls.getUnionDomainProperties());
            return allowedProperties;
        }


        public RDFResource pickResource() {
            return ProtegeUI.getSelectionDialogFactory().selectProperty(RDFPropertiesComponent.this, cls.getOWLModel(),
                    getSelectableResources(), "Select a property to add");
        }


        public void resourceSelected(RDFResource resource) {
            table.handleAddProperty((RDFProperty) resource);
        }
    };

    private RDFSNamedClass cls;

    private Action removePropertiesAction = new AbstractAction("Remove selected properties", OWLIcons.getRemoveIcon(OWLIcons.RDF_PROPERTY)) {
        public void actionPerformed(ActionEvent e) {
            table.handleRemoveProperties();
        }
    };

    private RDFPropertiesTable table;


    public RDFPropertiesComponent(OWLModel owlModel) {
        table = new RDFPropertiesTable();
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.white);
        OWLLabeledComponent lc = new OWLLabeledComponent("Properties", sp);
        CreatePropertyAction.addActions(lc, owlModel, new CreatePropertyAction.CallBack() {
            public void propertyCreated(RDFProperty property) {
                table.handlePropertyCreated(property);
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions();
            }
        });
        lc.addHeaderButton(addPropertyAction);
        lc.addHeaderButton(removePropertiesAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        updateActions();
    }


    public void setClass(RDFSNamedClass cls) {
        this.cls = cls;
        table.setClass(cls);
    }


    private void updateActions() {
        boolean enabled = false;
        final int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
            enabled = true;
            for (int i = 0; i < rows.length; i++) {
                int row = rows[i];
                if (!table.getTableModel().isDirectProperty(row)) {
                    enabled = false;
                    break;
                }
            }
        }
        removePropertiesAction.setEnabled(enabled);
    }
}
