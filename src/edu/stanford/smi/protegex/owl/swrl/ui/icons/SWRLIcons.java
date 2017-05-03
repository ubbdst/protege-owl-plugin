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

package edu.stanford.smi.protegex.owl.swrl.ui.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A singleton that provides access to the SWRL specific icons.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLIcons {

    public final static String IMP = "SWRLImp";

    public static final String VARIABLE = "SWRLVariable";


    public static ImageIcon getImpIcon() {
        return getImageIcon("SWRLImp");
    } // getImpIcon


    public static ImageIcon getOpenBrackets() {
        return getImageIcon("OpenBrackets");
    } // getOpenBrackets


    public static ImageIcon getCloseBrackets() {
        return getImageIcon("CloseBrackets");
    } // getCloseBrackets


    public static ImageIcon getOpenParenthesis() {
        return getImageIcon("OpenParenthesis");
    } // getOpenParenthesis


    public static ImageIcon getCloseParenthesis() {
        return getImageIcon("CloseParenthesis");
    } // getCloseParenthesis


    public static Icon getAndIcon() {
        return getImageIcon("Ugly");
    } // getAndIcon


    public static Icon getInsertImpIcon() {
        return getImageIcon("InsertImp");
    } // getInsertImpIcon


    public static Icon getVariableIcon() {
        return getImageIcon(VARIABLE);
    } // getVariableIcon


    public static Icon getBuiltinIcon() {
        return getImageIcon("SWRLBuiltin");
    } // getBuiltinIcon


    public static Icon getSameAsIcon() {
        return getImageIcon("SWRLSameAs");
    } // getSameAsIcon


    public static Icon getDifferentFromIcon() {
        return getImageIcon("SWRLDifferentFrom");
    } // getDifferentFromIcon


    public static ImageIcon getImageIcon(String name) {
        return OWLIcons.getImageIcon(name, SWRLIcons.class);
    }


    public static Icon getInsertAndIcon() {
        return getImageIcon("InsertAnd");
    }


    public static Icon getImpsIcon() {
      return getImageIcon("SWRLImps");
  }

    public static Icon getOWL2RLIcon() {
      return getImageIcon("OWL2RL");
  }
}
