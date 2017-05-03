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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.EventDispatchFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.util.SimpleStringMatcher;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;

/**
 * A FrameStore that intercepts any access to those slots that store the
 * classification results and redirects them to values stored in local maps.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LocalClassificationFrameStore extends FrameStoreAdapter {

    private OWLModel owlModel;

    /**
     * Slot -> instancesMap
     */
    private Map<Slot, Map<Frame, List>> slotsMap = null;


    public LocalClassificationFrameStore(OWLModel owlModel) {
        this.owlModel = owlModel;
    }
    
    @SuppressWarnings("unchecked")
    private Map<Slot, Map<Frame, List>> getSlotsMap() {
        if (slotsMap == null) {
            slotsMap = new HashMap<Slot, Map<Frame, List>>();
            slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.CLASSIFICATION_STATUS), new HashMap<Frame, List>());
            slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE), new HashMap<Frame, List>());
            slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_SUBCLASSES), new HashMap<Frame, List>());
            slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES), new HashMap<Frame, List>());
        }
        return slotsMap;
    }
    
    public static boolean isLocalClassificationProperty(Slot s) {
    	String name = s.getName();
    	return name.equals(ProtegeNames.Slot.CLASSIFICATION_STATUS) ||
    			name.equals(ProtegeNames.Slot.INFERRED_TYPE) ||
    			name.equals(ProtegeNames.Slot.INFERRED_SUBCLASSES) ||
    			name.equals(ProtegeNames.Slot.INFERRED_SUPERCLASSES);
    }
    
    public void deleteCls(Cls cls) {
        for (Iterator<Slot> it = getSlotsMap().keySet().iterator(); it.hasNext();) {
            Slot slot = it.next();
            Map<Frame, List> instancesMap = getSlotsMap().get(slot);
            instancesMap.remove(cls);
            for (Iterator<Frame> jt = new ArrayList<Frame>(instancesMap.keySet()).iterator(); 
                 jt.hasNext();) {
                Frame frame = jt.next();
                Collection values = instancesMap.get(frame);
                if (values.contains(cls)) {
                    List newValues = new ArrayList(values);
                    newValues.remove(cls);
                    frame.setDirectOwnSlotValues(slot, newValues);
                }
            }
        }
        super.deleteCls(cls);
    }


    public List getDirectOwnSlotValues(Frame frame, Slot slot) {
        final Map<Frame,List> instancesMap = getSlotsMap().get(slot);
        if (instancesMap != null) {
            final List values = instancesMap.get(frame);
            if (values == null) {
                return Collections.EMPTY_LIST;
            }
            else {
                return values;
            }
        }
        else {
            return super.getDirectOwnSlotValues(frame, slot);
        }
    }
    
    @Override
    public Collection getOwnSlotValues(Frame frame, Slot slot) {
        final Map<Frame,List> instancesMap = getSlotsMap().get(slot);
        if (instancesMap != null) {
            final List values = instancesMap.get(frame);
            if (values == null) {
                return Collections.EMPTY_LIST;
            }
            else {
                return values;
            }
        }
        else {
            return super.getOwnSlotValues(frame, slot);
        }
    }


    public int getDirectOwnSlotValuesCount(Frame frame, Slot slot) {
        final Map<Frame, List> instancesMap = getSlotsMap().get(slot);
        if (instancesMap != null) {
            return getDirectOwnSlotValues(frame, slot).size();
        }
        else {
            return super.getDirectOwnSlotValuesCount(frame, slot);
        }
    }


    private EventDispatchFrameStore getEventDispatchFrameStore() {
        for (Iterator it = ((KnowledgeBase) owlModel).getFrameStores().iterator(); it.hasNext();) {
            FrameStore frameStore = (FrameStore) it.next();
            if (frameStore instanceof EventDispatchFrameStore) {
                return (EventDispatchFrameStore) frameStore;
            }
        }
        return null;
    }


    public Set getFramesWithDirectOwnSlotValue(Slot slot, Object value) {
        final Map<Frame, List> instancesMap = getSlotsMap().get(slot);
        if (instancesMap != null) {
            final Set<Frame> result = new HashSet<Frame>();
            for (Iterator<Frame> it = instancesMap.keySet().iterator(); it.hasNext();) {
                final Frame key = it.next();
                final Collection values = instancesMap.get(key);
                if (values != null && values.contains(value)) {
                    result.add(key);
                }
            }
            return result;
        }
        else {
            return super.getFramesWithDirectOwnSlotValue(slot, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Frame> getFramesWithMatchingDirectOwnSlotValue(Slot slot, String regexp,
                                                       int maxMatches) {
        final Map<Frame, List> instancesMap = getSlotsMap().get(slot);
        if (instancesMap != null) {
            SimpleStringMatcher matcher = new SimpleStringMatcher(regexp);
            final Set<Frame> result = new HashSet<Frame>();
            for (Entry<Frame, List> entry : instancesMap.entrySet()) {
                Frame frame = entry.getKey();
                List values = entry.getValue();
                for (Object value : values) {
                    if (value instanceof String && matcher.isMatch((String) value)) {
                        result.add(frame);
                    }
                }
            }
            return result;
        }
        else  {
            return super.getFramesWithMatchingDirectOwnSlotValue(slot, regexp, maxMatches);
        }
    }

    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        final Map<Frame, List> instancesMap = getSlotsMap().get(slot);
        if (instancesMap != null) {
            if (values.isEmpty()) {
                instancesMap.remove(frame);
            }
            else {
                instancesMap.put(frame, new ArrayList(values));
            }
            // dispatchEvent(frame, slot);
        }
        else {
            super.setDirectOwnSlotValues(frame, slot, values);
        }
    }
}
