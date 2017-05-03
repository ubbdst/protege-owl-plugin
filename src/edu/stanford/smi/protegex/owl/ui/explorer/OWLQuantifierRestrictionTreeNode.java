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
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import java.util.Collections;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLQuantifierRestrictionTreeNode extends ExplorerTreeNode {

    public OWLQuantifierRestrictionTreeNode(LazyTreeNode parent,
                                            OWLQuantifierRestriction restriction,
                                            ExplorerFilter filter) {
        super(parent, restriction, filter);
    }


    protected List createChildObjects() {
        RDFResource filler = ((OWLQuantifierRestriction) getRDFSClass()).getFiller();
        if (filler instanceof RDFSClass) {
            return Collections.singletonList(filler);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    public String toString(boolean expanded) {
        String str = super.toString(expanded);
        if (expanded) {
            OWLRestriction restriction = (OWLRestriction) getRDFSClass();
            return /*str.substring(0, 2) + */restriction.getOnProperty().getBrowserText();
        }
        else {
            return str;
        }
    }
}
