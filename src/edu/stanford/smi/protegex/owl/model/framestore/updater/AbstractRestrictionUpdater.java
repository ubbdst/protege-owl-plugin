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

package edu.stanford.smi.protegex.owl.model.framestore.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;

public abstract class AbstractRestrictionUpdater implements RestrictionUpdater {
    private final static transient Logger log = Log.getLogger(AbstractRestrictionUpdater.class);

    /**
     * The OWLModel this operates on
     */
    protected OWLModel owlModel;


    public AbstractRestrictionUpdater(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    protected Cls getDirectRestriction(Cls cls, Slot slot, Class metaClass) {
        for (Iterator it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (metaClass.isAssignableFrom(superCls.getClass())) {
                final Slot restrictedSlot = ((OWLRestriction) superCls).getOnProperty();
                if (slot.equals(restrictedSlot)) {
                    return superCls;
                }
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
	protected <X extends OWLRestriction> Collection<X> getDirectRestrictions(Cls cls, Slot slot, Class<? extends X> metaClass) {
        Collection<X> result = new ArrayList<X>();
        for (Iterator<Cls> it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superCls = it.next();
            if (metaClass.isAssignableFrom(superCls.getClass())) {
            	X restriction = (X) superCls;
                Slot restrictedSlot = ((OWLRestriction) superCls).getOnProperty();
                if (restrictedSlot != null && restrictedSlot.equals(slot)) {
                    result.add(restriction);
                }
            }
        }
        return result;
    }


    protected void removeRestrictions(Cls cls, Slot slot, Cls metaCls) {
        Collection copy = new ArrayList(cls.getDirectSuperclasses());
        for (Iterator it = copy.iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls.getDirectType().equals(metaCls)) {
                OWLRestriction restriction = (OWLRestriction) superCls;
                Slot restrictedSlot = restriction.getOnProperty();
                if (restrictedSlot.equals(slot)) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("- OWLRestriction " + restriction.getBrowserText() + " from " + cls.getName() + "." + slot.getName());
                    }
                    cls.removeDirectSuperclass(restriction);
                }
            }
        }
    }
}
