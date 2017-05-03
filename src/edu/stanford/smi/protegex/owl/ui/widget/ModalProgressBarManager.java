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

package edu.stanford.smi.protegex.owl.ui.widget;

import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.creator.ProgressDisplay;

/**
 * An object managing a ModalProgressBarDialog serving as ProgressDisplay.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ModalProgressBarManager implements ProgressDisplay {

    private ModalProgressBarDialog dialog;


    public ModalProgressBarManager(String title) {
        JFrame frame = (JFrame) Application.getMainWindow();
        dialog = new ModalProgressBarDialog(0, 100, frame, title);
        Thread thread = new Thread(dialog, "ModalProgressBar");
        thread.start();
        try {
            Thread.sleep(1);
        }
        catch (InterruptedException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void setProgressText(String str) {
        dialog.setLabel(str);
        try {
            Thread.sleep(1);
        }
        catch (InterruptedException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void setProgressValue(double value) {
        if (dialog.setValueRelative(value)) {
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }
    }


    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.setVisible(true);
            }
        });
    }


    public void stop() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ComponentUtilities.dispose(dialog);
            }
        });
    }
}
