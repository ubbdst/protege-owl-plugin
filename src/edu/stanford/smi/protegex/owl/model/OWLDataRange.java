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
 * An RDFResource representing an owl:DataRange, i.e. an enumeration of
 * literal values.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLDataRange extends RDFResource {

    /**
     * Gets a collection of the values in this owl:DataRange.
     * The values are either primitive objects or RDFLiterals.
     *
     * @return the values
     */
    RDFList getOneOf();


    /**
     * Gets the values of the owl:oneOf property as RDFSLiterals.
     *
     * @return the values as RDFSLiterals
     */
    List getOneOfValueLiterals();


    /**
     * Gets the values of the owl:oneOf property.
     *
     * @return the values
     */
    List getOneOfValues();


    /**
     * Gets the RDFSDatatype of the first element in this data range.
     *
     * @return the RDFSDatatype or null if this is empty
     */
    RDFSDatatype getRDFDatatype();
}
