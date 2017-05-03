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

import java.util.Collection;
import java.util.Iterator;

/**
 * An enumerated class which lists valid individuals as its values of
 * the owl:oneOf property.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLEnumeratedClass extends OWLAnonymousClass {

    /**
     * Adds a resource to this enumeration.
     *
     * @param resource the RDFResource to add (typically individuals)
     */
    void addOneOf(RDFResource resource);


    /**
     * Gets the values of the owl:oneOf property at this, i.e. the
     * resources that are part of this enumeration.
     *
     * @return the values of owl:oneOf (a Collection of RDFResources)
     */
    Collection getOneOf();


    /**
     * @deprecated use getOneOf instead
     */
    Collection getOneOfValues();


    /**
     * Gets an Iterator of the values in the owl:oneOf list.
     * @return an Iterator of RDFResources
     */
    Iterator listOneOf();


    /**
     * Removes a resource from this enumeration.
     *
     * @param resource the resource to remove
     */
    void removeOneOf(RDFResource resource);


    /**
     * Sets the values of the owl:oneOf property at this.
     *
     * @param resources a Collection of RDFResources
     */
    void setOneOf(Collection resources);


    /**
     * @deprecated use setOneOf instead
     */
    void setOneOfValues(Collection values);
}
