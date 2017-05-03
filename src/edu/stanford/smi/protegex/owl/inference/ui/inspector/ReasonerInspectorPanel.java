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

package edu.stanford.smi.protegex.owl.inference.ui.inspector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.ui.icons.InferenceIcons;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerPreferences;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 15, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerInspectorPanel extends JPanel {

    private DIGReasonerIdentity reasonerIdentity;

    private OWLModel kb;

    private JLabel reasonerDesc;

    private JTree tree;

    private DefaultMutableTreeNode languageNode;

    private DefaultMutableTreeNode tellNode;

    private DefaultMutableTreeNode askNode;

    private DefaultMutableTreeNode rootNode;

    private Action refreshAction = new AbstractAction("Refresh") {
        public void actionPerformed(ActionEvent e) {
            refreshReasonerIdentity();
        }
    };


    public ReasonerInspectorPanel(OWLModel kb) {
        this.kb = kb;

        reasonerDesc = new JLabel("Press refresh to collect information.");
        rootNode = new DefaultMutableTreeNode("Supported Elements");
        rootNode.add(languageNode = new DefaultMutableTreeNode("Language"));
        rootNode.add(tellNode = new DefaultMutableTreeNode("Tell"));
        rootNode.add(askNode = new DefaultMutableTreeNode("Ask"));
        tree = new JTree(rootNode);
        tree.setRowHeight(0);
        tree.setCellRenderer(new Renderer());
        JScrollPane sp = new JScrollPane(tree);
        setLayout(new BorderLayout(12, 12));
        add(reasonerDesc, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JButton(refreshAction), BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(400, 400));
    }


    protected void refreshReasonerIdentity() {
        try {
            ProtegeOWLReasoner protegeOWLReasoner = ReasonerManager.getInstance().getReasoner(kb);
            protegeOWLReasoner.setURL(ReasonerPreferences.getInstance().getReasonerURL());
            languageNode.removeAllChildren();
            tellNode.removeAllChildren();
            askNode.removeAllChildren();

            boolean connected = protegeOWLReasoner.isConnected();
            if (connected == true) {
                reasonerIdentity = protegeOWLReasoner.getDIGReasoner().getIdentity();
                reasonerDesc.setText(reasonerIdentity.getName() + " " +
                        reasonerIdentity.getVersion() + " (" +
                        reasonerIdentity.getMessage() + ")");

                Iterator it;

                it = reasonerIdentity.getSupportedLanguageElements().iterator();
                while (it.hasNext()) {
                    languageNode.add(new DefaultMutableTreeNode(it.next()));
                }

                it = reasonerIdentity.getSupportedTellElements().iterator();
                while (it.hasNext()) {
                    tellNode.add(new DefaultMutableTreeNode(it.next()));
                }

                it = reasonerIdentity.getSupportedAskElements().iterator();
                while (it.hasNext()) {
                    askNode.add(new DefaultMutableTreeNode(it.next()));
                }
                repaint();
            }
            else {
                reasonerDesc.setText("No reasoner detected");
            }
        }
        catch (Exception e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private class Renderer extends DefaultTreeCellRenderer {

        private Icon icon = InferenceIcons.getReasonerInspectorTreeIcon();


        public Icon getDefaultOpenIcon() {
            return icon;
        }


        public Icon getDefaultClosedIcon() {
            return icon;
        }


        public Icon getDefaultLeafIcon() {
            return icon;
        }


        public Icon getOpenIcon() {
            return icon;
        }


        public Icon getClosedIcon() {
            return icon;
        }


        public Icon getLeafIcon() {
            return icon;
        }
    }
}

