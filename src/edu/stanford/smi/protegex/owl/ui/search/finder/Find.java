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

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Map;
import java.util.Set;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public interface Find {
    
    public enum FindStatus {
        INIT, RUNNING, CANCELLING, COMPLETED, CANCELLED;
    }

    int STARTS_WITH = 0;
    int CONTAINS = 1;
    int ENDS_WITH = 2;
    int EXACTLY_MATCHES = 3;

    String[] searchTypeString = {"starts with", "contains", "ends with", "matches"};

    void startSearch(String s);

    /**
     * This method should start the search.
     *
     * @param s          the string to search for
     * @param searchType
     */
    void startSearch(String s, int searchType);

//  /**
//   * Search again within the current results
//   * Useful for when the seach becomes more specific (eg when adding characters
//   * to the search string
//   * @param s the string to seach for
//   */
//  void refineSearch(String s);

    void cancelSearch();

    /**
     * This method can be called to get the current results
     *
     * @return a map of Resources as keys with SearchResultItem objects as values
     */
    Map getResults();

    Set getResultResources();

    int getResultCount();

    String getLastSearch();
    
    int getSearchType();

    String getSummaryText();

    String getDescription();

    OWLModel getModel();

    int getNumSearchProperties();

    void addResultListener(SearchListener l);

    boolean removeResultListener(SearchListener l);
    
    FindStatus getFindStatus();
    
    void waitForSearchComplete();

    void reset();
}
