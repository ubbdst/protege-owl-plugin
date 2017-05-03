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

package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLLogicalClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A basic implementation of OWLLogicalClass.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLLogicalClass extends AbstractOWLAnonymousClass
        implements OWLLogicalClass {


    public AbstractOWLLogicalClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    AbstractOWLLogicalClass() {
    }


    public Collection getDependingClasses() {
        Collection result = new ArrayList();
        addAnonymousClses(result, getOperands());
        return result;
    }


    public void getNestedNamedClasses(Set set) {
        for (Iterator it = getOperands().iterator(); it.hasNext();) {
            Cls operand = (Cls) it.next();
            if (operand instanceof RDFSClass) {
                ((RDFSClass) operand).getNestedNamedClasses(set);
            }
        }
    }


    public abstract Collection getOperands();
}
