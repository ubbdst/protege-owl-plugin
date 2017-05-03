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

package edu.stanford.smi.protegex.owl.model;

/**
 * The default implementation of TaskManager.
 * <p/>
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 11, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultTaskManager implements TaskManager {

	private TaskProgressDisplay taskProgressDisplay;


	public DefaultTaskManager() {
	}


	public void setProgressDisplay(TaskProgressDisplay taskProgressDisplay) {
		this.taskProgressDisplay = taskProgressDisplay;
	}


	public TaskProgressDisplay getProgressDisplay() {
		return taskProgressDisplay;
	}


	public void run(Task task)
	        throws Exception {
		// Delegate to the task progress display
		taskProgressDisplay.run(task);
	}


	public void setProgress(final Task task,
	                        final int progress) {
		taskProgressDisplay.setProgress(task, progress);
	}


	public void setIndeterminate(final Task task,
	                             final boolean b) {
		taskProgressDisplay.setProgressIndeterminate(task, b);
	}


	public void setMessage(final Task task,
	                       final String message) {
		taskProgressDisplay.setMessage(task, message);
	}
}

