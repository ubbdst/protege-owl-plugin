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

package edu.stanford.smi.protegex.owl.ui.repository;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 19, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryEntryLabel extends JLabel {

    private URI name;

    private Action copyToClipboardAction;

    private Action downloadToLocalFileAction;

    private OWLModel model;


    public RepositoryEntryLabel(OWLModel model, URI name, String locationDescription, boolean imported) {
        this.name = name;
        this.model = model;
        setIconTextGap(20);
        String colour = "";
        if (imported) {
            colour = "rgb(90, 50, 180)";
        }
        else {
            colour = "rgb(20, 20, 20)";
        }
        String html = "<html><body>&nbsp;&nbsp;<font color=\"" + colour + "\">" + name + "</font> <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"rgb(100, 100, 100)\" size=\"-2\">(" + locationDescription + ")</font></body></html>";
        setText(html);
        setBackground(Color.WHITE);
        setOpaque(true);
        copyToClipboardAction = new AbstractAction("Copy to clipboard") {
            public void actionPerformed(ActionEvent e) {
                StringSelection sel = new StringSelection(RepositoryEntryLabel.this.name.toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
            }
        };
        downloadToLocalFileAction = new AbstractAction("Copy to local file...") {
            public void actionPerformed(ActionEvent e) {
                copyToLocalFile();
            }
        };
        addMouseListener(new MouseAdapter() {

            private void showPopupMenu(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(copyToClipboardAction);
                    menu.add(downloadToLocalFileAction);
                    menu.show(RepositoryEntryLabel.this, e.getX(), e.getY());
                }
            }


            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e);
            }


            public void mouseClicked(MouseEvent e) {
                showPopupMenu(e);
            }
        });
    }


    private void copyToLocalFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                RepositoryUtil.createImportLocalCopy(model, name, f);
            }
            catch (OntologyLoadException e) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(model, e.getMessage());
            }
        }
    }


}

