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

package edu.stanford.smi.protegex.owl.ui.metadata;

import java.awt.Dimension;

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.widget.InstanceNameWidget;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.refactoring.RenameAcrossFilesAction;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 * @deprecated is no longer needed because the name is now shown in the header
 */
public class RDFResourceNameWidget extends InstanceNameWidget {

    private RenameAcrossFilesAction renameAcrossFilesAction = new RenameAcrossFilesAction();

    private JToolBar toolBar;


    public Dimension getPreferredSize() {
        return new Dimension(100, ComponentUtilities.getStandardRowHeight() / 2);
    }


    public JToolBar getToolBar() {
        return toolBar;
    }


    public void initialize() {
        super.initialize();

        getTextField().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (getTextField().getText().indexOf(' ') > 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            replaceSpaces();
                        }
                    });
                }
            }


            public void changedUpdate(DocumentEvent e) {
            }


            public void removeUpdate(DocumentEvent e) {
            }
        });
    }


    private void replaceSpaces() {
        String str = getTextField().getText();
        str = str.replace(' ', '_');
        int pos = getTextField().getCaretPosition();
        getTextField().setText(str);
        getTextField().setCaretPosition(pos);
    }


    public void setEditable(boolean b) {
        super.setEditable(b);
        // renameAcrossFilesAction.setEnabled(b);
    }


    public void setInstance(Instance instance) {
        super.setInstance(instance);
        renameAcrossFilesAction.initialize(this, (RDFResource) instance);
        renameAcrossFilesAction.setEnabled(renameAcrossFilesAction.isSuitable(this, (RDFResource) instance));
    }
}
