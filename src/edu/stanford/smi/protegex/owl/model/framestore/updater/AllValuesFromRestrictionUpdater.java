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

package edu.stanford.smi.protegex.owl.model.framestore.updater;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

public class AllValuesFromRestrictionUpdater extends QuantifierRestrictionUpdater {
    private static final transient Logger log = Log.getLogger(AllValuesFromRestrictionUpdater.class);

    private Cls metaCls;

    private Facet valueTypeFacet;


    public AllValuesFromRestrictionUpdater(OWLModel owlModel) {
        super(owlModel);
        metaCls = owlModel.getSystemFrames().getOwlAllValuesFromClass();
        valueTypeFacet = owlModel.getSystemFrames().getValueTypeFacet();
    }


    private void addAllRestriction(Cls cls, RDFProperty property, Cls allCls) {
        OWLAllValuesFrom restriction = owlModel.createOWLAllValuesFrom(property, (RDFSClass) allCls);
        cls.addDirectSuperclass(restriction);
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ OWLAllValuesFrom " + restriction.getBrowserText() + " to " + cls.getName() + "." + property.getName());
        }
    }


    @Override
    protected void clearFiller(OWLQuantifierRestriction restriction) {
        owlModel.setTemplateSlotAllowedClses(restriction, restriction.getOnProperty(), Collections.EMPTY_LIST);
        // restriction.setTemplateSlotAllowedClses(restriction.getOnProperty(), Collections.EMPTY_LIST);
    }


    // Implements RestrictionUpdater
    public void copyFacetValuesIntoNamedClass(RDFSNamedClass cls, OWLRestriction restriction) {
        Slot slot = restriction.getOnProperty();
        updateValueTypeFacet(cls, slot);
    }


    private void removeValueTypeOverride(RDFSNamedClass cls, Slot slot) {
        if (slot != null && ((Cls) cls).hasDirectlyOverriddenTemplateFacet(slot, valueTypeFacet)) {
            ((Cls) cls).setTemplateSlotAllowedClses(slot, Collections.EMPTY_LIST);
            ((Cls) cls).setTemplateFacetValues(slot, valueTypeFacet, Collections.EMPTY_LIST);
            if (log.isLoggable(Level.FINE)) {
                log.fine("- Removed :VALUE-TYPE override from " + cls.getName() + "." + slot.getName());
            }
        }
    }


    private void setAllowedClses(RDFSNamedClass cls, Slot slot, Collection newAllowedClses) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Setting allowed clses of " + cls.getName() + "." + slot.getName());
        }
        for (Iterator it = newAllowedClses.iterator(); it.hasNext();) {
            Cls ac = (Cls) it.next();
            if (log.isLoggable(Level.FINE)) {
                log.fine("  - " + ac.getBrowserText());
            }
        }
        ((Cls) cls).setTemplateSlotAllowedClses(slot, newAllowedClses);
    }


    private void setAllowedValues(RDFSNamedClass cls, Slot slot, Collection newAllowedValues) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Setting allowed values of " + cls.getName() + "." + slot.getName());
        }
        for (Iterator it = newAllowedValues.iterator(); it.hasNext();) {
            Object ac = it.next();
            if (log.isLoggable(Level.FINE)) {
                log.fine("  - " + ac);
            }
        }
        ((Cls) cls).setTemplateSlotAllowedValues(slot, newAllowedValues);
    }


    protected void updateAllRestrictions(Cls cls, RDFProperty property) {
        if (cls.hasDirectlyOverriddenTemplateFacet(property, valueTypeFacet)) {
            ValueType valueType = cls.getTemplateSlotValueType(property);
            if (valueType == ValueType.INSTANCE) {
                Collection clses = cls.getTemplateSlotAllowedClses(property);
                if (clses.size() == 0) {
                    removeRestrictions(cls, property, metaCls);
                    addAllRestriction(cls, property, owlModel.createOWLEnumeratedClass());
                }
                else {
                    ensureNoSurvivingClsesAreDeleted(cls, property, clses, metaCls);
                    if (clses.size() == 1) {
                        Cls allCls = (Cls) clses.iterator().next();
                        removeRestrictions(cls, property, metaCls);
                        addAllRestriction(cls, property, allCls);
                    }
                    else {
                        OWLUnionClass unionCls = owlModel.createOWLUnionClass(clses);
                        removeRestrictions(cls, property, metaCls);
                        addAllRestriction(cls, property, unionCls);
                    }
                }
            }
            else if (valueType != ValueType.SYMBOL && valueType != ValueType.CLS) {
                removeRestrictions(cls, property, metaCls);
                RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(XMLSchemaDatatypes.getDefaultXSDDatatype(valueType).getURI());
                OWLAllValuesFrom restriction = owlModel.createOWLAllValuesFrom(property, datatype);
                cls.addDirectSuperclass(restriction);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ OWLAllValuesFrom " + restriction.getBrowserText() + " to " + cls.getName() + "." + property.getName());
                }
            }
        }
        else {
            removeRestrictions(cls, property, metaCls);
        }
    }


    // Implements RestrictionUpdater
    public void updateRestrictions(OWLNamedClass cls, RDFProperty property, Facet facet) {
        updateAllRestrictions(cls, property);
    }


    /**
     * Called when the superclasses have changed for a given Cls.
     * This makes sure that the :VALUE-TYPE facet is overridden to contain all
     * the "all" values from the current restrictions and union superclasses.
     *
     * @param cls  the Cls where the restrictions have changed
     * @param slot the Slot that the restrictions restrict
     */
    void updateValueTypeFacet(RDFSNamedClass cls, Slot slot) {
        if (slot != null) {
            Collection<OWLAllValuesFrom> rs = getDirectRestrictions(cls, slot, OWLAllValuesFrom.class);
            if (rs.size() == 1) {
                OWLQuantifierRestriction restriction = rs.iterator().next();
                updateValueTypeFacet(cls, slot, restriction);
            }
            else {
                // Cannot do any overrides when more than one exist (this would be intersection)
                removeValueTypeOverride(cls, slot);
            }
        }
    }


    private void updateValueTypeFacet(RDFSNamedClass cls, Slot slot, OWLQuantifierRestriction restriction) {
        ValueType oldValueType = ((Cls) cls).getTemplateSlotValueType(slot);
        ValueType newValueType = oldValueType;
        Collection oldAllowedClses = ((Cls) cls).getTemplateSlotAllowedClses(slot);
        Collection newAllowedClses = oldAllowedClses;
        Collection oldAllowedValues = ((Cls) cls).getTemplateSlotAllowedValues(slot);
        Collection newAllowedValues = oldAllowedValues;
        RDFResource filler = restriction.getFiller();
        if (filler instanceof RDFSClass) {
            RDFSClass quantifierClass = (RDFSClass) filler;
            // this code looks seriously wrong?  LITERAL is a datatype?
            if (RDFSNames.Cls.LITERAL.equals(quantifierClass.getName())) {
                newValueType = ValueType.ANY;
            }
            else {
                newValueType = ValueType.INSTANCE;
                newAllowedClses = getQuantifierClsClses(quantifierClass);
            }
        }
        else {
            if (filler instanceof OWLDataRange) {
                newAllowedValues = ((OWLDataRange) filler).getOneOf().getValues();
                newValueType = ValueType.SYMBOL;
            }
            else {
                RDFSDatatype datatype = (RDFSDatatype) filler;
                if (datatype.equals(owlModel.getRDFXMLLiteralType())) {
                    newValueType = ValueType.STRING;
                }
                else {
                    XSDDatatype xsdDatatype = XMLSchemaDatatypes.getXSDDatatype(datatype);
                    if (xsdDatatype != null) {
                        String uri = xsdDatatype.getURI();
                        newValueType = XMLSchemaDatatypes.getValueType(uri);
                    }
                }
            }
        }

        if (oldValueType == newValueType) {
            if (newValueType == ValueType.INSTANCE && !oldAllowedClses.equals(newAllowedClses)) {
                setAllowedClses(cls, slot, newAllowedClses);
            }
            else if (newValueType == ValueType.SYMBOL && !oldAllowedValues.equals(newAllowedValues)) {
                setAllowedValues(cls, slot, newAllowedValues);
            }
        }
        else {
            if (log.isLoggable(Level.FINE)) {
                log.fine("+ Setting :VALUE-TYPE of " + cls.getName() + "." + slot.getName() + " to " + newValueType);
            }
            ((Cls) cls).setTemplateSlotValueType(slot, newValueType);
            if (newValueType == ValueType.INSTANCE) {
                setAllowedClses(cls, slot, newAllowedClses);
            }
            else if (newValueType == ValueType.SYMBOL) {
                setAllowedValues(cls, slot, newAllowedValues);
            }
        }
    }
}
