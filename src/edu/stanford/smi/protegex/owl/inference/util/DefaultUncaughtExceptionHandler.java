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

package edu.stanford.smi.protegex.owl.inference.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler{

	public void uncaughtException(Thread t, Throwable e) {
		if (e instanceof OutOfMemoryError) {
		    Log.getLogger().log(Level.SEVERE, "OutOfMemory caught. Trying to recover.");
			ReasonerManager.getInstance().dispose();

			System.gc();
			System.runFinalization();
			System.gc();

			Log.getLogger().log(Level.SEVERE, "OutOfMemory caught. Disposed reasoner and garbage collected. Thread: " + t + " Free memory: " + Runtime.getRuntime().freeMemory());
		} else {
			Log.getLogger().log(Level.WARNING, "Exception caught by default exception handler", e);
		}
	}
}