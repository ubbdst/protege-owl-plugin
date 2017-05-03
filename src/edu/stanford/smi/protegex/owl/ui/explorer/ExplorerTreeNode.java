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
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class ExplorerTreeNode extends LazyTreeNode {

    protected ExplorerFilter filter;

    private ClassListener listener = new ClassAdapter() {
        public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
            updateChildren();
        }


        public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
            updateChildren();
        }
    };


    public ExplorerTreeNode(LazyTreeNode parent, RDFSClass cls, ExplorerFilter filter) {
        super(parent, cls);
        cls.addClassListener(listener);
        this.filter = filter;
    }


    protected abstract List createChildObjects();


    protected LazyTreeNode createNode(Object o) {
        return ExplorerTreeNodeFactory.create(this, (RDFSClass) o, filter);
    }


    private void deleteChildren() {
        super.dispose();
    }


    public void dispose() {
        getRDFSClass().removeClassListener(listener);
        deleteChildren();
    }


    protected Collection getChildObjects() {
        List results = new ArrayList();
        Iterator it = createChildObjects().iterator();
        while (it.hasNext()) {
            RDFSClass childClass = (RDFSClass) it.next();
            if (filter.isValidChild(getRDFSClass(), childClass)) {
                results.add(childClass);
            }
        }
        return results;
    }


    protected int getChildObjectCount() {
        return getChildObjects().size();
    }


    public ExplorerTreeNode getChildNode(int index) {
        return (ExplorerTreeNode) getChildAt(index);
    }


    protected Comparator getComparator() {
        return null;
    }


    public RDFSClass getRDFSClass() {
        return (RDFSClass) getUserObject();
    }


    public String toString(boolean expanded) {
        return getRDFSClass().getBrowserText();
    }


    private void updateChildren() {
        reload();
    }
}
