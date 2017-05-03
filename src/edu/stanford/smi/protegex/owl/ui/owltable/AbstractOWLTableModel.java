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

package edu.stanford.smi.protegex.owl.ui.owltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * An AbstractTableModel that represents superclasses/equivalent classes with one row.
 * Its contents is automatically synchronized with the given Cls.
 * This is the common base class for three types of TableModels:
 * <UL>
 * <LI>EquivalentClassesTableModel</LI>
 * <LI>RestrictionsTableModel</LI>
 * <LI>SuperclassessTableModel</LI>
 * </UL>
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLTableModel extends AbstractTableModel
        implements OWLTableModel {

    /**
     * The currently edited class
     */
    private OWLNamedClass cls;

    /**
     * The index of the column that represents the expression
     */
    private int expressionColumn;

    /**
     * The List of RDFSClass instances in the table, sorted by rows
     */
    private List rows = new ArrayList();

    /**
     * The ClsListener that detects changes in the superclasses of the edited cls.
     */
    private ClassListener classListener = new ClassAdapter() {
        public void subclassAdded(RDFSClass cls, RDFSClass subclass) {
            refill();
        }


        public void subclassRemoved(RDFSClass cls, RDFSClass subclass) {
            refill();
        }


        public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
            //Cls superCls = event.getSuperclass();
            //removeOrInsertRow(superCls);
            refill();
        }


        public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
            //Cls superCls = event.getSuperclass();
            //removeOrInsertRow(superCls);
            refill();
        }
    };


    protected AbstractOWLTableModel(int expressionColumn) {
        this.expressionColumn = expressionColumn;
    }


    /**
     * Called to allow for editing.
     */
    public int addEmptyRow(int rowIndex) {
        int index = getRowCount();
        rows.add(null);
        fireTableRowsInserted(index, index);
        return index;
    }


    /**
     * This method completely fills the whole table and assumes that the rows list is empty.
     */
    protected void addRows() {
        Collection superClasses = cls.getSuperclasses(true);
        for (Iterator it = superClasses.iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (isSuitable(superCls)) {
                insertRow(superCls);
            }
        }
    }


    /**
     * Removes all rows from the table.
     */
    void clearRows() {
        int count = getRowCount();
        rows.clear();
        if(count > 0) {
            fireTableRowsDeleted(0, count - 1);
        }
    }


    /**
     * Deletes the superclass in a given row.  This method does not only change the table contents
     * but also modifies the underlying ontology.  The method completely removes the superclass
     * from the ontology if it was an anonymous class.
     *
     * @param index the index of the row to delete
     */
    public void deleteRow(int index) {
        RDFSClass superCls = (RDFSClass) rows.get(index);
        OWLModel kb = cls.getOWLModel();
        if (cls.getNamedSuperclasses().size() == 1 && superCls instanceof OWLNamedClass) {
            cls.addSuperclass(kb.getOWLThingClass());
        }
        cls.removeSuperclass(superCls);
        fireTableRowsDeleted(index, index);
    }


    public void displaySemanticError(String message) {
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(cls.getOWLModel(),
                message, "Semantic Error");
    }


    /**
     * Clears any dangling listeners.
     */
    public void dispose() {
        if (cls != null) {
            removeListeners();
        }
    }


    Cls getCls() {
        return cls;
    }


    public Class getColumnClass(int columnIndex) {
        return String.class;
    }


    public int getColumnCount() {
        return 1;
    }


    public String getColumnName(int column) {
        return "Expression";
    }


    public RDFSClass getClass(int rowIndex) {
        return (RDFSClass) rows.get(rowIndex);
    }


    public int getClassRow(RDFSClass superCls) {
        return rows.indexOf(superCls);
    }


    public OWLNamedClass getEditedCls() {
        return cls;
    }


    public RDFResource getRDFResource(int row) {
        return getClass(row);
    }


    public RDFResource getSubject() {
        return cls;
    }


    public int getSymbolColumnIndex() {
        return expressionColumn;
    }


    public Icon getIcon(RDFResource resource) {
        return ProtegeUI.getIcon(resource);
    }


    public boolean isAddEnabledAt(int rowIndex) {
        return isEditable();
    }


    public boolean isEditable() {
        return getEditedCls().isEditable();
    }


    protected abstract int getInsertRowIndex(Cls cls);


    public int getRowCount() {
        return rows.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rows.get(rowIndex) == null) {
            return "";
        }
        else {
            Cls superCls = getClass(rowIndex);
            return superCls.getBrowserText();
        }
    }


    protected void insertRow(Cls cls) {
        if (isSuitable(cls)) {
            int index = getInsertRowIndex(cls);
            rows.add(index, cls);
            fireTableRowsInserted(index, index);
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == expressionColumn;
    }


    /**
     * Checks whether a given superclass shall be displayed or not.
     *
     * @param cls the superclass to test
     * @return true if the cls shall be listed in this table
     */
    protected abstract boolean isSuitable(Cls cls);


    public void refill() {
        clearRows();
        addRows();
    }


    public void removeEmptyRow() {
        int rowIndex = rows.indexOf(null);
        if (rowIndex >= 0) {
            rows.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }


    private void removeListeners() {
        cls.removeClassListener(classListener);
    }


    public void setCls(OWLNamedClass newCls) {
        clearRows();
        if (cls != null) {
            removeListeners();
        }
        cls = newCls;
        if (cls != null) {
            cls.addClassListener(classListener);
            addRows();
        }
    }


    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return;
        }
        if (columnIndex == expressionColumn) {
            String text = (String) value;
            try {
                setValueAt(rowIndex, cls.getOWLModel(), text);
            }
            catch (Exception ex) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        }
    }


    protected abstract void setValueAt(int rowIndex, OWLModel owlModel, String parsableText)
            throws Exception;
}
