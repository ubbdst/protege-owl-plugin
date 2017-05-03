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
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * The default implementation of the OWLProperty interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLProperty extends DefaultRDFProperty implements OWLProperty {

    public AbstractOWLProperty(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    AbstractOWLProperty() {
    }


    public ImageIcon getBaseImageIcon() {
        return OWLIcons.getImageIcon(getIconName());
    }


    public Icon getIcon() {
        String iconName = getIconName();
        if (isEditable()) {
            return OWLIcons.getImageIcon(iconName);
        }
        else {
            if (isAnnotationProperty()) {
                return OWLIcons.getReadOnlyAnnotationPropertyIcon(OWLIcons.getImageIcon(iconName));
            }
            else {
                return OWLIcons.getReadOnlyPropertyIcon(OWLIcons.getImageIcon(iconName));
            }
        }
    }


    public String getIconName() {
        String iconName = isObjectProperty() ?
                OWLIcons.OWL_OBJECT_PROPERTY : OWLIcons.OWL_DATATYPE_PROPERTY;
        if (isAnnotationProperty()) {
            iconName = "Annotation" + iconName;
        }
        return iconName;
    }


    public boolean isInverseFunctional() {
        RDFSClass metaclass = getOWLModel().getRDFSNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY);
        if (hasProtegeType(metaclass)) {
            return true;
        }
        for (Iterator it = getSuperproperties(false).iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property instanceof OWLProperty) {
                if (((OWLProperty) property).isInverseFunctional()) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isObjectProperty() {
        return getKnowledgeBase().getSlot(getName()) instanceof OWLObjectProperty;
        //getValueType() == ValueType.INSTANCE || getValueType() == ValueType.CLS;
    }


    public void setAnnotationProperty(boolean value) {
        updateRDFType(value, (RDFSClass) getKnowledgeBase().getCls(OWLNames.Cls.ANNOTATION_PROPERTY));
    }


    public void setEquivalentProperties(Collection slots) {
        Slot equivalentClassesSlot = getOWLModel().getOWLEquivalentPropertyProperty();
        setOwnSlotValues(equivalentClassesSlot, slots);
    }


    public void setInverseFunctional(boolean value) {
        RDFSClass metaclass = getOWLModel().getRDFSNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY);
        updateRDFType(value, metaclass);
    }
}
