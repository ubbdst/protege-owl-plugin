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

import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGVocabulary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGReasonerIdentity  implements ReasonerIdentity {

    private String reasonerName;

    private String reasonerMessage;

    private String reasonerVersion;

    private HashSet supportedLanguageElements;

    private HashSet supportedTellElements;

    private HashSet supportedAskElements;


    public DIGReasonerIdentity() {
        supportedLanguageElements = new HashSet();

        supportedTellElements = new HashSet();

        supportedAskElements = new HashSet();
    }


    /**
     * Clears all of the information in this
     * inference identity object.
     */
    protected void clear() {
        supportedLanguageElements = new HashSet();

        supportedTellElements = new HashSet();

        supportedAskElements = new HashSet();

        reasonerName = "";

        reasonerMessage = "";

        reasonerVersion = "";
    }


    /**
     * Parses the DIG identity response and fills
     * this object with the inference identity information
     */
    public void parseIdentityDescrtiption(Document doc) {
        clear();

        processDocument(doc);
    }


    protected void processDocument(Document doc) {
        Element docElement = doc.getDocumentElement();

        reasonerName = docElement.getAttribute("name");

        reasonerMessage = docElement.getAttribute("message");

        reasonerVersion = docElement.getAttribute("version");

        // A nasty hack because Racer doesn't return iset, or individual
        // even though it supports it!
        if (reasonerName.equals("Racer")) {
            supportedLanguageElements.add(DIGVocabulary.Language.ISET);
            supportedLanguageElements.add(DIGVocabulary.Language.INDIVIDUAL);
        }

        Node node;

        // Get supports

        Element supportsElement = (Element) docElement.getElementsByTagName("supports").item(0);

        // Could be either language, tells, or asks

        node = supportsElement.getElementsByTagName("language").item(0);

        fillSet(node, supportedLanguageElements);

        node = supportsElement.getElementsByTagName("tell").item(0);

        fillSet(node, supportedTellElements);

        node = supportsElement.getElementsByTagName("ask").item(0);

        fillSet(node, supportedAskElements);
    }


    /**
     * Gets the name of the inference.
     */
    public String getName() {
        return reasonerName;
    }


    /**
     * Gets the version of a reaonser.  The version
     * is a <code>String</code> of the form num.num.num
     * e.g. 1.7.12
     */
    public String getVersion() {
        return reasonerVersion;
    }


    /**
     * Gets the inference message.
     * e.g. "Racer is running on localhost:8080"
     */
    public String getMessage() {
        return reasonerMessage;
    }


    /**
     * Gets a <code>Collection</code> that holds the supported
     * langauge elements.
     *
     * @return A <code>Collection</code> of <code>Strings</code>
     *         that describe the language elements supported by the
     *         inference.  These language elements are contained in
     *         <code>DIGVocabulary.Language</code>
     */
    public Collection getSupportedLanguageElements() {
        return Collections.unmodifiableCollection(supportedLanguageElements);
    }


    /**
     * Gets a <code>Collection</code> that holds the supported
     * tell elements.
     *
     * @return A <code>Collection</code> of <code>Strings</code>
     *         that describe the tell elements supported by the
     *         inference.  These tell elements are contained in
     *         <code>DIGVocabulary.Tell</code>
     */
    public Collection getSupportedTellElements() {
        return Collections.unmodifiableCollection(supportedTellElements);
    }


    /**
     * Gets a <code>Collection</code> that holds the supported
     * ask elements.
     *
     * @return A <code>Collection</code> of <code>Strings</code>
     *         that describe the ask elements supported by the
     *         inference.  These ask elements are contained in
     *         <code>DIGVocabulary.Ask</code>
     */
    public Collection getSupportedAskElements() {
        return Collections.unmodifiableCollection(supportedAskElements);
    }


    /**
     * Determines if the specified language element is
     * supported by the inference.
     *
     * @param constructName The name of the language
     *                      element (see <code>DIGVocabulary.Language</code>)
     * @return <code>true</code> if supported, <code>false</code>
     *         if not supported.
     */
    public boolean supportsLanguageElement(String constructName) {
        return supportedLanguageElements.contains(constructName);
    }


    /**
     * Determines if the specified tell element is
     * supported by the inference.
     *
     * @param constructName The name of the tell
     *                      element (see <code>DIGVocabulary.Tell</code>)
     * @return <code>true</code> if supported, <code>false</code>
     *         if not supported.
     */
    public boolean supportsTellElemement(String constructName) {
        return supportedTellElements.contains(constructName);
    }


    /**
     * Determines if the specified ask element is
     * supported by the inference.
     *
     * @param constructName The name of the ask
     *                      element (see <code>DIGVocabulary.Ask</code>)
     * @return <code>true</code> if supported, <code>false</code>
     *         if not supported.
     */
    public boolean supportsAskElement(String constructName) {
        return supportedAskElements.contains(constructName);
    }


    private void fillSet(Node node, Set set) {
        NodeList nodeList;

        nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                String nodeName = nodeList.item(i).getNodeName();
                set.add(nodeName);
            }
        }
    }


}

