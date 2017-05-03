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
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public abstract class QuantifierRestrictionUpdater extends AbstractRestrictionUpdater {
    private static final transient Logger log = Log.getLogger(QuantifierRestrictionUpdater.class);

    QuantifierRestrictionUpdater(OWLModel owlModel) {
        super(owlModel);
    }


    protected void ensureNoSurvivingClsesAreDeleted(Cls cls, Slot slot, Collection clses, Cls metaCls) {
        for (Iterator it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls.getDirectType().equals(metaCls)) {
                OWLQuantifierRestriction restriction = (OWLQuantifierRestriction) superCls;
                Slot restrictedSlot = restriction.getOnProperty();
                if (restrictedSlot.equals(slot)) {
                    RDFSClass allCls = (RDFSClass) restriction.getFiller();
                    if (allCls instanceof OWLAnonymousClass) {
                        if (clses.contains(allCls)) {
                            if (log.isLoggable(Level.FINE)) {
                                log.fine("& Clearing filler of " + restriction.getBrowserText());
                            }
                            clearFiller(restriction);
                        }
                        else if (allCls instanceof OWLUnionClass) {
                            OWLUnionClass unionCls = (OWLUnionClass) allCls;
                            for (Iterator oit = new ArrayList(unionCls.getOperands()).iterator(); oit.hasNext();) {
                                RDFSClass operand = (RDFSClass) oit.next();
                                if (operand instanceof OWLAnonymousClass && clses.contains(operand)) {
                                    if (log.isLoggable(Level.FINE)) {
                                        log.fine("& Clearing operand " + operand.getBrowserText() +
                                            " from: " + restriction.getBrowserText());
                                    }
                                    unionCls.removeOperand(operand);
                                    //Collection newOperands = new ArrayList(unionCls.getOperands());
                                    //newOperands.remove(operand);
                                    //((Cls) unionCls).setDirectOwnSlotValues(unionCls.getOperandsProperty(), newOperands);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    protected abstract void clearFiller(OWLQuantifierRestriction restriction);


    protected Collection getQuantifierClsClses(RDFSClass quantifierClass) {
        if (quantifierClass != null) {
            if (quantifierClass instanceof OWLUnionClass) {
                return ((OWLUnionClass) quantifierClass).getOperands();
            }
            else {
                return Collections.singleton(quantifierClass);
            }
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    protected Collection getSafeClses(Collection base) {
        ArrayList copy = new ArrayList();
        for (Iterator it = base.iterator(); it.hasNext();) {
            Cls baseCls = (Cls) it.next();
            if (baseCls instanceof RDFSClass) {
                final Cls clone = ((RDFSClass) baseCls).createClone();
                copy.add(clone);
            }
            else {
                copy.add(baseCls);
            }
        }
        return copy;
    }
}
