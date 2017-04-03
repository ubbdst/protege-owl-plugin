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

package edu.stanford.smi.protegex.owl.inference.dig.translator;

import java.util.Collection;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 19, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface DIGQueryResponse {

    /**
     * Gets the ID of the query that the reponse corresponds to
     */
    public String getID();


    /**
     * If the query resulted in a concept set response type
     * for example a query for super concepts then
     * this method can be used to get the concepts.
     *
     * @return A <code>Collection</code> of <code>RDFSClass</code>s
     */
    public Collection getConcepts();


    /**
     * If the query resulted in an individual set response type
     * then this method may be used to obtain the individuals
     * in the response.
     *
     * @return A <code>Collection</code> of <code>RDFIndividual</code>s
     */
    public Collection getIndividuals();


    /**
     * If the query resultied in a boolean response,
     * for example asking if a concept was satisfiable, then
     * this method may be used to get the boolean result.
     */
    public boolean getBoolean();
}
