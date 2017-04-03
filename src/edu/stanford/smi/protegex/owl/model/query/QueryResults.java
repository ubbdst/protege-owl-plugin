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

package edu.stanford.smi.protegex.owl.model.query;

import java.util.List;
import java.util.Map;

/**
 * An object wrapping the results of a query.  A query results in an Iterator of
 * variable bindings, where each variable can be bound to an RDFObject.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface QueryResults {


    /**
     * Gets a list of all variables that have been declared in the query.
     * The variables are the keys in the Maps returned by the <CODE>next()</CODE> method.
     *
     * @return a List of Objects (e.g. Strings)
     * @see #next
     */
    List getVariables();


    /**
     * Checks if there are more results available.
     *
     * @return true  if there are more results
     */
    boolean hasNext();


    /**
     * Gets the next results, where each result is a Map from variables to bindings.
     * This method can only be called as long as <CODE>hasNext()</CODE> delivers true.
     *
     * @return a Map (keys are the members of the <CODE>getVariables()</CODE> call).
     * @see #getVariables
     */
    Map next();
}
