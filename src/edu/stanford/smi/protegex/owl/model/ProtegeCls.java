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

import edu.stanford.smi.protege.event.ClsListener;
import edu.stanford.smi.protege.model.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeCls extends ProtegeInstance, Cls {

    /**
     * @see RDFSClass#addClassListener
     * @deprecated
     */
    void addClsListener(ClsListener listener);


    /**
     * @see RDFSClass#addSuperclass
     * @deprecated
     */
    void addDirectSuperclass(Cls cls);


    /**
     * @see RDFProperty#addUnionDomainClass
     * @deprecated
     */
    void addDirectTemplateSlot(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    void addTemplateFacetValue(Slot slot, Facet facet, Object value);


    /**
     * @deprecated no OWL equivalent
     */
    void addTemplateSlotValue(Slot slot, Object value);


    /**
     * @see RDFSClass#createInstance
     * @deprecated
     */
    Instance createDirectInstance(String name);


    /**
     * @deprecated not needed
     */
    Collection getConcreteSubclasses();


    BrowserSlotPattern getBrowserSlotPattern();


    BrowserSlotPattern getDirectBrowserSlotPattern();


    BrowserSlotPattern getInheritedBrowserSlotPattern();


    /**
     * @see RDFSClass#getInstanceCount (false)
     * @deprecated
     */
    int getDirectInstanceCount();


    /**
     * @see RDFSClass#getInstances (false)
     * @deprecated
     */
    Collection getDirectInstances();


    /**
     * @see RDFSClass#getSubclassCount
     * @deprecated
     */
    int getDirectSubclassCount();


    /**
     * @see RDFSClass#getSubclasses (false)
     * @deprecated
     */
    Collection getDirectSubclasses();


    /**
     * @see RDFSClass#getSuperclassCount
     * @deprecated
     */
    int getDirectSuperclassCount();


    /**
     * @see RDFSClass#getSuperclasses (false)
     * @deprecated
     */
    Collection<Cls> getDirectSuperclasses();


    /**
     * @deprecated no OWL equivalent
     */
    List getDirectTemplateFacetValues(Slot slot, Facet facet);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getOverriddenTemplateFacets(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getDirectlyOverriddenTemplateFacets(Slot slot);


    /**
     * @see RDFSClass#getUnionDomainProperties
     * @deprecated
     */
    Collection getDirectTemplateSlots();


    /**
     * @deprecated no OWL equivalent
     */
    List getDirectTemplateSlotValues(Slot slot);


    /**
     * @see RDFSClass#getInstanceCount
     * @deprecated
     */
    int getInstanceCount();


    /**
     * @see RDFSClass#getInstances
     * @deprecated
     */
    Collection getInstances();


    /**
     * @see RDFSClass#getSubclasses (true)
     * @deprecated
     */
    Collection getSubclasses();


    /**
     * @see RDFSClass#getSuperclasses (true)
     * @deprecated
     */
    Collection getSuperclasses();


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateFacets(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Object getTemplateFacetValue(Slot slot, Facet facet);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateFacetValues(Slot slot, Facet facet);


    /**
     * @see RDFSNamedClass#getUnionRangeClasses
     * @deprecated
     */
    Collection getTemplateSlotAllowedClses(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateSlotAllowedParents(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateSlotAllowedValues(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    boolean getTemplateSlotAllowsMultipleValues(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateSlotDefaultValues(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateSlotDocumentation(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    int getTemplateSlotMaximumCardinality(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Number getTemplateSlotMaximumValue(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    int getTemplateSlotMinimumCardinality(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Number getTemplateSlotMinimumValue(Slot slot);


    /**
     * @see RDFSClass#getUnionDomainProperties
     * @deprecated
     */
    Collection getTemplateSlots();


    /**
     * @deprecated no OWL equivalent
     */
    Object getTemplateSlotValue(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    Collection getTemplateSlotValues(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    ValueType getTemplateSlotValueType(Slot slot);


    /**
     * @deprecated not needed
     */
    int getVisibleDirectSubclassCount();


    /**
     * @deprecated not needed
     */
    Collection getVisibleDirectSubclasses();


    /**
     * @deprecated not needed
     */
    Collection getVisibleTemplateSlots();


    /**
     * @deprecated no OWL equivalent
     */
    boolean hasDirectlyOverriddenTemplateFacet(Slot slot, Facet facet);


    /**
     * @deprecated no OWL equivalent
     */
    boolean hasDirectlyOverriddenTemplateSlot(Slot slot);


    /**
     * @see RDFSClass#isSubclassOf
     * @deprecated
     */
    boolean hasDirectSuperclass(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean hasDirectTemplateSlot(Slot slot);


    /**
     * @deprecated not needed
     */
    boolean hasInheritedTemplateSlot(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    boolean hasOverriddenTemplateFacet(Slot slot, Facet facet);


    /**
     * @deprecated no OWL equivalent
     */
    boolean hasOverriddenTemplateSlot(Slot slot);


    /**
     * @deprecated not needed
     */
    boolean hasSuperclass(Cls cls);


    /**
     * @deprecated no OWL equivalent
     */
    boolean hasTemplateFacet(Slot slot, Facet facet);


    /**
     * @deprecated not needed
     */
    boolean hasTemplateSlot(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    boolean isAbstract();


    /**
     * @deprecated not needed
     */
    boolean isClsMetaCls();


    /**
     * @deprecated no OWL equivalent
     */
    boolean isConcrete();


    /**
     * @deprecated not needed
     */
    boolean isDefaultClsMetaCls();


    /**
     * @deprecated not needed
     */
    boolean isDefaultFacetMetaCls();


    /**
     * @deprecated not needed
     */
    boolean isDefaultSlotMetaCls();


    /**
     * @deprecated not needed
     */
    boolean isFacetMetaCls();


    /**
     * @see RDFSClass#isMetaclass
     * @deprecated
     */
    boolean isMetaCls();


    /**
     * @deprecated not needed
     */
    boolean isRoot();


    /**
     * @deprecated not needed
     */
    boolean isSlotMetaCls();


    /**
     * @deprecated not needed
     */
    void moveDirectSubclass(Cls movedSubclass, Cls afterCls);


    /**
     * @deprecated not needed
     */
    void moveDirectTemplateSlot(Slot slot, int toIndex);


    /**
     * @see RDFSClass#removeClassListener
     * @deprecated
     */
    void removeClsListener(ClsListener listener);


    /**
     * @see RDFSClass#removeSuperclass
     * @deprecated
     */
    void removeDirectSuperclass(Cls cls);


    /**
     * @see RDFProperty#removeUnionDomainClass
     * @deprecated
     */
    void removeDirectTemplateSlot(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    void removeTemplateFacetOverrides(Slot slot);


    /**
     * @deprecated no OWL equivalent
     */
    void setAbstract(boolean v);


    // Will be deprecated later
    void setDirectBrowserSlot(Slot slot);


    // Will probably be deprecated later
    void setDirectBrowserSlotPattern(BrowserSlotPattern pattern);


    /**
     * @deprecated not needed
     */
    void setDirectTypeOfSubclasses(Cls metaCls);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateFacetValue(Slot slot, Facet facet, Object value);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateFacetValues(Slot slot, Facet facet, Collection c);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotAllowedClses(Slot slot, Collection clses);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotAllowedParents(Slot slot, Collection clses);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotAllowedValues(Slot slot, Collection values);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotAllowsMultipleValues(Slot slot, boolean b);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotDefaultValues(Slot slot, Collection values);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotDocumentation(Slot slot, String documentation);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotDocumentation(Slot slot, Collection documentation);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotMaximumCardinality(Slot slot, int max);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotMaximumValue(Slot slot, Number max);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotMinimumCardinality(Slot slot, int min);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotMinimumValue(Slot slot, Number min);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotValue(Slot slot, Object value);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotValues(Slot slot, Collection c);


    /**
     * @deprecated no OWL equivalent
     */
    void setTemplateSlotValueType(Slot slot, ValueType valueType);
}
