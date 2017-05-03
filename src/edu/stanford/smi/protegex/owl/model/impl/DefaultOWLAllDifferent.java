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

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;

import java.util.*;

public class DefaultOWLAllDifferent extends DefaultOWLIndividual
        implements OWLAllDifferent {

    public DefaultOWLAllDifferent(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLAllDifferent() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLAllDifferent(this);
    }


    public void addDistinctMember(RDFResource resource) {
        RDFProperty property = getOWLModel().getRDFProperty(OWLNames.Slot.DISTINCT_MEMBERS);
        RDFList value = (RDFList) getPropertyValue(property);
        if (value == null) {
            RDFList list = getOWLModel().createRDFList();
            list.setFirst(resource);
            list.setRest(getOWLModel().getRDFNil());
            setPropertyValue(property, list);
        }
        else {
            value.append(resource);
        }
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof OWLAllDifferent) {
            OWLAllDifferent comp = (OWLAllDifferent) object;
            return OWLUtil.equalsStructurally(getDistinctMembers(), comp.getDistinctMembers());
        }
        return false;
    }


    public String getBrowserText() {
        String str = "AllDifferent {";
        Iterator it = listDistinctMembers();
        while (it.hasNext() && str.length() < 80) {
            RDFResource instance = (RDFResource) it.next();
            str += instance.getBrowserText();
            if (it.hasNext()) {
                str += ", ";
            }
        }
        if (it.hasNext()) {
            str += "...";
        }
        str += "}";
        return str;
    }


    public Collection getDistinctMembers() {
        RDFProperty property = getOWLModel().getRDFProperty(OWLNames.Slot.DISTINCT_MEMBERS);
        RDFList list = (RDFList) getPropertyValue(property);
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        else {
            return list.getValues();
        }
    }


    public Iterator listDistinctMembers() {
        return getDistinctMembers().iterator();
    }


    public Set getReferringAnonymousClasses() {
        return Collections.EMPTY_SET;
    }


    public void removeDistinctMember(RDFResource instance) {
        RDFProperty property = getOWLModel().getRDFProperty(OWLNames.Slot.DISTINCT_MEMBERS);
        DefaultRDFList.removeListValue(this, property, instance);
    }


    public void setDistinctMembers(List values) {
        for (Iterator it = getDistinctMembers().iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            removeDistinctMember(resource);
        }
        for (Iterator it = values.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            addDistinctMember(resource);
        }
    }
}
