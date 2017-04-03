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

import java.util.List;

/**
 * An RDFResource represents an rdf:List.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFList extends RDFResource {

    /**
     * Appends a value to this list. This method will first find the last
     * RDFList following the rdf:rest links.
     * If this does not have a rdf:first yet, then this will be assigned to it.
     * Otherwise, it will create a new RDFList of the same type like this
     * as rdf:rest to the end of this list, and assigns the given instance as its
     * rdf:first value.
     *
     * @param value the value to append a list node for
     */
    void append(Object value);


    /**
     * Checks whether a given value is among the entries in this list.
     *
     * @param value the value to look for
     * @return true if the values contain value
     */
    boolean contains(Object value);


    Object getFirst();


    /**
     * Gets the rdf:first value of this as an RDFSLiteral.
     * The calling method must make sure that we have indeed a primitive value.
     *
     * @return an RDFSLiteral
     */
    RDFSLiteral getFirstLiteral();


    RDFList getRest();


    /**
     * Gets the start of the RDFList chain containing this.
     * This method basically follows the backward references where this is rdf:rest,
     * and does so recursively until it reaches a node which is never used as rdf:rest
     * anywhere in the OWLModel.
     *
     * @return the start RDFList node (may be this)
     */
    RDFList getStart();


    /**
     * Gets the values in this list as RDFSLiterals.
     * The caller must make sure that only primitive values are currently
     * in the list.
     *
     * @return a List of RDFSLiteral values.
     */
    List getValueLiterals();


    /**
     * Gets the values in this list.
     *
     * @return a List of Object instances
     */
    List getValues();


    /**
     * Checks whether this is eventually terminated with an rdf:rest rdf:nil triple.
     *
     * @return true  if the last entry in this list points to rdf:nil.
     */
    boolean isClosed();


    void setFirst(Object value);


    void setRest(RDFList rest);


    int size();
}
