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

import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskEvent;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 16, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The classify taxonomy task excompases three reasoner tasks:
 * <br>a) Checking an updating for inconsistent concepts
 * <br>b) Computing the inferred superclasses for consistent concepts
 * <br>c) Computing equivalent concepts for consistent concepts
 */
public class ClassifyTaxonomyTask extends AbstractReasonerTask implements ReasonerTaskListener {

	private ResetInferredHierarchyTask resetInferredHierarchyTask;

	private UpdateInconsistentClassesTask inconsistentClassesTask;

	private UpdateInferredHierarchyTask inferredHierarchyTask;

	private UpdateEquivalentClassesTask equivalentClassesTask;


	public ClassifyTaxonomyTask(ProtegeReasoner protegeReasoner) {

		super(protegeReasoner);
		resetInferredHierarchyTask = new ResetInferredHierarchyTask(protegeReasoner);
		resetInferredHierarchyTask.addTaskListener(this);
		inconsistentClassesTask = new UpdateInconsistentClassesTask(protegeReasoner);
		inconsistentClassesTask.addTaskListener(this);
		inferredHierarchyTask = new UpdateInferredHierarchyTask(protegeReasoner);
		inferredHierarchyTask.addTaskListener(this);
		equivalentClassesTask = new UpdateEquivalentClassesTask(protegeReasoner);
		equivalentClassesTask.addTaskListener(this);
	}


	public int getTaskSize() {
		int taskSize = resetInferredHierarchyTask.getTaskSize() +
		inconsistentClassesTask.getTaskSize() +
		inferredHierarchyTask.getTaskSize() +
		equivalentClassesTask.getTaskSize();

		return taskSize;
	}


	public void run() throws ProtegeReasonerException {
		// Run each task in order.  Check to see
		// if the user has requested that the task
		// be aborted after each task has run

		setProgress(0);
		doAbortCheck();
		resetInferredHierarchyTask.run();
		doAbortCheck();
		inconsistentClassesTask.run();
		doAbortCheck();
		inferredHierarchyTask.run();
		doAbortCheck();
		equivalentClassesTask.run();
		setDescription("Finished");
		setMessage("Classification complete");
		setTaskCompleted();			

	}


	public void progressChanged(ReasonerTaskEvent event) {
		int progress = resetInferredHierarchyTask.getProgress() +
		inconsistentClassesTask.getProgress() +
		inferredHierarchyTask.getProgress() +
		equivalentClassesTask.getProgress();

		setProgress(progress);

	}


	public void progressIndeterminateChanged(ReasonerTaskEvent event) {
		setProgressIndeterminate(event.getSource().isProgressIndeterminate());
	}


	public void descriptionChanged(ReasonerTaskEvent event) {
		setDescription(event.getSource().getDescription());
	}


	public void messageChanged(ReasonerTaskEvent event) {
		setMessage(event.getSource().getMessage());
	}


	public void taskFailed(ReasonerTaskEvent event) {
		setMessage(event.getSource().getMessage());
	}


	public void taskCompleted(ReasonerTaskEvent event) {
		// Don't do anything!
	}


	public void addedToTask(ReasonerTaskEvent event) {
		// Don't need to do anything here
	}


	public void setRequestAbort() {
		super.setRequestAbort();
		// Propagate the request to sub tasks
		resetInferredHierarchyTask.setRequestAbort();
		inconsistentClassesTask.setRequestAbort();
		inferredHierarchyTask.setRequestAbort();
		equivalentClassesTask.setRequestAbort();
	}


}

