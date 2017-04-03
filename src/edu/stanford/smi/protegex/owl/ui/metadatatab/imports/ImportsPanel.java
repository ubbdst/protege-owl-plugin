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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.importstree.ImportsTreePanel;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.emptyimport.EmptyImportWizard;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard.AddedPrefixesTable;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard.ImportEntry;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard.ImportWizard;
import edu.stanford.smi.protegex.owl.ui.metadatatab.prefixes.PrefixesPanel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 * TT: This class is not referenced from anywhere in the code.
 * We should consider it for clean-up.
 */
public class ImportsPanel extends JPanel implements Disposable {

    private Action addImportAction = new AbstractAction("Import ontology...", OWLIcons.getAddIcon(OWLIcons.IMPORT)) {
        public void actionPerformed(ActionEvent e) {
            addImport();
        }
    };

    private Action createImportedTripleStoreAction = new AbstractAction(CreateImportedTripleStorePanel.TITLE,
            OWLIcons.getCreateIcon(OWLIcons.IMPORT)) {
        public void actionPerformed(ActionEvent e) {
            createImportedTripleStore();
        }
    };

    private Action showImportsTreeAction = new AbstractAction("Show imports tree",
            OWLIcons.getImageIcon(OWLIcons.VIEW)) {
        public void actionPerformed(ActionEvent e) {
            ImportsTreePanel.showDialog(owlModel);
        }
    };

    private OWLModel owlModel;

    private PrefixesPanel prefixesPanel;

    private Action removeAction = new AbstractAction("Remove import", OWLIcons.getRemoveIcon(OWLIcons.IMPORT)) {
        public void actionPerformed(ActionEvent e) {
            removeSelectedImport();
        }
    };

    private JTable table;

    private ImportsTableModel tableModel;


    public ImportsPanel(OWLOntology ontology) {

        this.owlModel = ontology.getOWLModel();

        tableModel = new ImportsTableModel(ontology);
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        setLayout(new BorderLayout(0, 10));
        JScrollPane scrollPane = new JScrollPane(table);
        LabeledComponent lc = new OWLLabeledComponent("Imports", scrollPane);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        lc.addHeaderButton(createImportedTripleStoreAction);
        lc.addHeaderButton(addImportAction);
        lc.addHeaderButton(showImportsTreeAction);
        lc.addHeaderButton(removeAction);
        removeAction.setEnabled(false);
        add(BorderLayout.CENTER, lc);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                enableActions();
                synchronizePrefixesPanel();
            }
        });
    }


    private void addImport() {
	    try {
		    ImportWizard wizard = new ImportWizard(this, owlModel);
		    if (wizard.execute() == Wizard.RESULT_FINISH) {
			    ImportHelper importHelper = new ImportHelper((JenaOWLModel) owlModel);
			    Collection prefixes = new ArrayList(owlModel.getNamespaceManager().getPrefixes());
				for(Iterator it = wizard.getImportData().getImportEntries().iterator(); it.hasNext(); ) {
					ImportEntry importEntry = (ImportEntry) it.next();
					Repository rep = importEntry.getRepositoryToAdd();
					if(rep != null) {
						owlModel.getRepositoryManager().addProjectRepository(importEntry.getRepository());
					}
					URI ontologyURI = importEntry.getOntologyURI();
					importHelper.addImport(ontologyURI);
				}
				importHelper.importOntologies();
				Collection addedPrefixes = new ArrayList(owlModel.getNamespaceManager().getPrefixes());
				addedPrefixes.removeAll(prefixes);
				if (addedPrefixes.size() > 0) {
					Component topLevelContainer = ProtegeUI.getTopLevelContainer(owlModel.getProject());
					AddedPrefixesTable.showDialog(topLevelContainer, owlModel, addedPrefixes);
				}
		    }
	    }
	    catch(Exception e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
	    }
    }


    private void createImportedTripleStore() {
        EmptyImportWizard wizard = new EmptyImportWizard(this, owlModel);
        wizard.execute();
    }


    public void dispose() {
        tableModel.dispose();
    }


    private void enableActions() {
        boolean rowSelected = table.getSelectedRow() >= 0 && table.getSelectedRow() < tableModel.getRowCount();
        removeAction.setEnabled(rowSelected);
    }


    private void removeSelectedImport() {
        int sel = table.getSelectedRow();
        if (sel >= 0 && sel < tableModel.getRowCount()) {
            if (OWLUtil.confirmSaveAndReload(owlModel.getProject())) {
                tableModel.deleteRow(sel);
                OWLUtil.saveAndReloadProject();
            }
        }
    }


    public void setPrefixesPanel(PrefixesPanel prefixesPanel) {
        this.prefixesPanel = prefixesPanel;
    }


    public void setSelectedNamespace(String namespace) {
        String uri = namespace;
        if (Jena.isNamespaceWithSeparator(namespace)) {
            uri = namespace.substring(0, namespace.length() - 1);
        }
        int row = tableModel.getURIRow(uri);
        if (row >= 0) {
            table.getSelectionModel().setSelectionInterval(row, row);
        }
        else {
            table.getSelectionModel().clearSelection();
        }
    }


    private void synchronizePrefixesPanel() {
        if (prefixesPanel != null) {
            int sel = table.getSelectedRow();
            if (sel >= 0 && sel < tableModel.getRowCount()) {
                String uri = tableModel.getURI(sel);
                prefixesPanel.setSelectedURI(uri);
            }
        }
    }


	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
		addImportAction.setEnabled(enabled);
		createImportedTripleStoreAction.setEnabled(enabled);
		showImportsTreeAction.setEnabled(enabled);
		removeAction.setEnabled(enabled);
	}


}
