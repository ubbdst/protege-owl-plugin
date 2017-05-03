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

package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableAction;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A SlotWidget used for describing logical class characteristics, i.e.
 * superclasses and equivalent classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractConditionsWidget extends AbstractPropertyWidget
        implements ConditionsTableConstants, PropertyConditionsDisplay {


    protected ConditionsTable table;

    protected ConditionsTableModel tableModel;

    /*private OWLTableAction navigateToClsAction =
            new AbstractOWLTableAction("Navigate to class in hierarchy", Icons.getBlankIcon()) {
                public void actionPerformed(ActionEvent e) {
                    Cls aClassassass = table.getSelectedCls();
                    if (aClassassass instanceof RDFSNamedClass) {
                        navigateToCls((RDFSNamedClass) aClassassass);
                    }
                }


                public boolean isEnabledFor(Cls superCls, int rowIndex) {
                    return superCls instanceof RDFSNamedClass;
                }
            };*/

    private OWLTableAction viewAction =
            new AbstractOWLTableAction("View/edit class", OWLIcons.getViewIcon()) {
                public void actionPerformed(ActionEvent e) {
                    Cls cls = table.getSelectedCls();
                    if (cls != null) {
                        showInstance(cls);
                    }
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    return cls instanceof RDFSNamedClass;
                }
            };


    public void displayRowsWithProperty(OWLProperty property) {
        table.displayRowsWithProperty(property);
    }


    protected OWLClassesTab getClsesTab() {
        return OWLClassesTab.getOWLClassesTab(this);
    }


    protected OWLNamedClass getEditedCls() {
        return (OWLNamedClass) getEditedResource();
    }


    public ConditionsTable getTable() {
        return table;
    }


    protected void initialize(String label, Slot superclassesSlot) {
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        tableModel = new ConditionsTableModel(superclassesSlot);
        table = new ConditionsTable(owlModel, tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        LabeledComponent labeledComponent = new OWLLabeledComponent(label, scrollPane, true, true);
        //labeledComponent.setHeaderIcon(headerIcon);
        initializeButtons(labeledComponent);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, labeledComponent);
    }


    protected void initializeButtons(LabeledComponent labeledComponent) {
        WidgetUtilities.addViewButton(labeledComponent, viewAction);
        // table.registerAction(viewAction);
        // table.registerAction(navigateToClsAction, 3);
    }


    private void navigateToCls(RDFSNamedClass rdfsClass) {
        OWLClassesTab tab = getClsesTab();
        if (tab != null) {
            tab.setSelectedCls(rdfsClass);
        }
    }


    public void refresh() {
        tableModel.refresh();
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (table != null) {
            if (newInstance instanceof OWLNamedClass) {
                table.setCls((OWLNamedClass) newInstance);
                tableModel.setCls((OWLNamedClass) newInstance);
            }
            else {
                tableModel.setCls(null);
            }
        }
    }
}
