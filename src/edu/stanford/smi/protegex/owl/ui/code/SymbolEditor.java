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

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface SymbolEditor {

    /**
     * Ends the editing process and assigns the expression.
     * If the expression is invalid, then it calls <CODE>displayError</CODE>.
     */
    void assignExpression();


    /**
     * Performs a backspace operation.
     */
    void backspace();


    /**
     * Cancels the editing process without assigning a value.
     */
    void cancelEditing();


    /**
     * Checks the syntax of the current input and displays the error.
     */
    void displayError();


    /**
     * Gets the currently entered text.
     *
     * @return the text (raw)
     */
    String getText();


    /**
     * Inserts some text at the caret position.
     *
     * @param text the text to insert
     */
    void insertText(String text);


    /**
     * Inserts some text at the caret position and specifies the new caret position within
     * the inserted text.
     *
     * @param text        the text to insert
     * @param caretOffset the caret offset within the text
     */
    void insertText(String text, int caretOffset);
}
