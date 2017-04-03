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

package edu.stanford.smi.protegex.owl.ui.classform.component.property;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.owltable.SymbolTableModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * A TableModel displaying one column containing the fillers of someValuesFrom restrictions
 * at a given class/property pair.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyFormTableModel extends AbstractTableModel implements Disposable, SymbolTableModel {

    /**
     * The index of the column holding the classes/fillers.
     * This is currently the only column but we may decide to add
     * QCRs later, so this constant should be used.
     */
    public static final int COL_FILLER = 0;

    /**
     * The list of resources displayed as main contents (fillers of existential restrictions)
     */
    private List rows = new ArrayList();

    private List restrictions = new ArrayList();


    /**
     * If this is represents a definition (equivalent class), then this
     * field points to it.
     */
    private RDFSClass definition;

    private ClassListener listener = new ClassAdapter() {
        public void subclassAdded(RDFSClass cls, RDFSClass subclass) {
            refill();
        }


        public void subclassRemoved(RDFSClass cls, RDFSClass subclass) {
            refill();
        }


        public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
            refill();
        }


        public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
            refill();
        }
    };

    /**
     * The OWLNamedClass hosting the restrictions
     */
    private OWLNamedClass namedClass;

    /**
     * The property being (possibly) restricted
     */
    private RDFProperty property;


    /**
     * Constructs a new PropertyFormTableModel with no definition (only necessary conditions).
     *
     * @param namedClass the named class hosting the table
     * @param property   the property being shown
     */
    public PropertyFormTableModel(OWLNamedClass namedClass, RDFProperty property) {
        this(namedClass, property, null);
    }


    /**
     * Creates a new PropertyFormTableModel with an (optional) definition.
     *
     * @param namedClass the named class hosting the table
     * @param property   the property being shown
     * @param definition the definition or null
     */
    public PropertyFormTableModel(OWLNamedClass namedClass, RDFProperty property, RDFSClass definition) {
        this.namedClass = namedClass;
        this.property = property;
        this.definition = definition;
        namedClass.addClassListener(listener);
        addRows();
    }


    private void addRows() {
        assert rows.isEmpty();
        Iterator rs = listRestrictions();
        while (rs.hasNext()) {
            OWLRestriction restriction = (OWLRestriction) rs.next();
            if (restriction instanceof OWLSomeValuesFrom) {
                OWLSomeValuesFrom someValuesFrom = (OWLSomeValuesFrom) restriction;
                RDFResource filler = someValuesFrom.getFiller();
                if (filler instanceof RDFSClass) {
                    rows.add(filler);
                    restrictions.add(someValuesFrom);
                }
            }
            else if (restriction instanceof OWLHasValue) {
                OWLHasValue hasValue = (OWLHasValue) restriction;
                Object filler = hasValue.getHasValue();
                if (filler instanceof RDFResource) {
                    rows.add(filler);
                    restrictions.add(hasValue);
                }
            }
        }
        Collections.sort(rows, new Comparator() {
            public int compare(Object o1, Object o2) {
                RDFResource resource1 = (RDFResource) o1;
                RDFResource resource2 = (RDFResource) o2;
                if (!(resource1 instanceof OWLAnonymousClass)) {
                    if (resource2 instanceof OWLAnonymousClass) {
                        return -1;
                    }
                }
                else if (!(resource2 instanceof OWLAnonymousClass)) {
                    return 1;
                }
                return resource1.getBrowserText().compareTo(resource2.getBrowserText());
            }
        });
        fireTableRowsInserted(0, getRowCount() - 1);
    }


    public void dispose() {
        namedClass.removeClassListener(listener);
    }


    public int getColumnCount() {
        return 1;
    }


    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_FILLER) {
            return String.class;
        }
        else {
            throw new IllegalArgumentException("Unknown column index " + columnIndex);
        }
    }


    public Icon getIcon(RDFResource resource) {
        return ProtegeUI.getIcon(resource);
    }


    public OWLNamedClass getNamedClass() {
        return namedClass;
    }


    public RDFProperty getPredicate(int row) {
        return null;
    }


    public RDFProperty getProperty() {
        return property;
    }


    public RDFResource getRDFResource(int row) {
        return (RDFResource) rows.get(row);
    }


    public RDFResource getSubject() {
        return null;
    }


    public List getRDFResources() {
        return new ArrayList(rows);
    }


    public OWLExistentialRestriction getRestriction(int row) {
        return (OWLExistentialRestriction) restrictions.get(row);
    }


    public int getRowCount() {
        return rows.size();
    }


    public int getSymbolColumnIndex() {
        return COL_FILLER;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == COL_FILLER) {
            return getRDFResource(rowIndex).getBrowserText();
        }
        else {
            throw new IllegalArgumentException("Unknown column index " + columnIndex);
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;  // TODO
    }


    private Iterator listRestrictions() {
        return namedClass.getRestrictions(property, true).iterator();
    }


    private void refill() {
        if (getRowCount() > 0) {
            fireTableRowsDeleted(0, getRowCount() - 1);
        }
        restrictions.clear();
        rows.clear();
        addRows();
        fireTableDataChanged();
    }
}
