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
import edu.stanford.smi.protege.ui.ClsesTreeDragSourceListener;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import javax.swing.tree.TreePath;
import java.util.Collection;
import java.util.Iterator;

/**
 * A special handler of drag and drop for OWL classes: The core Protege one would
 * remove named superclasses even if they were part of an equivalent intersection.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLClassesTreeDragSourceListener extends ClsesTreeDragSourceListener {

    public void doMove(Collection paths) {
        Iterator i = paths.iterator();
        while (i.hasNext()) {
            TreePath path = (TreePath) i.next();
            LazyTreeNode draggedNode = (LazyTreeNode) path.getLastPathComponent();
            LazyTreeNode draggedNodeParent = (LazyTreeNode) draggedNode.getParent();
            Cls draggedCls = (Cls) draggedNode.getUserObject();
            Cls draggedClsParent = (Cls) draggedNodeParent.getUserObject();
            if (draggedCls instanceof OWLNamedClass) {
                OWLNamedClass namedClass = (OWLNamedClass) draggedCls;
                if (namedClass.isDefinedClass()) {
                    for (Iterator it = namedClass.getEquivalentClasses().iterator(); it.hasNext();) {
                        RDFSClass equi = (RDFSClass) it.next();
                        if (equi instanceof OWLIntersectionClass) {
                            if (((OWLIntersectionClass) equi).hasOperandWithBrowserText(draggedClsParent.getBrowserText())) {
                                return;
                            }
                        }
                    }
                }
            }
            draggedCls.removeDirectSuperclass(draggedClsParent);
        }
    }
}
