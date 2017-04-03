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

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.awt.*;

/**
 * The abstraction of objects that can create various dialogs
 * (comparable to JOptionPane etc).
 * <p/>
 * A static instance of this class can be acquired from
 * <CODE>ProtegeUI.getModalDialogFactory()</CODE>/
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ModalDialogFactory {

    public static final int OPTION_OK = 1;

    public static final int OPTION_YES = 2;

    public static final int OPTION_NO = 3;

    public static final int OPTION_CANCEL = 4;

    public static final int OPTION_CLOSE = 5;

    public static final int RESULT_ERROR = 6;

    public static final int MODE_OK_CANCEL = 11;

    public static final int MODE_YES_NO_CANCEL = 12;

    public static final int MODE_YES_NO = 13;

    public static final int MODE_CLOSE = 14;


    public static interface CloseCallback {

        boolean canClose(int result);
    }


    void attemptDialogClose(int result);


    int showDialog(Component parent, Component panel, String title, int mode);


    int showDialog(Component parent, Component panel, String title, int mode, CloseCallback callback);


    int showDialog(Component parent, Component panel,
                          String title, int mode, CloseCallback callback,
                          boolean enableCloseButton);


    int showDialog(Component parent, Component panel,
            String title, int mode, CloseCallback callback,
            boolean enableCloseButton, Component componentToFocus);
    
    
    int showDialog(Component parent, Component panel, String title, int mode, Component componentToFocus);
    
    int showConfirmCancelDialog(OWLModel owlModel, String message, String title);


    /**
     * Shows a dialog with Yes, No, and Cancel options.
     * @param parent
     * @param message
     * @param title
     * @return OPTION_YES, OPTION_NO or OPTION_CANCEL
     */
    int showConfirmCancelDialog(Component parent, String message, String title);


    boolean showConfirmDialog(OWLModel owlModel, String message, String title);


    boolean showConfirmDialog(Component parent, String message, String title);


    void showErrorMessageDialog(OWLModel owlModel, String message);


    void showErrorMessageDialog(OWLModel owlModel, String message, String title);


    void showErrorMessageDialog(Component parent, String message);


    void showErrorMessageDialog(Component parent, String message, String title);


    String showInputDialog(OWLModel owlModel, String message, String initialValue);


    String showInputDialog(Component parent, String message, String initialValue);


    void showMessageDialog(OWLModel owlModel, String message);


    void showMessageDialog(OWLModel owlModel, String message, String title);


    void showMessageDialog(Component parent, String message);


    void showMessageDialog(Component parent, String message, String title);


    void showThrowable(OWLModel owlModel, Throwable t);
}
