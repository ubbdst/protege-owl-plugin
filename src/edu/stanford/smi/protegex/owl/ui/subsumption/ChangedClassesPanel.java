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

package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A JPanel hosting a table which displays the changed classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangedClassesPanel extends ResultsPanel {

    private Action assertAction = new AbstractAction("Assert selected change(s)",
            OWLIcons.getAssertChangeIcon()) {
        public void actionPerformed(ActionEvent e) {
            assertSelectedChanges();
        }
    };

    private static Map panels = new HashMap();

    private Action showAction = new AbstractAction("Show selected class in hierarchies",
            OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            showSelectedCls();
        }
    };

    private Action saveAction = new AbstractAction("Save classification results",
            OWLIcons.getSaveInferredIcon()) {
        public void actionPerformed(ActionEvent e) {
            saveClassificationResults();
        }
    };
    
    private JButton sortJButton;
    private boolean sortedByClass = true;
    public static final String SORT_BY_CLASS = "Sort Classification Results by Class";
    public static final String SORT_BY_CHANGE = "Sort Classification Results by Change Type";
    private Action sortAction = new AbstractAction(SORT_BY_CHANGE,
                                                   OWLIcons.getDisplayChangedClassesIcon()) {
        public void actionPerformed(ActionEvent e) {
            sortClassificationResults();
        }
    };


    private ChangedClassesTableModel tableModel;

    private JTable table;

    public static final String TAB_NAME = "Classification Results";


    public ChangedClassesPanel(OWLModel owlModel) {
        super(owlModel);
        tableModel = new ChangedClassesTableModel(owlModel);
        table = new JTable(tableModel);
        table.setRowHeight(getFontMetrics(getFont()).getHeight());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(false);
        table.setRowMargin(0);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelectedCls();
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions();
            }
        });
        table.setDefaultRenderer(Frame.class, new ResourceRenderer());
        table.getColumnModel().getColumn(ChangedClassesTableModel.COL_CLS).setPreferredWidth(180);
        table.getColumnModel().getColumn(ChangedClassesTableModel.COL_TEXT).setPreferredWidth(250);
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        // addButton(showAction);
        addButton(assertAction);
        sortJButton = addButton(sortAction);
        addButton(saveAction);
        updateActions();
        setCenterComponent(scrollPane);
    }


    private void assertSelectedChanges() {
        int[] rows = table.getSelectedRows();
        tableModel.assertChanges(rows);
    }


    public boolean contains(Cls cls) {
        return tableModel.contains(cls);
    }


    public void dispose() {
        tableModel.dispose();
    }


    /**
     * Should be called to clean up any dangling (memory-expensive) references
     * to OWLModels when a project is closed.
     *
     * @param owlModel the OWLModel to delete references to
     */
    public static void dispose(OWLModel owlModel) {
        ChangedClassesPanel panel = (ChangedClassesPanel) panels.get(owlModel);
        if (panel != null) {
            panels.remove(owlModel);
            panel.dispose();
        }
    }


    public static ChangedClassesPanel get(OWLModel owlModel) {
        ChangedClassesPanel panel = (ChangedClassesPanel) panels.get(owlModel);
        if (panel == null) {
            panel = new ChangedClassesPanel(owlModel);
            panels.put(owlModel, panel);
        }
        return panel;
    }


    public int getChangeCount() {
        return tableModel.getRowCount();
    }


    public String getChangeText(Cls cls) {
        return tableModel.getChangeText(cls);
    }


    public Icon getIcon() {
        return OWLIcons.getImageIcon("ChangedClasses");
    }


    public String getTabName() {
        return TAB_NAME;
    }


    ChangedClassesTableModel getTableModel() {
        return tableModel;
    }


    public void refresh() {
        tableModel.refill();
    }


    private void showSelectedCls() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Cls cls = tableModel.getCls(row);
            if (cls instanceof RDFSClass) {
                showHostResource((RDFSClass) cls);
            }
        }
    }


    private void updateActions() {
        int[] rows = table.getSelectedRows();
        showAction.setEnabled(rows.length == 1);
        if (rows.length > 0) {
            for (int i = 0; i < rows.length; i++) {
                int row = rows[i];
                Cls cls = tableModel.getCls(row);
                if (!OWLUtil.isInconsistent(cls) && cls.isEditable()) {
                    assertAction.setEnabled(true);
                    return;
                }
            }
        }
        assertAction.setEnabled(false);
        saveAction.setEnabled(true);
    }
    
 
	private void saveClassificationResults() {
		JFileChooser chooser = ComponentFactory.createFileChooser("Save classfication results", "Property Files", "");
        
		int openDialogResult = chooser.showSaveDialog(ChangedClassesPanel.this);

        if  (openDialogResult == JFileChooser.APPROVE_OPTION) {
        	File propFile = chooser.getSelectedFile();

        	if (savePropertyFile(propFile)) {
        		ModalDialog.showMessageDialog(this, "Classification results written out successfully to:\n" + propFile.getAbsolutePath(), "Classification results saved");
        	}
        	else
        		ModalDialog.showMessageDialog(this, "Error writing the classification results", "Error");
        }
	}

	public boolean savePropertyFile(File propFile) {
    	try {
    		OutputStream out = new FileOutputStream(propFile);
        	getClassificationResults().store(out, "Generated by Protege OWL. Export of classification results.");
        	
        	return true;
		} catch (Exception e) {
			Log.getLogger().warning("Could not write property file: " + propFile.getAbsolutePath());
		}
		return false;
	}

	
	public Properties getClassificationResults() {
		Properties classificationResults = new Properties();
		
		for (int i = 0; i < getTableModel().getRowCount(); i++) {
			try {
				Cls cls = (Cls) getTableModel().getValueAt(i, ChangedClassesTableModel.COL_CLS);
				String value = (String) getTableModel().getValueAt(i, ChangedClassesTableModel.COL_TEXT);
				
				classificationResults.put(cls.getBrowserText(), value);				
			} catch (Exception e) {
				Log.getLogger().warning("Error at writing classification result from row " + i);
			}
		}
		
		return classificationResults;		
	}
	
	public void sortClassificationResults() {
	    sortedByClass = !sortedByClass;
	    tableModel.refill(sortedByClass);
	    sortJButton.setToolTipText(sortedByClass ? SORT_BY_CHANGE : SORT_BY_CLASS);
	}
    
}
