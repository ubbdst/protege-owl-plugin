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

package edu.stanford.smi.protegex.owl.ui.metadatatab.prefixes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableTable;
import edu.stanford.smi.protege.widget.PropertiesTableModel;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A JPanel hosting a JTable of all prefixes and their namespaces.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PrefixesPanel extends JPanel implements Disposable {


    private Action addAction = new AbstractAction("Add new prefix", OWLIcons.getAddIcon(OWLIcons.PREFIX)) {
        public void actionPerformed(ActionEvent e) {
            OWLModel owlModel = ontology.getOWLModel();
            try {
                owlModel.beginTransaction("Add new namespace prefix", ontology.getName());
                addPrefix();
                owlModel.commitTransaction();
            }
            catch (Exception ex) {
            	owlModel.rollbackTransaction();
                OWLUI.handleError(owlModel, ex);
            }
        }
    };

    private TableCellRenderer namespaceCellRenderer = new DefaultTableCellRenderer() {
        protected void setValue(Object value) {
            super.setValue(value);
            if (value instanceof String && !tableModel.isNamespaceEditable((String) value)) {
                setForeground(UNEDITABLE_COLOR);
            }
            else {
                setForeground(Color.black);
            }
        }
    };

    private TableCellRenderer prefixCellRenderer = new DefaultTableCellRenderer() {
        protected void setValue(Object value) {
            super.setValue(value);
            if (value instanceof String && !tableModel.isPrefixEditable((String) value)) {
                setForeground(UNEDITABLE_COLOR);
            }
            else {
                setForeground(Color.black);
            }
        }
    };

    private JTextField defaultNamespaceField;

    private NamespaceManagerAdapter namespaceManagerListener = new NamespaceManagerAdapter() {
        public void defaultNamespaceChanged(String oldValue, String newValue) {
            defaultNamespaceField.setText(newValue);
        }
    };

    private OWLOntology ontology;

    private Action removeAction = new AbstractAction("Remove selected prefix", OWLIcons.getRemoveIcon(OWLIcons.PREFIX)) {
        public void actionPerformed(ActionEvent e) {
            removePrefix();
        }
    };

    private SelectableTable table;

    private PrefixesTableModel tableModel;

    private static final Color UNEDITABLE_COLOR = Color.darkGray;


    public PrefixesPanel(OWLOntology ontology) {

        this.ontology = ontology;


        table = new SelectableTable() {
            private static final long serialVersionUID = 6379511196511156925L;

            public void editingStopped(javax.swing.event.ChangeEvent e) {               
                int editingColumn = getEditingColumn();
                String cellValue = (String) getCellEditor().getCellEditorValue();
                    
                super.editingStopped(e);
                
                if (editingColumn == PrefixesTableModel.COL_PREFIX) {                   
                    int newRow = ((PrefixesTableModel) getModel()).getRowOfPrefix(cellValue);
                    
                    if (newRow >= 0) {
                        ComponentUtilities.scrollToVisible(table, newRow, PrefixesTableModel.COL_PREFIX);
                        table.setRowSelectionInterval(newRow, newRow);                       
                        table.editCellAt(newRow, PropertiesTableModel.getValueColumnIndex());
                        table.requestFocus();                                
                    }
                }               
            };
        };
        
        tableModel = new PrefixesTableModel(ontology);
        table.setModel(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn prefixColumn = table.getColumnModel().getColumn(PrefixesTableModel.COL_PREFIX);
        TableColumn namespaceColumn = table.getColumnModel().getColumn(PrefixesTableModel.COL_NAMESPACE);
        prefixColumn.setPreferredWidth(80);
        prefixColumn.setMaxWidth(80);
        prefixColumn.setCellRenderer(prefixCellRenderer);
        namespaceColumn.setPreferredWidth(200);
        namespaceColumn.setCellRenderer(namespaceCellRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        LabeledComponent lc = new OWLLabeledComponent("Namespace Prefixes", scrollPane);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());

        //makeDefaultAction.setEnabled(false);
        removeAction.setEnabled(false);
        lc.addHeaderButton(addAction);
        lc.addHeaderButton(removeAction);
        //lc.addHeaderButton(makeDefaultAction);
        createDefaultNamespacePanel();
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                enableActions();
            }
        });
        tableModel.getNamespaceManager().addNamespaceManagerListener(namespaceManagerListener);
    }


    private String addPrefix() {
        NamespaceManager nsm = tableModel.getNamespaceManager();
        String prefix = addPrefix(nsm);
        int row = tableModel.getPrefixRow(prefix);
        table.getSelectionModel().setSelectionInterval(row, row);
        table.scrollRectToVisible(table.getCellRect(row, 0, true));
        table.requestFocus();
        table.editCellAt(row, PrefixesTableModel.COL_PREFIX);
        return prefix;
    }


    public static String addPrefix(NamespaceManager nsm) {
        int index = 1;
        String prefix = null;
        do {
            prefix = "p" + index++;
        }
        while (nsm.getNamespaceForPrefix(prefix) != null);
        String namespace = null;
        do {
            namespace = "http://www.domain" + (index++) + ".com#";
        }
        while (nsm.getPrefix(namespace) != null);
        nsm.setPrefix(namespace, prefix);
        return prefix;
    }


    private void createDefaultNamespacePanel() {

        final NamespaceManager nsm = tableModel.getNamespaceManager();
        String namespace = nsm.getDefaultNamespace();
        defaultNamespaceField = new JTextField(namespace);
        defaultNamespaceField.setEditable(false);
        defaultNamespaceField.setMinimumSize(new Dimension(defaultNamespaceField.getPreferredSize().width, 30));
//        defaultNamespaceField.addFocusListener(new FocusListener() {
//
//            public void focusGained(FocusEvent arg0) {
//            }
//
//
//            public void focusLost(FocusEvent arg0) {
//                String text = defaultNamespaceField.getText().trim();
//                if (Jena.isNamespaceWithSeparator(text)) {
//                    nsm.setDefaultNamespace(text);
//                }
//                else {
//                    OWLUI.showErrorMessageDialog("Error: Namespace must be a valid URI, and must end with a valid\n" +
//                            "XML Namespace separator such as # or /",
//                            "Illegal Namespace");
//                }
//            }
//        });
    }


    public void dispose() {
    }


    private void enableActions() {
        removeAction.setEnabled(enableRemove());
    }
    
    private boolean enableRemove() {
        int selIndex = table.getSelectedRow();
        if (selIndex < 0 || !isEnabled()) {
            return false;
        }
        if (!tableModel.getNamespaceManager().isModifiable(tableModel.getPrefix(selIndex))) {
            return false;
        }
        TripleStore topTripleStore = ontology.getOWLModel().getTripleStoreModel().getTopTripleStore();
        NamespaceManager topNamespaceManager = topTripleStore.getNamespaceManager();
        Object prefix = table.getValueAt(selIndex, PrefixesTableModel.COL_PREFIX);
        if (prefix != null && prefix instanceof String  && topNamespaceManager.getNamespaceForPrefix((String) prefix) != null) {
            return true;
        }
        return false;
    }


    public Component getDefaultNamespaceField() {
        return defaultNamespaceField;
    }


    public String getSelectedNamespace() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 1) {
            return tableModel.getNamespace(rows[0]);
        }
        else {
            return null;
        }
    }


    private void makeDefault() {
        OWLModel owlModel = ontology.getOWLModel();
        try {
            int row = table.getSelectedRow();
            String prefix = tableModel.getPrefix(row);
            NamespaceManager nsm = tableModel.getNamespaceManager();
            String namespace = nsm.getNamespaceForPrefix(prefix);
            try {
                owlModel.beginTransaction("Make " + namespace + " the default namespace", ontology.getName());
                nsm.setDefaultNamespace(namespace);
                nsm.removePrefix(prefix);
                owlModel.commitTransaction();
			} catch (Exception e) {
				owlModel.rollbackTransaction();
				OWLUI.handleError(owlModel, e);
			}
        }
        catch (Exception ex) {
            OWLUI.handleError(owlModel, ex);
        }
    }


    private void removePrefix() {
        if (table.isEditing()) {
            table.getCellEditor().cancelCellEditing();
        }
        int row = table.getSelectedRow();
        String prefix = tableModel.getPrefix(row);
        OWLModel owlModel = ontology.getOWLModel();
        if (owlModel.getRDFResource(prefix + ":") instanceof OWLOntology) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                                                     "The prefix \"" + prefix + "\" is used by an owl:Ontology element\n" +
                                                                     "of an imported ontology.  Please remove the corresponding\n" +
                                                                     "import first, and then remove the prefix.");
        }
        else {
            NamespaceManager nsm = tableModel.getNamespaceManager();
            try {
                owlModel.beginTransaction("Remove namespace prefix " + prefix, ontology.getName());
                nsm.removePrefix(prefix);
                owlModel.commitTransaction();
            }
            catch (Exception ex) {
            	owlModel.rollbackTransaction();
                OWLUI.handleError(owlModel, ex);
            }
        }
    }


    public void setSelectedURI(String uri) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String namespace = tableModel.getNamespace(i);
            if (namespace.startsWith(uri)) {
                table.getSelectionModel().setSelectionInterval(i, i);
                return;
            }
        }
        table.getSelectionModel().clearSelection();
    }


    public void setEnabled(boolean enabled) {        
        addAction.setEnabled(enabled);
        removeAction.setEnabled(enabled);
        table.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
