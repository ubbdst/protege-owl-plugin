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

package edu.stanford.smi.protegex.owl.model.classparser.manchester;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ManchesterOWLParserUtil {

    private static boolean lowerCase = true;


    public static boolean isLowerCase() {
        return lowerCase;
    }


    public static void setLowerCase(boolean lowerCase) {
        ManchesterOWLParserUtil.lowerCase = lowerCase;
    }


    public static String getAndKeyword() {
        return getKeyword("and");
    }


    public static String getOrKeyword() {
        return getKeyword("or");
    }


    public static String getNotKeyword() {
        return getKeyword("not");
    }


    public static String getSomeKeyword() {
        return getKeyword("some");
    }


    public static String getAllKeyword() {
        return getKeyword("only");
    }


    public static String getHasKeyword() {
        return getKeyword("has");
    }


    public static String getMinKeyword() {
        return getKeyword("min");
    }


    public static String getExactKeyword() {
        return getKeyword("exactly");
    }


    public static String getMaxKeyword() {
        return getKeyword("max");
    }


    private static String getKeyword(String s) {
        if (lowerCase == false) {
            s = s.toUpperCase();
        }
        return s;
    }
}

