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
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import org.w3c.dom.Document;

import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 17, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetConceptSatisfiableTask extends AbstractReasonerTask implements BooleanResultReasonerTask {

    private OWLClass aClass;

    private ProtegeOWLReasoner protegeOWLReasoner;

    private boolean satisfiable;


    public GetConceptSatisfiableTask(OWLClass aClass,
                                     ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.aClass = aClass;
        this.protegeOWLReasoner = protegeOWLReasoner;
        this.satisfiable = true;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run() throws DIGReasonerException {
        OWLModel kb = protegeOWLReasoner.getKnowledgeBase();

        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();
        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Checking consistency of " + aClass.getBrowserText(),
                null);
        postLogRecord(parentRecord);
        setDescription("Computing consistency");
        setMessage("Building reasoner query...");

        Document doc = getTranslator().createAsksDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        getTranslator().createSatisfiableQuery(doc, "q0", aClass);

        setMessage("Querying reasoner...");
        TimeDifference td = new TimeDifference();
        td.markStart();

        Document responseDoc = protegeOWLReasoner.getDIGReasoner().performRequest(doc);

        td.markEnd();
        postLogRecord(logRecordFactory.createInformationMessageLogRecord("Time to query reasoner = " + td,
                parentRecord));

        Iterator it = getTranslator().getDIGQueryResponseIterator(kb, responseDoc);

        if (it.hasNext()) {
            DIGQueryResponse response = (DIGQueryResponse) it.next();

            satisfiable = response.getBoolean();
        }
        postLogRecord(logRecordFactory.createConceptConsistencyLogRecord(aClass, satisfiable, parentRecord));
        setProgress(1);
        setMessage("Finished");
        setTaskCompleted();
    }


    public boolean getResult() {
        return satisfiable;
    }
}

