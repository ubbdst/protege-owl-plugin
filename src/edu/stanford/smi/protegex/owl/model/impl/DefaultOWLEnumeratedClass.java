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
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


/**
 * The default implementation of OWLEnumeratedClass.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLEnumeratedClass extends AbstractOWLAnonymousClass
        implements OWLEnumeratedClass {


    public DefaultOWLEnumeratedClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLEnumeratedClass() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLEnumeratedClass(this);
    }


    public void addOneOf(RDFResource resource) {
        final RDFProperty property = getOWLModel().getOWLOneOfProperty();
        RDFList list = (RDFList) getPropertyValue(property);
        if (list == null || getOWLModel().getRDFNil().equals(list)) {
            list = getOWLModel().createRDFList();
            setPropertyValue(property, list);
        }
        list.append(resource);
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof OWLEnumeratedClass) {
            OWLEnumeratedClass compCls = (OWLEnumeratedClass) object;
            return OWLUtil.equalsStructurally(getOneOf(), compCls.getOneOf());
        }
        else {
            return false;
        }
    }

    /*public String getBrowserText() {
       Collection values = getOneOf();
       String str = "{";
       for (Iterator it = values.iterator(); it.hasNext();) {
           Instance instance = (Instance) it.next();
           str += instance.getBrowserText();
           if (it.hasNext()) {
               str += " ";
           }
       }
       return str + "}";
   } */


    public String getIconName() {
        return OWLIcons.OWL_ENUMERATED_CLASS;
    }


    public Collection getOneOf() {
        RDFList list = (RDFList) getPropertyValue(getOWLModel().getOWLOneOfProperty());
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        else {
            return list.getValues();
        }
    }


    public Collection getOneOfValues() {
        return getOneOf();
    }


    public Iterator listOneOf() {
        return getOneOf().iterator();
    }


    public String getNestedBrowserText() {
        return getBrowserText();
    }


    public void getNestedNamedClasses(Set set) {
        for (Iterator it = getOneOf().iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSClass) {
                ((RDFSClass) o).getNestedNamedClasses(set);
            }
        }
    }


    public void removeOneOf(RDFResource resource) {
        DefaultRDFList.removeListValue(this, getOWLModel().getOWLOneOfProperty(), resource);
    }


    public void setOneOf(Collection resources) {
        // Stupid implementation -> too tired to think right now
        Collection oldValues = getOneOf();
        for (Iterator it = oldValues.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            removeOneOf(resource);
        }
        for (Iterator it = resources.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            addOneOf(resource);
        }
    }


    public void setOneOfValues(Collection values) {
        setOneOf(values);
    }
}
