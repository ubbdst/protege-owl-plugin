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

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.DeleteRowAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableAction;
import edu.stanford.smi.protegex.owl.ui.restrictions.RestrictionEditorPanel;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;

/**
 * An AbstractConditionWidget to display and edit the asserted conditions
 * of a class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AssertedConditionsWidget extends AbstractConditionsWidget {

    private AddNamedClassAction addNamedClassAction;

    private Action createExpressionAction =
            new AbstractAction("Create new expression",
                    OWLIcons.getCreateIcon(OWLIcons.ANONYMOUS_OWL_CLASS)) {

                public void actionPerformed(ActionEvent e) {
                    table.selectNecessaryIfNothingSelected();
                    table.createAndEditRow();
                }
            };


    private Action createRestrictionAction =
            new AbstractAction("Create restriction...",
                    OWLIcons.getCreateIcon(OWLIcons.OWL_RESTRICTION)) {
                public void actionPerformed(ActionEvent e) {
                    createRestriction();
                }
            };


    private DeleteRowAction deleteAction;


    private OWLTableAction deriveRestrictionAction =
            new AbstractOWLTableAction("Derive similar restriction...",
                    OWLIcons.getAddIcon(OWLIcons.OWL_RESTRICTION)) {

                public void actionPerformed(ActionEvent e) {
                    deriveRestriction();
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    return tableModel.isAddEnabledAt(rowIndex) &&
                            table.getSelectedCls() instanceof OWLRestriction;
                }
            };


    private OWLTableAction negateAnonymousClsAction =
            new AbstractOWLTableAction("Negate expression",
                    OWLIcons.getAddIcon(OWLIcons.OWL_COMPLEMENT_CLASS)) {

                public void actionPerformed(ActionEvent e) {
                    negateAnonymousClass();
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    return tableModel.isAddEnabledAt(rowIndex) &&
                            table.getSelectedCls() instanceof OWLAnonymousClass;
                }
            };


    private void createRestriction() {
    	OWLRestriction newRestriction = null;
        OWLClassesTab owlClassesTab = table.getOWLClsesTab();
        table.selectNecessaryIfNothingSelected();
        
        Cls metaCls = getKnowledgeBase().getCls(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
        try {
            beginTransaction("Create restriction at " + getEditedCls().getBrowserText(), getEditedCls().getName());
            newRestriction = RestrictionEditorPanel.showCreateDialog(table, getEditedCls(), metaCls);
            if (newRestriction != null) 
                table.addRestriction(newRestriction);                
            
            commitTransaction();
        }
        catch (Exception ex) {
        	rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }
        
        try {
            if (newRestriction !=null)
            	table.ensureEditedClassSelectedInExplorer(owlClassesTab);			
		} catch (Exception e) {
			Log.getLogger().warning("Cannot select in class tree: " + tableModel.getEditedCls());
		}
    }


    private void deriveRestriction() {
    	OWLRestriction newRestriction = null;
        OWLRestriction restriction = (OWLRestriction) table.getSelectedCls();
        Cls metaCls = restriction.getProtegeType();
        OWLProperty property = (OWLProperty) restriction.getOnProperty();
        String fillerText = restriction.getFillerText();
        try {
            beginTransaction("Derive restriction from " + restriction.getBrowserText() +
                    " at " + getEditedCls().getBrowserText(), getEditedCls().getName());
            newRestriction = RestrictionEditorPanel.showCreateDialog(table, getEditedCls(), metaCls, property, fillerText);
            if (newRestriction != null)               
                table.addRestriction(newRestriction);               
            
            commitTransaction();
        }
        catch (Exception ex) {
        	rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }
        
        try {
        	if (newRestriction != null) {
        		OWLClassesTab owlClassesTab = table.getOWLClsesTab();
        		table.ensureEditedClassSelectedInExplorer(owlClassesTab);
        	}
		} catch (Exception e) {
			Log.getLogger().warning("Cannot select in class tree: " + tableModel.getEditedCls());
		}
    }


    public void initialize() {
        Slot superclassesSlot = getKnowledgeBase().getSlot(Model.Slot.DIRECT_SUPERCLASSES);
        initialize("Asserted Conditions", superclassesSlot);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateCreateActions();
            }
        });
    }


    protected void initializeButtons(LabeledComponent labeledComponent) {

        super.initializeButtons(labeledComponent);

        addNamedClassAction = new AddNamedClassAction(table);
        deleteAction = new DeleteRowAction(table) {
            public void actionPerformed(ActionEvent e) {
                OWLClassesTab tab = table.getOWLClsesTab();
                super.actionPerformed(e);
                table.ensureEditedClassSelectedInExplorer(tab);
            }
        };

        labeledComponent.addHeaderSeparator();
        labeledComponent.addHeaderButton(createExpressionAction);
        labeledComponent.addHeaderButton(createRestrictionAction);
        labeledComponent.addHeaderButton(addNamedClassAction);
        labeledComponent.addHeaderButton(deleteAction);

        table.registerAction(deleteAction);
        table.registerActionSeparator();
        table.registerAction(deriveRestrictionAction);
        table.registerAction(negateAnonymousClsAction);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(Model.Slot.DIRECT_SUPERCLASSES);
    }


    private void negateAnonymousClass() {
        OWLClassesTab owlClassesTab = table.getOWLClsesTab();
        OWLClassDisplay display = table.getOWLModel().getOWLClassDisplay();
        int rowIndex = table.getSelectedRow();
        Cls sel = table.getSelectedCls();
        String expression = sel.getBrowserText();
        if (sel instanceof OWLComplementClass) {
            int len = display.getOWLComplementOfSymbol().length();
            expression = expression.substring(len);  // Throw away '!'
        }
        else {
            expression = display.getOWLComplementOfSymbol() + "(" + expression + ")";
        }
        table.setValueAt(expression, rowIndex, COL_EXPRESSION);
        table.ensureEditedClassSelectedInExplorer(owlClassesTab);
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (table != null) {
            updateCreateActions();
        }
    }


    private void updateCreateActions() {
        if (table.getSelectedRow() >= tableModel.getRowCount()) {
            table.setSelectedRow(-1);
        }
        boolean thing = getKnowledgeBase().getRootCls().equals(getEditedResource());
        if (table.getSelectedRowCount() == 1) {
            int row = table.getSelectedRow();
            boolean enabled = !thing && tableModel.isCreateEnabledAt(row);
            createExpressionAction.setEnabled(enabled);
            createRestrictionAction.setEnabled(enabled);
            addNamedClassAction.setEnabled(tableModel.isAddEnabledAt(row));
        }
        else {
            createExpressionAction.setEnabled(!thing);
            createRestrictionAction.setEnabled(!thing);
            addNamedClassAction.setEnabled(!thing);
        }
    }
}
