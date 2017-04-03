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

package edu.stanford.smi.protegex.owl.model.event;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * A listener for property-related events.
 * Note that this currently extends ProtegeSlotListener for technical
 * reasons, but none of the inherited methods from SlotListener
 * should be used in application code.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface PropertyListener extends ProtegeSlotListener {

    /**
     * Called after a subproperty has been added to a property.
     *
     * @param property    the property where the subproperty was added
     * @param subproperty the new subproperty
     */
    void subpropertyAdded(RDFProperty property, RDFProperty subproperty);


    /**
     * Called after a subproperty has been removed from a property.
     *
     * @param property    the property where the subproperty was removed
     * @param subproperty the old subproperty
     */
    void subpropertyRemoved(RDFProperty property, RDFProperty subproperty);


    /**
     * Called after a superproperty has been added to a property.
     *
     * @param property      the property where the superproperty was added
     * @param superproperty the new superproperty
     */
    void superpropertyAdded(RDFProperty property, RDFProperty superproperty);


    /**
     * Called after a superproperty has been removed from a property.
     *
     * @param property      the property where the superproperty was removed
     * @param superproperty the old superproperty
     */
    void superpropertyRemoved(RDFProperty property, RDFProperty superproperty);


    /**
     * Called after a class has been added to the union domain of a property.
     *
     * @param property  the property where the domain has changed
     * @param rdfsClass the domain class that was added
     */
    void unionDomainClassAdded(RDFProperty property, RDFSClass rdfsClass);


    /**
     * Called after a class has been removed from the union domain of a property.
     *
     * @param property  the property where the domain has changed
     * @param rdfsClass the domain class that was removed
     */
    void unionDomainClassRemoved(RDFProperty property, RDFSClass rdfsClass);
}
