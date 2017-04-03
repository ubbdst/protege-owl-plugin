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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import javafx.scene.control.Alert;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2006
 */
public class BasicFind implements Find {
    private static Logger log = Log.getLogger(BasicFind.class);
    
    private FindStatus status = FindStatus.INIT;

    private static final int MAX_MATCHES = -1;

    OWLModel owlModel;

    private Map<RDFResource, FindResult> results = new HashMap<RDFResource, FindResult>();
    
    private String searchString;

    private int searchType;

    private List<SearchListener> listeners;

    public BasicFind(OWLModel owlModel, int type) {
        this.owlModel = owlModel;
        this.searchType = type;
    }

    public void startSearch(String s) {
        startSearch(s, searchType);
    }

    public void startSearch(String s, int type) {
      if (log.isLoggable(Level.FINE)) {
          log.fine("Starting search on " + s + " with type " + type + " [" + Thread.currentThread().getName() + "]");
      }
      try {
        synchronized (this) {
            switch (status) {
            case INIT:
            case COMPLETED:
            case CANCELLED:
                searchString = s;
                searchType = type;
                results.clear();
                status = FindStatus.RUNNING;
                break;
            default:
                throw new IllegalStateException("Should not start new search before existing search completes");
            }
        }

        notifySearchStarted();
        
        if ((s != null) && (s.length() > 0)) {
            log.info("Searching for text [" + s + "]" );

          List<Slot> searchProps = getSearchProperties();
          for (Slot searchProp : searchProps) {
              if (status == FindStatus.CANCELLING) {
                  break;
              }
              Map<RDFResource, FindResult> res = searchOnSlot(searchProp, s, null, type);
              synchronized (this) {
                  results.putAll(res);
              }
              notifyResultsUpdated();
          }
          String lang = owlModel.getDefaultLanguage();
          if (lang != null) {
              for (Slot searchProp : searchProps) {
                  if (status == FindStatus.CANCELLING) {
                      break;
                  }
                  if (!searchProp.equals(((KnowledgeBase) owlModel).getNameSlot())) {
                      Map<RDFResource, FindResult> res = searchOnSlot(searchProp, s, lang, type);
                      synchronized (this) {
                          results.putAll(res);
                      }
                      notifyResultsUpdated();
                  }
              }
          }
        }
      } finally {
        synchronized (this) {
            switch (status) {
            case RUNNING:
                status = FindStatus.COMPLETED;
                break;
            case CANCELLING:
                status = FindStatus.CANCELLED;
                break;
            default:
                throw new RuntimeException("Programmer error");
            }
            this.notifyAll();
        }  
      }
      switch (status) {
      case COMPLETED:
          notifySearchComplete();
          break;
      case CANCELLED:
          notifySearchCancelled();
          break;
      default:
          throw new RuntimeException("Programmer error");
      }
      if (log.isLoggable(Level.FINE)) {
          log.fine("Finished search on " + s + " with type " + type + " [" + Thread.currentThread().getName() + "]");
      }
    }

    public void cancelSearch() {
        synchronized (this) {
            switch (status) {
            case RUNNING:
                status = FindStatus.CANCELLING;
                break;
            default:
                break;
            }

        }
    }

    @SuppressWarnings("deprecation")
    protected Map<RDFResource, FindResult> searchOnSlot(Slot searchProp, 
                                                        String searchStr,
                                                        String lang, 
                                                        int searchType) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Starting search on slot = " + searchProp + " [" + Thread.currentThread().getName() + "]");
        }
        Map<RDFResource, FindResult> slotResults = new HashMap<RDFResource, FindResult>();
        Slot nameSlot = owlModel.getNameSlot();

        Collection<Frame> frames = null;

        String actualSearchString = searchStr;

        switch (searchType) {
            case STARTS_WITH:
                actualSearchString += "*"; // no break
            case EXACTLY_MATCHES:
                if (!searchProp.equals(nameSlot) && lang != null) {
                    actualSearchString = "~#" + lang + " " + actualSearchString;
                }
                break;

            case CONTAINS:
                actualSearchString += "*"; // no break
            case ENDS_WITH:
                actualSearchString = "*" + actualSearchString;
                break;
        }
        if ((searchType == STARTS_WITH || searchType == EXACTLY_MATCHES) && searchProp.equals(nameSlot)) {
            actualSearchString = "*" + actualSearchString;
        }

        frames = ((KnowledgeBase) owlModel).getMatchingFrames(searchProp, null, false, actualSearchString, BasicFind.MAX_MATCHES);

        if ((searchType == STARTS_WITH || searchType == EXACTLY_MATCHES) && searchProp.equals(nameSlot)) {
            Set<Frame> selectedFrames = new HashSet<Frame>();
            for (Frame frame : frames) {
                if (searchType == STARTS_WITH && frame instanceof RDFResource 
                        && ((RDFResource) frame).getLocalName().startsWith(searchStr)) {
                    selectedFrames.add(frame);
                }
                else if (searchType == EXACTLY_MATCHES && frame instanceof RDFResource
                            && ((RDFResource) frame).getLocalName().equals(searchStr)) {
                    selectedFrames.add(frame);
                }
            }
            frames = selectedFrames;
        }
        
        if (frames != null) {
            for (Frame f : frames) {
                if (status == FindStatus.CANCELLING) {
                    break;
                }
                if (isValidFrameToSearch(f)) {
                    RDFResource res = (RDFResource) f;
                    FindResult item = FindResult.createFindResult(res, searchProp, searchStr);
                    if (item != null) {
                        slotResults.put(res, item);
                    }
                }
            }
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("Finished searching on slot " + searchProp + " [" + Thread.currentThread().getName() + "]");
        }
        return slotResults;
    }

    /**
     * by default automatically search the name and the current browser slot
     * as well as the search synonym slots (if set)
     * FIXME: TT: This method is wrong. It does not treat correctly the browser text
     */
    @SuppressWarnings("unchecked")
    protected List<Slot> getSearchProperties() {
        List<Slot> searchProps = new ArrayList<Slot>();

        Collection synonymProps = owlModel.getSearchSynonymProperties();
        searchProps.addAll(synonymProps);

        searchProps.add(owlModel.getNameSlot());

        Slot bs = owlModel.getOWLNamedClassClass().getBrowserSlotPattern().getFirstSlot();
        if ((!bs.equals(owlModel.getNameSlot())) && (!synonymProps.contains(bs))) {
            searchProps.add(bs);
        }

        return searchProps;
    }

    /*
     * If the following calls are expensive we can build thread-safe Map and Set objects
     * to use.
     */

    public Map<RDFResource, FindResult> getResults() {
      synchronized (this) {
        return new HashMap<RDFResource, FindResult>(results);
      }
    }

    public Set<RDFResource> getResultResources() {
      synchronized (this) {
        return new HashSet<RDFResource>(results.keySet());
      }
    }


    public int getResultCount() {
      synchronized (this) {
        return results.size();
      }
    }


    public String getSummaryText() {
        synchronized (this) {
            switch (status) {
            case RUNNING:
            case CANCELLING:
                return "Searching for \"" + searchString + "\" : (" + results.size() + " matches)";
            case COMPLETED:
            case CANCELLED:
                return "Search Results for \"" + searchString + "\" : (" + results.size() + " matches)";
            case INIT:
                return "Search Starting...";
            default:
                throw new RuntimeException("Programmer error: unknown state");
            }
        }
    }


    public String getLastSearch() {
      synchronized  (this) {
        return searchString;
      }
    }


    public String getDescription() {
        return "Find Resource";
    }


    public OWLModel getModel() {
        return owlModel;
    }


    public int getNumSearchProperties() {
        return getSearchProperties().size();
    }

    /**
     * Only look at classes, properties and individuals
     *
     * @param f a frame
     * @return true if the given frame should be included in the results
     */
    protected boolean isValidFrameToSearch(Frame f) {
        Class fclass = f.getClass();
        return (RDFResource.class.isAssignableFrom(fclass));
    }

    public int getSearchType() {
      synchronized (this) {
        return searchType;
      }
    }
    
    protected void notifySearchStarted() {
        for (SearchListener l : getListeners()) {
            l.searchStartedEvent(this);
            l.searchEvent(this);
        }
    }

    protected void notifyResultsUpdated() {
        for (SearchListener l : getListeners()) {
            l.resultsUpdatedEvent(results.size(), this);
            l.searchEvent(this);
        }
    }

    protected void notifySearchComplete() {
        for (SearchListener l : getListeners()) {
            l.searchCompleteEvent(results.size(), this);
            l.searchEvent(this);
        }
    }

    protected void notifySearchCancelled() {
        for (SearchListener l : getListeners()) {
            l.searchCancelledEvent(this);
            l.searchEvent(this);
        }
    }

    public void addResultListener(SearchListener l) {
      synchronized (this) {
        if (listeners == null) {
            listeners = new ArrayList<SearchListener>();
        }
        listeners.add(l);
      }
    }

    public boolean removeResultListener(SearchListener l) {
      synchronized (this) {
        if (listeners != null) {
            return listeners.remove(l);
        }
        return false;
      }
    }

    public Collection<SearchListener> getListeners() {
        synchronized (this) {
            return new ArrayList<SearchListener>(listeners);
        }
    }
    
    public FindStatus getFindStatus() {
        synchronized (this) {
            return status;
        }
    }
    
    public void waitForSearchComplete() {
        synchronized (this) {
            while (true) {
                switch (status) {
                case RUNNING:
                case CANCELLING:
                    try {
                        wait();
                    }
                    catch (InterruptedException ie) {
                        log.log(Level.SEVERE, "Unexpeccted interrupt", ie);
                    }
                    break;
                default:
                    return;
                }
            }
        }
    }
    
    public void reset() {   
        synchronized (this) {
            switch (status) {
            case INIT:
            case COMPLETED:
            case CANCELLED:
                searchString = "";
                results.clear();   
                break;
            default:
                throw new IllegalStateException("Attempted reset while still running");
            }

        }
    }
}
