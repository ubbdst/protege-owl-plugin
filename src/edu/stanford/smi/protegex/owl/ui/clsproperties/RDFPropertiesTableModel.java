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

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.ui.ResourceComparator;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFPropertiesTableModel extends AbstractTableModel
        implements Disposable, RDFPropertiesTableColumns {

    /**
     * The currently edited class
     */
    private RDFSNamedClass cls;

    private ClassListener classListener = new ClassAdapter() {

        public void addedToUnionDomainOf(RDFSClass cls, RDFProperty property) {
            property.addPropertyValueListener(valueListener);
            listenedToProperties.add(property);
            refill();
        }


        public void removedFromUnionDomainOf(RDFSClass cls, RDFProperty property) {
            property.removePropertyValueListener(valueListener);
            listenedToProperties.remove(property);
            refill();
        }


        public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
            refill();
        }


        public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
            refill();
        }
    };

    private int directCount = 0;

    private Collection listenedToProperties = new HashSet();

    private List properties = new ArrayList();

    private PropertyValueListener valueListener = new PropertyValueAdapter() {
        public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            int row = getRow(property);
            fireTableCellUpdated(row, COL_MULTIPLICITY);
            fireTableCellUpdated(row, COL_PROPERTY);
            fireTableCellUpdated(row, COL_RANGE);
        }
    };


    public RDFPropertiesTableModel() {
    }


    public void dispose() {
        unregisterValueListener();
        if (cls != null) {
            cls.removeClassListener(classListener);
        }
    }


    private void fill() {
        List directProperties = new ArrayList(cls.getUnionDomainProperties());
        Collections.sort(directProperties, new ResourceComparator());
        properties.addAll(directProperties);
        directCount = properties.size();
        List os = new ArrayList();
        for (Iterator it = cls.getUnionDomainProperties(true).iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (!property.isSystem() && !directProperties.contains(property)) {
                os.add(property);
            }
        }
        Collections.sort(os, new ResourceComparator());
        properties.addAll(os);
        fireTableRowsInserted(0, getRowCount());
    }


    public int getColumnCount() {
        return COLCOUNT;
    }


    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_PROPERTY || columnIndex == COL_RANGE) {
            return RDFResource.class;
        }
        else {
            return super.getColumnClass(columnIndex);
        }
    }


    public String getColumnName(int column) {
        if (column == COL_PROPERTY) {
            return "Property";
        }
        else if (column == COL_MULTIPLICITY) {
            return "Cardinality";
        }
        else if (column == COL_RANGE) {
            return "Type";
        }
        else {
            return super.getColumnName(column);
        }
    }


    public RDFProperty getRDFProperty(int row) {
        return (RDFProperty) properties.get(row);
    }


    public int getRow(RDFProperty property) {
        return properties.indexOf(property);
    }


    public int getRowCount() {
        return properties.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        final RDFProperty property = getRDFProperty(rowIndex);
        if (columnIndex == COL_PROPERTY) {
            return property;
        }
        else if (columnIndex == COL_MULTIPLICITY) {
            return property.isFunctional() ? "Single" : "Multiple";
        }
        else if (columnIndex == COL_RANGE) {
            return property.getRange();
        }
        else {
            return null;
        }
    }


    public boolean isDirectProperty(int row) {
        return row < directCount;
    }


    private void refill() {
        int count = getRowCount();
        properties.clear();
        if (count > 0) {
            fireTableRowsDeleted(0, count - 1);
        }
        fill();
    }


    public void setClass(RDFSNamedClass cls) {
        unregisterValueListener();
        if (this.cls != null) {
            cls.removeClassListener(classListener);
        }
        this.cls = cls;
        if (cls != null) {
            cls.addClassListener(classListener);
        }
        refill();
    }


    private void unregisterValueListener() {
        for (Iterator it = listenedToProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            property.removePropertyValueListener(valueListener);
        }
    }
}
