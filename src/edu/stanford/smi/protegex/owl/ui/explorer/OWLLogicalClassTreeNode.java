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
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLLogicalClassTreeNode extends ExplorerTreeNode {


    public OWLLogicalClassTreeNode(LazyTreeNode parent, OWLLogicalClass cls, ExplorerFilter filter) {
        super(parent, cls, filter);
    }


    protected List createChildObjects() {
        if (getRDFSClass() instanceof OWLComplementClass) {
            return Collections.singletonList(((OWLComplementClass) getRDFSClass()).getComplement());
        }
        else {
            return new ArrayList(((OWLNAryLogicalClass) getRDFSClass()).getOperands());
        }
    }


    public String toString(boolean expanded) {
        OWLLogicalClass logicalClass = (OWLLogicalClass) getRDFSClass();
        if (expanded) {
            return "" + logicalClass.getOperatorSymbol();
        }
        else {
            return logicalClass.getBrowserText();
        }
    }
}
