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

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;

import java.util.Collection;

/**
 * A OWLTable for editing the superclasses of a given class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassDescriptionTable extends OWLTable {

    ClassDescriptionTable(OWLModel owlModel, OWLTableModel tableModel) {
        super(tableModel, owlModel, true);
        setColumnWidths();
    }


    void addCls(RDFSClass aClass) {
        OWLTableModel compactOWLTableModel = getOWLTableModel();
        if (compactOWLTableModel.getClassRow(aClass) >= 0) {
            compactOWLTableModel.displaySemanticError("The class " + aClass.getBrowserText() +
                    " is already in the list.");
        }
        else {
            if (aClass.equals(compactOWLTableModel.getEditedCls())) {
                compactOWLTableModel.displaySemanticError("Cannot assign " +
                        aClass.getBrowserText() + " to itself.");
            }
            else {
                OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(this);
                compactOWLTableModel.addRow(aClass, getSelectedRow());
                int row = compactOWLTableModel.getClassRow(aClass);
                if (row >= 0) {
                    getSelectionModel().setSelectionInterval(row, row);
                }
                if (tab != null) {
                    tab.ensureClassSelected(getEditedCls(), -1);
                }
            }
        }
    }


    void createAndEditRow() {
        int row = getOWLTableModel().addEmptyRow(-1);
        editExpression(row);
    }


    protected SymbolEditorComponent createSymbolEditorComponent(OWLModel model,
                                                                SymbolErrorDisplay errorDisplay) {
        return new ClassDescriptionEditorComponent(model, errorDisplay, false);
    }


    public void hideSymbolPanel() {
        super.hideSymbolPanel();
        getOWLTableModel().removeEmptyRow();
    }

    /*
   public Component prepareEditor(TableCellEditor editor, int row, int column) {
       if (column == getOWLTableModel().getSymbolColumnIndex()) {
           String expression = (String) getOWLTableModel().getValueAt(row, column);
           showSymbolPanel(expression.length() == 0);
       }
       return super.prepareEditor(editor, row, column);
   } */


    private void setColumnWidths() {
        getColumnModel().getColumn(getOWLTableModel().getSymbolColumnIndex()).setPreferredWidth(350);
    }


    void toggleSuperClsIntoDefintion() {
        int sel = getSelectedRow();
        if (sel >= 0) {
            OWLNamedClass cls = (OWLNamedClass) getOWLTableModel().getEditedCls();
            RDFSClass superClass = (RDFSClass) getOWLTableModel().getClass(sel);
            toggleSuperclassIntoEquivalentClass(cls, superClass);
            getSelectionModel().setSelectionInterval(sel, sel);
        }
    }


    void toggleDefinitionIntoSuperCls() {
        int sel = getSelectedRow();
        if (sel >= 0) {
            OWLNamedClass cls = (OWLNamedClass) getOWLTableModel().getEditedCls();
            RDFSClass superClass = (RDFSClass) getOWLTableModel().getClass(sel);
            toggleEquivalentClassIntoSuperclass(cls, superClass);
            getSelectionModel().setSelectionInterval(sel, sel);
        }
    }


    private void toggleEquivalentClassIntoSuperclass(OWLNamedClass cls, RDFSClass superClass) {
        OWLUtil.convertEquivalentClsIntoSuperClses(cls, superClass);
    }


    private void toggleSuperclassIntoEquivalentClass(OWLNamedClass cls, RDFSClass superClass) {
        Collection namedDirectSuperclasses = cls.getNamedSuperclasses();
        // Collection directSuperclasses = cls.getDirectSuperclasses();
        namedDirectSuperclasses.remove(superClass);
        if (namedDirectSuperclasses.size() == 0) {
            // Add :THING if superClass is the final superclass
            cls.addSuperclass(getOWLModel().getOWLThingClass());
        }
        OWLUtil.convertSuperClsIntoEquivalentCls(cls, superClass);
    }
}
