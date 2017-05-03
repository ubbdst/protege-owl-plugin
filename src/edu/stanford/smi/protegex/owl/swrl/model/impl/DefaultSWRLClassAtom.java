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

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

public class DefaultSWRLClassAtom extends DefaultSWRLAtom implements SWRLClassAtom {

    public DefaultSWRLClassAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLClassAtom


    public DefaultSWRLClassAtom() {
    }


    public void getReferencedInstances(Set<RDFResource> set) {
        Cls cls = getClassPredicate();
        if (cls instanceof RDFResource) {
            set.add((RDFResource)cls);
        }
        RDFResource argument1 = getArgument1();
        if (argument1 != null) {
            set.add(argument1);
        }
    }


    public RDFResource getArgument1() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
    } // getArgument1


    public void setArgument1(RDFResource iObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), iObject);
    } // setArgument1


  public RDFSClass getClassPredicate() 
  {
    Object propertyValue = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.CLASS_PREDICATE));
    if (propertyValue instanceof RDFSClass) return (RDFSClass)propertyValue;
    else return null;
  } // getClassPredicate


    public void setClassPredicate(RDFSClass aClass) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.CLASS_PREDICATE), aClass);
    } // setClassPredicate


  public String getBrowserText() 
  {
    Object aClass = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.CLASS_PREDICATE));
    RDFResource argument = getArgument1();

    String clsStr = SWRLUtil.getSWRLBrowserText(aClass, "CLASS");
    String argStr = SWRLUtil.getSWRLBrowserText(argument, "ARGUMENT1");

    return clsStr + "(" + argStr + ")";
  } // getBrowserText

} // DefaultSWRLClassAtom


