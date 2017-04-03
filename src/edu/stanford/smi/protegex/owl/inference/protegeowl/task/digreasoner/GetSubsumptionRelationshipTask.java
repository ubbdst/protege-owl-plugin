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

package edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGQueryResponse;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import org.w3c.dom.Document;

import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 6, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetSubsumptionRelationshipTask extends AbstractReasonerTask implements IntegerResultReasonerTask {


    private ProtegeOWLReasoner protegeOWLReasoner;

    private OWLClass cls1;

    private OWLClass cls2;

    private int result;


    public GetSubsumptionRelationshipTask(ProtegeOWLReasoner protegeOWLReasoner,
                                          OWLClass cls1,
                                          OWLClass cls2) {
        super(protegeOWLReasoner);
        this.protegeOWLReasoner = protegeOWLReasoner;
        this.cls1 = cls1;
        this.cls2 = cls2;
        result = 0;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run()
            throws DIGReasonerException {
        Document asksDoc = getTranslator().createAsksDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        getTranslator().createSubsumesQuery(asksDoc, "q1Sub2", cls1, cls2);
        getTranslator().createSubsumesQuery(asksDoc, "q2Sub1", cls2, cls1);
        Document responseDoc = protegeOWLReasoner.getDIGReasoner().performRequest(asksDoc);
        Iterator it = getTranslator().getDIGQueryResponseIterator(protegeOWLReasoner.getKnowledgeBase(), responseDoc);
        boolean cls1SubsumesCls2 = false;
        boolean cls2SubsumedCls1 = false;
        while (it.hasNext()) {
            DIGQueryResponse queryResponse = (DIGQueryResponse) it.next();
            if (queryResponse.getID().equals("q1Sub2")) {
                cls1SubsumesCls2 = queryResponse.getBoolean();
            }
            else {
                cls2SubsumedCls1 = queryResponse.getBoolean();
            }
        }
        if (cls1SubsumesCls2 == true) {
            if (cls2SubsumedCls1 == true) {
                result = ProtegeOWLReasoner.CLS1_EQUIVALENT_TO_CLS2;
            }
            else {
                result = ProtegeOWLReasoner.CLS1_SUBSUMES_CLS2;
            }
        }
        else {
            if (cls2SubsumedCls1 == true) {
                result = ProtegeOWLReasoner.CLS1_SUBSUMED_BY_CLS2;
            }
            else {
                result = ProtegeOWLReasoner.NO_SUBSUMPTION_RELATIONSHIP;
            }
        }
        setProgress(1);
        setTaskCompleted();
    }


    public int getResult() {
        return result;
    }
}

