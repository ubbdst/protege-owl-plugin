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

package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.Collection;
import java.util.Collections;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.ValueTypeConstraint;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

public class RangeUpdateFrameStore extends  FrameStoreAdapter {
    
    private RDFProperty rdfsRangeProperty;
    private Slot valueType;
    
    public RangeUpdateFrameStore(OWLModel owlModel) {
        rdfsRangeProperty = owlModel.getRDFSRangeProperty();
        valueType = owlModel.getSystemFrames().getValueTypeSlot();
    }
    
    @SuppressWarnings("unchecked")
    public void synchronizeRDFSRangeWithProtegeAllowedValues(RDFProperty property) {
        Collection ranges = super.getOwnSlotValues(property, rdfsRangeProperty);
        updateAllowedValues(property, ranges);
    }
    
    /**
     * Updates the ValueType of a slot in response to changes in the range.
     */
    @SuppressWarnings("unchecked")
    private void updateAllowedValues(RDFProperty property, Collection ranges) {
        if (ranges.size() > 1) {
            ((Slot) property).setValueType(ValueType.ANY);
            return;
        } else if (property instanceof OWLObjectProperty && ranges.isEmpty() && super.getSuperslots(property).size() >= 1) {
            super.setDirectOwnSlotValues(property, valueType, Collections.EMPTY_LIST);
            return;
        }
        ValueType newValueType = ValueType.ANY;
        if (property instanceof OWLObjectProperty) {
            newValueType = ValueType.INSTANCE;
        }
        if (!ranges.isEmpty()) {
            Object range = ranges.iterator().next();
            if (range instanceof RDFSDatatype) {
                newValueType = XMLSchemaDatatypes.getValueType(((RDFSDatatype) range).getURI());
            }
            else if (range instanceof RDFSClass) {
                newValueType = ValueType.INSTANCE;
            }
            else if (range instanceof OWLDataRange) {
                RDFSDatatype datatype = ((OWLDataRange) range).getRDFDatatype();
                if (datatype != null) {
                    newValueType = XMLSchemaDatatypes.getValueType(datatype.getURI());
                }
            }
            if (newValueType == ValueType.INSTANCE) {
                if (range instanceof RDFSClass) {
                    RDFSClass rangeClass = (RDFSClass) range;
                    if (rangeClass instanceof OWLUnionClass) {
                        super.setDirectOwnSlotValues(property, valueType, ValueTypeConstraint.getValues(ValueType.INSTANCE, ((OWLUnionClass) rangeClass).getOperands()));
                    }
                    else {
                        super.setDirectOwnSlotValues(property, valueType, ValueTypeConstraint.getValues(ValueType.INSTANCE, Collections.singleton(rangeClass)));
                    }
                    return;
                }
            }
        }
        if (newValueType == ValueType.INSTANCE) {
            ((Slot) property).setAllowedClses(Collections.EMPTY_LIST);
        }
        else if (newValueType != ((Slot) property).getValueType()) {
            super.setDirectOwnSlotValues(property, valueType, ValueTypeConstraint.getValues(newValueType));
        }

    }
    
    /*
     * FrameStore implementations
     */
    
    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        if (frame instanceof RDFProperty && slot.equals(rdfsRangeProperty)) {
            updateAllowedValues((RDFProperty) frame, values);
        }
        super.setDirectOwnSlotValues(frame, slot, values);
    }
    
    @Override
    public Slot createSlot(FrameID id, Collection directTypes, Collection directSuperslots, boolean loadDefaults) {
        Slot slot = super.createSlot(id, directTypes, directSuperslots, loadDefaults);
        if (slot instanceof OWLObjectProperty && directSuperslots.isEmpty()) {
            slot.setValueType(ValueType.INSTANCE);
        }
        return slot;
    }
    
    @Override
    public void addDirectSuperslot(Slot slot, Slot superSlot) {
        super.addDirectSuperslot(slot, superSlot);
        if (slot instanceof OWLObjectProperty && ((RDFProperty) slot).getRange() == null) {
            slot.setDirectOwnSlotValue(valueType, null);
        }
    }
    
    @Override
    public void removeDirectSuperslot(Slot slot, Slot superslot) {
        super.removeDirectSuperslot(slot, superslot);
        if (slot instanceof OWLObjectProperty &&
            slot.getDirectSuperslotCount() == 0 &&
            slot.getAllowedClses().isEmpty()) {
            slot.setValueType(ValueType.INSTANCE);
        }
    }
    
}
