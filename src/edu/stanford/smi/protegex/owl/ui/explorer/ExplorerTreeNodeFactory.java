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
import edu.stanford.smi.protegex.owl.model.OWLLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExplorerTreeNodeFactory {

    public static ExplorerTreeNode create(LazyTreeNode parent, RDFSClass cls, ExplorerFilter filter) {
        if (cls instanceof RDFSNamedClass) {
            return new RDFSNamedClassTreeNode(parent, (RDFSNamedClass) cls, filter);
        }
        else if (cls instanceof OWLQuantifierRestriction) {
            return new OWLQuantifierRestrictionTreeNode(parent, (OWLQuantifierRestriction) cls, filter);
        }
        else if (cls instanceof OWLLogicalClass) {
            return new OWLLogicalClassTreeNode(parent, (OWLLogicalClass) cls, filter);
        }
        else {
            return new LeafExplorerTreeNode(parent, cls, filter);
        }
    }
}
