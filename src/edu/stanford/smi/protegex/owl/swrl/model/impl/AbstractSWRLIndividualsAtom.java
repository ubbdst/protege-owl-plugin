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

package edu.stanford.smi.protegex.owl.swrl.model.impl;

import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

public abstract class AbstractSWRLIndividualsAtom extends DefaultSWRLAtom implements SWRLIndividualsAtom {

    public AbstractSWRLIndividualsAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public AbstractSWRLIndividualsAtom() {
    }


    protected abstract String getOperatorName();


    public void getReferencedInstances(Set<RDFResource> set) {

    }


    public RDFResource getArgument1() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
    } // getArgument1


    public void setArgument1(RDFResource instance) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), instance);
    } // setArgument1


    public RDFResource getArgument2() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
    } // getArgument2


    public void setArgument2(RDFResource instance) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2), instance);
    } // setArgument2


    public String getBrowserText() {

        String s = getOperatorName() + "(";

        RDFResource argument1 = getArgument1();
        if (argument1 == null) {
            s += "<null>";
        }
        else {
            s += argument1.getBrowserText();
        }
        s += ", ";
        RDFResource argument2 = getArgument2();
        if (argument2 == null) {
            s += "<null>";
        }
        else {
            s += argument2.getBrowserText();
        }
        s += ")";

        return s;

    } // getBrowserText

} // AbstractSWRLIndividualsAtom


