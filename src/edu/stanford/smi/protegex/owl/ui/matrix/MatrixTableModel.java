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

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class MatrixTableModel extends AbstractTableModel implements Disposable {

    public final static int COL_NAME = 0;

    public final static int COL_PREFIX = 1;

    private List columns = new ArrayList();

    private MatrixFilter filter;

    private List instances = new ArrayList();

    private ModelListener modelListener = new ModelAdapter() {
        public void classCreated(RDFSClass cls) {
            handleInstanceCreated(cls);
        }


        public void classDeleted(RDFSClass cls) {
            handleInstanceDeleted(cls);
        }


        public void individualCreated(RDFResource resource) {
            handleInstanceCreated(resource);
        }


        public void individualDeleted(RDFResource resource) {
            handleInstanceDeleted(resource);
        }


        public void propertyCreated(RDFProperty property) {
            handleInstanceCreated(property);
        }


        public void propertyDeleted(RDFProperty property) {
            handleInstanceDeleted(property);
        }
    };


    private OWLModel owlModel;

    private int sortColumn = COL_NAME;

    private MatrixTable table;


    public MatrixTableModel(OWLModel owlModel, MatrixFilter filter) {
        this.owlModel = owlModel;
        this.filter = filter;
        addDefaultColumns();
        owlModel.addModelListener(modelListener);
        addInstances();
    }


    /**
     * Adds a new MatrixColumn programmatically.  This should not be called by
     * user code directly: Instead, use the corresponding method in MatrixTable.
     *
     * @param column the MatrixColumn to add
     * @see MatrixTable#addColumn
     */
    public void addColumn(MatrixColumn column) {
        columns.add(column);
    }


    void addColumn(MatrixColumn column, int index) {
        columns.add(index, column);
    }


    protected void addDefaultColumns() {
        addColumn(new NameMatrixColumn());
        addColumn(new PrefixMatrixColumn());
    }


    private void addInstances() {
        instances.addAll(filter.getInitialValues());
        sortInstances();
    }


    public void dispose() {
        owlModel.removeModelListener(modelListener);
    }


    public int getColumnCount() {
        return columns.size();
    }


    public Class getColumnClass(int columnIndex) {
        return RDFResource.class;
    }


    public String getColumnName(int column) {
        MatrixColumn matrixColumn = getMatrixColumn(column);
        String str = matrixColumn.getName();
        if (str != null && column == sortColumn) {
            str = "[" + str + "]";
        }
        return str;
    }


    private int getIndexFor(RDFResource instance) {
        SortableMatrixColumn matrixColumn = getSortableMatrixColumn();
        Comparator c = matrixColumn.getSortComparator();
        int index = Collections.binarySearch(instances, instance, c);
        if (index >= 0) {
            return index;
        }
        else {
            return -index - 1;
        }
        // return matrixColumn.getIndexFor(this, instance);
    }


    public RDFResource getInstance(int row) {
        return (RDFResource) instances.get(row);
    }


    public MatrixColumn getMatrixColumn(int column) {
        return (MatrixColumn) columns.get(column);
    }


    public int getNewColumnIndex(MatrixColumn col) {
        return columns.size();
    }


    public int getRowCount() {
        return instances.size();
    }


    private SortableMatrixColumn getSortableMatrixColumn() {
        return (SortableMatrixColumn) getMatrixColumn(sortColumn);
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        return getInstance(rowIndex);
    }


    public Collection getVisibleAnnotationProperties() {
        Collection results = new ArrayList();
        for (Iterator it = columns.iterator(); it.hasNext();) {
            Object col = it.next();
            if (col instanceof AnnotationPropertyMatrixColumn) {
                results.add(((AnnotationPropertyMatrixColumn) col).getAnnotationProperty());
            }
        }
        return results;
    }


    private void handleInstanceCreated(Instance instance) {
        if (instance instanceof RDFResource) {
            RDFResource RDFResource = (RDFResource) instance;
            if (filter.isSuitable(RDFResource)) {
                int index = getIndexFor(RDFResource);
                instances.add(index, RDFResource);
                fireTableRowsInserted(index, index);
            }
        }
    }


    private void handleInstanceDeleted(RDFResource instance) {
        if (isDependentOn(instance)) {
            table.close();
        }
        else {

            removeDependentColumns(instance);

            int index = instances.indexOf(instance);
            if (index >= 0) {
                instances.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        MatrixColumn col = getMatrixColumn(columnIndex);
        if (col instanceof EditableMatrixColumn) {
            RDFResource instance = getInstance(rowIndex);
            if (instance.isEditable()) {
                return ((EditableMatrixColumn) col).isCellEditable(instance);
            }
        }
        return false;
    }


    protected boolean isDependentOn(RDFResource instance) {
        if (filter instanceof DependentMatrixFilter) {
            return ((DependentMatrixFilter) filter).isDependentOn(instance);
        }
        else {
            return false;
        }
    }


    public boolean isSortableColumn(int column) {
        return getMatrixColumn(column) instanceof SortableMatrixColumn;
    }


    public void refill() {
        instances.clear();
        addInstances();
        fireTableDataChanged();
    }


    private void removeDependentColumns(RDFResource RDFResource) {
        for (Iterator it = new ArrayList(columns).iterator(); it.hasNext();) {
            MatrixColumn column = (MatrixColumn) it.next();
            if (column instanceof DependentMatrixColumn) {
                if (((DependentMatrixColumn) column).isDependentOn(RDFResource)) {
                    removeColumn(column);
                }
            }
        }
    }


    private void removeColumn(MatrixColumn column) {
        int index = columns.indexOf(column);
        columns.remove(index);
        // table.removeColumn(table.getColumnModel().getColumn(index));
        fireTableStructureChanged();
        table.initColumns();
    }


    public void setSortColumn(int column) {
        if (column != this.sortColumn) {
            this.sortColumn = column;
            sortInstances();
            fireTableStructureChanged();
        }
    }


    void setTable(MatrixTable table) {
        this.table = table;
    }


    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        MatrixColumn col = getMatrixColumn(columnIndex);
        if (col instanceof EditableMatrixColumn) {
            RDFResource instance = getInstance(rowIndex);
            ((EditableMatrixColumn) col).setValueAt(instance, value);
        }
    }


    private void sortInstances() {
        SortableMatrixColumn c = getSortableMatrixColumn();
        Collections.sort(instances, c.getSortComparator());
    }
}
