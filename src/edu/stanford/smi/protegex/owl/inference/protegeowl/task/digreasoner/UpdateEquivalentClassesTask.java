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

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGQueryResponse;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;

import org.w3c.dom.Document;

import java.util.Collection;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 23, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UpdateEquivalentClassesTask extends AbstractReasonerTask {

    private ProtegeOWLReasoner protegeOWLReasoner;


    public UpdateEquivalentClassesTask(ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.protegeOWLReasoner = protegeOWLReasoner;
    }


    public int getTaskSize() {
        return ReasonerUtil.getInstance().getNamedClses(protegeOWLReasoner.getKnowledgeBase()).size();
    }


    public void run() throws DIGReasonerException {
        OWLModel kb = protegeOWLReasoner.getKnowledgeBase();

        ReasonerLogRecord parentRecord = ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Compute equivalent classes",
                null);

        doAbortCheck();

        postLogRecord(parentRecord);

        TimeDifference td = new TimeDifference();

        setProgress(0);

        setDescription("Computing equivalent classes");

        // Build the query
        setMessage("Building equivalent classes reasoner query...");

        td.markStart();

        Document asksDoc = getTranslator().createAsksDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());

        Collection namedClses = ReasonerUtil.getInstance().getNamedClses(kb);

        Iterator namedClsesIt = namedClses.iterator();

        while (namedClsesIt.hasNext()) {
            final OWLNamedClass curNamedCls = (OWLNamedClass) namedClsesIt.next();

            getTranslator().createEquivalentConceptsQuery(asksDoc, curNamedCls.getName(), curNamedCls);
        }

        td.markEnd();

        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to build query = " + td,
                parentRecord));

        doAbortCheck();

        // Send the query and get the response

        setMessage("Querying reasoner...");

        td.markStart();

        Document responseDoc = protegeOWLReasoner.getDIGReasoner().performRequest(asksDoc);

        td.markEnd();

        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to query reasoner = " + td,
                parentRecord));

        doAbortCheck();

        // Update Protge-OWL

        setMessage("Updating Protege-OWL...");

        td.markStart();
        
        boolean eventsEnabled = kb.setGenerateEventsEnabled(false);

        try {
	        kb.beginTransaction("Compute and update equivalent classes");
	
	        Iterator responseIt = getTranslator().getDIGQueryResponseIterator(kb, responseDoc);
	
	        while (responseIt.hasNext()) {
	            doAbortCheck();
	
	            final DIGQueryResponse response = (DIGQueryResponse) responseIt.next();
	            final String curQueryID = response.getID();
	            final OWLNamedClass curNamedCls = kb.getOWLNamedClass(curQueryID);
	
	            if (curNamedCls != null) {
	                if (curNamedCls.isConsistent()) {
	                    Iterator clsesIt;
	
	                    clsesIt = response.getConcepts().iterator();
	
	                    while (clsesIt.hasNext()) {
	                        final OWLNamedClass curSuperCls = (OWLNamedClass) clsesIt.next();
	
	                        if (curSuperCls.equals(curNamedCls) == false) {
	                            if (curNamedCls.getInferredSuperclasses().contains(curSuperCls) == false) {
	                                curNamedCls.addInferredSuperclass(curSuperCls);
	                            }
	
	                            if (curSuperCls.getInferredSuperclasses().contains(curNamedCls) == false) {
	                                curSuperCls.addInferredSuperclass(curNamedCls);
	                            }
                                curNamedCls.setClassificationStatus(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED);
	                        }
	                    }
	                }
	            }
	
	            setProgress(getProgress() + 1);
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

