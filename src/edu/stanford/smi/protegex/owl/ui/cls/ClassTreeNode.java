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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.event.ClsAdapter;
import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.event.ClsListener;
import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.ui.ParentChildNodeComparator;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * Tree node that contains the superclass-subclass relations.
 *
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassTreeNode extends LazyTreeNode {

    private ClsListener _clsListener = new ClsAdapter() {
        public void directSubclassAdded(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            if (event.getSubclass().isVisible()) {
                childAdded(event.getSubclass());
            }
        }


        public void directSubclassRemoved(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            if (event.getSubclass().isVisible()) {
                childRemoved(event.getSubclass());
            }
        }


        public void directSubclassMoved(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
            
        	Cls subclass = event.getSubclass();
            int index = (new ArrayList(getChildObjects())).indexOf(subclass);
            if (index != -1) {
                childRemoved(subclass);
                childAdded(subclass, index);
            }
        }


        public void directInstanceAdded(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            notifyNodeChanged();
        }


        public void directInstanceRemoved(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            notifyNodeChanged();
        }


        public void templateFacetValueChanged(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            notifyNodeChanged();
        }


        public void directSuperclassAdded(ClsEvent event) {
        	if (event.isReplacementEvent()) return;
        	
            notifyNodeChanged();
        }
    };

    private FrameListener _frameListener = new FrameAdapter() {
    	@Override
    	public void frameReplaced(FrameEvent event) {
    		Frame oldFrame = event.getFrame();
    		Frame newFrame = event.getNewFrame();
    		Cls cls = getCls();
    		if (cls != null && cls.equals(oldFrame)) {
    			reload(newFrame);
    			//the commented lines should provide support for 
    			//inserting in order, but they don't work right
    			//in all cases. We need to figure this out later.
    			//((LazyTreeNode)getParent()).childRemoved(oldFrame);    			
    			//((LazyTreeNode)getParent()).childRemoved(newFrame);
    			//((LazyTreeNode)getParent()).childAdded(newFrame);
    		}
    	}
    	
        public void browserTextChanged(FrameEvent event) {
        	if (event.isReplacementEvent()) return;
            notifyNodeChanged();
        }


        public void ownSlotValueChanged(FrameEvent event) {
        	if (event.isReplacementEvent()) return;
            if (event.getSlot().getName().equals(Model.Slot.DIRECT_TYPES)) {
                // refresh the stale cls reference
                Cls cls = getCls().getKnowledgeBase().getCls(getCls().getName());
                reload(cls);
            }
            else {
                notifyNodeChanged();
            }
        }


        public void visibilityChanged(FrameEvent event) {
        	if (event.isReplacementEvent()) return;
            notifyNodeChanged();
        }
    };


    public ClassTreeNode(LazyTreeNode parentNode, Cls parentCls) {
        super(parentNode, parentCls, parentNode.isSorted());
        parentCls.addClsListener(_clsListener);
        parentCls.addFrameListener(_frameListener);
    }


    protected LazyTreeNode createNode(Object o) {
        return new ClassTreeNode(this, (Cls) o);
    }


    protected void dispose() {
        super.dispose();
        getCls().removeClsListener(_clsListener);
        getCls().removeFrameListener(_frameListener);
    }


    protected int getChildObjectCount() {
        if(showHidden()) {
            return getCls().getDirectSubclassCount();
        }
        else {
            return getChildObjects().size();
        }
    }


    @SuppressWarnings("unchecked")
    protected Collection getChildObjects() {
        if (showHidden()) {
            return new HashSet(getCls().getDirectSubclasses());
        }
        else {
            Cls cls = getCls();
            Collection result = new HashSet(cls.getVisibleDirectSubclasses());
            // Collections.sort(result);
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


    protected Cls getCls() {
        return (Cls) getUserObject();
    }


    protected Comparator getComparator() {
        return new ParentChildNodeComparator();
    }


    protected void notifyNodeChanged() {
        notifyNodeChanged(this);
    }


    private boolean showHidden() {
        return getCls().getProject().getDisplayHiddenClasses();
    }


    public String toString() {
        return "ParentChildNode(" + getCls() + ")";
    }
}
