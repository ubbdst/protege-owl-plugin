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

package edu.stanford.smi.protegex.owl.ui;

import java.util.Collection;
import java.util.List;

/**
 * A common interface for user interface components that can select
 * triples from an ontology.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleSelectable extends edu.stanford.smi.protege.util.Selectable {


    /**
     * Gets triples with an empty object or subject, indicating the typical values
     * of this.  For example, if this component displays the rdfs:comment of the
     * resource Person, then the result would be the Triple (Person, rdfs:comment, null).
     *
     * @return a List of Triples
     */
    List getPrototypeTriples();


    /**
     * Gets the selected triples in an order that is meaningful to the component.
     *
     * @return a List of Triples
     */
    List getSelectedTriples();


    /**
     * Attempts to select given Triples in this.  The method may just do nothing
     * if none of the triple is not shown.
     * @param triples  the Triples to show
     */
    void setSelectedTriples(Collection triples);
}
