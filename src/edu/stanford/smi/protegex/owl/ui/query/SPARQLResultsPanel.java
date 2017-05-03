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

package edu.stanford.smi.protegex.owl.ui.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.hp.hpl.jena.query.QueryException;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SPARQLResultsPanel extends ResultsPanel {

    private OWLModel owlModel;

    private SPARQLQueryPanel queryPanel;

    private JSplitPane splitPane;

    private JTable table;

    private Action toggleLayoutAction = new AbstractAction("Toggle layout", OWLIcons.getImageIcon(OWLIcons.LAYOUT_VERTICALLY)) {
        public void actionPerformed(ActionEvent e) {
            if(splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
                splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
                toggleLayoutAction.putValue(Action.SMALL_ICON, OWLIcons.getImageIcon(OWLIcons.LAYOUT_VERTICALLY));
            }
            else {
                splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
                toggleLayoutAction.putValue(Action.SMALL_ICON, OWLIcons.getImageIcon(OWLIcons.LAYOUT_HORIZONTALLY));
            }
            repaint();
        }
    };

    public static final String NAME = "SPARQL";


    public SPARQLResultsPanel(OWLModel owlModel, boolean withQueryArea) {
        super(owlModel);
        this.owlModel = owlModel;
        table = new JTable(new DefaultTableModel());
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
        });
        table.setCellSelectionEnabled(true);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.white);
        OWLLabeledComponent lc = new OWLLabeledComponent("Results", sp);
        if (withQueryArea) {
            queryPanel = new SPARQLQueryPanel(owlModel);
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, queryPanel, lc);
            add(BorderLayout.CENTER, splitPane);
            addButton(toggleLayoutAction);
            addButton(new FindAssertionsAction(owlModel));
        }
        else {
            add(BorderLayout.CENTER, lc);
        }
    }


    private TableModel createTableModel(QueryResults results) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        java.util.List vars = results.getVariables();
        for (int i = 0; i < vars.size(); i++) {
            String varName = (String) vars.get(i);
            tableModel.addColumn(varName);
        }
        while (results.hasNext()) {
            Map map = results.next();
            Object[] data = new Object[vars.size()];
            for (int i = 0; i < vars.size(); i++) {
                String varName = (String) vars.get(i);
                RDFObject value = (RDFObject) map.get(varName);
                data[i] = value;
            }
            tableModel.addRow(data);
        }
        return tableModel;
    }


    public void dispose() {
        if(queryPanel != null) {
            queryPanel.rememberQueryText();
        }
    }


    public void executeQuery(String queryText) {
        try {
            table.setModel(new DefaultTableModel());
            table.revalidate();

        	QueryResults results = owlModel.executeSPARQLQuery(queryText);
                       
            if (results.hasNext()) {
                TableModel tableModel = createTableModel(results);
                table.setModel(tableModel);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    TableColumn col = table.getColumnModel().getColumn(i);
                    col.setCellRenderer(new ResourceRenderer());
                }
            }
            else {
                table.setModel(new DefaultTableModel());
                ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, "No matches found.");
            }
        }
        catch (QueryException ex){
        	//Log.getLogger().log(Level.WARNING, "Exception caught", ex);
        	ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Query parse error:\n" + ex.getMessage());        	
		}
        catch (Exception ex) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Query failed:\n" + ex.getMessage());
        }
    }


    public Icon getIcon() {
        return OWLIcons.getImageIcon(OWLIcons.SPARQL_RESULTS_PANEL);
    }


    public String getTabName() {
        return NAME;
    }


    private void handleDoubleClick() {
        int[] sels = table.getSelectedRows();
        if (sels.length == 1) {
            int row = sels[0];
            int col = table.getSelectedColumn();
            if (col >= 0 && col < table.getModel().getColumnCount()) {
                Object sel = table.getModel().getValueAt(row, col);
                if (sel instanceof RDFResource) {
                    RDFResource resource = (RDFResource) sel;
                    showHostResource(resource);
                }
            }
        }
    }


    public void setQueryText(String str) {
        queryPanel.setQueryText(str);
    }
}
