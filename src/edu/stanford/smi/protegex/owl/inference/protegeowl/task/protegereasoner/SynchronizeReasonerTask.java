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

package edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner;

import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class SynchronizeReasonerTask extends AbstractReasonerTask {

    private ProtegeReasoner protegeReasoner;


    public SynchronizeReasonerTask(ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.protegeReasoner = protegeReasoner;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run() throws ProtegeReasonerException {
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

        OWLModel owlModel = protegeReasoner.getOWLModel();
        boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);

        try {
            // Clear the knowledgebase
            clearKnowledgeBase(parentRecord);
            doAbortCheck();

            // Transmit the kb to the reasoner
            transmitToReasoner(parentRecord);
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Errors at synchronizing OWL model with the reasoner", e);
			postLogRecord(ReasonerLogRecordFactory.getInstance().createErrorMessageLogRecord("Errors at synchronization: " + e.getMessage(), null));
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



    protected void clearKnowledgeBase(ReasonerLogRecord parentRecord) throws ProtegeReasonerException {
        TimeDifference td = new TimeDifference();
        td.markStart();
        setMessage("Clearing knowledge base...");

        protegeReasoner.reset();

        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to clear knowledgebase = " + td, parentRecord));
    }


    protected void transmitToReasoner(ReasonerLogRecord parentRecord) throws ProtegeReasonerException {
        TimeDifference td = new TimeDifference();
        setMessage("Updating reasoner...");
        td.markStart();

        // Send the whole knowledge base to the reasoner

        protegeReasoner.rebind();
        td.markEnd();
        doAbortCheck();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update reasoner = " + td, parentRecord));

    }

}

