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

package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter;

import java.util.Comparator;

public class NativeValueComparator implements Comparator {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int compare(Object o1, Object o2) {
        if (!o1.getClass().equals(o2.getClass())) {
            return o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
        }
        else if (o1 instanceof Comparable) { // this case covers RDFResource, RDFSLiteral and many other things as well.
            return ((Comparable) o1).compareTo(o2);
        }
        else {  // WARNING! Note that if the following gives equality for two unequal values then a value will be removed from the saved ontology.
                //          I don't know of an ideal fix for this
                //      There are a limited number of value types that can be achieved by the object o1.
            return o1.toString().compareTo(o2.toString());
        }
    }

}
