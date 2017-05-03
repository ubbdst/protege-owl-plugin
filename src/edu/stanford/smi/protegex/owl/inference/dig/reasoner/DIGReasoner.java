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

package edu.stanford.smi.protegex.owl.inference.dig.reasoner;

import java.util.logging.Logger;

import org.w3c.dom.Document;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 14, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface DIGReasoner {

    public static final String LOGGER_NAME = "edu.stanford.smi.protegex.owl.inference.dig.reasoner";
    public static final Logger digLogger = Logger.getLogger(DIGReasoner.LOGGER_NAME);

    /**
     * Sets the URL of the reasoner.
     *
     * @param url The URL
     */
    public void setReasonerURL(String url);


    /**
     * Gets the URL of the reasoner.
     */
    public String getReasonerURL();


    /**
     * A helper method that gets the identity of
     * the inference
     *
     * @return A<code>DIGReasonerIdentity</code> object that encapsulates the
     *         information about the inference.
     */
    public DIGReasonerIdentity getIdentity() throws DIGReasonerException;


    /**
     * A helper method that asks the inference to create
     * a new knowledgebase.
     *
     * @return A <code>String</code> that represents a URI
     *         that is an identifier for the newly created knowledgebase.
     */
    public String createKnowledgeBase() throws DIGReasonerException;


    /**
     * A helper method that releases a previously created
     * knowledgebase.
     *
     * @param kbURI The <code>URI</code> of the knowledgebase
     */
    public void releaseKnowledgeBase(String kbURI) throws DIGReasonerException;


    /**
     * A helper method that clears the knowledge base
     *
     * @param kbURI The uri that identifies the knowledge
     *              base to be cleared.
     */
    public void clearKnowledgeBase(String kbURI) throws DIGReasonerException;


    /**
     * Sends a request to the reasoner and retrieves the response.
     *
     * @param request A <code>Document</code> containing the
     *                DIG request
     * @return A <code>Document</code> containing the reponse from the reasoner
     * @throws DIGReasonerException
     */
    public Document performRequest(Document request) throws DIGReasonerException;
}
