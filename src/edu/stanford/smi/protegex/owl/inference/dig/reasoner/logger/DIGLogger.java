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

package edu.stanford.smi.protegex.owl.inference.dig.reasoner.logger;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGError;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasoner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Feb 15, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGLogger {

    private static Map loggerMap;


    static {
        loggerMap = new WeakHashMap();
    }


    private ArrayList listeners;


    private DIGLogger() {
        listeners = new ArrayList();
    }


    public static DIGLogger getInstance(DIGReasoner digReasoner) {
        DIGLogger instance = (DIGLogger) loggerMap.get(digReasoner);
        if (instance == null) {
            instance = new DIGLogger();
            loggerMap.put(digReasoner, instance);
        }
        return instance;
    }


    public void logError(DIGError error) {
        for (Iterator it = new ArrayList(listeners).iterator(); it.hasNext();) {
            WeakReference ref = (WeakReference) it.next();
            if (ref.get() == null) {
                it.remove();
            }
            else {
                ((DIGLoggerListener) it.next()).errorLogged(error);
            }
        }
    }


    public void addListener(DIGLoggerListener listener) {
        listeners.add(new WeakReference(listener));
    }


    public void removeListener(DIGLoggerListener listener) {
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            WeakReference ref = (WeakReference) it.next();
            if (ref.get() == null) {
                it.remove();
            }
            else if (ref.get() == listener) {
                it.remove();
            }
        }
    }


}

