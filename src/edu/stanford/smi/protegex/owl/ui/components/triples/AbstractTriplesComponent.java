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
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditorManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.metadata.AnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.metadata.DateAnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.metadata.DateTimeAnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.metadata.TimeAnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A PropertyWidget to edit the values of properties in a table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTriplesComponent extends AbstractPropertyValuesComponent implements Disposable {

    private Icon icon;

    private OWLModel owlModel;

    private static Set<AnnotationsWidgetPlugin> plugins = new HashSet<AnnotationsWidgetPlugin>();

    private TriplesTable table;

    private TriplesTableModel tableModel;

    private Action viewValueAction;
    
    private boolean enabled = true; 


    public AbstractTriplesComponent(RDFProperty predicate) {
        this(predicate, "Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLE));
    }

    public AbstractTriplesComponent(RDFProperty predicate, String label, Icon icon) {
    	this(predicate, label, icon, false);
    }
    
    public AbstractTriplesComponent(RDFProperty predicate, String label, Icon icon, boolean isReadOnly) {
        super(predicate, label, isReadOnly);

        this.icon = icon;

        addPlugin(new DateAnnotationsWidgetPlugin());   // Ugly, but static {} block has class loader problem
        addPlugin(new DateTimeAnnotationsWidgetPlugin());
        addPlugin(new TimeAnnotationsWidgetPlugin());

        this.owlModel = getOWLModel();

        viewValueAction = new AbstractAction("View selected value", OWLIcons.getViewIcon()) {
            public void actionPerformed(ActionEvent e) {
                viewValue();
            }
        };
        viewValueAction.setEnabled(false);

        tableModel = createTableModel();
        table = createTable(owlModel.getProject());

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                handleSelectionChanged();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && isEnabled()) {                	
                    handleTableDoubleClick();
                }
            }
        });

        // forces any current editing to stop if the window closes
        addContainerListener(new ContainerAdapter(){
            public void componentRemoved(ContainerEvent e) {
                stopCellEditing();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(table.getBackground());
        LabeledComponent lc = new OWLLabeledComponent(label, scrollPane, true, true);
        if (icon != null) {
            lc.setHeaderIcon(icon);
        }
        WidgetUtilities.addViewButton(lc, viewValueAction);
        addButtons(lc);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    protected abstract void addButtons(LabeledComponent lc);


    public static void addPlugin(AnnotationsWidgetPlugin plugin) {
        for (Iterator<AnnotationsWidgetPlugin> it = plugins.iterator(); it.hasNext();) {
            AnnotationsWidgetPlugin p = it.next();
            if (p.getClass() == plugin.getClass()) {
                return;
            }
        }
        plugins.add(plugin);
    }


    protected TriplesTable createTable(Project project) {
        return new TriplesTable(project, tableModel, "property");
    }


    protected TriplesTableModel createTableModel() {
        return new TriplesTableModel(null);
    }


    public void dispose() {
        tableModel.dispose();
    }


    public Icon getIcon() {
        return icon;
    }


    public TriplesTable getTable() {
        return table;
    }


    public TriplesTableModel getTableModel() {
        return tableModel;
    }


    protected void handleSelectionChanged() {
        updateActions();
    }

    /* 
     * WARNING!
     *    See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    private void handleTableDoubleClick() {
        int selIndex = table.getSelectedRow();
        RDFProperty p = tableModel.getPredicate(selIndex);
        if (selIndex >= 0 && selIndex < tableModel.getRowCount() && !p.isReadOnly() && viewValueAction.isEnabled()) {
            viewValue();
        }
    }


    public static Iterator<AnnotationsWidgetPlugin> plugins() {
        return plugins.iterator();
    }


    public static void removePlugin(AnnotationsWidgetPlugin plugin) {
        plugins.remove(plugin);
    }


    public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        if(table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        tableModel.setSubject(subject);
    }


    protected void updateActions() {
        final int row = table.getSelectedRow();
        RDFProperty property = row >= 0 ? tableModel.getPredicate(row) : null;
        if (row >= 0) {
            Object value = tableModel.getValue(row);
            viewValueAction.setEnabled(true);
        }
        else {
            viewValueAction.setEnabled(false);
        }
    }


    public void valuesChanged() {
        tableModel.updateValues();
    }


    private void viewValue() {
        final int row = table.getSelectedRow();
        viewValue(row);
    }

    /* 
     * WARNING!
     *    See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    private void viewValue(final int row) {
        Object value = tableModel.getValue(row);
        if (value instanceof RDFResource) {
            ProtegeUI.show((RDFResource) value);
        }
        else {
        	RDFProperty property = tableModel.getPredicate(row);                
            
            RDFResource subject = tableModel.getSubject();
            PropertyValueEditor editor = PropertyValueEditorManager.getEditor(subject, property, value);
            if(editor != null && !property.isReadOnly()) {
                Object newValue = editor.editValue(this, subject, property, value);
                if(newValue != null) {
                    tableModel.setValue(newValue, row);
                    return;
                }
                else {
                    return;
                }
            }

            for (Iterator<AnnotationsWidgetPlugin> it = plugins.iterator(); it.hasNext();) {
                AnnotationsWidgetPlugin plugin = it.next();
                if (plugin.canEdit(subject, property, value)) {
                    Object newValue = plugin.editValue(null, subject, property, value);
                    if (newValue != null) {
                        tableModel.setValue(newValue, row);
                    }
                    return;
                }
            }
            String name = property.getBrowserText();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            JTextArea textArea = new JTextArea(value.toString());
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            /*
             * Warning... The property.isReadOnly() really is  here deliberately to support an unusual
             *            NCI definition of a read only property.
             */
            textArea.setEditable(!property.isReadOnly());
            Component comp = new JScrollPane(textArea);
            LabeledComponent lc = new LabeledComponent(name, comp);
            lc.setPreferredSize(new Dimension(400, 400));
            int r = ProtegeUI.getModalDialogFactory().showDialog(this, lc, "Edit annotation", ModalDialogFactory.MODE_OK_CANCEL);
            /*
             * Warning... The property.isReadOnly() really is  here deliberately to support an unusual
             *            NCI definition of a read only property.
             */
            if (r == ModalDialogFactory.OPTION_OK && !property.isReadOnly()) {
                String newValue = textArea.getText();
                tableModel.setValue(newValue, row);
            }
        }
    }

    private boolean stopCellEditing() {
        try {
            int column = table.getEditingColumn();
            if (column > -1) {
                TableCellEditor cellEditor = table.getColumnModel().getColumn(column).getCellEditor();
                if (cellEditor == null) {
                    cellEditor = table.getDefaultEditor(table.getColumnClass(column));
                }
                if (cellEditor != null) {
                    cellEditor.stopCellEditing();
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
    
}
