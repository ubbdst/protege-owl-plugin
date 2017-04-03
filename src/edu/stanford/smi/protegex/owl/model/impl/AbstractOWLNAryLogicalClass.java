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

package edu.stanford.smi.protegex.owl.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * The base class of DefaultOWLIntersectionClass and DefaultOWLUnionClass.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLNAryLogicalClass extends AbstractOWLLogicalClass
        implements OWLNAryLogicalClass {


    protected AbstractOWLNAryLogicalClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    protected AbstractOWLNAryLogicalClass() {
    }


    public void addOperand(RDFSClass operand) {
        RDFList list = (RDFList) getPropertyValue(getOperandsProperty());
        if (list == null || getOWLModel().getRDFNil().equals(list)) {
            list = getOWLModel().createRDFList();
            setOwnSlotValue(getOperandsProperty(), list);
        }
        list.append(operand);
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof AbstractOWLNAryLogicalClass) {
            AbstractOWLNAryLogicalClass compCls = (AbstractOWLNAryLogicalClass) object;
            if (getOperatorSymbol() == compCls.getOperatorSymbol()) {
                return OWLUtil.equalsStructurally(getOperands(), compCls.getOperands());
            }
        }
        return false;
    }

    /*public String getBrowserText() {
       final Collection operands = getOperands();
       char operator = getOperatorSymbol();
       if (operands.size() == 0) {
           return "<empty " + getClass().getName() + ">";
       }
       String text = "";
       for (Iterator it = operands.iterator(); it.hasNext();) {
           Cls cls = (Cls) it.next();
           String clsText = cls instanceof RDFSClass ?
                   ((RDFSClass) cls).getNestedBrowserText() :
                   cls.getBrowserText();
           text += clsText;
           if (it.hasNext()) {
               text += " " + operator + " ";
           }
       }
       return text;
   } */


    public Collection<RDFSNamedClass> getNamedOperands() {
        Collection<RDFSNamedClass> result = new HashSet<RDFSNamedClass>();
        for (RDFSClass operand : getOperands()) {
            if (operand instanceof RDFSNamedClass) {
                result.add((RDFSNamedClass) operand);
            }
        }
        return result;
    }


    public String getNestedBrowserText() {
        return "(" + getBrowserText() + ")";
    }


    @SuppressWarnings("unchecked")
    public Collection<RDFSClass> getOperands() {
        Collection<RDFSClass> operands = null;
        try {
            operands = (Collection<RDFSClass>) new GetOperandsJob(getOWLModel(), 
                                                                  this, getOperandsProperty()).execute();
        } catch (Throwable t) {
            Log.getLogger().log(Level.WARNING, "Could not get operands for " + this, t);
        }
        return (Collection<RDFSClass>) (operands == null ? Collections.emptyList() : operands);
    }


    public boolean hasOperandWithBrowserText(String browserText) {
        for (Iterator it = getOperands().iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            if (browserText.equals(operand.getBrowserText())) {
                return true;
            }
        }
        return false;
    }


    public boolean hasSameOperands(OWLNAryLogicalClass other) {
        Set setA = new HashSet();
        for (Iterator it = getOperands().iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            setA.add(operand.getBrowserText());
        }
        Set setB = new HashSet();
        for (Iterator it = other.getOperands().iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            setB.add(operand.getBrowserText());
        }
        if (setA.size() == setB.size()) {
            setA.removeAll(setB);
            return setA.isEmpty();
        }
        else {
            return false;
        }
    }


    public Iterator listOperands() {
        return getOperands().iterator();
    }


    public void removeOperand(RDFSClass operand) {
        DefaultRDFList.removeListValue(this, getOperandsProperty(), operand);
    }
    
    private static class GetOperandsJob extends ProtegeJob {

        private OWLClass owlClass;
        private RDFProperty operandsProp;
        
        public GetOperandsJob(KnowledgeBase kb, OWLNAryLogicalClass owlClass, RDFProperty operandsProp) {
            super(kb);
            this.owlClass = owlClass;
            this.operandsProp = operandsProp;
        }

        @Override
        public Object run() throws ProtegeException {
            RDFList list = (RDFList) owlClass.getPropertyValue(operandsProp);
            if (list == null) {
                return Collections.emptyList();
            }
            else {
                return list.getValues();
            }
        }
        
        @Override
        public void localize(KnowledgeBase kb) {         
            super.localize(kb);
            LocalizeUtils.localize(owlClass, kb);
            LocalizeUtils.localize(operandsProp, kb);
        }
    }
}
