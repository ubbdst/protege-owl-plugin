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

package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This action is required for showing the Protege OWL
 * restriction syntax within a JDialog.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class SyntaxHelpAction extends AbstractAction {

    public SyntaxHelpAction() {
        super("Prot\u00E9g\u00E9-OWL Syntax...", OWLIcons.getImageIcon("Help"));
    }


    public void actionPerformed(ActionEvent arg0) {
        JDialog dialog = createHelpDialog();
        dialog.setModal(false);
    }


    private JDialog createHelpDialog() {

        JFrame parent = new JFrame();
        JDialog dialog = new JDialog(parent);
        dialog.setTitle("Prot\u00E9g\u00E9-OWL Syntax");
        JLabel label = new JLabel(OWLIcons.getImageIcon("CompactSyntax.png"));
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(BorderLayout.CENTER, label);
        dialog.show();
        int w = dialog.getWidth() - contentPane.getWidth();
        int h = dialog.getHeight() - contentPane.getHeight();
        Dimension pref = contentPane.getPreferredSize();
        dialog.setSize(pref.width + w, pref.height + h);
        contentPane.doLayout();
        contentPane.repaint();
        dialog.setVisible(false);
        dialog.setVisible(true);

        return dialog;
    }
}
