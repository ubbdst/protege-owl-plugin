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

package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.*;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An interface to wrap the Protege Instance and Frame interfaces for OWL.
 * This basically deprecates most methods and points to their replacements
 * in the OWL API.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeInstance extends Instance {


    /**
     * @see RDFResource#addPropertyValueListener
     * @deprecated
     */
    void addFrameListener(FrameListener listener);


    /**
     * @see RDFResource#addResourceListener
     * @deprecated
     */
    void addInstanceListener(InstanceListener listener);


    /**
     * @deprecated no OWL equivalent
     */
    boolean addOwnFacetValue(Slot slot, Facet facet, Object value);


    /**
     * @see RDFResource#addPropertyValue
     * @deprecated
     */
    void addOwnSlotValue(Slot slot, Object value);


    /**
     * @deprecated not needed
     */
    boolean areValidOwnSlotValues(Slot slot, Collection values);


    /**
     * @deprecated not needed
     */
    Frame deepCopy(KnowledgeBase kb, Map valueMap);


    /**
     * @deprecated not needed
     */
    Frame shallowCopy(KnowledgeBase kb, Map valueMap);


    /**
     * @deprecated not needed
     */
    Frame copy(KnowledgeBase kb, Map valueMap, boolean isDeep);


    String getBrowserText();


    /**
     * @deprecated use ProtegeUI.getIcon() instead
     * @see edu.stanford.smi.protegex.owl.ui.ProtegeUI#getIcon
     */
    Icon getIcon();


    /**
     * @see RDFResource#getComments
     * @deprecated
     */
    Collection getDocumentation();

    /**
     * @deprecated not needed
     */
    String getInvalidOwnSlotValuesText(Slot slot, Collection values);


    /**
     * @deprecated not needed
     */
    String getInvalidOwnSlotValueText(Slot slot, Object value);


    /**
     * @see RDFResource#getOWLModel
     * @deprecated
     */
    KnowledgeBase getKnowledgeBase();


    /**
     * @deprecated no OWL equivalent
     */
    Object getOwnFacetValue(Slot slot, Facet facet);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getOwnFacetValues(Slot slot, Facet facet);


    /**
     * @deprecated not needed
     */
    boolean getOwnSlotAllowsMultipleValues(Slot slot);


    /**
     * @deprecated not needed
     */
    Collection getOwnSlotAndSubslotValues(Slot slot);


    /**
     * @deprecated not needed
     */
    Collection getOwnSlotDefaultValues(Slot slot);


    /**
     * @deprecated not needed
     */
    Collection getOwnSlotFacets(Slot slot);


    /**
     * @deprecated not needed
     */
    Collection getOwnSlotFacetValues(Slot slot, Facet facet);


    /**
     * @see RDFResource#getPossibleRDFProperties
     * @deprecated replaced but includes subproperties!
     */
    Collection getOwnSlots();


    /**
     * @see RDFResource#getPropertyValue (false)
     * @deprecated
     */
    Object getDirectOwnSlotValue(Slot slot);


    /**
     * @see RDFResource#getPropertyValue (true)
     * @deprecated
     */
    Object getOwnSlotValue(Slot slot);


    /**
     * @see RDFResource#getPropertyValueCount
     * @deprecated
     */
    int getOwnSlotValueCount(Slot slot);


    /**
     * @see RDFResource#getPropertyValues (true)
     * @deprecated
     */
    Collection getOwnSlotValues(Slot slot);


    /**
     * @see RDFResource#getPropertyValues
     * @deprecated
     */
    List getDirectOwnSlotValues(Slot slot);


    /**
     * @deprecated not needed
     */
    ValueType getOwnSlotValueType(Slot slot);


    Project getProject();


    public Collection getReferences();


    Collection getReferences(int maxReferences);


    /**
     * @deprecated not needed
     */
    boolean hasOwnSlot(Slot slot);


    boolean isEditable();


    boolean isIncluded();


    boolean isSystem();


    void markDeleting();


    void markDeleted(boolean deleted);


    boolean isDeleted();


    boolean isBeingDeleted();


    /**
     * @deprecated not needed
     */
    boolean isValidOwnSlotValue(Slot slot, Object item);


    /**
     * @see RDFResource#removePropertyValueListener
     * @deprecated
     */
    void removeFrameListener(FrameListener listener);


    /**
     * @see RDFResource#removeResourceListener
     * @deprecated
     */
    void removeInstanceListener(InstanceListener listener);


    /**
     * @see RDFResource#removePropertyValue
     * @deprecated
     */
    public void removeOwnSlotValue(Slot slot, Object value);


    /**
     * @see RDFResource#setComment
     * @deprecated
     */
    void setDocumentation(String documentation);


    /**
     * @see RDFResource#setComments
     * @deprecated
     */
    void setDocumentation(Collection documentation);


    void setEditable(boolean b);


    /**
     * @deprecated this will be refactored into multiple imports
     */
    void setIncluded(boolean b);


    /**
     * @deprecated no OWL equivalent
     */
    void setOwnFacetValue(Slot slot, Facet facet, Object value);


    /**
     * @deprecated no OWL equivalent
     */
    void setOwnFacetValues(Slot slot, Facet facet, Collection values);


    /**
     * @see RDFResource#setPropertyValue
     * @deprecated
     */
    void setOwnSlotValue(Slot slot, Object value);


    /**
     * @see RDFResource#setPropertyValue
     * @deprecated
     */
    void setDirectOwnSlotValue(Slot slot, Object value);


    /**
     * @see RDFResource#setPropertyValues
     * @deprecated
     */
    void setOwnSlotValues(Slot slot, Collection values);


    /**
     * @see RDFResource#setPropertyValues
     * @deprecated
     */
    void setDirectOwnSlotValues(Slot slot, Collection values);


    /**
     * @see RDFResource#getProtegeType
     * @deprecated
     */
    Cls getDirectType();


    /**
     * @see RDFResource#getProtegeTypes
     * @deprecated
     */
    Collection getDirectTypes();


    /**
     * @deprecated not needed
     */
    Collection getReachableSimpleInstances();


    /**
     * @see RDFResource#hasProtegeType
     * @deprecated
     */
    boolean hasDirectType(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean hasType(Cls cls);


    /**
     * @see RDFResource#setProtegeType
     * @deprecated
     */
    Instance setDirectType(Cls cls);


    /**
     * @see RDFResource#setProtegeTypes
     * @deprecated
     */
    Instance setDirectTypes(Collection types);


    /**
     * @see RDFResource#addProtegeType
     * @deprecated
     */
    void addDirectType(Cls cls);


    /**
     * @see RDFResource#removeProtegeType
     * @deprecated
     */
    void removeDirectType(Cls cls);
}
