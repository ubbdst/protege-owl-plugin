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

package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public final class DefaultTriple implements Triple {

    private RDFResource subject;

    private RDFProperty predicate;

    private Object object;


    public DefaultTriple(RDFResource subject, RDFProperty predicate, Object object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            Triple other = (Triple) obj;
            return subject.equals(other.getSubject()) &&
                    predicate.equals(other.getPredicate()) &&
                    object.equals(other.getObject());
        }
        return false;
    }


    public final RDFResource getSubject() {
        return subject;
    }


    public final RDFProperty getPredicate() {
        return predicate;
    }


    public final Object getObject() {
        return object;
    }


    public int hashCode() {
        return subject.hashCode() + predicate.hashCode() + object.hashCode();
    }


    public String toString() {
        Object object = getObject();
        String str = object instanceof Frame ? ((Frame) object).getName() : object.toString();
        return "Triple(" + getSubject().getName() + ", " +
                getPredicate().getName() + ", " +
                str + ")";
    }
}
