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
 * Date: Sep 12, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractTask implements Task {

    private boolean cancelled;

    private String title;

    private boolean canBeCancelled;

    private TaskManager tm;

	private int progMin;

	private int progMax;


    public AbstractTask(String title, boolean canBeCancelled, TaskManager tm) {
        this(title, canBeCancelled, tm, 0, 100);
    }

	public AbstractTask(String title, boolean canBeCancelled, TaskManager tm, int progMin, int progMax) {
		this.title = title;
        this.canBeCancelled = canBeCancelled;
        this.tm = tm;
		this.progMin = progMin;
		this.progMax = progMax;
	}


    public boolean isPossibleToCancel() {
        return canBeCancelled;
    }


    public void cancelTask() {
        cancelled = true;
    }


    public String getTitle() {
        return title;
    }


    public int getProgressMin() {
        return progMin;
    }


    public int getProgressMax() {
        return progMax;
    }


    public boolean isCancelled() {
        return cancelled;
    }


    public void setProgress(int value) {
        tm.setProgress(this, value);
    }


    public void setProgressIndeterminate(boolean b) {
        tm.setIndeterminate(this, true);
    }


    public void setMessage(String message) {
        tm.setMessage(this, message);
    }


}

