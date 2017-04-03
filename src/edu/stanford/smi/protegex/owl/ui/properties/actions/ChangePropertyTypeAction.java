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

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangePropertyTypeAction extends ResourceAction {

    public ChangePropertyTypeAction() {
        super("Change property metaclass...",
                Icons.getBlankIcon(),
                ConvertToDatatypePropertyAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        RDFProperty property = (RDFProperty) getResource();
        Slot slot = property;
        Cls rootType = getRootType(slot);

        RDFSNamedClass rdfPropertyClass = getOWLModel().getRDFPropertyClass();
        boolean rdfPropertyWasVisible = rdfPropertyClass.isVisible();
        rdfPropertyClass.setVisible(true);

        RDFSNamedClass owlObjectPropertyClass = getOWLModel().getOWLObjectPropertyClass();
        boolean owlObjectPropertyWasVisible = owlObjectPropertyClass.isVisible();
        boolean objectPropertiesAllowed = ProfilesManager.isFeatureSupported(getOWLModel(), OWLProfiles.Create_ObjectProperty);
        owlObjectPropertyClass.setVisible(objectPropertiesAllowed);

        RDFSNamedClass owlDatatypePropertyClass = getOWLModel().getOWLDatatypePropertyClass();
        boolean owlDatatypePropertyWasVisible = owlDatatypePropertyClass.isVisible();
        boolean datatypePropertiesAllowed = ProfilesManager.isFeatureSupported(getOWLModel(), OWLProfiles.Create_DatatypeProperty);
        owlDatatypePropertyClass.setVisible(datatypePropertiesAllowed);

        RDFSClass type = (RDFSClass) ProtegeUI.getSelectionDialogFactory().selectClass(getComponent(), getOWLModel(),
                Collections.singleton(rootType),
                "Select property metaclass");

        rdfPropertyClass.setVisible(rdfPropertyWasVisible);
        owlObjectPropertyClass.setVisible(owlObjectPropertyWasVisible);
        owlDatatypePropertyClass.setVisible(owlDatatypePropertyWasVisible);

        if (type != null && !property.hasRDFType(type)) {
            final boolean isDatatype = type.getSuperclasses(true).contains(owlDatatypePropertyClass);
            if (property instanceof OWLObjectProperty && isDatatype) {
                property.setRange(null);
            }
            else {
                final boolean isObject = type.getSuperclasses(true).contains(owlObjectPropertyClass);
                if (property instanceof OWLDatatypeProperty && isObject) {
                    property.setRange(null);
                }
                else if (!(property instanceof OWLProperty) && (isDatatype || isObject)) {
                    property.setRange(null);
                }
            }
            property.setProtegeType((RDFSClass) type);
        }
    }


    private Cls getRootType(Slot slot) {
        OWLModel owlModel = (OWLModel) slot.getKnowledgeBase(); // Don't use getOWLModel()!
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.RDF) || !(slot instanceof OWLProperty)) {
            return owlModel.getRDFPropertyClass();
        }
        else {
            if (slot instanceof OWLDatatypeProperty) {
                return owlModel.getOWLDatatypePropertyClass();
            }
            else { // must be ObjectProperty
                return owlModel.getOWLObjectPropertyClass();
            }
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        if (resource.isEditable() && resource instanceof RDFProperty) {
            if (resource instanceof OWLProperty) {
                Collection types = Collections.singleton(getRootType((Slot) resource));
                return DisplayUtilities.hasMultipleConcreteClses(getOWLModel(), types);
            }
            else { // pure rdf:Property
                return true;
            }
        }
        return false;
    }
}
