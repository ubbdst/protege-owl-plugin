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


/**
 * The common base interface of OWLDatatypeProperty and OWLObjectProperty.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLProperty extends RDFProperty {


    /**
     * Checks whether this is an inverse functional property.
     * This is true if this either has the rdf:type owl:InverseFunctionalProperty
     * or one of its super properties is inverse functional.
     *
     * @return true   if this is inverse functional
     */
    boolean isInverseFunctional();


    /**
     * Checks whether this is an object slot or a datatype slot.
     * This method is probably hardly ever needed - it is for the
     * case where instanceof fails because a property has just changed
     * from datatype to object property and the Java object still has
     * the old type.
     *
     * @return true  if this is an Object property
     */
    boolean isObjectProperty();


    void setInverseFunctional(boolean value);
}
