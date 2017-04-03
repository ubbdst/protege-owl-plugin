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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractPropertyWidget extends AbstractSlotWidget
        implements PropertyWidget {

    public OWLModel getOWLModel() {
        return (OWLModel) getKnowledgeBase();
    }


    /**
     * @deprecated
     */
    public Instance getInstance() {
        return super.getInstance();
    }


    public RDFProperty getRDFProperty() {
        return (RDFProperty) super.getSlot();
    }


    public RDFResource getEditedResource() {
        return (RDFResource) super.getInstance();
    }


    /**
     * @deprecated
     */
    public Slot getSlot() {
        return super.getSlot();
    }


    protected void updateBorder(Collection values) {
        // Don't do anything to avoid facet testing
    }
}
