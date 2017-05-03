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

package edu.stanford.smi.protegex.owl.ui.dialogs;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultModalDialogFactory extends AbstractModalDialogFactory {

    public void attemptDialogClose(int result) {
        ModalDialog.attemptDialogClose(result);
    }


    private Component getParentComponent(OWLModel owlModel) {
        if (owlModel == null) {
            return Application.getMainWindow();
        }
        else {
            Component comp = ProtegeUI.getProjectView(owlModel.getProject());
            if (comp == null) {
                comp = Application.getMainWindow();
            }
            return comp;
        }
    }


    public int showConfirmCancelDialog(OWLModel owlModel, String message, String title) {
        Component parentComponent = getParentComponent(owlModel);
        return showConfirmCancelDialog(parentComponent, message, title);
    }


    public int showConfirmCancelDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
        if(result == JOptionPane.NO_OPTION) {
            return ModalDialogFactory.OPTION_NO;
        }
        else if(result == JOptionPane.YES_OPTION) {
            return ModalDialogFactory.OPTION_YES;
        }
        else {
            return ModalDialogFactory.OPTION_CANCEL;
        }
    }


    public boolean showConfirmDialog(OWLModel owlModel, String message, String title) {
        Component parentComponent = getParentComponent(owlModel);
        return showConfirmDialog(parentComponent, message, title);
    }


    public boolean showConfirmDialog(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }


    public int showDialog(Component parent, Component panel, String title, int mode, CloseCallback callback, 
    		boolean enableCloseButton, Component componentToFocus) {
        return ModalDialog.showDialog(parent, panel, title, mode, callback, enableCloseButton, componentToFocus);
    }


    public void showErrorMessageDialog(OWLModel owlModel, String message, String title) {
        Component parentComponent = getParentComponent(owlModel);
        showErrorMessageDialog(parentComponent, message, title);
    }


    public void showErrorMessageDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }


    public String showInputDialog(OWLModel owlModel, String message, String initialValue) {
        Component parentComponent = getParentComponent(owlModel);
        return showInputDialog(parentComponent, message, initialValue);
    }


    public String showInputDialog(Component parent, String message, String initialValue) {
        if(initialValue == null) {
            initialValue = "";
        }
        return JOptionPane.showInputDialog(parent, message, initialValue);
    }


    public void showMessageDialog(OWLModel owlModel, String message, String title) {
        Component parentComponent = getParentComponent(owlModel);
        showMessageDialog(parentComponent, message, title);
    }


    public void showMessageDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
