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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

public class ClassTreeWithBrowserTextRoot extends LazyTreeRoot {

    public ClassTreeWithBrowserTextRoot(Collection<? extends Cls> roots, boolean isSorted) {
        super(FrameWithBrowserText.getFramesWithBrowserText(filter(roots)), isSorted);
    }
    
    public ClassTreeWithBrowserTextRoot(Cls rootCls, boolean isSorted) {
        super(new FrameWithBrowserText(rootCls, rootCls.getBrowserText(), rootCls.getDirectTypes(), 
        		ProtegeUI.getPotentialIconName(rootCls)), isSorted);
    }

    @Override
    public LazyTreeNode createNode(Object o) {
        return new ClassTreeWithBrowserTextNode(this, (FrameWithBrowserText) o);
    }

    @Override
    protected Comparator getComparator() {
        // TODO Auto-generated method stub
        return null;
    }  
    
    private static Collection filter(Collection roots) {
        Collection visibleRoots = new ArrayList(roots);
        Cls firstRoot = (Cls) CollectionUtilities.getFirstItem(roots);
        if (firstRoot != null && !firstRoot.getProject().getDisplayHiddenClasses()) {
            Iterator i = visibleRoots.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                if (!cls.isVisible()) {
                    i.remove();
                }
            }
        }
        return visibleRoots;
    }

}
