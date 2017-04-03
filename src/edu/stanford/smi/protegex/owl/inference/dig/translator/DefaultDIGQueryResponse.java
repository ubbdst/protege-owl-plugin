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

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 20, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultDIGQueryResponse implements DIGQueryResponse {

    private Element element;

    private OWLModel kb;


    public DefaultDIGQueryResponse(OWLModel kb) {
        this.kb = kb;
    }


    public void setElement(Element element) {
        this.element = element;
    }


    public String getID() {
        return element.getAttribute("id");
    }


    public Collection getConcepts() {
        NodeList synonymsList = element.getElementsByTagName(DIGVocabulary.Response.SYNONYMS);
        Collection conceptList = new HashSet(synonymsList.getLength());
        for (int i = 0; i < synonymsList.getLength(); i++) {
            final NodeList catomList = ((Element) synonymsList.item(i)).getElementsByTagName(DIGVocabulary.Language.CATOM);
            for (int j = 0; j < catomList.getLength(); j++) {
                String name = ((Element) catomList.item(j)).getAttribute("name");
                final RDFResource aClass = kb.getRDFResource(name);
                if (aClass != null) {
                	if (aClass instanceof OWLNamedClass) {
                		conceptList.add(aClass);
                	} else {
                		Log.getLogger().warning("Error at getting inferred type from DIG Response." +
                				" Expected an OWL Named Class, got: " + aClass);
                	}
                }
            }
            if (((Element) synonymsList.item(i)).getElementsByTagName(DIGVocabulary.Language.TOP).getLength() != 0) {
                conceptList.add(kb.getOWLThingClass());
            }
        }

        return conceptList;
    }


    public Collection getIndividuals() {
        Collection individuals = new HashSet();
        NodeList individualElementList = element.getElementsByTagName(DIGVocabulary.Language.INDIVIDUAL);
        for (int i = 0; i < individualElementList.getLength(); i++) {
            final Element individualElement = (Element) individualElementList.item(i);
            final OWLIndividual curInd = kb.getOWLIndividual(individualElement.getAttribute("name"));
            if (curInd != null) {
                individuals.add(curInd);
            }
        }
        return individuals;
    }


    public boolean getBoolean() {
        String val = element.getTagName();
        boolean b = true;
        if (val.equals("false")) {
            b = false;
        }
        return b;
    }
}

