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
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * The base class of MaxCardi and OWLMinCardinality.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLCardinalityBase extends AbstractOWLRestriction
        implements OWLCardinalityBase {

    public AbstractOWLCardinalityBase(KnowledgeBase kb, FrameID id, char operatorChar) {
        super(kb, id);
    }


    AbstractOWLCardinalityBase(char operatorChar) {
    }


    public void checkFillerText(String text) throws Exception {
        checkFillerText(text, getOnProperty());
    }


    public static void checkFillerText(String text, RDFProperty property) throws OWLClassParseException, NumberFormatException {
        text = text.trim();
        if (ProfilesManager.isFeatureSupported(property.getOWLModel(), OWLProfiles.Qualified_Cardinality_Restrictions))
        {
            int spaceIndex = text.indexOf(' ');
            if (spaceIndex > 0) {
                String qualifierString = text.substring(spaceIndex + 1);
                OWLClassParser parser = property.getOWLModel().getOWLClassDisplay().getParser();
                parser.checkClass(property.getOWLModel(), qualifierString);
                text = text.substring(0, spaceIndex);
            }
        }
        int i = Integer.valueOf(text).intValue();
        if (i < 0) {
            throw new NumberFormatException("Negative cardinality: " + i);
        }
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof AbstractOWLCardinalityBase) {
            AbstractOWLCardinalityBase base = (AbstractOWLCardinalityBase) object;
            return getOperator() == base.getOperator() &&
                   getCardinality() == base.getCardinality() &&
                   getOnProperty().equalsStructurally(base.getOnProperty()) &&
                   getQualifier().equalsStructurally(base.getQualifier());
        }
        return false;
    }

    //public String getBrowserText() {
    //    return getBrowserTextPropertyName() + " " +
    //            operatorChar + " " + getBrowserTextFiller();
    //}


    public int getCardinality() {
        Object value = getPropertyValue(getFillerProperty());
        if (value instanceof RDFSLiteral) {
            return ((RDFSLiteral) value).getInt();
        }
        else {
            final Integer i = (Integer) value;
            return i == null ? 0 : i.intValue();
        }
    }


    public RDFSClass getQualifier() {
        RDFSClass valuesFrom = getValuesFrom();
        if (valuesFrom == null) {
            return getOWLModel().getOWLThingClass();
        }
        else {
            return valuesFrom;
        }
    }


    public RDFSClass getValuesFrom() {
        return (RDFSClass) getPropertyValue(getOWLModel().getOWLValuesFromProperty());
    }


    public String getFillerText() {
        if (isQualified()) {
            return Integer.toString(getCardinality()) + " " + getValuesFrom().getNestedBrowserText();
        }
        else {
            return Integer.toString(getCardinality());
        }
    }


    public void getNestedNamedClasses(Set set) {
        // Do nothing
    }


    public boolean isQualified() {
        return getValuesFrom() != null;
    }


    public void setCardinality(int value) {
        setDirectOwnSlotValue(getFillerProperty(), new Integer(value));
    }


    public void setFillerText(String text) throws Exception {
        if (text.length() > 0) {

            int spaceIndex = text.indexOf(' ');
            if (spaceIndex > 0) {
                String qualifierString = text.substring(spaceIndex + 1);
                OWLClassParser parser = getOWLModel().getOWLClassDisplay().getParser();
                RDFSClass qualifier = parser.parseClass(getOWLModel(), qualifierString);
                setValuesFrom(qualifier);
                text = text.substring(0, spaceIndex);
            }

            Integer value = Integer.valueOf(text);
            if (value.intValue() >= 0) {
                setCardinality(value.intValue());
            }
        }
        else {
            setDirectOwnSlotValue(getFillerProperty(), null);
        }
    }


    public void setValuesFrom(RDFSClass value) {
        setPropertyValue(getOWLModel().getOWLValuesFromProperty(), value);
    }

    public Collection getDependingClasses() {
        if (isQualified()) {
            RDFSClass qualifier = getQualifier();
            if (qualifier.isAnonymous()) {
                return Collections.singleton(qualifier);
            }
        }
        return Collections.EMPTY_LIST;
    }
}
