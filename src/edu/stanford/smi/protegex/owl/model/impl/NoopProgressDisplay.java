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

package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protegex.owl.model.Task;
import edu.stanford.smi.protegex.owl.model.TaskProgressDisplay;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 13, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class NoopProgressDisplay implements TaskProgressDisplay {

	public void run(Task task) throws Exception {
		// Just run the task in what ever thread it
		// was called from
		task.runTask();
	}


	public void setProgress(Task task,
	                        int progress) {
		// Do nothing
	}


	public void setProgressIndeterminate(Task task,
	                                     boolean b) {
		// Do nothing
	}


	public void setMessage(Task task,
	                       String message) {
		// Do nothing
	}


	public void end(Task task) {
		// Do nothing
	}
}

