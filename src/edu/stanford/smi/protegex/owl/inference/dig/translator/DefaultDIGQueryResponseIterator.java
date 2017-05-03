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

import edu.stanford.smi.protegex.owl.model.OWLModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 29, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultDIGQueryResponseIterator implements Iterator {

    private Document doc;

    private int currentElementIndex = 0;

    private NodeList nodeList;

    private boolean elementAvailable = false;

    private DefaultDIGQueryResponse interpreter;


    public DefaultDIGQueryResponseIterator(Document doc, OWLModel kb) {
        this.doc = doc;
        nodeList = this.doc.getDocumentElement().getChildNodes();
        currentElementIndex = 0;
        interpreter = new DefaultDIGQueryResponse(kb);
        advanceToNextElement();
    }


    public boolean hasNext() {
        return elementAvailable;
    }


    protected void advanceToNextElement() {
        int index = currentElementIndex;
        elementAvailable = true;
        // Search for the next element
        while (nodeList.item(index).getNodeType() != Node.ELEMENT_NODE ||
                nodeList.item(index).getNodeName().equals(DIGVocabulary.Response.ERROR)) {
            // Check to see if we are already on the last
            // node.  If we are then no more elements
            // are available
            if (index == nodeList.getLength() - 1) {
                // Last element
                elementAvailable = false;
                break;
            }
            // Carry on searching
            index++;
        }

        currentElementIndex = index;
    }


    public Object next() {
        Element element = (Element) nodeList.item(currentElementIndex);
        currentElementIndex++;
        interpreter.setElement(element);
        advanceToNextElement();
        return interpreter;
    }


    public void remove() {
        throw new UnsupportedOperationException();
    }
}

