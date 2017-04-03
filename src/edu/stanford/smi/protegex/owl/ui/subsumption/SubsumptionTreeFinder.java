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

package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.BrowserComparator;
import edu.stanford.smi.protege.ui.Finder;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.*;

import javax.swing.*;
import java.util.*;

/**
 * Implementation of the Finder interface to locate and highlight classes whose
 * names match a given string.  This class has been generalized from Ray's
 * original ClsTreeFinder class to allow for searching in arbitrary subsumption
 * hierarchies.
 *
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class SubsumptionTreeFinder extends Finder {

    private KnowledgeBase kb;

    private Slot superclassesProperty;

    private JTree tree;


    public SubsumptionTreeFinder(KnowledgeBase kb, JTree tree, String description, Slot upProperty) {
        super(description);
        this.kb = kb;
        this.tree = tree;
        this.superclassesProperty = upProperty;
    }


    protected int getBestMatch(List matches, String text) {
        int result = Collections.binarySearch(matches, text, new BrowserComparator());
        if (result < 0) {
            int index = -(result + 1);
            if (index < matches.size()) {
                Instance instance = (Instance) matches.get(index);
                String browserText = instance.getBrowserText().toLowerCase();
                if (browserText.startsWith(text.toLowerCase())) {
                    result = index;
                }
            }
        }
        return result;
    }


    protected List getMatches(String text, int maxMatches) {
        Cls kbRoot = kb.getRootCls();
        List matches = getMatchingClses(text, maxMatches);
        LazyTreeRoot root = (LazyTreeRoot) tree.getModel().getRoot();
        Set rootNodes = new HashSet((Collection) root.getUserObject());
        if (rootNodes.size() != 1 || !equals(CollectionUtilities.getFirstItem(rootNodes), kbRoot)) {
            // Log.trace("removing bad matches", this, "getMatches");
            Iterator i = matches.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                boolean isValid = rootNodes.contains(cls);
                if (!isValid) {
                    Collection superclasses = kb.getDirectOwnSlotValuesClosure(cls, superclassesProperty);
                    isValid = superclasses.removeAll(rootNodes);
                }
                if (!isValid) {
                    i.remove();
                }
            }
        }
        Collections.sort(matches, new FrameComparator());
        return matches;
    }


    private List getMatchingClses(String text, int maxMatches) {
        if (!text.endsWith("*")) {
            text += '*';
        }
        Slot slot = kb.getDefaultClsMetaCls().getBrowserSlotPattern().getFirstSlot();
        List matches = new ArrayList(kb.getMatchingFrames(slot, null, false, text, maxMatches));
        Iterator i = matches.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (!(o instanceof Cls)) {
                i.remove();
            }
        }
        return matches;
    }

    /*
    private void _getPathToRoot(Cls cls, Collection clses) {
        Collection rootClses = (Collection) ((LazyTreeNode) tree.getModel().getRoot()).getUserObject();
        clses.add(cls);
        Collection superclasses = cls.getDirectSuperclasses();
        Cls parent = (Cls) CollectionUtilities.getFirstItem(superclasses);
        if (parent == null) {
            Log.error("no parents", this, "getPathToRoot", cls);
        } else if (rootClses.contains(parent)) {
            clses.add(parent);
        } else {
            getPathToRoot(parent, clses);
        }
    }
    */


    private void getVisiblePathToRoot(Cls cls, Collection path) {
        Collection roots = new ArrayList((Collection) ((LazyTreeNode) tree.getModel().getRoot()).getUserObject());
        Iterator i = roots.iterator();
        while (i.hasNext()) {
            Cls root = (Cls) i.next();
            if (!root.isVisible()) {
                i.remove();
            }
        }
        path.add(cls);
        boolean succeeded = getVisiblePathToRoot(cls, roots, path);
        if (!succeeded) {
            Log.getLogger().warning("No visible path found to " + cls);
        }
    }


    private boolean getVisiblePathToRoot(Cls cls, Collection roots, Collection path) {
        boolean found = false;
        Iterator i = cls.getDirectOwnSlotValues(superclassesProperty).iterator();
        while (i.hasNext() && !found) {
            Cls parent = (Cls) i.next();
            if (parent.isVisible()) {
                path.add(parent);
                if (roots.contains(parent)) {
                    found = true;
                }
                else {
                    found = getVisiblePathToRoot(parent, roots, path);
                }
                if (!found) {
                    path.remove(parent);
                }
            }
        }
        return found;
    }


    protected void select(Object o) {
        Cls cls = (Cls) o;

        WaitCursor cursor = new WaitCursor(this);
        try {
            ArrayList clses = new ArrayList();
            getVisiblePathToRoot(cls, clses);
            Collections.reverse(clses);
            ComponentUtilities.setSelectedObjectPath(tree, clses);
        }
        finally {
            cursor.hide();
        }
    }


    public String toString() {
        return "SubsumptionTreeFinder";
    }
}
