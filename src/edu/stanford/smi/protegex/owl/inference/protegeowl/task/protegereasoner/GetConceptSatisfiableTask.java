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

import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLClass;

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

    private ProtegeReasoner protegeReasoner;

    private boolean satisfiable;


    public GetConceptSatisfiableTask(OWLClass aClass,
                                     ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.aClass = aClass;
        this.protegeReasoner = protegeReasoner;
        this.satisfiable = true;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run() throws ProtegeReasonerException {      

        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();
        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Checking consistency of " + aClass.getBrowserText(),
                null);
        postLogRecord(parentRecord);
        setDescription("Computing consistency");

        setMessage("Querying reasoner...");
        TimeDifference td = new TimeDifference();
        td.markStart();

        satisfiable = protegeReasoner.isSatisfiable(aClass);

        td.markEnd();
        
        postLogRecord(logRecordFactory.createInformationMessageLogRecord("Time to query reasoner = " + td, parentRecord));
  

        postLogRecord(logRecordFactory.createConceptConsistencyLogRecord(aClass, satisfiable, parentRecord));
        setProgress(1);
        setMessage("Finished");
        setTaskCompleted();
    }


    public boolean getResult() {
        return satisfiable;
    }
}

