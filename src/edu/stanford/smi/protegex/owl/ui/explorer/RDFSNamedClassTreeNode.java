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

package edu.stanford.smi.protegex.owl.ui.explorer;

import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFSNamedClassTreeNode extends ExplorerTreeNode {

    public RDFSNamedClassTreeNode(LazyTreeNode parent, RDFSNamedClass namedClass, ExplorerFilter filter) {
        super(parent, namedClass, filter);
    }


    protected List createChildObjects() {
        RDFSNamedClass namedClass = (RDFSNamedClass) getRDFSClass();
        List results = new ArrayList();
        Collection superclasses = namedClass.getSuperclasses(false);
        if (filter.getUseInferredSuperclasses() && namedClass instanceof OWLNamedClass) {
            superclasses = ((OWLNamedClass) namedClass).getInferredSuperclasses();
        }
        Iterator it = superclasses.iterator();
        while (it.hasNext()) {
            RDFSClass superclass = (RDFSClass) it.next();
            if (superclass instanceof OWLIntersectionClass && superclass.getSuperclasses(false).contains(namedClass)) {  // Equivalent
                OWLIntersectionClass intersectionClass = (OWLIntersectionClass) superclass;
                Iterator ot = intersectionClass.getOperands().iterator();
                while (ot.hasNext()) {
                    RDFSClass operand = (RDFSClass) ot.next();
                    if (!results.contains(operand)) {
                        results.add(operand);
                    }
                }
            }
            else {
                if (!results.contains(superclass)) {
                    results.add(superclass);
                }
            }
        }
        return results;
    }
}
