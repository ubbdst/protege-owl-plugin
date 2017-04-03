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

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFPropertiesTable extends JTable implements Disposable, RDFPropertiesTableColumns {

    private RDFSNamedClass cls;

    private RDFPropertiesTableModel tableModel;


    public RDFPropertiesTable() {
        tableModel = new RDFPropertiesTableModel();
        setModel(tableModel);
        setColumnWidth(COL_PROPERTY, 150);
        setColumnWidth(COL_MULTIPLICITY, 100);
        setColumnWidth(COL_RANGE, 150);
        setDefaultRenderer(RDFResource.class, new ResourceRenderer() {

            private int nextRow;


            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int col) {
                nextRow = row;
                return super.getTableCellRendererComponent(table, value, selected, hasFocus, row, col);
            }


            protected void loadSlot(Slot slot) {
                super.loadSlot(slot);
                RDFProperty property = (RDFProperty) slot;
                if (!tableModel.isDirectProperty(nextRow)) {
                    setMainIcon(property.getInheritedIcon());
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
        });
    }


    public void dispose() {
        tableModel.dispose();
    }


    public RDFPropertiesTableModel getTableModel() {
        return tableModel;
    }


    public void handleAddProperty(RDFProperty property) {
        property.addUnionDomainClass(cls);
        setSelectedProperty(property);
    }


    protected void handleDoubleClick() {
        int[] rows = getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            RDFProperty property = tableModel.getRDFProperty(row);
            property.getProject().show(property);
        }
    }


    public void handlePropertyCreated(RDFProperty property) {
        property.setDomain(cls);
        setSelectedProperty(property);
    }


    private void setSelectedProperty(RDFProperty property) {
        int row = tableModel.getRow(property);
        if (row >= 0) {
            getSelectionModel().setSelectionInterval(row, row);
        }
    }


    public void handleRemoveProperties() {
        int[] rows = getSelectedRows();
        List properties = new ArrayList();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            properties.add(tableModel.getRDFProperty(row));
        }
        for (Iterator it = properties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            property.removeUnionDomainClass(cls);
        }
    }


    private void setColumnWidth(int columnIndex, int width) {
        TableColumn col = getColumnModel().getColumn(columnIndex);
        col.setPreferredWidth(width);
    }


    public void setClass(RDFSNamedClass cls) {
        this.cls = cls;
        tableModel.setClass(cls);
    }
}
