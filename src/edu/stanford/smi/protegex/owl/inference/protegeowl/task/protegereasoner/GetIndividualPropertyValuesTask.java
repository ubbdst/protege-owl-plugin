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

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

public class GetIndividualPropertyValuesTask extends AbstractReasonerTask{
	private OWLIndividual individual;

	private ProtegeReasoner protegeReasoner;

	public GetIndividualPropertyValuesTask(OWLIndividual individual,
			ProtegeReasoner protegeReasoner) {
		super(protegeReasoner);
		this.individual = individual;
		this.protegeReasoner = protegeReasoner;
	}

	public int getTaskSize() {
		return 1;
	}

	@SuppressWarnings("deprecation")
	public void run() throws ProtegeReasonerException {
		ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

		setDescription("Getting properties values for individual");
		setProgress(0);

		setMessage("Querying reasoner...");

		ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Inferred properties values for: " + individual.getBrowserText(), null);
		postLogRecord(parentRecord);

		Collection<Slot> slots = individual.getOwnSlots();

		for (Slot slot : slots) {
			if (!slot.isSystem() && slot instanceof RDFProperty) {
				RDFProperty prop = (RDFProperty) slot;

				if (prop instanceof OWLObjectProperty) {
					Collection<OWLIndividual> values = protegeReasoner.getRelatedIndividuals(individual, (OWLObjectProperty) prop);
					if (values != null && values.size() > 0) {
						ReasonerLogRecord propLogRecord = logRecordFactory.createOWLPropertyLogRecord(prop, parentRecord);
						postLogRecord(propLogRecord);

						for (OWLIndividual individual : values) {
							postLogRecord(logRecordFactory.createOWLInstanceLogRecord(individual, propLogRecord));
						}
					}
				} else if (prop instanceof OWLDatatypeProperty) {
					Collection values = protegeReasoner.getRelatedValues(individual, (OWLDatatypeProperty) prop);
					if (values != null && values.size() > 0) {
						ReasonerLogRecord propLogRecord = logRecordFactory.createOWLPropertyLogRecord(prop, parentRecord);
						postLogRecord(propLogRecord);

						for (Object value : values) {
							String message = value.toString();
							
							if (value instanceof RDFSLiteral) {
								message = ((RDFSLiteral)value).getBrowserText();
							}
							
							postLogRecord(logRecordFactory.createInformationMessageLogRecord(message, propLogRecord));
						}
					}
				}			
			}
		}

		setMessage("Finished");

		setTaskCompleted();		
	}

}
