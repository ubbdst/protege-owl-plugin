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

package edu.stanford.smi.protegex.owl.ui.resourceselection;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Log;

import java.util.Comparator;

/**
 * A Comparator for Frame instances that compares the lower case browser texts.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceIgnoreCaseComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        if (o1 instanceof Frame && o2 instanceof Frame) {
            String name1 = ((Frame) o1).getBrowserText().toLowerCase();
            String name2 = ((Frame) o2).getBrowserText().toLowerCase();
            return name1.compareTo(name2);
        }
        else {
            Log.getLogger().severe("[ResourceIgnoreCareComparator]  Invalid types " + o1 + ", " + o2);
            return 0;
        }
    }
}
