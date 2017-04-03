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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.ExclusiveRunnable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2006
 */
public class ThreadedFind extends BasicFind {
    private static Logger log = Log.getLogger(ThreadedFind.class);

    private Thread searchThread;
    private DoFind currentfind;

    public ThreadedFind(OWLModel owlModel, int type) {
        super(owlModel, type);
        currentfind = new DoFind();
    }

    /**
     * Starts the search.
     * 
     * Note that this routine  will wait for any previous searches to complete.
     * 
     * @param s The string to search for.
     * @param type The type of search.
     * 
     */
    public void startSearch(String s, int type) {
      synchronized (this) {
        currentfind.setString(s);
        currentfind.setType(type);

        searchThread = new Thread(currentfind);

        searchThread.start();
      }      
    }
    
    private void startSuperSearch(String s, int type) {
      super.startSearch(s, type);
    }

    public void cancelSearch() {
      if (log.isLoggable(Level.FINE)) {
          log.fine("Cancelling search [" + Thread.currentThread().getName() + "]");
      }
      super.cancelSearch();
      if (currentfind != null) {
        currentfind.abort();
      }
    }
    
    class DoFind extends ExclusiveRunnable {
      private String string;
      private int searchType;
      
      public void setString(String string) {
        this.string = string;
      }
      
      public void setType(int searchType) {
        this.searchType = searchType; 
      }
      
      public void execute() {
        startSuperSearch(string, searchType);
      }
    }
}
