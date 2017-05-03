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
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * An AbstractTableModel that represents each disjoint class with one row.
 * Its contents is automatically synchronized with the given Cls.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DisjointClassesTableModel extends AbstractTableModel
        implements ClassDescriptionTableModel {

    /**
     * The currently edited class
     */
    private OWLNamedClass cls;

    /**
     * The List of RDFSClass instances in the table, sorted by rows
     */
    private List rows = new ArrayList();

    /**
     * The FrameListener that detects changes in the disjoint classes own slot of the Cls
     */
    private PropertyValueListener valueListener = new PropertyValueAdapter() {
        @Override
		public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            if (property.getName().equals(OWLNames.Slot.DISJOINT_WITH)) {
                clearRows();
                addRows();
            }
        }
    };


    public int addEmptyRow(int rowIndex) {
        int index = getRowCount();
        rows.add(null);
        fireTableRowsInserted(index, index);
        return index;
    }


    public boolean addRow(RDFSClass rdfsClass, int rowIndex) {
        addDisjointClass(rdfsClass);
        return true;
    }


    private void addDisjointClass(RDFSClass rdfsClass) {
        OWLModel owlModel = rdfsClass.getOWLModel();
        if (rdfsClass.equals(owlModel.getOWLThingClass())) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "You cannot make a class disjoint with owl:Thing.");
            return;
        }
        try {
            owlModel.beginTransaction("Add " + rdfsClass.getBrowserText() +
                    " to disjoint classes of " + getEditedCls().getBrowserText(), getEditedCls().getName());
            getEditedCls().addDisjointClass(rdfsClass);
            if (rdfsClass instanceof OWLNamedClass && rdfsClass.isEditable()) {
                OWLNamedClass namedCls = (OWLNamedClass) rdfsClass;
                if (!namedCls.getDisjointClasses().contains(getEditedCls())) {
                    namedCls.addDisjointClass(getEditedCls()); // Add inverse direction
                }
            }
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    /**
     * Adds the rows to represent all disjoint classes on the given class.
     * This method completely fills the whole table and assumes that the rows list is empty.
     */
    private void addRows() {
        Collection disjointClasses = cls.getDisjointClasses();
        for (Iterator it = disjointClasses.iterator(); it.hasNext();) {
            RDFSClass disjointClass = (RDFSClass) it.next();
            insertRow(disjointClass);
        }
    }


    /**
     * Removes all rows from the table.
     */
    void clearRows() {
        int count = getRowCount();
        rows.clear();
        if (count > 0) {
            fireTableRowsDeleted(0, count - 1);
        }
    }


    public void deleteRow(int index) {
        RDFSClass disjointClass = getClass(index);
        OWLModel owlModel = disjointClass.getOWLModel();
        try {
            owlModel.beginTransaction("Delete disjoint class " + disjointClass.getBrowserText() +
                    " from " + getEditedCls().getBrowserText(), getEditedCls().getName());
            if (disjointClass instanceof OWLNamedClass && disjointClass.isEditable()) {
                OWLNamedClass namedCls = (OWLNamedClass) disjointClass;
                if (namedCls.getDisjointClasses().contains(getEditedCls())) {
                    namedCls.removeDisjointClass(getEditedCls());
                }
            }
            cls.removeDisjointClass(disjointClass);            
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
        
        //events might have already dealt with this
        //following code might be unnecessary
        int row = getClassRow(disjointClass);

        if (row != -1) {
        	try {
        		fireTableRowsDeleted(row, row);
        	} catch (Exception e) {
        		Log.getLogger().log(Level.WARNING, "Error at removing disjoint class from disjoint table: " + disjointClass + " Row: " + row, e);
        	}
        }
    }


    public void displaySemanticError(String message) {
        OWLModel owlModel = cls == null ? null : cls.getOWLModel();
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                "Semantic Error");
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


    public int getColumnCount() {
        return 1;
    }


    @Override
	public String getColumnName(int column) {
        return "Expression";
    }


    @Override
	public Class getColumnClass(int columnIndex) {
        return String.class;
    }


    public RDFSClass getClass(int rowIndex) {
        return (RDFSClass) rows.get(rowIndex);
    }


    public int getClassRow(RDFSClass cls) {
        return rows.indexOf(cls);
    }


    public OWLNamedClass getEditedCls() {
        return cls;
    }


    public RDFProperty getPredicate(int row) {
        return cls.getOWLModel().getOWLDisjointWithProperty();
    }


    public RDFResource getRDFResource(int row) {
        return getClass(row);
    }


    public RDFResource getSubject() {
        return cls;
    }


    public int getSymbolColumnIndex() {
        return 0;
    }


    public Icon getIcon(RDFResource resource) {
        return ProtegeUI.getIcon(resource);
    }


    int getRow(Cls superCls) {
        return rows.indexOf(superCls);
    }


    public int getRowCount() {
        return rows.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rows.get(rowIndex) == null) {
            return "";
        }
        else {
            Cls cls = getClass(rowIndex);
            return cls.getBrowserText();
        }
    }


    private void insertRow(RDFSClass rdfsClass) {
        int index = rows.size();
        rows.add(index, rdfsClass);
        fireTableRowsInserted(index, index);
    }


    public boolean isAddEnabledAt(int rowIndex) {
        return isEditable();
    }


    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        RDFSClass cls = getClass(rowIndex);
        return cls == null || isDeleteEnabledFor(cls);
    }


    public boolean isDeleteEnabledFor(RDFSClass cls) {
        if (cls != null) {
            OWLModel owlModel = cls.getOWLModel();
            RDFProperty disjointWithProperty = owlModel.getOWLDisjointWithProperty();
            return owlModel.getTripleStoreModel().isActiveTriple(getEditedCls(), disjointWithProperty, cls);
        }
        else {
            return false;
        }
    }


    public boolean isEditable() {
        return getEditedCls().isEditable();
    }


    public boolean isRemoveEnabledFor(Cls otherClass) {
        for (Iterator it = getEditedCls().getSuperclasses(false).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof OWLNamedClass && ((OWLNamedClass) superCls).getSubclassesDisjoint()) {
                if (superCls.getDirectSubclasses().contains(otherClass)) {
                    return false;
                }
            }
        }
        final OWLModel owlModel = getEditedCls().getOWLModel();
        return otherClass instanceof OWLNamedClass &&
                owlModel.getTripleStoreModel().isActiveTriple(getEditedCls(), owlModel.getOWLDisjointWithProperty(), otherClass);
    }


    public void removeEmptyRow() {
        int rowIndex = rows.indexOf(null);
        if (rowIndex >= 0) {
            rows.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }


    private void removeListeners() {
        cls.removePropertyValueListener(valueListener);
    }


    public void setCls(OWLNamedClass newCls) {
        clearRows();
        if (cls != null) {
            removeListeners();
        }
        cls = newCls;
        if (cls != null) {
            cls.addPropertyValueListener(valueListener);
            addRows();
        }
    }


    @Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return;
        }
        String text = (String) value;
        OWLModel owlModel = cls.getOWLModel();
        try {
            OWLClassParser parser = owlModel.getOWLClassParser();
            RDFSClass newClass = parser.parseClass(owlModel, text);
            RDFSClass oldClass = getClass(rowIndex);
            if (oldClass == null || !value.equals(oldClass)) {
                if (cls.getDisjointClasses().contains(newClass)) {
                    displaySemanticError("The class " + newClass.getBrowserText() +
                            " is already a disjoint class of " + cls.getBrowserText() + ".");
                }
                else {
                    setValueAt(newClass, oldClass);
                }
            }
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
    }


    private void setValueAt(RDFSClass newClass, RDFSClass oldClass) {
        OWLModel owlModel = newClass.getOWLModel();
        try {
            owlModel.beginTransaction("Change disjoint class to " + newClass.getBrowserText(), getEditedCls().getName());
            addDisjointClass(newClass);
            if (oldClass != null) {
                cls.removeDisjointClass(oldClass);
            }
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }
}
