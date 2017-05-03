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

package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.OWLTestResult;
import edu.stanford.smi.protegex.owl.testing.RepairableOWLTest;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.individuals.OWLIndividualsTab;
import edu.stanford.smi.protegex.owl.ui.properties.OWLPropertiesTab;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collection;

/**
 * A JPanel to display the results of a "Find usage" search.
 * This mainly consists of a JTable with a FindUsageTableModel and a view button.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestResultsPanel extends ResultsPanel
        implements OWLTestResultsTableModelColumns {

    private JFileChooser fileChooser;

    private OWLModel owlModel;

    private OWLTestResultsTableModel tableModel;

    private JTable table;

    private Instance testSource;

    private Action repairAction = new AbstractAction("Repair selected item",
                                                     OWLIcons.getImageIcon("Repair")) {
        public void actionPerformed(ActionEvent e) {
            repairSelectedTestResult();
        }
    };

    private Action repairAllAction = new AbstractAction("Repair all repairable items",
                                                        OWLIcons.getImageIcon("RepairAll")) {
        public void actionPerformed(ActionEvent e) {
            repairAllTestResults();
        }
    };

    private Action saveAction = new AbstractAction("Save list to file...",
                                                   OWLIcons.getImageIcon("TestSave")) {
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };


    private Action settingsAction = new AbstractAction("Test settings...",
                                                       OWLIcons.getTestSettingsIcon()) {
        public void actionPerformed(ActionEvent e) {
            OWLTestSettingsPanel.showOWLTestSettingsDialog(owlModel);
        }
    };

    private Action viewAction = new AbstractAction("View resource", OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            viewSelectedHostInstance();
        }
    };

    public static MouseListener mouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent event) {
            AbstractButton button = getButton(event);
            button.setBorderPainted(button.isEnabled());
        }


        public void mouseExited(MouseEvent event) {
            getButton(event).setBorderPainted(false);
        }


        private AbstractButton getButton(MouseEvent event) {
            return (AbstractButton) event.getSource();
        }
    };


    public OWLTestResultsPanel(OWLModel owlModel, Collection items,
                               Instance testSource, boolean withButtons) {
        super(owlModel);

        this.owlModel = owlModel;

        this.testSource = testSource;
        tableModel = new OWLTestResultsTableModel(owlModel, items);
        table = new JTable(tableModel);
        table.setRowHeight(getFontMetrics(getFont()).getHeight());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(false);
        table.setRowMargin(0);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                enableActions();
            }
        });
        table.getColumnModel().getColumn(COL_SOURCE).setCellRenderer(new ResourceRenderer());
        table.getColumnModel().getColumn(COL_SOURCE).setPreferredWidth(60);
        TableColumn typeColumn = table.getColumnModel().getColumn(COL_TYPE);
        typeColumn.setPreferredWidth(64);
        typeColumn.setMaxWidth(64);
        table.getColumnModel().getColumn(COL_MESSAGE).setPreferredWidth(400);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && viewAction.isEnabled()) {
                    viewAction.actionPerformed(null);
                }
            }
        });

        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(table.getBackground());
        viewAction.setEnabled(false);
        repairAction.setEnabled(false);
        repairAllAction.setEnabled(true);

        if (withButtons) {
            addButton(repairAction);
            addButton(repairAllAction);
            addButton(settingsAction);
            addButton(saveAction);
        }

        setCenterComponent(scrollPane);
    }


    public void dispose() {
        tableModel.dispose();
    }


    private void enableActions() {
        int sel = table.getSelectedRow();
        if (sel >= 0) {
            OWLTestResult result = tableModel.getOWLTestResult(sel);
            OWLTest test = result.getOWLTest();
            repairAction.setEnabled(test instanceof RepairableOWLTest);
        }
        else {
            repairAction.setEnabled(false);
        }
        viewAction.setEnabled(sel >= 0);
        repairAllAction.setEnabled(sel >= 0);
    }


    /**
     * Gets a list of tabs that could be used to display a given instance.
     * This list is used to switch to the most suitable tab when the user
     * has clicked on an entry.  The tab should implement HostResourceDisplay
     * so that the chosen instance can be highlighted/selected there as well.
     *
     * @return a (possibly empty) list of tab class names
     */
    protected String[] getDisplayTabClassNames(RDFResource instance) {
        if (instance instanceof RDFSClass) {
            return new String[]{OWLClassesTab.class.getName()};
        }
        else if (instance instanceof OWLProperty) {
            return new String[]{OWLPropertiesTab.class.getName()};
        }
        else {
            return new String[]{OWLIndividualsTab.class.getName()};
        }
    }


    public Icon getIcon() {
        return OWLIcons.getTestIcon();
    }


    public String getTabName() {
        if (testSource == null) {
            return "Test Results";
        }
        else {
            return "Tests of " + testSource.getBrowserText();
        }
    }


    protected OWLTestResultsTableModel getTableModel() {
        return tableModel;
    }


    protected JTable getTable() {
        return table;
    }


    private void repairSelectedTestResult() {
        final int row = table.getSelectedRow();
        OWLTestResult result = tableModel.getOWLTestResult(row);
        RepairableOWLTest test = (RepairableOWLTest) result.getOWLTest();
        try {
            owlModel.beginTransaction("Repair test for " + result.getHost().getBrowserText() +
                                      ": " + result.getMessage());
            boolean repaired = test.repair(result);
            if (repaired) {
                tableModel.removeRow(row);
            }
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }

    private void repairAllTestResults() {
        final int numResults = tableModel.getRowCount();
        for (int i = numResults - 1; i >= 0; i--) {
            OWLTestResult result = tableModel.getOWLTestResult(i);
            OWLTest test = result.getOWLTest();
            if (test instanceof RepairableOWLTest) {
                try {
                    owlModel.beginTransaction("Repair test for " + result.getHost().getBrowserText() +
                                              ": " + result.getMessage());
                    boolean repaired = ((RepairableOWLTest) test).repair(result);
                    if (repaired) {
                        tableModel.removeRow(i);
                    }
                    owlModel.commitTransaction();
                }
                catch (Exception ex) {
                	owlModel.rollbackTransaction();
                    OWLUI.handleError(owlModel, ex);
                }
            }
        }
    }

    private void save() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String message = tableModel.saveToFile(file);
            if (message == null) {
                JOptionPane.showMessageDialog(this, "The list has been saved to " + file);
            }
            else {
                JOptionPane.showMessageDialog(this, "Could not save list:\n" + message,
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void setResults(java.util.List results) {
        tableModel.setItems(results);
    }


    private void viewSelectedHostInstance() {
        int row = table.getSelectedRow();
        RDFResource hostResource = tableModel.getSource(row);
        if (hostResource != null) {
            showHostResource(hostResource);
        }
    }
}
