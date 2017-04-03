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

package edu.stanford.smi.protegex.owl.ui.triplestore;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * A JPanel that can be used to switch the active TripleStore in an OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TripleStoreSelectionPanel extends JPanel {

    private OWLModel owlModel;

    private JTable table;

    private TripleStoreTableModel tableModel;


    public TripleStoreSelectionPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        tableModel = new TripleStoreTableModel(owlModel);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout(0, 16));
        LabeledComponent lc = new LabeledComponent("Ontologies", scrollPane);
        add(BorderLayout.CENTER, lc);
        setPreferredSize(new Dimension(700, Math.min(700,
                tableModel.getRowCount() * table.getRowHeight() + 240)));
        scrollPane.getViewport().setBackground(table.getBackground());
        setColumnWidth(TripleStoreTableModel.COL_ACTIVE, 50);
        setColumnWidth(TripleStoreTableModel.COL_URI, 400);
        setColumnWidth(TripleStoreTableModel.COL_EDITABLE, 50);
        Component helpPanel = OWLUI.createHelpPanel(HELP_TEXT, "What is the Active Ontology?");
        add(BorderLayout.SOUTH, helpPanel);
    }


    private void setColumnWidth(int column, int width) {
        TableColumn col = table.getColumnModel().getColumn(column);
        //col.setMinWidth(width);
        col.setPreferredWidth(width);
    }


    public static void showDialog(OWLModel owlModel) {
        TripleStoreSelectionPanel panel = new TripleStoreSelectionPanel(owlModel);
        ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(owlModel.getProject()), panel,
                "Active Ontology", ModalDialogFactory.MODE_CLOSE);
    }


    private final static String HELP_TEXT =
            "<p>If the main ontology imports other ontologies, this panel indicates whether or not the " +
                    "various imports are editable. If an imported ontology is editable then it can be set to " +
                    "be the 'Active Ontology', which is the ontology that any edits are applied to.</p>" +
                    "<p>Whether or not an imported ontology is editable depends on where the ontology was " +
                    "imported from.  For example, ontologies that are imported from the local file system " +
                    "will probably be editable, where as ontologies that are imported from the web will not " +
                    "be editable.</p>";
}
