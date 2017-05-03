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
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;

/**
 * A basic implementation of the OWLRestriction interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLRestriction extends AbstractOWLAnonymousClass implements OWLRestriction {


    public AbstractOWLRestriction(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    AbstractOWLRestriction() {
    }


    public static void checkExpression(String text, RDFProperty onProperty, RDFProperty restrictionProperty) throws OWLClassParseException {
        if (onProperty == null) {
            throw new OWLClassParseException("Please select a property.");
        }
        if (text.trim().length() == 0) {
            throw new OWLClassParseException("Please enter a filler.");
        }
        if (restrictionProperty.getName().equals(OWLNames.Slot.HAS_VALUE)) {
            DefaultOWLHasValue.checkFillerText(text, onProperty);
        }
        else if (restrictionProperty.getName().equals(OWLNames.Slot.ALL_VALUES_FROM)) {
            DefaultOWLAllValuesFrom.checkFillerText(text, onProperty, restrictionProperty.getOWLModel());
        }
        else if (restrictionProperty.getName().equals(OWLNames.Slot.SOME_VALUES_FROM)) {
            DefaultOWLSomeValuesFrom.checkFillerText(text, onProperty);
        }
        else {
            AbstractOWLCardinalityBase.checkFillerText(text, onProperty);
        }
    }


    protected String getBrowserTextFiller() {
        return isDefined() ? getFillerText() : "?";
    }


    /**
     * Gets a displayable form of the restricted slot.
     * If the slot is not specified yet, this will return a placeholder.
     * If the slot name contains spaces then the name will be put into apostrophs.
     *
     * @return the browser text of the slot
     */
    protected String getBrowserTextPropertyName() {
        RDFProperty property = getOnProperty();
        if (property == null) {
            return "<property>";
        }
        else {
            return property.getBrowserText();
        }
    }


    public String getNestedBrowserText() {
        return "(" + getBrowserText() + ")";
    }


    public RDFProperty getOnProperty() {
        final RDFProperty slot = getOWLModel().getRDFProperty(OWLNames.Slot.ON_PROPERTY);
        return (RDFProperty) getDirectOwnSlotValue(slot);
    }


    public boolean isDefined() {
        return getOnProperty() != null && isFillerDefined();
    }


    protected boolean isFillerDefined() {
        return getDirectOwnSlotValue(getFillerProperty()) != null;
    }


    public void setOnProperty(RDFProperty property) {
        final RDFProperty onPropertySlot = getOWLModel().getRDFProperty(OWLNames.Slot.ON_PROPERTY);
        setDirectOwnSlotValue(onPropertySlot, property);
    }
}
