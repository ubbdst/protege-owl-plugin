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

package edu.stanford.smi.protegex.owl.ui.existential;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.LazyTreeNodeFrameComparator;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLClass;

import java.util.Collection;
import java.util.Comparator;

/**
 * A LazyTreeNode for a relationship between classes via someValuesFrom
 * restrictions on a (transitive) property.
 *
 * @author Holger Knublauch   <holger@knublauch.com>
 */
public class ExistentialTreeNode extends LazyTreeNode {

    private ExistentialFillerProvider fillerProvider;

    private FrameListener frameListener = new FrameAdapter() {

        public void browserTextChanged(FrameEvent event) {
            notifyNodeChanged();
        }


        public void ownSlotValueChanged(FrameEvent event) {
            notifyNodeChanged();
            if (event.getSlot().equals(superclassesSlot)) {
                reload();
            }
        }


        public void visibilityChanged(FrameEvent event) {
            notifyNodeChanged();
        }
    };

    private Slot superclassesSlot;

    private OWLObjectProperty existentialProperty;


    public ExistentialTreeNode(LazyTreeNode parentNode,
                               OWLClass parentCls,
                               Slot superclassesSlot,
                               OWLObjectProperty existentialProperty) {
        super(parentNode, parentCls);
        this.fillerProvider = new ExistentialFillerProvider(existentialProperty);
        parentCls.accept(fillerProvider);
        this.superclassesSlot = superclassesSlot;
        this.existentialProperty = existentialProperty;
        ((Cls) parentCls).addFrameListener(frameListener);
    }


    protected LazyTreeNode createNode(Object o) {
        return new ExistentialTreeNode(this, (OWLClass) o, superclassesSlot, existentialProperty);
    }


    protected void dispose() {
        super.dispose();
        ((Cls) getOWLClass()).removeFrameListener(frameListener);
    }


    protected int getChildObjectCount() {
        return fillerProvider.getFillers().size();
    }


    protected Collection getChildObjects() {
        return fillerProvider.getFillers();
    }

    public OWLClass getOWLClass() {
        return (OWLClass) getUserObject();
    }

    public void reload() {
        fillerProvider.reset();
        getOWLClass().accept(fillerProvider);
        super.reload();
    }

    protected Comparator getComparator() {
        return new LazyTreeNodeFrameComparator();
    }


    protected void notifyNodeChanged() {
        notifyNodeChanged(this);
    }


    public String toString() {
        return "ExistentialTreeNode(" + getOWLClass() + ")";
    }
}
