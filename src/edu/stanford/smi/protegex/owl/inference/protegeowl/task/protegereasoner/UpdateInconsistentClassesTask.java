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
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;


public class UpdateInconsistentClassesTask extends AbstractReasonerTask {

    private ProtegeReasoner protegeReasoner;


    public UpdateInconsistentClassesTask(ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.protegeReasoner = protegeReasoner;
    }


    public int getTaskSize() {
        // The size depends on the number
        // of named classes.

        return ReasonerUtil.getInstance().getNamedClses(protegeReasoner.getOWLModel()).size();
    }


    public void run() throws ProtegeReasonerException {

        OWLModel owlModel = protegeReasoner.getOWLModel();

        ReasonerLogRecord parentRecord = ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Check concept consistency",
                null);
        postLogRecord(parentRecord);

        setProgress(0);

        TimeDifference td = new TimeDifference();

        setDescription("Computing inconsistent concepts");

        // This ought to be changed so that only concepts in the TBox are queried

        setMessage("Querying reasoner for inconsistent concepts and updating Protege-OWL...");
        
        ReasonerLogRecord icParentRecord = null;
        
        td.markStart();
        
        boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);        

        try {
	        owlModel.beginTransaction("Compute and mark inconsistent classes");
	
	        // Get an iterator which we can use to
	        // traverse the query responses
	
	        OWLNamedClass owlNothing = owlModel.getOWLNothing();
	        
	        Collection allClses = ReasonerUtil.getInstance().getNamedClses(owlModel);
	        
	        for (Iterator iterator = allClses.iterator(); iterator.hasNext();) {
				OWLNamedClass curNamedCls = (OWLNamedClass) iterator.next();
				
			    doAbortCheck();
			    
			    boolean isConsistent = protegeReasoner.isSatisfiable(curNamedCls);
		    			    
                if (isConsistent) {
                    curNamedCls.setClassificationStatus(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED);
                    curNamedCls.removeInferredSuperclass(owlNothing);
                }
                else {
                    if (icParentRecord == null) {
                        icParentRecord = ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Inconsistent concepts",
                                parentRecord);
                        postLogRecord(icParentRecord);
                    }

                    postLogRecord(ReasonerLogRecordFactory.getInstance().createConceptConsistencyLogRecord(curNamedCls,
                            false, icParentRecord));
                    curNamedCls.setClassificationStatus(OWLNames.CLASSIFICATION_STATUS_INCONSISTENT);
                    
                    Collection inferredSuperclasses = curNamedCls.getInferredSuperclasses();
                    
                    if (!inferredSuperclasses.contains(owlNothing)) {
                    	curNamedCls.addInferredSuperclass(owlNothing);
                    }
                }
			
                setProgress(getProgress() + 1);
			}
	         
	
	        td.markEnd();
	
	        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update Protege-OWL = " + td,
	                parentRecord));
	
	        setTaskCompleted();
	        owlModel.commitTransaction();
        }        
        catch (ProtegeReasonerException e) {
        	owlModel.rollbackTransaction();
        	throw e;
        }
        catch (Exception e) {        	
        	Log.getLogger().log(Level.WARNING, "Exception in transaction. Rollback. Exception: " + e.getMessage(), e);
        	owlModel.rollbackTransaction();
        	RuntimeException re = new RuntimeException();
        	re.initCause(e);
        	throw re;
		}
        
        owlModel.setGenerateEventsEnabled(eventsEnabled);
    }


}

