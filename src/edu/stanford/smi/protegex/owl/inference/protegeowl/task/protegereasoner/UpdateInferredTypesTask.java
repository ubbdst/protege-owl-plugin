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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;


public class UpdateInferredTypesTask extends AbstractReasonerTask {
    private static transient final Logger log = Log.getLogger(UpdateInferredTypesTask.class);
	private final static String DISPLAY_DIRECT_TYPES_ONLY = "reasoner.ui.display.direct.types.only";  
	
    private ProtegeReasoner protegeReasoner;
    
    private boolean isDisplayDirectTypesOnly = false;
    

    public UpdateInferredTypesTask(ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.protegeReasoner = protegeReasoner;
        this.isDisplayDirectTypesOnly = isDisplayDirectTypesOnly();
    }

    /**
     * Gets the size of the task.  When the progress
     * reaches this size, the task should be complete.
     */
    public int getTaskSize() {
        return ReasonerUtil.getInstance().getIndividuals(protegeReasoner.getOWLModel()).size();
    }

    /**
     * Executes the task.
     *
     * @throws edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException
     *
     */
    @SuppressWarnings("deprecation")
    public void run() throws ProtegeReasonerException {
        OWLModel kb = protegeReasoner.getOWLModel();
       
        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();
        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Computing inferred types", null);
        postLogRecord(parentRecord);
        setDescription("Computing inferred types");
      
        // Query the reasoner
        setMessage("Querying reasoner and updating Protege-OWL...");
        TimeDifference td = new TimeDifference();
        td.markStart();

        // Disable the events as we may not be updating protege
        // from the event dispatch thread
        boolean eventsEnabled = kb.setGenerateEventsEnabled(false);        
        try {
	        kb.beginTransaction("Compute and update inferred types");
	
	        Slot inferredTypesSlot = kb.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
	        Slot classificationStatusSlot = ((AbstractOWLModel) kb).getProtegeClassificationStatusProperty();

	        Collection allIndividuals = ReasonerUtil.getInstance().getIndividuals(kb);
	        
	        for (Iterator iterator = allIndividuals.iterator(); iterator.hasNext();) {
	        	OWLIndividual curInd = (OWLIndividual) iterator.next();
	        	// Check the inferred types and asserted types
	        	// if there is a mismatch between the two then
	        	// mark the classification status of the individual
	        	// as changed. (MH - 15/09/04)
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Computing Inteffed types for individual: " + curInd);
                }
	        	        	
	        	Collection<OWLClass> inferredTypes = (isDisplayDirectTypesOnly == true) ? 
	        				protegeReasoner.getIndividualDirectTypes(curInd):
	        				protegeReasoner.getIndividualTypes(curInd);

	        	
	          	if (inferredTypes.size() == 0) {
	        		inferredTypes.add(curInd.getOWLModel().getOWLThingClass());
	        	}
	          	
	        	final Collection assertedTypes = curInd.getProtegeTypes();
	        	kb.setOwnSlotValues(curInd, inferredTypesSlot, inferredTypes);

	        	if (inferredTypes.containsAll(assertedTypes) == false &&
	        			assertedTypes.containsAll(inferredTypes) == false) {
	        		kb.setOwnSlotValues(curInd, classificationStatusSlot, Collections.singleton(new Integer(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED)));
	        	}
	        	else {
	        		kb.setOwnSlotValues(curInd, classificationStatusSlot, Collections.singleton(new Integer(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED)));
	        	}      

	        	setProgress(getProgress() + 1);
	        	doAbortCheck();
	        }
	        kb.commitTransaction();
        }
        catch (ProtegeReasonerException e) {
        	kb.rollbackTransaction();
        	throw e;
        }
        catch (Exception e) {
        	kb.rollbackTransaction();
        	
        	RuntimeException re = new RuntimeException();
        	re.initCause(e);
        	throw re;
		} finally{
			kb.setGenerateEventsEnabled(eventsEnabled);
		}

        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update Protege-OWL = " + td, parentRecord));
        setTaskCompleted();
    }
    
    
    private boolean isDisplayDirectTypesOnly() {
    	return ApplicationProperties.getBooleanProperty(DISPLAY_DIRECT_TYPES_ONLY, false);
    }
    
}

