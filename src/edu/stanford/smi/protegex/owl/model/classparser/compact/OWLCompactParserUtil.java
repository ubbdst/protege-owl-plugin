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

package edu.stanford.smi.protegex.owl.model.classparser.compact;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLCompactParserUtil {

    /**
     * Detects usages of xsd: types as expression fillers, and makes sure that
     * they start with xsd:.  This is needed to simplify the grammar which
     * grew historically.  Eventually this should be cleaned up.
     *
     * @param owlModel the OWLModel
     * @param text     the text to preprocess
     * @return the next text or the same if no xsd: types were found
     */
    public static String preprocess(OWLModel owlModel, String text) {
        boolean changed = true;
        while (changed) {
            changed = false;
            int next = text.indexOf('*');
            if (next < 0) {
                next = text.indexOf('?');
            }
            if (next >= 0) {
                String rest = text.substring(next + 1);
                String newRest = preprocessFiller(owlModel, rest);
                if (newRest != rest) {
                    String start = text.substring(0, next + 1);
                    text = start + newRest;
                    changed = true;
                }
            }
        }
        return text;
    }


    public static String preprocessFiller(OWLModel owlModel, String text) {
        text = text.trim();
        if (text.length() > 0 && text.charAt(0) >= 'a' && text.charAt(0) <= 'z') {
            int index = 1;
            while (index < text.length() && text.charAt(index) >= 'a' && text.charAt(index) <= 'z') {
                index++;
            }
            String sub = text.substring(0, index);
            if (sub.equals("xsd")) {
                return text;
            }
            else {
                String name = "xsd:" + sub;
                if (owlModel.getRDFSDatatypeByName(name) != null) {
                    return name + text.substring(index);
                }
            }
        }
        return text;
    }
}
