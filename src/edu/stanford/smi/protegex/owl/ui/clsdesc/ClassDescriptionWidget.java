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

package edu.stanford.smi.protegex.owl.ui.clsdesc;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.DeleteRowAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

/**
 * An AbstractSlotWidget that displays the superclasses / equivalent classes in a table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class ClassDescriptionWidget extends AbstractPropertyWidget {

    private ClassDescriptionTable table;

    private OWLTableAction viewAction = new AbstractOWLTableAction("View/edit class", Icons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            Cls cls = table.getSelectedCls();
            if (cls != null) {
                showInstance(cls);
            }
        }


        public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
            return cls instanceof OWLNamedClass;
        }
    };


    protected abstract ResourceSelectionAction createAddAction(final ClassDescriptionTable table);


    protected abstract Action createCreateAction(final ClassDescriptionTable table);


    protected abstract Icon createHeaderIcon();


    protected abstract java.util.List createCustomActions(final ClassDescriptionTable table);


    protected abstract OWLTableModel createTableModel();


    public void dispose() {
        ((OWLTableModel) table.getModel()).dispose();
        super.dispose();
    }


    public OWLNamedClass getEditedCls() {
        return table.getEditedCls(); // (OWLNamedClass) getInstance();
    }


    protected OWLLabeledComponent getLabeledComponent() {
        return (OWLLabeledComponent) getComponent(0);
    }


    protected abstract String getLabelText();


    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }


    public ClassDescriptionTable getTable() {
        return table;
    }

    public void initialize() {
        OWLTableModel tableModel = createTableModel();
        OWLModel owlModel = (OWLModel) getProject().getKnowledgeBase();
        table = new ClassDescriptionTable(owlModel, tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        LabeledComponent labeledComponent = new OWLLabeledComponent(getLabelText(), scrollPane, true, true);
        Icon headerIcon = createHeaderIcon();
        if (headerIcon != null) {
            labeledComponent.setHeaderIcon(headerIcon);
        }
        WidgetUtilities.addViewButton(labeledComponent, viewAction);
        labeledComponent.addHeaderButton(createCreateAction(table));
        ResourceSelectionAction addAction = createAddAction(table);
        labeledComponent.addHeaderButton(addAction);
        table.registerAction(viewAction);
        for (Iterator it = createCustomActions(table).iterator(); it.hasNext();) {
            Action action = (Action) it.next();
            labeledComponent.addHeaderButton(action);
            if (action instanceof OWLTableAction) {
                table.registerAction((OWLTableAction) action);
            }
        }
        OWLTableAction deleteAction = new DeleteRowAction(table);
        //OWLTableAction removeAction = new RemoveRowAction(table);
        //labeledComponent.addHeaderButton(removeAction);
        labeledComponent.addHeaderButton(deleteAction);
        //table.registerAction(removeAction);
        table.registerAction(deleteAction);
        table.registerActionSeparator();
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, labeledComponent);
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance instanceof OWLNamedClass) {
            table.setCls((OWLNamedClass) newInstance);
        }
        else {
            table.setCls(null);
        }
    }
}
