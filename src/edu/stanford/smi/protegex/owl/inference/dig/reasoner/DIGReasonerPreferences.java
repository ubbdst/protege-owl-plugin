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

package edu.stanford.smi.protegex.owl.inference.dig.reasoner;

import java.io.OutputStream;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Feb 15, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGReasonerPreferences {
    private static DIGReasonerPreferences instance;

    private boolean treatErrorsAsWarnings;

    private OutputStream logOutputStream;


    private DIGReasonerPreferences() {
        treatErrorsAsWarnings = false;
    }


    public static DIGReasonerPreferences getInstance() {
        if (instance == null) {
            instance = new DIGReasonerPreferences();
        }
        return instance;
    }


    /**
     * Determines if DIG Errors should be regarded
     * as warnings.
     */
    public boolean isTreatErrorsAsWarnings() {
        return treatErrorsAsWarnings;
    }


    /**
     * Specifies whether DIG Errors should be regarded as
     * warnings
     *
     * @param b <code>true</code> if errors should be regarded
     *          as warnings, otherwise <code>false</code>.
     */
    public void setTreatErrorsAsWarnings(boolean b) {
        this.treatErrorsAsWarnings = b;
    }


    /**
     * Gets the output stream used for logging.
     */
    public OutputStream getLogOutputStream() {
        if (logOutputStream == null) {
            return System.out;
        }
        else {
            return logOutputStream;
        }
    }


    /**
     * Sets the output stream used for logging.
     */
    public void setLogOutputStream(OutputStream logOutputStream) {
        this.logOutputStream = logOutputStream;
    }
}

