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

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.SWRLUtil;

import java.util.Set;

public class DefaultSWRLDatavaluedPropertyAtom extends DefaultSWRLAtom implements SWRLDatavaluedPropertyAtom {

    public DefaultSWRLDatavaluedPropertyAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLDatavaluedPropertyAtom


    public DefaultSWRLDatavaluedPropertyAtom() {
    }


    public void getReferencedInstances(Set<RDFResource> set) {
        RDFResource argument1 = getArgument1();
        if (argument1 != null) {
            set.add(argument1);
        }
        RDFProperty property = getPropertyPredicate();
        if (property != null) {
            set.add(property);
        }
    }


  public RDFResource getArgument1() 
  {
    return (RDFResource)getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
  } // getArgument1

  public void setArgument1(RDFResource iObject) {
    setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), iObject);
  } // setArgument1

  public RDFObject getArgument2() 
  {
    Object o = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
    if (o instanceof RDFResource) return (RDFResource) o;
    return getPropertyValueLiteral(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
  } // getArgument2
  
  public void setArgument2(RDFObject dObject) 
  {
    setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2), dObject);
  } // setArgument2

  public OWLDatatypeProperty getPropertyPredicate() 
  {
    Object propertyValue = getDirectOwnSlotValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE));
    if (propertyValue instanceof OWLDatatypeProperty) return (OWLDatatypeProperty)propertyValue;
    else return null;
  } // getPropertyPredicate

  public void setPropertyPredicate(OWLDatatypeProperty datatypeSlot) {
    setOwnSlotValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE), datatypeSlot);
  } // setPropertyPredicate

  public String getBrowserText() 
  {
    Object propertyPredicate = getDirectOwnSlotValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE));
    RDFResource argument1 = getArgument1();
    RDFObject argument2 = getArgument2();
    String s = "";

    s += SWRLUtil.getSWRLBrowserText(propertyPredicate, "PROPERTY");
    s += "(";
    s += SWRLUtil.getSWRLBrowserText(argument1, "ARGUMENT1");
    s += ", ";
    s += SWRLUtil.getSWRLBrowserText(argument2, "ARGUMENT2");
    s += ")";

    return s;
  } // getBrowserText

} // DefaultSWRLDatavaluedPropertyAtom


