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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGQueryResponse;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 28, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UpdateInferredTypesTask extends AbstractReasonerTask {
    private static transient final Logger log = Log.getLogger(UpdateInferredTypesTask.class);
    private ProtegeOWLReasoner protegeOWLReasoner;


    public UpdateInferredTypesTask(ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.protegeOWLReasoner = protegeOWLReasoner;
    }


    /**
     * Gets the size of the task.  When the progress
     * reaches this size, the task should be complete.
     */
    public int getTaskSize() {
        return ReasonerUtil.getInstance().getIndividuals(protegeOWLReasoner.getKnowledgeBase()).size();
    }


    /**
     * Executes the task.
     *
     * @throws edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException
     *
     */
    public void run() throws DIGReasonerException {
        OWLModel kb = protegeOWLReasoner.getKnowledgeBase();

        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Computing inferred types",
                null);

        postLogRecord(parentRecord);
        setDescription("Computing inferred types");
        setMessage("Building reasoner query");

        // Build the types query
        TimeDifference td = new TimeDifference();
        td.markStart();
        Document doc = getTranslator().createAsksDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        Iterator individualsIt = ReasonerUtil.getInstance().getIndividuals(kb).iterator();
        while (individualsIt.hasNext()) {
            final RDFIndividual curInd = (RDFIndividual) individualsIt.next();

            getTranslator().createIndividualTypesQuery(doc, curInd.getName(), curInd);
        }
        td.markEnd();

        // Log time
        postLogRecord(logRecordFactory.createInformationMessageLogRecord("Time to build query = " + td,
                parentRecord));

        // Query the reasoner

        setMessage("Querying reasoner...");

        td.markStart();

        Document responseDoc = protegeOWLReasoner.getDIGReasoner().performRequest(doc);


        td.markEnd();

        // Log time
        postLogRecord(logRecordFactory.createInformationMessageLogRecord("Time to query reasoner = " + td,
                parentRecord));

        // Update Protge-OWL
        setMessage("Updating Protege-OWL...");
        td.markStart();

        // Disable the events as we may not be updating protege
        // from the event dispatch thread
        boolean eventsEnabled = kb.setGenerateEventsEnabled(false);        
        try {
	        kb.beginTransaction("Compute and update inferred types");
	        Iterator responseIt = getTranslator().getDIGQueryResponseIterator(kb, responseDoc);
	
	
	        Slot inferredTypesSlot = kb.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
	        Slot classificationStatusSlot = ((AbstractOWLModel) kb).getProtegeClassificationStatusProperty();
	
	        while (responseIt.hasNext()) {
	            final DIGQueryResponse curResponse = (DIGQueryResponse) responseIt.next();
	            final RDFIndividual curInd = kb.getRDFIndividual(curResponse.getID());
	
	            if (curInd != null) {
	                // Check the inferred types and asserted types
	                // if there is a mismatch between the two then
	                // mark the classification status of the individual
	                // as changed. (MH - 15/09/04)
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("Current individual: " + curInd);
                        }
	                final Collection inferredTypes = curResponse.getConcepts();
	                if (inferredTypes.size() == 0) {
	                    inferredTypes.add(curInd.getOWLModel().getOWLThingClass());
	                }
	                final Collection assertedTypes = curInd.getProtegeTypes();
	                KnowledgeBase k = kb;
	                k.setOwnSlotValues(curInd, inferredTypesSlot, inferredTypes);
	
	                if (inferredTypes.containsAll(assertedTypes) &&
	                        assertedTypes.containsAll(inferredTypes)) {
	                	k.setOwnSlotValues(curInd, classificationStatusSlot, Collections.singleton(new Integer(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED)));	                    
	                }
	                else {
	                	k.setOwnSlotValues(curInd, classificationStatusSlot, Collections.singleton(new Integer(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED)));
	                }
	            }
	
	            setProgress(getProgress() + 1);
	            doAbortCheck();
	        }
	        kb.commitTransaction();
        }
        catch (DIGReasonerException e) {
        	kb.rollbackTransaction();
        	throw e;
        }
        catch (Exception e) {
        	kb.rollbackTransaction();
        	Log.getLogger().warning("Exception in transaction. Rollback. Exception: " + e.getMessage());
        	RuntimeException re = new RuntimeException();
        	re.initCause(e);
        	throw re;
		}
        kb.setGenerateEventsEnabled(eventsEnabled);

        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update Protege-OWL = " + td,
                parentRecord));
        setTaskCompleted();

    }
}

