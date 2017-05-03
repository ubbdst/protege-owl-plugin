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
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;

/**
 * The default implementation of the OWLDatatypeProperty interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLDatatypeProperty extends AbstractOWLProperty implements OWLDatatypeProperty {

    public final static String ICON_NAME = "OWLDatatypeProperty";


    public DefaultOWLDatatypeProperty(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLDatatypeProperty() {
    }


    public String getIconName() {
        if (isAnnotationProperty()) {
            return OWLIcons.OWL_DATATYPE_ANNOTATION_PROPERTY;
        }
        else {
            return OWLIcons.OWL_DATATYPE_PROPERTY;
        }
    }


    public Icon getInheritedIcon() {
        return OWLIcons.getImageIcon(OWLIcons.OWL_DATATYPE_PROPERTY_INHERITED);
    }


    public boolean hasDatatypeRange() {
        return true;
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLDatatypeProperty(this);
    }
}
