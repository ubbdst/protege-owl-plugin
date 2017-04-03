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

package edu.stanford.smi.protegex.owl.ui.components.triples;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.actions.CopyPropertyValueAction;
import edu.stanford.smi.protegex.owl.ui.actions.DeleteAllPropertyValuesAction;
import edu.stanford.smi.protegex.owl.ui.actions.DeleteMatchingPropertyValuesAction;
import edu.stanford.smi.protegex.owl.ui.actions.triple.TripleAction;
import edu.stanford.smi.protegex.owl.ui.actions.triple.TripleActionManager;
import edu.stanford.smi.protegex.owl.ui.actions.triple.TripleActionSwingAction;
import edu.stanford.smi.protegex.owl.ui.components.ComponentUtil;
import edu.stanford.smi.protegex.owl.ui.results.TripleDisplay;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * A JTable optimized for displaying a TriplesTableModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TriplesTable extends JTable implements TripleDisplay {

    private TriplesTableModel tableModel;

    private JTextField textField;

    private JComboBox typeComboBox;


    public TriplesTable(Project project, TriplesTableModel model, final String partialActionName) {
        super(model);
        this.tableModel = model;

        model.setTable(this);

        textField = new JTextField();
        OWLUI.addCopyPastePopup(textField);
        setDefaultEditor(Object.class, new DefaultCellEditor(textField));

        TableColumn propertyColumn = getColumnModel().getColumn(TriplesTableModel.COL_PROPERTY);
        TableColumn valueColumn = getColumnModel().getColumn(TriplesTableModel.COL_VALUE);
        TableColumn languageColumn = getColumnModel().getColumn(tableModel.getColumnCount() - 1);
        propertyColumn.setCellRenderer(new FrameRenderer());
        valueColumn.setCellRenderer(new ResourceRenderer());
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        JComboBox comboBox = ComponentUtil.createLangCellEditor(owlModel, this);
        languageColumn.setCellEditor(new DefaultCellEditor(comboBox));
        if (tableModel.hasTypeColumn()) {
            TableColumn typeColumn = getColumnModel().getColumn(tableModel.getColumnCount() - 2);
            typeColumn.setCellRenderer(new ResourceRenderer());
            Vector datatypes = new Vector();
            typeComboBox = new JComboBox(datatypes);
            typeComboBox.setRenderer(new ResourceRenderer());
            typeColumn.setCellEditor(new DefaultCellEditor(typeComboBox));
        }

        //setRowHeight(getFontMetrics(getFont()).getHeight());
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        getTableHeader().setReorderingAllowed(false);
        setShowGrid(false);
        setRowMargin(0);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        propertyColumn.setPreferredWidth(100);
        valueColumn.setPreferredWidth(200);
        languageColumn.setPreferredWidth(60);
        languageColumn.setMaxWidth(100);
        

        addMouseListener(new PopupMenuMouseListener(this) {
            protected JPopupMenu getPopupMenu() {
                return createPopupMenu(partialActionName);
            }


            protected void setSelection(JComponent c, int x, int y) {
                int row = y / getRowHeight();
                if (row >= 0 && row < getRowCount()) {
                    getSelectionModel().setSelectionInterval(row, row);
                }
            }
        });
    }


    protected JPopupMenu createPopupMenu(String partialActionName) {
        TriplesTableModel tableModel = (TriplesTableModel) getModel();
        RDFResource resource = tableModel.getSubject();
        final JPopupMenu menu = new JPopupMenu();
        final int row = getSelectedRow();
        RDFProperty property = tableModel.getPredicate(row);
        Object value = tableModel.getValue(row);
        if (property.isAnnotationProperty()) {
            if (resource instanceof RDFSNamedClass) {
                RDFSNamedClass rootClass = (RDFSNamedClass) resource;
                Collection targetResources = new ArrayList();
                for (Iterator it = rootClass.getSubclasses(true).iterator(); it.hasNext();) {
                    RDFSClass subClass = (RDFSClass) it.next();
                    if (subClass instanceof RDFSNamedClass) {
                        targetResources.add(subClass);
                    }
                }
                menu.add(new CopyPropertyValueAction("subclasses", property, value, targetResources, partialActionName));
                menu.add(new DeleteMatchingPropertyValuesAction("subclasses", property, value, targetResources, partialActionName));
                menu.add(new DeleteAllPropertyValuesAction("subclasses", property, targetResources, partialActionName));
            }
            else if (resource instanceof RDFProperty) {
                Collection targetResources = new ArrayList(((RDFProperty) resource).getSubproperties(true));
                menu.add(new CopyPropertyValueAction("subproperties", property, value, targetResources, partialActionName));
                menu.add(new DeleteMatchingPropertyValuesAction("subproperties", property, value, targetResources, partialActionName));
                menu.add(new DeleteAllPropertyValuesAction("subproperties", property, targetResources, partialActionName));
            }
        }

        final Triple triple = new DefaultTriple(tableModel.getSubject(), property, value);
        TripleActionManager.addTripleActionsToMenu(triple, new TripleActionManager.Adder() {
            public void addTripleAction(TripleAction action) {
                menu.add(new TripleActionSwingAction(action, triple));
            }
        });
        if (menu.getComponentCount() > 0) {
            return menu;
        }
        else {
            return null;
        }
    }


    public boolean displayTriple(Triple triple) {
        if (triple.getSubject().equals(tableModel.getSubject())) {
            int row = tableModel.getPropertyValueRow(triple.getPredicate(), triple.getObject());
            if (row >= 0) {
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, 0, false));
            }
        }
        return false;
    }


    public void editCell(final int row) {
        getSelectionModel().setSelectionInterval(row, row);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scrollRectToVisible(getCellRect(row, TriplesTableModel.COL_VALUE, true));
                textField.requestFocus();
            }
        });
        editCellAt(row, TriplesTableModel.COL_VALUE);
    }


    public TableCellEditor getCellEditor(int row, int column) {
        TriplesTableModel tableModel = (TriplesTableModel) getModel();
        RDFProperty property = tableModel.getPredicate(row);
        RDFResource range = property.getRange();
        if (property.getOWLModel().getXSDboolean().equals(range)) {
            JComboBox comboBox = new JComboBox(new Boolean[]{
                    Boolean.FALSE,
                    Boolean.TRUE
            });
            return new DefaultCellEditor(comboBox);
        }
        else if (range instanceof OWLDataRange) {
            Collection allowedValues = ((OWLDataRange) range).getOneOfValues();
            JComboBox comboBox = new JComboBox(allowedValues.toArray());
            return new DefaultCellEditor(comboBox);
        }
        else {
            return super.getCellEditor(row, column);
        }
    }


    public TriplesTableModel getTableModel() {
        return tableModel;
    }


    public void setValueAt(Object aValue, int row, int column) {
        RDFProperty property = tableModel.getPredicate(row);
        Object newValue = tableModel.setValueAndGetIt(aValue, row, column);
        setSelectedRow(property, newValue);
    }


    private void setSelectedRow(RDFProperty property, Object value) {
        int sel = getSelectedRow();
        int row = tableModel.getPropertyValueRow(property, value);
        if (row >= 0) {
            sel = row;
        }
        if (sel >= 0 && sel < tableModel.getRowCount()) {
            getSelectionModel().setSelectionInterval(sel, sel);
        }
    }
}
