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

package edu.stanford.smi.protegex.owl.inference.dig.exception;

import java.util.ArrayList;
import java.util.Collection;


/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 14, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGErrorException extends DIGReasonerException {

    private ArrayList errorList;


    public DIGErrorException(ArrayList errorList) {
        super(((DIGError) (errorList.get(0))).getMessage());

        this.errorList = errorList;
    }


    /**
     * Gets the exception code associated with this
     * DIGErrorException
     *
     * @return An integer exception code.
     */
    public int getErrorCode(int index) {
        return ((DIGError) errorList.get(index)).getErrorCode();
    }


    /**
     * Gets the exception message of the specified
     * exception.
     *
     * @param index The index of the exception
     * @return The exception message
     */
    public String getMessage(int index) {
        return ((DIGError) errorList.get(index)).getMessage();
    }


    /**
     * Gets the <code>DIGError</code> object
     * associated with this exception.
     */
    public DIGError getDIGError(int index) {
        return (DIGError) errorList.get(index);
    }


    /**
     * Gets the number of DIGErrors
     */
    public int getNumberOfErrors() {
        return errorList.size();
    }


    public Collection getErrors() {
        return errorList;
    }
}

