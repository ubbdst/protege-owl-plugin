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

package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-Feb-2006
 */
public class HiddenClassTreeNode extends ClassTreeNode {

    public HiddenClassTreeNode(HiddenClassTreeRoot hiddenClassTreeRoot, Cls cls) {
        super(hiddenClassTreeRoot, cls);
    }


    protected LazyTreeNode createNode(Object o) {
        return new ClassTreeNode(this, (Cls) o);
    }

    protected int getChildObjectCount() {
        return getChildObjects().size();
    }

    protected Collection getChildObjects() {
        Cls cls = getCls();
        List result = new ArrayList(HiddenClassTreeRoot.filter(cls.getDirectSubclasses()));
        // Remove all equivalent classes that have other superclasses as well
        if (cls instanceof OWLNamedClass) {
            Iterator equis = ((OWLNamedClass) cls).getEquivalentClasses().iterator();
            while (equis.hasNext()) {
                RDFSClass equi = (RDFSClass) equis.next();
                if (equi instanceof OWLNamedClass && equi.getSuperclassCount() > 1) {
                    result.remove(equi);
                }
            }
        }
        return result;
    }
}
