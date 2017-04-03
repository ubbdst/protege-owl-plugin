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

import java.awt.Component;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory.CloseCallback;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractModalDialogFactory implements ModalDialogFactory {

    private static final String ERROR = "Error";

    private static final String INFORMATION = "Information";


    public int showDialog(Component parent, Component panel, String title, int mode) {
        return showDialog(parent, panel, title, mode, (CloseCallback)null);
    }

    public int showDialog(Component parent, Component panel, String title, int mode, CloseCallback callback) {
        return showDialog(parent, panel, title, mode, callback, true);
    }

    public int showDialog(Component parent, Component panel,  String title, int mode, CloseCallback callback, boolean enableCloseButton) {
    	return showDialog(parent, panel, title, mode, callback, enableCloseButton, null);
    }
    
    public int showDialog(Component parent, Component panel, String title, int mode, Component componentToFocus) {
    	return showDialog(parent, panel, title, mode, null, true, componentToFocus);
    }
    
    public void showErrorMessageDialog(OWLModel owlModel, String message) {
        showErrorMessageDialog(owlModel, message, ERROR);
    }


    public void showErrorMessageDialog(Component parent, String message) {
        showErrorMessageDialog(parent, message, ERROR);
    }


    public void showMessageDialog(OWLModel owlModel, String message) {
        showMessageDialog(owlModel, message, INFORMATION);
    }


    public void showMessageDialog(Component parent, String message) {
        showMessageDialog(parent, message, INFORMATION);
    }


    public void showThrowable(OWLModel owlModel, Throwable t) {
        Log.getLogger().log(Level.SEVERE, "Exception caught", t);
        showErrorMessageDialog(owlModel, "Unexpected Error - please see console for stack trace.\n" + t.getMessage(), "Unexpected Error");
    }
}
