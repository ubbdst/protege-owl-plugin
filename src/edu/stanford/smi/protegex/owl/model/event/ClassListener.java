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
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ClassListener extends ProtegeClsListener {

    /**
     * Called after a class has been added to the union domain of a property.
     *
     * @param cls      the class that was added
     * @param property the property that has changed its domain
     */
    void addedToUnionDomainOf(RDFSClass cls, RDFProperty property);


    /**
     * Called after a (new) resource was made an instance of a class.
     *
     * @param cls      the RDFSClass of the instance
     * @param instance the instance that was added
     */
    void instanceAdded(RDFSClass cls, RDFResource instance);


    /**
     * Called after a resource was removed from the instances of a class.
     *
     * @param cls      the RDFSClass of the instance
     * @param instance the instance that was removed
     */
    void instanceRemoved(RDFSClass cls, RDFResource instance);


    /**
     * Called after a class has been removed from the union domain of a property.
     *
     * @param cls      the class that was removed
     * @param property the property that has changed its domain
     */
    void removedFromUnionDomainOf(RDFSClass cls, RDFProperty property);


    /**
     * Called after a class has been added as a subclass to another class.
     *
     * @param cls      the class that was changed
     * @param subclass the new subclass of cls
     */
    void subclassAdded(RDFSClass cls, RDFSClass subclass);


    /**
     * Called after a class has been removed from the subclasses of another class.
     *
     * @param cls      the class that was changed
     * @param subclass the former subclass of cls
     */
    void subclassRemoved(RDFSClass cls, RDFSClass subclass);


    /**
     * Called after a class has been added as a superclass to another class.
     *
     * @param cls        the class that was changed
     * @param superclass the new superclass of cls
     */
    void superclassAdded(RDFSClass cls, RDFSClass superclass);


    /**
     * Called after a class has been removed from the superclasses of another class.
     *
     * @param cls        the class that was changed
     * @param superclass the former superclass of cls
     */
    void superclassRemoved(RDFSClass cls, RDFSClass superclass);
}
