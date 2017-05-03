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
import java.util.HashSet;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 17, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetIndividualInferredTypesTask extends AbstractReasonerTask implements CollectionResultReasonerTask {

    private OWLIndividual individual;

    private ProtegeReasoner protegeReasoner;

    private HashSet types;


    public GetIndividualInferredTypesTask(OWLIndividual individual,
                                          ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.individual = individual;
        this.protegeReasoner = protegeReasoner;

        types = new HashSet();
    }


    public int getTaskSize() {
        return 1;
    }


    public void run()
            throws ProtegeReasonerException {
        
        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

        setDescription("Getting types for individual");
        setProgress(0);
        setMessage("Building reasoner query...");

        setMessage("Querying reasoner...");

        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Inferred types for: " + individual.getBrowserText(), null);
        postLogRecord(parentRecord);
  
        types.addAll(protegeReasoner.getIndividualTypes(individual));

        Iterator typesIt = types.iterator();

        while (typesIt.hasNext()) {
            final RDFSClass curClass = (RDFSClass) typesIt.next();

            postLogRecord(logRecordFactory.createOWLInstanceLogRecord(curClass, parentRecord));
        }

        setMessage("Finished");

        setTaskCompleted();
    }


    public Collection getResult() {
        return types;
    }
}

