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

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.Tuple;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public final class DefaultTuple implements Tuple {

    private RDFResource subject;

    private RDFProperty predicate;


    public DefaultTuple(RDFResource subject, RDFProperty predicate) {
        this.subject = subject;
        this.predicate = predicate;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Tuple && !(obj instanceof Triple)) {
            Tuple other = (Tuple) obj;
            return subject.equals(other.getSubject()) &&
                    predicate.equals(other.getPredicate());
        }
        return false;
    }


    public final RDFResource getSubject() {
        return subject;
    }


    public final RDFProperty getPredicate() {
        return predicate;
    }


    public int hashCode() {
        return subject.hashCode() + predicate.hashCode();
    }


    public String toString() {
        return "Tuple(" + getSubject().getName() + ", " +
                getPredicate().getName() + ")";
    }
}
