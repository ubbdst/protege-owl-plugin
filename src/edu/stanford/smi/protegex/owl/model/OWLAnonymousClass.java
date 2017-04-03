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
 * The base class of all anonymous OWL class types.
 * Anonymous classes should be handled with care, as they do follow some
 * life cycle restrictions.  Their life cycle depends on a host class,
 * which references them.  When the host class is deleted, then the
 * frame store will automatically delete any depending anonymous classes.
 * As a result, it is not permitted to share anonymous classes between
 * instances.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLAnonymousClass extends OWLClass {

    /**
     * This routine performs very slowly.  Usually this
     * can be avoided by keeping track of how this OWLAnonymous class was constructed.
     * 
     * Gets the root of the expression three where this is a part of.
     * For example, if this is the !A in the expression (!A & B), then
     * this method will return the OWLIntersectionClass (!A & B).
     *
     * @return the root (may be this if noone is pointing to this)
     */
    OWLAnonymousClass getExpressionRoot();


    /**
     * Gets the named class where this is attached to (directly or indirectly
     * as part of a nested expression).
     *
     * @return the owning named class
     */
    OWLNamedClass getOwner();
}
