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

package edu.stanford.smi.protegex.owl.ui.matrix.cls;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.matrix.DependentMatrixFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SubclassesMatrixFilter implements DependentMatrixFilter {

    private RDFSNamedClass parentClass;


    public SubclassesMatrixFilter(RDFSNamedClass parentClass) {
        this.parentClass = parentClass;
    }


    public Collection getInitialValues() {
        Collection results = new ArrayList();
        Iterator it = parentClass.getSubclasses(true).iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof RDFSNamedClass) {
                results.add(next);
            }
        }
        results.remove(parentClass);
        return results;
    }


    public String getName() {
        return "Subclasses of " + parentClass.getBrowserText();
    }


    public boolean isDependentOn(RDFResource instance) {
        return parentClass.equals(instance);
    }


    public boolean isSuitable(RDFResource instance) {
        return instance instanceof RDFSNamedClass &&
                instance.isVisible() &&
                (instance.isEditable() || instance.isIncluded()) &&
                ((RDFSNamedClass) instance).isSubclassOf(parentClass);
    }
}
