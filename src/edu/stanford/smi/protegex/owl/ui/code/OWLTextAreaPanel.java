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

package edu.stanford.smi.protegex.owl.ui.code;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory.CloseCallback;

import javax.swing.*;
import java.awt.*;

/**
 * A panel which can be used to edit an OWL expression in a multi-line dialog.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTextAreaPanel extends JPanel implements ModalDialogFactory.CloseCallback {

    private OWLModel owlModel;

    private OWLSymbolPanel symbolPanel;

    private OWLTextArea textArea;


    OWLTextAreaPanel(OWLModel anOWLModel, RDFSClass inputClass) {
        this.owlModel = anOWLModel;
        symbolPanel = new OWLSymbolPanel(anOWLModel, false);
        textArea = new OWLTextArea(anOWLModel, symbolPanel) {
            protected void checkExpression(String text) throws Throwable {
                owlModel.getOWLClassDisplay().getParser().checkClass(owlModel, text);
            }
        };
        textArea.setPreferredSize(new Dimension(600, 300));
        if (inputClass != null) {
            textArea.setText(inputClass);
        }
        symbolPanel.setSymbolEditor(textArea);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, new JScrollPane(textArea));
        add(BorderLayout.SOUTH, symbolPanel);
    }


    public boolean canClose(int result) {
        if (result == ModalDialogFactory.OPTION_OK) {
            String text = textArea.getText();
            if (text.length() == 0) {
                return false;
            }
            else {
                try {
                    owlModel.getOWLClassDisplay().getParser().checkClass(owlModel, text);
                    return true;
                }
                catch (Throwable ex) {
                    symbolPanel.displayError(ex);
                    return false;
                }
            }
        }
        else {
            return true;
        }
    }

    /*RDFSClass getResultAsCls() {
       try {
           String uniCodeText = textArea.getText();
           String text = OWLTextFormatter.getParseableString(uniCodeText);
           OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
           return parser.parseClass(owlModel, text);
       }
       catch (Exception ex) {
           return null;
       }
   } */


    String getResultAsString() {
        return textArea.getText();
    }


    public static String showEditDialog(Component parent, OWLModel owlModel, RDFSClass input) {
        OWLTextAreaPanel panel = new OWLTextAreaPanel(owlModel, input);
        String title = "Edit OWL Expression";
        if (ProtegeUI.getModalDialogFactory().showDialog(parent, panel, title, ModalDialogFactory.MODE_OK_CANCEL, (CloseCallback)panel) == ModalDialogFactory.OPTION_OK) {
            return panel.getResultAsString();
        }
        else {
            return null;
        }
    }
}
