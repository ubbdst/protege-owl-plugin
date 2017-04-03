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

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.clsproperties.RDFPropertiesComponent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFPropertiesWidget extends AbstractSlotWidget {

    private RDFPropertiesComponent component;


    public RDFPropertiesWidget() {
        setPreferredColumns(2);
        setPreferredRows(2);
    }


    public void initialize() {
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        component = new RDFPropertiesComponent(owlModel);
        add(component);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                Model.Slot.DIRECT_TEMPLATE_SLOTS.equals(slot.getName());
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance instanceof RDFSNamedClass) {
            component.setClass((RDFSNamedClass) newInstance);
        }
    }
}
