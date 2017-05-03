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

package edu.stanford.smi.protegex.owl.ui.explorer.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.stanford.smi.protege.ui.SlotSubslotNode;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.properties.OWLPropertySubpropertyRoot;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A JPanel to select a valid RDFProperty for a DefaultExplorerFilter.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ValidPropertyPanel extends JPanel {

    private DefaultExplorerFilter filter;

    private JTree tree;


    public ValidPropertyPanel(OWLModel owlModel, DefaultExplorerFilter filter) {
        this.filter = filter;
        tree = new SelectableTree(null, new OWLPropertySubpropertyRoot(owlModel));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                updateFilter();
            }
        });
        tree.setCellRenderer(new ResourceRenderer());
        OWLLabeledComponent lc = new OWLLabeledComponent("Show only Restrictions on Property", new JScrollPane(tree));
        lc.addHeaderButton(new AbstractAction("Clear selection", OWLIcons.getDeleteIcon()) {
            public void actionPerformed(ActionEvent e) {
                tree.clearSelection();
            }
        });
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        setPreferredSize(new Dimension(ValidClassesPanel.PREFERRED_WIDTH, 260));
    }


    private void updateFilter() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            filter.setValidProperty(null);
        }
        else {
            SlotSubslotNode node = (SlotSubslotNode) path.getLastPathComponent();
            RDFProperty property = (RDFProperty) node.getUserObject();
            filter.setValidProperty(property);
        }
    }
}
