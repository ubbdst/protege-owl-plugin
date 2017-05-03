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

package edu.stanford.smi.protegex.owl.ui.properties.actions;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConvertToObjectPropertyAction extends ResourceAction {

    public ConvertToObjectPropertyAction() {
        super("Convert to owl:ObjectProperty",
                OWLIcons.getImageIcon(OWLIcons.OWL_OBJECT_PROPERTY),
                ConvertToDatatypePropertyAction.GROUP,
                false);
    }


    public void actionPerformed(ActionEvent e) {
        RDFSClass type = getOWLModel().getOWLObjectPropertyClass();
        final RDFProperty property = (RDFProperty) getResource();
        try {
            getOWLModel().beginTransaction("Convert " + property.getBrowserText() + " to owl:DatatypeProperty", property.getName());
            property.setRange(null);
            property.setProtegeType(type);
            getOWLModel().commitTransaction();
        }
        catch (Exception ex) {
        	getOWLModel().rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource.isEditable() &&
                resource instanceof Slot &&
                ((Slot) resource).getDirectSuperslotCount() == 0 &&
                ((Slot) resource).getDirectSubslotCount() == 0 &&
                !(resource instanceof OWLObjectProperty);
    }
}
