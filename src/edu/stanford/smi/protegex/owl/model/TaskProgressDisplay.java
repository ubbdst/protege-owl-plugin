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
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 11, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface TaskProgressDisplay {

    /**
     * Runs the task and starts the display of progress to the user.
     * @param task The <code>Task</code> that will be run and
     * whose progress will be monitored and displayed.
     */
    public void run(Task task) throws Exception;


    /**
     * Updates the value of the progress that is
     * displayed to the user.
     *
     * @param task The task which the progress relates to
     * @param progress The progress that will
     *                 be between the min and max progress for
     *                 the <code>Task</code>
     */
    public void setProgress(Task task, int progress);


    /**
     * Sets the progress display to indicate that the
     * progress cannot be determined, but the task is
     * proceding as normal.
     *
     * @param b <code>true</code> if the progress is
     *          indeterminate, or <code>false</code> if the progress
     *          is not indeterminate.
     */
    public void setProgressIndeterminate(Task task, boolean b);


    /**
     * Sets the message that will be displayed to the user.
     */
    public void setMessage(Task task, String message);


    /**
     * Stops (hides) the progress display. This methods is
     * generally called when the task is complete.
     */
    public void end(Task task);
}
