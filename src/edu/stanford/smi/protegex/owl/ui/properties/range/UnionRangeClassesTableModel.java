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

package edu.stanford.smi.protegex.owl.ui.properties.range;

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
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.PropertyWidget;

/**
 * An AbstractTableModel that represents each of the classes in the union range
 * of a property with one row.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class UnionRangeClassesTableModel extends AbstractTableModel
        implements OWLTableModel {

    private PropertyWidget propertyWidget;

    /**
     * The List of RDFSClass instances in the table, sorted by rows
     */
    private List rows = new ArrayList();


    UnionRangeClassesTableModel(PropertyWidget propertyWidget) {
        this.propertyWidget = propertyWidget;
    }


    public boolean addRow(RDFSClass aClass, int rowIndex) {
        RDFProperty property = getEditedProperty();
        OWLModel owlModel = property.getOWLModel();
        ArrayList newClses = new ArrayList();
        if (!(property instanceof OWLProperty) && !ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Union_Classes)) {
            newClses.add(aClass);
        }
        else {
            Collection clses = property.getUnionRangeClasses();
            for (Iterator it = clses.iterator(); it.hasNext();) {
                RDFSClass oldClass = (RDFSClass) it.next();
                newClses.add(oldClass.createClone());
            }
            if (property.getSuperpropertyCount() > 0 && !property.isRangeDefined()) {
                removeSuperclasses(newClses, aClass);
            }
            if (!newClses.contains(aClass)) {
                newClses.add(aClass);
            }
        }
        try {
            aClass.getOWLModel().beginTransaction("Add " + aClass.getBrowserText() +
                    " to range of " + property.getBrowserText(), property.getName());
            property.setUnionRangeClasses(newClses);
            aClass.getOWLModel().commitTransaction();
        }
        catch (Exception ex) {
        	aClass.getOWLModel().rollbackTransaction();
            OWLUI.handleError(aClass.getOWLModel(), ex);
        }
        return true;
    }


    public int addEmptyRow(int rowIndex) {
        int index = getRowCount();
        rows.add(null);
        fireTableRowsInserted(index, index);
        return index;
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
        RDFProperty property = getEditedProperty();
        Cls cls = getClass(index);
        Collection clses = property.getUnionRangeClasses();
        ArrayList newClses = new ArrayList();
        for (Iterator it = clses.iterator(); it.hasNext();) {
            RDFSClass oldClass = (RDFSClass) it.next();
            if (!cls.equals(oldClass)) {
                newClses.add(oldClass.createClone());
            }
        }
        try {
            property.getOWLModel().beginTransaction("Remove " + cls.getBrowserText() +
                    " from range of " + property.getBrowserText(), property.getName());
            if (newClses.isEmpty() && property.getSuperpropertyCount() > 0) {
                property.setRange(null);
                property.getOWLModel().commitTransaction();
                fireTableDataChanged();
            }
            else {
                property.setUnionRangeClasses(newClses);
                property.getOWLModel().commitTransaction();
                fireTableRowsDeleted(index, index);
            }
        }
        catch (Exception ex) {
        	property.getOWLModel().rollbackTransaction();
            OWLUI.handleError(property.getOWLModel(), ex);
        }
    }


    public void displaySemanticError(String message) {
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(propertyWidget.getOWLModel(), message);
    }


    public void dispose() {
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
        return null;  // Ignore
    }


    RDFProperty getEditedProperty() {
        return (RDFProperty) propertyWidget.getEditedResource();
    }


    public Icon getIcon(RDFResource resource) {
        return ProtegeUI.getIcon(resource);
    }


    public RDFProperty getPredicate(int row) {
        return getEditedProperty().getOWLModel().getRDFSRangeProperty();
    }


    public RDFResource getRDFResource(int row) {
        return getClass(row);
    }


    public int getRowCount() {
        return rows.size();
    }


    public RDFResource getSubject() {
        return getEditedProperty();
    }


    public int getSymbolColumnIndex() {
        return 0;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rows.get(rowIndex) == null) {
            return "";
        }
        else {
            RDFSClass cls = getClass(rowIndex);
            return cls.getBrowserText();
        }
    }


    Collection getValues() {
        RDFProperty property = getEditedProperty();
        return property.getUnionRangeClasses();
    }


    private void insertRow(RDFSClass cls) {
        int index = rows.size();
        rows.add(index, cls);
        fireTableRowsInserted(index, index);
    }


    public boolean isAddEnabledAt(int rowIndex) {
        return isEditable();
    }


    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


    public boolean isDeleteEnabledFor(RDFSClass cls) {
        return !isInherited(cls);
    }


    public boolean isEditable() {
        return getEditedProperty().isEditable();
    }


    boolean isInherited(RDFSClass cls) {
        final RDFProperty property = getEditedProperty();
        final Collection values = property.getUnionRangeClasses();
        return property.getRange() == null || !values.contains(cls);
    }


    void refill() {
        clearRows();
        Collection values = getValues();
        for (Iterator it = values.iterator(); it.hasNext();) {
            RDFSClass cls = (RDFSClass) it.next();
            insertRow(cls);
        }
    }


    public void removeEmptyRow() {
        int rowIndex = rows.indexOf(null);
        if (rowIndex >= 0) {
            rows.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }


    private void removeSuperclasses(Collection clses, RDFSClass aClass) {
        for (Iterator it = clses.iterator(); it.hasNext();) {
            RDFSClass rdfsClass = (RDFSClass) it.next();
            if (aClass.isSubclassOf(rdfsClass)) {
                it.remove();
            }
        }
    }


    public void setCls(OWLNamedClass cls) {
        // Ignore
    }


    @Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return;
        }
        String text = (String) value;
        if (text.trim().length() == 0) {
            removeEmptyRow();
            return;
        }
        try {
            OWLModel owlModel = propertyWidget.getOWLModel();
            OWLClassParser parser = owlModel.getOWLClassParser();
            RDFSClass newClass = parser.parseClass(owlModel, text);
            RDFSClass oldClass = getClass(rowIndex);
            RDFProperty property = getEditedProperty();
            if (oldClass == null || !value.equals(oldClass)) {
                if (newClass instanceof OWLUnionClass) {
                    ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                            "Please do not assign unions as range.  The range in Protege has union\n" +
                                    "semantics, i.e. if you assign A or B, then this is equivalent to adding\n" +
                                    "A and B separately in two rows.  Please convert your union into single rows.");
                    newClass.delete();
                }
                else {
                    Collection clses = property.getUnionRangeClasses();
                    if (clses.contains(newClass)) {
                        displaySemanticError("The class " + newClass.getBrowserText() +
                                " is already in the union range of " + property.getBrowserText() + ".");
                    }
                    else {
                        ArrayList newClses = new ArrayList();
                        for (Iterator it = clses.iterator(); it.hasNext();) {
                            RDFSClass old = (RDFSClass) it.next();
                            if (old.equals(oldClass)) {
                                newClses.add(newClass);
                            }
                            else {
                                newClses.add(old.createClone());
                            }
                        }
                        if (oldClass == null) {
                            newClses.add(newClass);
                        }
                        property.setUnionRangeClasses(newClses);
                    }
                }
            }
        }
        catch (Exception ex) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
    }
}
