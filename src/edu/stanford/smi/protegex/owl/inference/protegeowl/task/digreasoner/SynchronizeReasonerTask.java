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
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import org.w3c.dom.Document;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 16, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SynchronizeReasonerTask extends AbstractReasonerTask {

    private ProtegeOWLReasoner protegeOWLReasoner;


    public SynchronizeReasonerTask(ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.protegeOWLReasoner = protegeOWLReasoner;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run() throws DIGReasonerException {
        TimeDifference td = new TimeDifference();
        setDescription("Synchronizing reasoner");
        setMessage("Synchronizing reasoner...");
        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();
        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Synchronize reasoner",
                null);
        postLogRecord(parentRecord);

        td.markStart();
        setProgressIndeterminate(true);

        doAbortCheck();
        setMessage("Updating reasoner...");

        OWLModel owlModel = protegeOWLReasoner.getKnowledgeBase(); 
        
        boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);
        try {
            // Clear the knowledgebase
            clearKnowledgeBase(parentRecord);
            doAbortCheck();

            // Transmit the kb to the reasoner
            transmitToReasoner(parentRecord);
            
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			owlModel.setGenerateEventsEnabled(eventsEnabled);
		}
        
		doAbortCheck();

        setProgressIndeterminate(false);
        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to synchronize = " + td, parentRecord));
        setMessage("Reasoner synchronized");
        setProgress(1);
        setTaskCompleted();
    }


    protected void clearKnowledgeBase(ReasonerLogRecord parentRecord) throws DIGReasonerException {
        TimeDifference td = new TimeDifference();
        td.markStart();
        setMessage("Clearing knowledge base...");
        protegeOWLReasoner.getDIGReasoner().clearKnowledgeBase(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to clear knowledgebase = " + td, parentRecord));
    }


    protected void transmitToReasoner(ReasonerLogRecord parentRecord) throws DIGReasonerException {
        TimeDifference td = new TimeDifference();
        setMessage("Generating DIG representation...");
        td.markStart();

        // Send the whole knowledge base to the reasoner
        Document doc = getTranslator().createTellsDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        getTranslator().translateToDIG(protegeOWLReasoner.getKnowledgeBase(), doc, doc.getDocumentElement());
        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time for DIG conversion = " + td, parentRecord));
        doAbortCheck();
        setMessage("Updating reasoner");
        td.markStart();
        protegeOWLReasoner.getDIGReasoner().performRequest(doc);
        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update reasoner = " + td, parentRecord));

    }
}

